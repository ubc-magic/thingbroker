package ca.ubc.magic.thingbroker.services.interfaces;

import java.util.List;
import java.util.Map;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;

public interface EventService {
   public Event create(Event event, EventData [] data, boolean mustSave);
   public Event update(Event event, EventData [] data) throws ThingBrokerException;
   public Event retrieve(Event event);
   public List<Event> retrieveByCriteria(Event event, Map<String, String> params) throws Exception;
   public EventData retrieveEventData(EventData eventData) throws Exception;
   public EventData retrieveEventDataInfo(EventData eventData) throws Exception;
}