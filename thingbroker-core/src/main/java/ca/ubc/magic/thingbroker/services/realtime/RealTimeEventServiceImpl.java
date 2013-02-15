/**
 * 
 */
package ca.ubc.magic.thingbroker.services.realtime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import ca.ubc.magic.thingbroker.services.interfaces.RealTimeEventService;

/**
 * @author mike
 *
 */
public class RealTimeEventServiceImpl implements RealTimeEventService, DisposableBean {
	private static final Log logger = LogFactory.getLog(RealTimeEventService.class);

	/**
	 * things that the system is currently tracking for real time events
	 */
	private Map<String, ThingEventHandler> things;
	private Connection connection;

	public RealTimeEventServiceImpl(ConnectionFactory connectionFactory) {
		this.things = new ConcurrentHashMap<String, ThingEventHandler>();
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
	
	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.services.interfaces.RealTimeThingEventService#sendEvent(java.lang.String, ca.ubc.magic.thingbroker.model.Event)
	 */
	public void sendEvent(String thingId, Event event) {
		// TODO Auto-generated method stub
		// consider moving event sending to JMS broker here, so its all in one place?

	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.services.interfaces.RealTimeThingEventService#follow(java.lang.String, java.lang.String)
	 */
	public void follow(String thingId, String followedThing) {
		
		// first see if we are alreading getting real time messages for the thing
		ThingEventHandler thingHandler = things.get(thingId);
		Session session;
		try {
			if (thingHandler != null) {
				// already following - nothing to do
				if (thingHandler.isFollowing(followedThing))
					return;
				
				// use the same session for a single thing
				session = thingHandler.getSession();
			} else {
				// otherwise make a new session and handler and add it to the map
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				thingHandler = new ThingEventHandler(session);
				// add it for next time
				things.put(thingId, thingHandler);
			}
			// now build a new destination and a consumer for the thing's JMS session, add it to the
			// event handler and start listening.
			Destination thingQueue = session.createTopic("thingbroker.things.thing." + followedThing);
			MessageConsumer consumer = session.createConsumer(thingQueue);
			thingHandler.addFollower(followedThing, consumer);
			consumer.setMessageListener(thingHandler);

		} catch (JMSException e) {
			logger.error(e.getMessage());
			throw new ThingBrokerException(
					"JMS Exception occurred when subscribing", e);
		}

	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.services.interfaces.RealTimeThingEventService#unfollow(java.lang.String, java.lang.String)
	 */
	public void unfollow(String thingId, String followedThing) {
		// TODO Auto-generated method stub
		ThingEventHandler thingHandler = things.get(thingId);
		if (thingHandler == null)
			return;
		thingHandler.removeFollower(followedThing);
	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.services.interfaces.RealTimeThingEventService#getEvents(java.lang.String, long)
	 */
	public List<Event> getEvents(String thingId, long waitTime) throws Exception {
		
		ThingEventHandler thingHandler = things.get(thingId);
		if (thingHandler == null)
			return new ArrayList<Event>();
		
		return thingHandler.getEvents(waitTime);
	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.services.interfaces.RealTimeEventService#getEvents(java.lang.String, java.util.Set, int, boolean)
	 */
	@Override
	public List<Event> getEvents(String thingId, Set<String> following, int waitTime, boolean followingOnly) {
		// if we got here, we know we need real time events
		// first see if we are alreading getting real time messages for the thing
		ThingEventHandler thingHandler = things.get(thingId);
		Session session;
		try {
			// create a ThingHandler if we need to, allocates a JMS session and a 
			// queue for receiving real time events
			if (thingHandler != null) {
				// use the same session for a single thing
				session = thingHandler.getSession();
			} else {
				// otherwise make a new session and handler and add it to the map
				session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
				thingHandler = new ThingEventHandler(session);
				// add it for next time
				things.put(thingId, thingHandler);
			}

			// sync up the followers -- all of the things we are following
			// creates or removes message consumers for each
			
			// 1 - remove any things we're no longer following
			for(String f: thingHandler.getFollowing()) {
				if (!following.contains(f))
					thingHandler.removeFollower(f);
			}
			
			// 2 - add any things we should be following but we are not
			for (String f: following) {
				if (!thingHandler.isFollowing(f)) {
					thingHandler.addFollower(f);
				}
			}
			
			// finally, should we follow ourselves?
			if (followingOnly && thingHandler.isFollowing(thingId)) {
				thingHandler.removeFollower(thingId);
			} else if (!followingOnly && (!thingHandler.isFollowing(thingId))) {
				thingHandler.addFollower(thingId);
			}
			return thingHandler.getEvents(waitTime);

		} catch (JMSException e) {
			logger.error(e.getMessage());
			throw new ThingBrokerException(
					"JMS Exception occurred when subscribing", e);
		}		
		
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		// shut down gracefully
		for (ThingEventHandler thingHandler: things.values()) {
			thingHandler.cleanUp();
		}
		this.connection.close();
	}
	
	/**
	 * Call clean up periodically to shut down unused thingHandlers and their associated session and 
	 * message consumers that are no longer being used.
	 * 
	 */
	public void cleanUp() {
		for (Entry<String, ThingEventHandler> entry: things.entrySet()) {
			ThingEventHandler eventHandler = entry.getValue();
			if (eventHandler.isExpired()) {
				things.remove(entry.getKey());
				eventHandler.cleanUp();
			}
		}
	}

}
