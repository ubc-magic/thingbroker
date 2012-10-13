package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.Map;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Thing;

public interface ThingService {
   
   public void storeThing(Thing thing);
   public Thing getThing(Map<String,String> searchParams);
   public Map<String,Object> getThingMetadata(Thing id);
   public Thing addMetadata(Thing metadata) throws ThingBrokerException;
   public void followThings(Thing thing, String[] thingsToFollow) throws ThingBrokerException;
   public void unfollowThings(Thing thing, String[] thingsToUnfollow) throws ThingBrokerException;
}
