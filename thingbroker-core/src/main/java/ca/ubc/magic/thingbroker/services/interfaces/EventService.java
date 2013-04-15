package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Content;
import ca.ubc.magic.thingbroker.model.Event;

public interface EventService {
	
	public enum Filter {
		ALL("all"),
		FOLLOWING_ONLY("following-only"),
		THING_ONLY("thing-only");
		
		Filter(String param) {
			this.param = param;
		}
		private final String param;
		public String getParam() {
			return this.param;
		}

		public static Filter fromString(String text) {
			if (text != null) {
				for (Filter b : Filter.values()) {
					if (text.equalsIgnoreCase(b.param)) {
						return b;
					}
				}
			}
			throw new IllegalArgumentException("No constant with text " + text + " found");
		}
	}
	
	/**
	 * Send/Create an event
	 * 
	 * @param event
	 * @param data
	 * @param mustSave
	 * @return
	 */
	public Event create(Event event, Content[] data, boolean mustSave);

	/**
	 * Update event data
	 * 
	 * @param event
	 * @param data
	 * @return
	 * @throws ThingBrokerException
	 */
	public Event update(Event event, Content[] data) throws ThingBrokerException;

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
	public List<Event> getEvents(String thingId, Map<String, String> queryParams, int waitTime, Filter filter);

	public Content retrieveEventData(Content eventData) throws Exception;

	public Content retrieveEventDataInfo(Content eventData) throws Exception;

	/**
	 * Update the info field of an event
	 * 
	 * @param event
	 * @param info
	 * @return
	 * @throws Exception
	 */
	public Event addDataToInfoField(Event event, HashMap<String, Object> info) throws Exception;
}