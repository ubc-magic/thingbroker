package ca.ubc.magic.thingbroker.services.realtime;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Follow;
import ca.ubc.magic.utils.Utils;

public class JmsEventHandler implements MessageListener {
	static final Log logger = LogFactory.getLog(JmsEventHandler.class);

	private static final int QUEUE_CAPACITY = 200;

	private Follow follow;
	private Session subSession;
	private MessageConsumer consumer;
	private LinkedBlockingQueue<Event> messageQueue;

	public JmsEventHandler(Follow follow, Session subSession,MessageConsumer consumer) {
		this.follow = follow;
		this.subSession = subSession;
		this.consumer = consumer;
		this.messageQueue = new LinkedBlockingQueue<Event>(QUEUE_CAPACITY);
	}

	public Follow getFollow() {
		return follow;
	}

	public void setFollow(Follow follow) {
		this.follow = follow;
	}

	public Session getSubSession() {
		return subSession;
	}

	public void setSubSession(Session subSession) {
		this.subSession = subSession;
	}

	public MessageConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(MessageConsumer consumer) {
		this.consumer = consumer;
	}

	public Set<Event> getEvents(long waitTime) throws Exception {
		if (messageQueue == null)
			throw new Exception("attempt to get events from an event handler subscription");

		Event newEvent = null;
		try {
			newEvent = messageQueue.poll(waitTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("interrupted waiting for events", e);
		}

		Set<Event> events = new LinkedHashSet<Event>();
		if (newEvent != null) {
			events.add(newEvent);
		}
		messageQueue.drainTo(events);
		cleanUp();
		return events;
	}

	public void onMessage(Message message) {
		try {
			System.out.println("Nova mensagem");
			MapMessage ac = (MapMessage) message;
			Event event = Utils.parseToObject(ac.getString("event"),Event.class);
			messageQueue.offer(event);
		} catch (JMSException jmse) {
			logger.error("JMSException", jmse);
		} catch (ClassCastException cce) {
			logger.error("ClassCastException", cce);
		} catch (Exception ex) {
			logger.error("Exception", ex);
		}
	}

	public void cleanUp() {
		try {
			this.consumer.close();
			this.subSession.close();
		} catch (JMSException e) {
			logger.error("error while closing JMS consumer or session");
		}
	}

}
