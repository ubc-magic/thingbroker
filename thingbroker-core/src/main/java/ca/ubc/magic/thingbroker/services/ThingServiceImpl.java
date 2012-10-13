package ca.ubc.magic.thingbroker.services;

import java.util.HashMap;
import java.util.Map;

import ca.ubc.magic.thingbroker.controller.dao.ThingDAO;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.ThingService;
import ca.ubc.magic.utils.Utils;

public class ThingServiceImpl implements ThingService {
	
	public void storeThing(Thing thing) {
		ThingDAO.create(thing);
	}
	
	public Thing getThing(Map<String, String> searchParams) {
		return ThingDAO.retrieve(searchParams);
	}

	public Map<String, Object> getThingMetadata(Thing id) {
		return ThingDAO.retrieveMetada(id);
	}

	public Thing addMetadata(Thing metadata) throws ThingBrokerException
	{
		return ThingDAO.putMetadata(metadata);
	}

	public void followThings(Thing thing, String[] thingsToFollow) throws ThingBrokerException {
		Map<String,String> searchParams = new HashMap<String, String>();
		searchParams.put("thingId", thing.getThingId());
		Thing t = ThingDAO.retrieve(searchParams);
		if (t != null) {
			for(String thingToFollow : thingsToFollow) {
				t.getFollowing().add(thingToFollow);
				searchParams.put("thingId", thingToFollow);
				Thing tToFollow = ThingDAO.retrieve(searchParams);
				tToFollow.getFollowers().add(thing.getThingId());
				ThingDAO.update(tToFollow);
			}
			ThingDAO.update(t);
		} else {
			throw new ThingBrokerException(Utils.getMessage("THING_NOT_FOUND"));
		}
	}

	public void unfollowThings(Thing thing, String[] thingsToUnfollow)
			throws ThingBrokerException {
		Map<String,String> searchParams = new HashMap<String, String>();
		searchParams.put("thingId", thing.getThingId());
		Thing t = ThingDAO.retrieve(searchParams);
		if (t != null) {
			for(String thingToUnfollow : thingsToUnfollow) {
				t.getFollowing().remove(thingToUnfollow);
				searchParams.put("thingId", thingToUnfollow);
				Thing tToFollow = ThingDAO.retrieve(searchParams);
				tToFollow.getFollowers().remove(thing.getThingId());
				ThingDAO.update(tToFollow);
			}
			ThingDAO.update(t);
		} else {
			throw new ThingBrokerException(Utils.getMessage("THING_NOT_FOUND"));
		}
	}
}
