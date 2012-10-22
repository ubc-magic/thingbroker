package ca.ubc.magic.thingbroker.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ca.ubc.magic.thingbroker.controller.config.Constants;
import ca.ubc.magic.thingbroker.controller.dao.ThingDAO;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.ThingService;
import ca.ubc.magic.utils.Utils;

public class ThingServiceImpl implements ThingService {

	public Thing storeThing(Thing thing)  throws ThingBrokerException {
		if (thing.getThingId() == null || thing.getThingId().equals("")) {
			thing.setThingId(UUID.randomUUID().toString());
		} else {
			Map<String, String> searchParam = new HashMap<String, String>();
			searchParam.put("thingId", thing.getThingId());
			List<Thing> storedThings = getThing(searchParam);
			if (storedThings != null && storedThings.size() > 0) {
				throw new ThingBrokerException(
						Constants.CODE_THING_ALREADY_REGISTERED,
						Utils.getMessage("THING_ALREADY_REGISTERED"));
			}
		}
		return ThingDAO.create(thing);
	}

	public List<Thing> getThing(Map<String, String> searchParams) throws ThingBrokerException {
		List<Thing> t = ThingDAO.retrieve(searchParams);
		if(t != null && t.size() > 0) {
		   return t;	
		}
		throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,
				Utils.getMessage("THING_NOT_FOUND"));
	}

	public Map<String, Object> getThingMetadata(Thing id) throws ThingBrokerException {
		Map<String, String> searchParam = new HashMap<String, String>();
		searchParam.put("thingId", id.getThingId());
		List<Thing> storedThings = getThing(searchParam);
		if (storedThings != null) {
			return storedThings.get(0).getMetadata();
		}
		throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,
				Utils.getMessage("THING_NOT_FOUND"));
	}

	public Thing addMetadata(Thing thing) throws ThingBrokerException {
		Map<String, String> searchParams = new HashMap<String, String>();
		searchParams.put("thingId", thing.getThingId());
		List<Thing> thingToUpdate = getThing(searchParams);
		if (thingToUpdate != null && thingToUpdate.size() > 0) {
			thingToUpdate.get(0).getMetadata().putAll(thing.getMetadata());
			return ThingDAO.putMetadata(thingToUpdate.get(0));
		}
		throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,
					Utils.getMessage("THING_NOT_FOUND"));
	}

	public Thing followThings(Thing thing, String[] thingsToFollow)
			throws ThingBrokerException {
		Map<String, String> searchParams = new HashMap<String, String>();
		searchParams.put("thingId", thing.getThingId());
		Thing t = ThingDAO.retrieve(searchParams).get(0);
		if (t != null) {
			for (String thingToFollow : thingsToFollow) {
				t.getFollowing().add(thingToFollow);
				searchParams.put("thingId", thingToFollow);
				Thing tToFollow = ThingDAO.retrieve(searchParams).get(0);
				tToFollow.getFollowers().add(thing.getThingId());
				ThingDAO.update(tToFollow);
			}
			ThingDAO.update(t);
		} else {
			throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,Utils.getMessage("THING_NOT_FOUND"));
		}
		return t;
	}

	public Thing unfollowThings(Thing thing, String[] thingsToUnfollow)
			throws ThingBrokerException {
		Map<String, String> searchParams = new HashMap<String, String>();
		searchParams.put("thingId", thing.getThingId());
		Thing t = ThingDAO.retrieve(searchParams).get(0);
		if (t != null) {
			for (String thingToUnfollow : thingsToUnfollow) {
				t.getFollowing().remove(thingToUnfollow);
				searchParams.put("thingId", thingToUnfollow);
				Thing tToFollow = ThingDAO.retrieve(searchParams).get(0);
				tToFollow.getFollowers().remove(thing.getThingId());
				ThingDAO.update(tToFollow);
			}
			ThingDAO.update(t);
		} else {
			throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,Utils.getMessage("THING_NOT_FOUND"));
		}
		return t;
	}

	public Thing update(Thing thing) throws ThingBrokerException {
    	Map<String, String> searchParam = new HashMap<String, String>();
		searchParam.put("thingId",thing.getThingId());
		Thing storedThing = getThing(searchParam).get(0);
		if(storedThing != null) {
	         return ThingDAO.update(thing);
		}
		throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,Utils.getMessage("THING_NOT_FOUND"));	
	}

	public Thing delete(Thing id) throws ThingBrokerException {
    	Map<String, String> searchParam = new HashMap<String, String>();
		searchParam.put("thingId",id.getThingId());
		List<Thing> storedThings = getThing(searchParam);
    	if(storedThings != null) {
    		ThingDAO.delete(id);
    		return storedThings.get(0);
    	}
		throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,Utils.getMessage("THING_NOT_FOUND"));
	}
}
