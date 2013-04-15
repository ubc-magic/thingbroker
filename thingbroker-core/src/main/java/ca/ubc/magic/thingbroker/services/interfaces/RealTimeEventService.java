package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.List;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.EventService.Filter;

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
	 * Get events from the thing and its followers using the default application id
	 * 
	 * @param thingId
	 * @param following the list of things that the thing is following
	 * @param waitTime
	 * @param filter - ALL - all events, FOLLOWING_ONLY - only from followed things
	 * 		THING_ONLY - only sent directly to thing
	 * @return events that have been received in waitTime
	 */
	public List<Event> getEvents(Thing thing, int waitTime, Filter filter);

	/**
	 * Get events from the thing and its followers for a specific application id
	 * 
	 * @param appId
	 * @param thingId
	 * @param following
	 * @param waitTime
	 * @param followingOnly
	 * @param thingOnly
	 * @return events that have been received in waitTime
	 */
	public List<Event> getEvents(String appId, Thing thing, int waitTime, Filter filter);
}
