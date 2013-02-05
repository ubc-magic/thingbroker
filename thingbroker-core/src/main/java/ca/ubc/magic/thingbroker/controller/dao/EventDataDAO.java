package ca.ubc.magic.thingbroker.controller.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;

public class EventDataDAO {
	private final MongoOperations mongoOperations;
	
	public EventDataDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	public void create(EventData data) {
		mongoOperations.save(data,"eventcontent");
	}
	
	public EventData retrieve(EventData data) {
		Query q = new Query(Criteria.where("contentId").is(data.getContentId()));
		return mongoOperations.findOne(q, EventData.class, "eventcontent");
	}
	
    public void delete(Event event) {
		Query q = new Query(Criteria.where("contentId").is(event.getEventId()));
		mongoOperations.remove(q, "eventcontent");
    }
}
