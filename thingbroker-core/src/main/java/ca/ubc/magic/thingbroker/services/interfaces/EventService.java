package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.List;
import java.util.Map;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;

public interface EventService {
   public Event create(Event event, EventData [] data, boolean mustSave);
   public void update(Event event, EventData [] data);
   public Event retrieve(Event event);
   public List<Event> retrieveByCriteria(Event event, Map<String, String> params) throws Exception;
}
