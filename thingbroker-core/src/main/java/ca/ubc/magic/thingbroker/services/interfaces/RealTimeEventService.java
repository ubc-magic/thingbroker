package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.List;
import java.util.Set;

import ca.ubc.magic.thingbroker.model.Event;

/**
 * Service for handling real time events from things.
 * 
 * @author mike
 *
 */
public interface RealTimeEventService {
	
	/**
	 * Send event to thing
	 * 
	 * @param thingId
	 * @param event
	 */
	public void sendEvent(String thingId, Event event);
	
	/**
	 * Follow thing
	 * @param thingId thing
	 * @param followedThing the thing to follow
	 * @deprecated
	 * @return
	 */
	public void follow(String thingId, String followedThing);
	
	/**
	 * Unfollow thing
	 * 
	 * @param thingId
	 * @param followedThing
	 * @return
	 * 
	 * @deprecated
	 */
	public void unfollow(String thingId, String followedThing);
	
	/**
	 * Get events from the thing and its followers
	 * 
	 * @param thingId
	 * @param waitTime
	 * @return
	 * @throws Exception
	 * @deprecated
	 */
	public List<Event> getEvents(String thingId, long waitTime) throws Exception;

	/**
	 * Get events from the thing and its followers
	 * 
	 * @param thingId
	 * @param waitTime
	 * @param followingOnly
	 * @return
	 */
	public List<Event> getEvents(String thingId, Set<String> following, int waitTime, boolean followingOnly);
}
