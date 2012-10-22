package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.List;
import java.util.Map;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Thing;

public interface ThingService {
   
   public Thing storeThing(Thing thing) throws ThingBrokerException;
   public List<Thing> getThing(Map<String,String> searchParams) throws ThingBrokerException;
   public Map<String,Object> getThingMetadata(Thing id) throws ThingBrokerException;
   public Thing update(Thing thing) throws ThingBrokerException;
   public Thing delete(Thing id) throws ThingBrokerException;
   public Thing addMetadata(Thing metadata) throws ThingBrokerException;
   public Thing followThings(Thing thing, String[] thingsToFollow) throws ThingBrokerException;
   public Thing unfollowThings(Thing thing, String[] thingsToUnfollow) throws ThingBrokerException;
}
