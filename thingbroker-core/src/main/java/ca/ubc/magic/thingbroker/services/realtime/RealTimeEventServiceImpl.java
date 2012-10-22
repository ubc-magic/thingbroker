package ca.ubc.magic.thingbroker.services.realtime;

import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Follow;
import ca.ubc.magic.thingbroker.services.interfaces.RealTimeEventService;

public class RealTimeEventServiceImpl implements RealTimeEventService,DisposableBean {
	private static final Log logger = LogFactory.getLog(RealTimeEventServiceImpl.class);
	private static long nextId = 0;

	private Map<Long, JmsEventHandler> following;
	private Connection connection;
	
	public RealTimeEventServiceImpl() {
		
	}

	public RealTimeEventServiceImpl(ConnectionFactory connectionFactory) {
		this.following = new ConcurrentHashMap<Long, JmsEventHandler>();
		this.connection = null;
		try {
			// we start the connection -- adding sessions doesn't seem to be a problem
			this.connection = connectionFactory.createConnection();
			this.connection.start();
		} catch (JMSException e) {
			logger.error(e.getMessage());
			throw new ThingBrokerException("JMS Exception on startup", e);
		}
	}

	public long follow(String thingId) {
		return doFollow(thingId, null, null);
	}

	private long doFollow(String thingId, String url, EventHandler eventHandler) {
		Follow follow = new Follow();
		follow.setId(nextId + 1);
		List<String> followers = new ArrayList<String>();
		followers.add(thingId);
		follow.setFollowers(followers);
		
		try {
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination thingQueue = session.createTopic("thingbroker.things.thing." + thingId);
			MessageConsumer consumer = session.createConsumer(thingQueue);
			JmsEventHandler s = new JmsEventHandler(follow, session, consumer);
			consumer.setMessageListener(s);
			following.put(follow.getId(), s);
		} catch (JMSException e) {
			logger.error(e.getMessage());
			throw new ThingBrokerException("JMS Exception occurred when subscribing", e);
		}
		nextId++;
		return follow.getId();
	}

	public void addToSubscription(long id, String name) {
		// add another topic to an existing subscription
		// this may be done by having the JmsSubscription's session have multiple consumers, then shut them all down.
		// currently not supported.

		// sub get session
		// consumer.sub.createConsumer(topic)
		// s.addConsumer(consumer) - this will call consumer.setMessageListener(this), and add it to the list of consumers
		throw new UnsupportedOperationException("TODO");
	}

	public void unsubscribe(long subId) {
		JmsEventHandler sub = following.get(subId);
		sub.cleanUp();
		following.remove(subId);
	}

	public Set<Event> getEvents(long id, long waitTime) throws Exception {
		JmsEventHandler sub = following.get(id);
		return sub.getEvents(waitTime);
	}

	public List<Follow> getFollows() {
		List<Follow> subs = new ArrayList<Follow>();
		for (Long key: following.keySet()) {
			subs.add(following.get(key).getFollow());
		}
		return subs;
	}

	public Follow getFollow(long id) throws Exception {
		return following.get(id).getFollow();
	}

	public void destroy() throws Exception {
		connection.close();
	}
}