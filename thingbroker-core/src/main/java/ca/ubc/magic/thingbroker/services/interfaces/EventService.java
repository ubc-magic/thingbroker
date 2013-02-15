package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;

public interface EventService {
	
	/**
	 * Send/Create an event
	 * 
	 * @param event
	 * @param data
	 * @param mustSave
	 * @return
	 */
	public Event create(Event event, EventData[] data, boolean mustSave);

	/**
	 * Update event data
	 * 
	 * @param event
	 * @param data
	 * @return
	 * @throws ThingBrokerException
	 */
	public Event update(Event event, EventData[] data)
			throws ThingBrokerException;

	public Event retrieve(Event event);

	/**
	 * Get events associated with a thing
	 * 
	 * @param thingId the thing
	 * @param queryParams query parameters: start, end, before, after, limit, offset
	 * @param waitTime time to wait in seconds for real time events
	 * @param followingOnly only get events from things the thing is following
	 * @return
	 */
	public List<Event> getEvents(String thingId, Map<String, String> queryParams, int waitTime, boolean followingOnly);

	public EventData retrieveEventData(EventData eventData) throws Exception;

	public EventData retrieveEventDataInfo(EventData eventData)
			throws Exception;

	/**
	 * Update the info field
	 * 
	 * @param event
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public Event addDataToInfoField(Event event, HashMap<String, Object> info)
			throws Exception;
}