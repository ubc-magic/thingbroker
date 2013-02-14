package ca.ubc.magic.thingbroker.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import ca.ubc.magic.thingbroker.controller.config.Constants;
import ca.ubc.magic.thingbroker.controller.dao.EventDAO;
import ca.ubc.magic.thingbroker.controller.dao.EventDataDAO;
import ca.ubc.magic.thingbroker.controller.dao.ThingDAO;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.EventService;
import ca.ubc.magic.thingbroker.services.interfaces.RealTimeEventService;
import ca.ubc.magic.utils.Messages;
import ca.ubc.magic.utils.Utils;

public class EventServiceImpl implements EventService {
	static final Log logger = LogFactory.getLog(EventServiceImpl.class);

	private final JmsTemplate jmsTemplate;
	private final RealTimeEventService realTimeEventService;
	private final ThingDAO thingDao;
	private final EventDAO eventDao;
	private final EventDataDAO eventDataDao;
	private final Messages messages;

	public EventServiceImpl(JmsTemplate jmsTemplate,
			RealTimeEventService realTimeEventService,
			ThingDAO thingDao, EventDAO eventDao, EventDataDAO eventDataDao,
			Messages messages) {
		this.jmsTemplate = jmsTemplate;
		this.realTimeEventService = realTimeEventService;
		this.thingDao = thingDao;
		this.eventDao = eventDao;
		this.eventDataDao = eventDataDao;
		this.messages = messages;
	}

	public Event create(Event event, EventData[] data, boolean mustSave) {
		try {
			if (mustSave) {
				event = eventDao.create(event, data); // Get the event object
														// with eventId and
														// content ids (if there
														// is some content)
			}
			sendToBroker(event);
			return event;
		} catch (EmptyResultDataAccessException e) {
			throw new ThingBrokerException(
					Constants.CODE_SENT_EVENT_TO_NON_EXISTENT_THING,
					messages.getMessage("SENT_EVENT_TO_NON-EXISTENT_THING"));
		}
	}

	public synchronized Event update(Event event, EventData[] data)
			throws ThingBrokerException {
		Event storedEvent = eventDao.retrieveById(event);
		if (storedEvent != null) {
			if (storedEvent.getServerTimestamp().longValue() == event
					.getServerTimestamp().longValue()) {
				event.setThingId(storedEvent.getThingId());
				event.setServerTimestamp(System.currentTimeMillis());
				return create(event, data, true);
			}
			throw new ThingBrokerException(
					Constants.CODE_SERVER_TIMESTAMP_OUTDATED,
					messages.getMessage("SERVER_TIMESTAMP_OUTDATED"));
		}
		throw new ThingBrokerException(Constants.CODE_EVENT_NOT_FOUND,
				messages.getMessage("EVENT_NOT_FOUND"));
	}

	public synchronized Event addDataToInfoField(Event event,
			HashMap<String, Object> content) throws Exception {
		Event storedEvent = eventDao.retrieveById(event);
		if (storedEvent != null) {
			if (storedEvent.getServerTimestamp().longValue() == event
					.getServerTimestamp().longValue()) {
				if (storedEvent.getInfo() instanceof Map) {
					LinkedHashMap<String, Object> storedInfo = (LinkedHashMap<String, Object>) storedEvent.getInfo();
					storedInfo.putAll(content);
					return create(storedEvent, null, true);
				}
				throw new ThingBrokerException(
						Constants.CODE_INVALID_EVENT_INFO_FIELD,
						messages.getMessage("EVENT_INFO_FIELD_INVALID"));
			}
			throw new ThingBrokerException(
					Constants.CODE_SERVER_TIMESTAMP_OUTDATED,
					messages.getMessage("SERVER_TIMESTAMP_OUTDATED"));
		}
		throw new ThingBrokerException(Constants.CODE_EVENT_NOT_FOUND,
				messages.getMessage("EVENT_NOT_FOUND"));
	}

	public Event retrieve(Event event) {
		Event e = eventDao.retrieveById(event);
		if (e != null) {
			return e;
		}
		throw new ThingBrokerException(Constants.CODE_EVENT_NOT_FOUND,
				messages.getMessage("EVENT_NOT_FOUND"));
	}

	public List<Event> retrieveByCriteria(Event event, Map<String, String> params) throws Exception {
		String requester = params.get("requester");
		String timeout = params.get("timeout");
		if (requester != null) {
			params.remove("requester");
			params.remove("timeout");
			Map<String, String> searchParam = new HashMap<String, String>();
			searchParam.put("thingId", requester);
			List<Thing> req = thingDao.retrieve(searchParam);
			// add thing to following list of requester if it is not there already.
			if (req != null && req.size() > 0) {
				if (!req.get(0).getFollowing().contains(event.getThingId())) {
					req.get(0).getFollowing().add(event.getThingId());
					searchParam.put("thingId", event.getThingId());
					List<Thing> tToFollow = thingDao.retrieve(searchParam);
					if (tToFollow != null) {
						tToFollow.get(0).getFollowers().add(requester);
						thingDao.update(req.get(0));
						thingDao.update(tToFollow.get(0));
					}
				}
				// get previous events from the thing
				Set<Event> storedEvents = eventDao.retrieveEventsFromThing(event, params); // Using set to avoid duplicates
				List<Event> response = new ArrayList<Event>();
				
				// if there are some, return them immediately, otherwise get real time events.
				// note to avoid duplicates must query for events after the last one received.
				if (storedEvents != null && storedEvents.size() > 0) {
					response.addAll(storedEvents);
				} else {
					// requester should follow the thing if it isn't already
					realTimeEventService.follow(requester, event.getThingId());
					long waitTime = timeout == null ? Constants.REAL_TIME_EVENTS_WAITING_TIME
							: Long.valueOf(timeout);
					List<Event> realTimeEvents = realTimeEventService.getEvents(requester, waitTime);
					if (realTimeEvents.size() > 0) {
						response.addAll(realTimeEvents);
					}
				}
				Collections.sort(response);
				return response;
			}
			throw new ThingBrokerException(
					Constants.CODE_REQUESTER_NOT_REGISTERED,
					messages.getMessage("REQUESTER_NOT_REGISTERED"));
		}
		throw new ThingBrokerException(Constants.CODE_REQUESTER_NOT_INFORMED,
				messages.getMessage("REQUESTER_NOT_INFORMED"));
	}

	public EventData retrieveEventData(EventData eventData) throws Exception {
		EventData data = eventDataDao.retrieve(eventData);
		if (data != null) {
			return data;
		}
		throw new ThingBrokerException(Constants.CODE_EVENT_DATA_NOT_FOUND,
				messages.getMessage("EVENT_DATA_NOT_FOUND"));
	}

	public EventData retrieveEventDataInfo(EventData eventData)
			throws Exception {
		EventData data = eventDataDao.retrieve(eventData);
		if (data != null) {
			data.setData(null);
			return data;
		}
		throw new ThingBrokerException(Constants.CODE_EVENT_DATA_NOT_FOUND,
				messages.getMessage("EVENT_DATA_NOT_FOUND"));
	}

	private void sendToBroker(final Event event) {
		Destination destination = new ActiveMQTopic("thingbroker.things.thing."
				+ event.getThingId());
		jmsTemplate.send(destination, new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				MapMessage message = session.createMapMessage();
				message.setString("event", Utils.generateJSON(event));
				return message;
			}
		});
		logger.debug("sending " + event + " to broker");
	}
}
