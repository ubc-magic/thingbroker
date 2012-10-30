package ca.ubc.magic.thingbroker.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import ca.ubc.magic.thingbroker.controller.config.Constants;
import ca.ubc.magic.thingbroker.controller.dao.ThingDAO;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.ThingService;
import ca.ubc.magic.utils.Utils;

public class ThingServiceImpl implements ThingService {

	public Thing storeThing(Thing thing) throws ThingBrokerException {
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
		ThingDAO.create(thing);
		updateFollowingAndFollowersList(thing);
		return thing;
	}

	public List<Thing> getThing(Map<String, String> searchParams)
			throws ThingBrokerException {
		List<Thing> t = ThingDAO.retrieve(searchParams);
		if (t != null && t.size() > 0) {
			return t;
		}
		return null;
	}

	public Map<String, Object> getThingMetadata(Thing id)
			throws ThingBrokerException {
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
				if(!thingToFollow.equals(thing.getThingId())) {
				  t.getFollowing().add(thingToFollow);
				  searchParams.put("thingId", thingToFollow);
				  List<Thing> result = ThingDAO.retrieve(searchParams);
				  if(result != null && result.size() > 0) {
				    Thing tToFollow = ThingDAO.retrieve(searchParams).get(0);
				    tToFollow.getFollowers().add(thing.getThingId());
                    ThingDAO.update(t);
				    ThingDAO.update(tToFollow);
				  }
				  else {
				     throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,
						Utils.getMessage("THING_NOT_FOUND") + " - Thing id: " + thingToFollow);
				  }
				}
			}
		} else {
			throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,
					Utils.getMessage("THING_NOT_FOUND") + " - Thing id: " + thing.getThingId());
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
				List<Thing> result = ThingDAO.retrieve(searchParams);
				if(result != null && result.size() > 0) {
				  Thing tToFollow = result.get(0);
				  tToFollow.getFollowers().remove(thing.getThingId());
				  ThingDAO.update(tToFollow);
				}
			}
			ThingDAO.update(t);
		} else {
			throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,
					Utils.getMessage("THING_NOT_FOUND"));
		}
		return t;
	}

	public Thing update(Thing thing) throws ThingBrokerException {
		Map<String, String> searchParam = new HashMap<String, String>();
		searchParam.put("thingId", thing.getThingId());
		Thing storedThing = getThing(searchParam).get(0);
		if (storedThing != null) {
			if (thing.getFollowing() != null && thing.getFollowing().size() > 0) {
				List<String> thingsToFollow = new ArrayList<String>();
				for (String following : thing.getFollowing()) {
					if (!storedThing.getFollowing().contains(following)) {
						thingsToFollow.add(following);
					}
				}
				if (thingsToFollow.size() > 0) {
					String[] things = new String[thingsToFollow.size()];
					followThings(storedThing, thingsToFollow.toArray(things));
				}
			}
			if (storedThing.getFollowing() != null
					&& storedThing.getFollowing().size() > 0) {
				List<String> thingsToUnfollow = new ArrayList<String>();
				for (String following : storedThing.getFollowing()) {
					if (!thing.getFollowing().contains(following)) {
						thingsToUnfollow.add(following);
					}
				}
				if (thingsToUnfollow.size() > 0) {
					String[] things = new String[thingsToUnfollow.size()];
					unfollowThings(storedThing,
							thingsToUnfollow.toArray(things));
				}
			}
			storedThing = getThing(searchParam).get(0);
			thing.setFollowing(storedThing.getFollowing());
			thing.setFollowers(storedThing.getFollowers());
			return ThingDAO.update(thing);
		}
		throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,
				Utils.getMessage("THING_NOT_FOUND"));
	}

	public Thing delete(Thing id) throws ThingBrokerException {
		Map<String, String> searchParam = new HashMap<String, String>();
		searchParam.put("thingId", id.getThingId());
		List<Thing> storedThings = getThing(searchParam);
		if (storedThings != null) {
			ThingDAO.delete(id);
			return storedThings.get(0);
		}
		throw new ThingBrokerException(Constants.CODE_THING_NOT_FOUND,
				Utils.getMessage("THING_NOT_FOUND"));
	}

	private void updateFollowingAndFollowersList(Thing thing) {
		Map<String, String> searchParams = new HashMap<String, String>();
		if (thing.getFollowing() != null && thing.getFollowing().size() > 0) {
			for (String thingToFollow : thing.getFollowing()) {
				searchParams.put("thingId", thingToFollow);
				List<Thing> list = ThingDAO.retrieve(searchParams);
				if (list != null && list.size() > 0) {
					Thing tToFollow = list.get(0);
					tToFollow.getFollowers().add(thing.getThingId());
					ThingDAO.update(tToFollow);
				} else {
					throw new ThingBrokerException(
							Constants.CODE_THING_NOT_FOUND,
							Utils.getMessage("THING_NOT_FOUND")
									+ " - Thing id: " + thingToFollow);
				}
			}
		}
		if (thing.getFollowers() != null && thing.getFollowers().size() > 0) {
			for (String follower : thing.getFollowers()) {
				searchParams.put("thingId", follower);
				List<Thing> list = ThingDAO.retrieve(searchParams);
				if (list != null && list.size() > 0) {
					Thing tFollower = list.get(0);
					tFollower.getFollowing().add(thing.getThingId());
					ThingDAO.update(tFollower);
				} else {
					throw new ThingBrokerException(
							Constants.CODE_THING_NOT_FOUND,
							Utils.getMessage("THING_NOT_FOUND")
									+ " - Thing id: " + follower);
				}
			}
		}
	}
}
