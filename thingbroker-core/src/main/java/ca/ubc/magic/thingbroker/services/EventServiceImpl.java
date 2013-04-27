package ca.ubc.magic.thingbroker.services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import ca.ubc.magic.thingbroker.config.Constants;
import ca.ubc.magic.thingbroker.dao.ApplicationDao;
import ca.ubc.magic.thingbroker.dao.EventDAO;
import ca.ubc.magic.thingbroker.dao.EventDataDAO;
import ca.ubc.magic.thingbroker.dao.ThingDAO;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Application;
import ca.ubc.magic.thingbroker.model.Content;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.EventService;
import ca.ubc.magic.thingbroker.services.interfaces.RealTimeEventService;
import ca.ubc.magic.utils.Messages;
import ca.ubc.magic.utils.Utils;

/**
 * Event service implementation
 * 
 * @author Ricardo Almeida, Mike Blackstock
 *
 */
public class EventServiceImpl implements EventService {
	static final Log logger = LogFactory.getLog(EventServiceImpl.class);

	private final JmsTemplate jmsTemplate;
	private final RealTimeEventService realTimeEventService;
	private final ThingDAO thingDao;
	private final EventDAO eventDao;
	private final EventDataDAO eventDataDao;
	private final ApplicationDao applicationDao;
	private final Messages messages;

	public EventServiceImpl(JmsTemplate jmsTemplate,
			RealTimeEventService realTimeEventService,
			ThingDAO thingDao, EventDAO eventDao, EventDataDAO eventDataDao,
			ApplicationDao applicationDao,
			Messages messages) {
		this.jmsTemplate = jmsTemplate;
		this.realTimeEventService = realTimeEventService;
		this.thingDao = thingDao;
		this.eventDao = eventDao;
		this.eventDataDao = eventDataDao;
		this.applicationDao = applicationDao;
		this.messages = messages;
	}

	public Event create(Event event, Content[] data, boolean mustSave) {
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

	public synchronized Event update(Event event, Content[] data)
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
					@SuppressWarnings("unchecked")
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
	
	/**
	 * Get the events from a thing.  First we query past events.  If we don't get any, 
	 * we then wait for real time events.
	 * 
	 * @param thingId Id of the thing
	 * @param waitTime time to wait
	 * @param followingOnly get events only from the things it is following
	 * @return
	 */
	public List<Event> getEvents(String appId, String thingId, Map<String, String> queryParams, int waitTime, Filter filter) {
		Thing t = thingDao.getThing(thingId);
		Application a = null;

		if (appId != null) {
			a = applicationDao.find(appId);
			if (a != null)
				appId = a.getId();
		}
		// do a query for past events first
		List<Event> events = eventDao.getEvents(t, queryParams, filter);
		
		if (events.size() > 0)
			return events;
		
		return realTimeEventService.getEvents(appId, t, waitTime, filter);
	}
	
	/**
	 * Same as above, use default application (null)
	 */
	public List<Event> getEvents(String thingId, Map<String, String> queryParams, int waitTime, Filter filter) {
		return getEvents(null, thingId, queryParams, waitTime, filter);
	}


	public Content retrieveEventData(Content eventData) throws Exception {
		Content data = eventDataDao.retrieve(eventData);
		if (data != null) {
			return data;
		}
		throw new ThingBrokerException(Constants.CODE_EVENT_DATA_NOT_FOUND,
				messages.getMessage("EVENT_DATA_NOT_FOUND"));
	}

	public Content retrieveEventDataInfo(Content eventData)
			throws Exception {
		Content data = eventDataDao.retrieve(eventData);
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
