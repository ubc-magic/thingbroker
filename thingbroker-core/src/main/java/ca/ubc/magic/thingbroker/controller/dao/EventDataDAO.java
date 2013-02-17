package ca.ubc.magic.thingbroker.controller.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Content;

public class EventDataDAO {
	private final MongoOperations mongoOperations;
	
	public EventDataDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	public void create(Content data) {
		mongoOperations.save(data,"eventcontent");
	}
	
	public Content retrieve(Content data) {
		Query q = new Query(Criteria.where("contentId").is(data.getContentId()));
		return mongoOperations.findOne(q, Content.class, "eventcontent");
	}
	
    public void delete(Event event) {
		Query q = new Query(Criteria.where("contentId").is(event.getEventId()));
		mongoOperations.remove(q, "eventcontent");
    }
}
