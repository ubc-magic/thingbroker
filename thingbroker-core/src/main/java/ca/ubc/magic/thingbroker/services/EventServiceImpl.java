package ca.ubc.magic.thingbroker.services;

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

import ca.ubc.magic.thingbroker.controller.dao.EventDAO;
import ca.ubc.magic.thingbroker.exceptions.ThingNotFoundException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;
import ca.ubc.magic.thingbroker.services.interfaces.EventService;
import ca.ubc.magic.utils.Utils;

public class EventServiceImpl implements EventService {
	static final Log logger = LogFactory.getLog(EventServiceImpl.class);

	private final JmsTemplate jmsTemplate;

	public EventServiceImpl() {
		this.jmsTemplate = new JmsTemplate();
	}

	public EventServiceImpl(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}

	public Event create(Event event, EventData[] data, boolean mustSave) {
		try {
			if (mustSave) {
				event = EventDAO.create(event, data); //Get the event object with eventId and content ids (if there is some content)
			}
			sendToBroker(event);
			return event;
		} catch (EmptyResultDataAccessException e) {
			throw new ThingNotFoundException(Utils.getMessage("SENT_EVENT_TO_NON-EXISTENT_THING"), e);
		}
	}

	public void update(Event event, EventData[] data) {

	}

	public Event retrieve(Event event) {
		return EventDAO.retrieveById(event);
	}

	public List<Event> retrieveByCriteria(Event event,Map<String, String> params) {
		return EventDAO.retrieveEventsFromThing(event, params);
	}

	private void sendToBroker(final Event event) {
		Destination destination = new ActiveMQTopic(event.getThingId());
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
