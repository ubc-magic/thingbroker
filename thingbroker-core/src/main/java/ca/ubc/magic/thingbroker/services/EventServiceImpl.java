package ca.ubc.magic.thingbroker.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import ca.ubc.magic.thingbroker.controller.dao.ThingDAO;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.EventService;
import ca.ubc.magic.thingbroker.services.interfaces.RealTimeEventService;
import ca.ubc.magic.thingbroker.services.realtime.RealTimeEventServiceImpl;
import ca.ubc.magic.utils.Utils;

public class EventServiceImpl implements EventService {
	static final Log logger = LogFactory.getLog(EventServiceImpl.class);

	private final JmsTemplate jmsTemplate;
	private final RealTimeEventService realTimeEventService;

	public EventServiceImpl() {
		this.jmsTemplate = new JmsTemplate();
		realTimeEventService = new RealTimeEventServiceImpl();
	}

	public EventServiceImpl(JmsTemplate jmsTemplate, RealTimeEventService realTimeEventService) {
		this.jmsTemplate = jmsTemplate;
		this.realTimeEventService = realTimeEventService;
	}

	public Event create(Event event, EventData[] data, boolean mustSave) {
		try {
			if (mustSave) {
				event = EventDAO.create(event, data); //Get the event object with eventId and content ids (if there is some content)
			}
			sendToBroker(event);
			return event;
		} catch (EmptyResultDataAccessException e) {
			throw new ThingBrokerException(Constants.CODE_SENT_EVENT_TO_NON_EXISTENT_THING,Utils.getMessage("SENT_EVENT_TO_NON-EXISTENT_THING"));
		}
	}

	public void update(Event event, EventData[] data) {

	}

	public Event retrieve(Event event) {
		return EventDAO.retrieveById(event);
	}

	public List<Event> retrieveByCriteria(Event event,Map<String, String> params) throws Exception {
		String requester = params.get("requester");
		if(requester != null) {
			params.remove("requester");
			Map<String, String> searchParam = new HashMap<String, String>();
			searchParam.put("thingId", requester);
			List<Thing> req = ThingDAO.retrieve(searchParam);
			if(req != null && req.size() > 0) {
				if(!req.get(0).getFollowing().contains(event.getThingId())) {
					req.get(0).getFollowing().add(event.getThingId());
					searchParam.put("thingId", event.getThingId());
					List<Thing> tToFollow = ThingDAO.retrieve(searchParam);
					if(tToFollow != null) {
					  tToFollow.get(0).getFollowers().add(requester);
					  ThingDAO.update(req.get(0));
					  ThingDAO.update(tToFollow.get(0));
					}
				}
				long followingId = realTimeEventService.follow(event.getThingId());
				Set<Event> realTimeEvents = realTimeEventService.getEvents(followingId, Constants.REAL_TIME_EVENTS_WAITING_TIME);
				Set<Event> storedEvents = EventDAO.retrieveEventsFromThing(event, params); //Using set to avoid duplicates
				List<Event> response = new ArrayList<Event>();
				if(storedEvents != null) {
					realTimeEvents.addAll(storedEvents);
				}
				response.addAll(realTimeEvents);
				Collections.sort(response);
				return response;
			}
			throw new ThingBrokerException(Constants.CODE_REQUESTER_NOT_REGISTERED,Utils.getMessage("REQUESTER_NOT_REGISTERED"));
		}
		throw new ThingBrokerException(Constants.CODE_REQUESTER_NOT_INFORMED,Utils.getMessage("REQUESTER_NOT_INFORMED"));
	}

	private void sendToBroker(final Event event) {
		Destination destination = new ActiveMQTopic("thingbroker.things.thing." + event.getThingId());
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
