package ca.ubc.magic.thingbroker.services.realtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.utils.Utils;

/**
 * Object to handle JMS MessageConsumers and Sessions for followed things
 * 
 * @author mike
 *
 */
public class ThingEventHandler implements MessageListener {
	static final Log logger = LogFactory.getLog(ThingEventHandler.class);

	private static final int QUEUE_CAPACITY = 200;
	// time in seconds to keep this event handler around since we last called real time get events
	// this should be much longer than a typical wait time on long polling!
	private static final int EXPIRY_TIME = 300;	// 5 minutes

	private Map<String, MessageConsumer> following;
	
	private Session session;
	private LinkedBlockingQueue<Event> messageQueue;
	private long accessTime;

	public ThingEventHandler(Session session) {
		this.following = new ConcurrentHashMap<String, MessageConsumer>();
		this.session = session;
		this.messageQueue = new LinkedBlockingQueue<Event>(QUEUE_CAPACITY);
		this.accessTime = System.currentTimeMillis();
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public List<Event> getEvents(long waitTime) {
		this.accessTime = System.currentTimeMillis();

		Event newEvent = null;
		try {
			newEvent = messageQueue.poll(waitTime, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			logger.error("interrupted waiting for events", e);
		}

		List<Event> events = new ArrayList<Event>();
		if (newEvent != null) {
			events.add(newEvent);
		}
		messageQueue.drainTo(events);
		return events;
	}

	public void onMessage(Message message) {
		try {
			logger.debug("no messages");
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
			// this will close any messageconsumers as well (http://docs.oracle.com/javaee/1.4/api/javax/jms/Session.html#close())
			this.session.close();
		} catch (JMSException e) {
			logger.error("error while closing JMS consumer or session");
		}
	}

	public void addFollower(String followerThing, MessageConsumer consumer) {
		following.put(followerThing, consumer);
	}
	
	public Set<String> getFollowing() {
		return following.keySet();
	}
	
	public void removeFollower(String followerThing) {
		MessageConsumer consumer = following.get(followerThing);
		if (consumer != null)
			try {
				consumer.close();
			} catch (JMSException e) {
				logger.error("error while closing consumer");
			}
	}
	
	public void addFollower(String following) throws JMSException {
		Destination thingQueue = session.createTopic("thingbroker.things.thing." + following);
		MessageConsumer consumer = session.createConsumer(thingQueue);
		addFollower(following, consumer);
		consumer.setMessageListener(this);		
	}

	public boolean isFollowing(String followerThing) {
		return following.containsKey(followerThing);
	}

	public boolean isExpired() {
		return (System.currentTimeMillis() - this.accessTime) > EXPIRY_TIME*1000;
	}
	
}
