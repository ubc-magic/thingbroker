package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.List;
import java.util.Map;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Thing;

public interface ThingService {

	public Thing storeThing(Thing thing) throws ThingBrokerException;

	/**
	 * Get things that match the queryParameters.  If the parameters are empty, get all things.
	 * @param queryParams
	 * @return
	 * @throws ThingBrokerException
	 */
	public List<Thing> getThings(Map<String, String> queryParams) throws ThingBrokerException;

	public Map<String, Object> getThingMetadata(Thing id)
			throws ThingBrokerException;

	public Thing update(Thing thing) throws ThingBrokerException;

	public Thing delete(String thingId) throws ThingBrokerException;

	public Thing addMetadata(Thing metadata) throws ThingBrokerException;

	public Thing followThings(Thing thing, String[] thingsToFollow)
			throws ThingBrokerException;

	public Thing unfollowThings(Thing thing, String[] thingsToUnfollow)
			throws ThingBrokerException;

	/**
	 * Get the thing with the specified id
	 * @param thingId
	 * @return
	 */
	public Thing getThing(String thingId);
}
