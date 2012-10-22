package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.List;
import java.util.Set;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Follow;

public interface RealTimeEventService {
	public long follow(String thingId);
	public void addToSubscription(long id, String name);
	public void unsubscribe(long subId);
	public Set<Event> getEvents(long id, long waitTime) throws Exception;
	public List<Follow> getFollows();
	public Follow getFollow(long id) throws Exception;
}
