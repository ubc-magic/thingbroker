/**
 * 
 */
package ca.ubc.magic.thingbroker.services.realtime;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.EventService.Filter;
import ca.ubc.magic.thingbroker.services.interfaces.RealTimeEventService;

/**
 * Real time retrieval of events
 * 
 * @author Mike Blackstock, Ricardo Almeida
 *
 */
public class RealTimeEventServiceImpl implements RealTimeEventService, DisposableBean {
	private static final Log logger = LogFactory.getLog(RealTimeEventService.class);

	// at most hang waiting for data for 15 seconds before returning
	final static int MAX_WAIT_TIME = 15;
	
	/**
	 * things that the system is currently tracking for real time events.
	 * thing id to ThingEventHandler
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
	
	@Override
	public List<Event> getEvents(String appId, Thing thing, int waitTime, Filter filter) {
		
		// if we got here, we know we need real time events
		
		// we use an empty string for the default application id 
		appId = appId == null?"":appId;
		waitTime = Math.max(waitTime, MAX_WAIT_TIME);
		
		// we keep a ThingHandler for every 'appId:thingId' so that every app-thing pair gets its own
		// JMS subscriptions and queues.  The subscriptions (all, following, thing-only) are app-specific.
		String appThingId = appId+":"+thing.getThingId();

		// first see if we are alreading getting real time messages for the thing
		ThingEventHandler thingHandler = things.get(appThingId);

		Set<String> following = new HashSet<String>(thing.getFollowing());
		
		// first figure out what things we should be following or not
		switch (filter) {
		case FOLLOWING_ONLY:
			// done - following list contains only the followers
			break;
		case THING_ONLY:
			// clear the following list
			following.clear();
		case ALL:
			// add ourself
			following.add(thing.getThingId());
			break;
		}
		// 'following' now contains the things we should be getting events from from this thingHandler
		logger.debug("get real time events for:"+appThingId+" on "+following);
		
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
				things.put(appThingId, thingHandler);
			}
			// sync up the followers -- all of the things we are following
			// this will create or remove message consumers for each
			
			// 1 - add any things we should be following but we are not
			for (String f: following) {
				if (!thingHandler.isFollowing(f)) {
					thingHandler.addFollowing(f);
				}
			}

			// 2 - remove any things we're no longer following
			for(String f: thingHandler.getFollowing()) {
				if (!following.contains(f))
					thingHandler.removeFollowing(f);
			}
						
			// note that changes in who we are following will
			// only affect newly received events
			return thingHandler.getEvents(waitTime);

		} catch (JMSException e) {
			logger.error(e.getMessage());
			throw new ThingBrokerException(
					"JMS Exception occurred when subscribing", e);
		}		

	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.services.interfaces.RealTimeEventService#getEvents(java.lang.String, java.util.Set, int, boolean)
	 */
	@Override
	public List<Event> getEvents(Thing thing, int waitTime, Filter filter) {
		return getEvents(null, thing, waitTime, filter);
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
