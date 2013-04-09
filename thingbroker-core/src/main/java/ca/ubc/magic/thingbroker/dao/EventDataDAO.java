package ca.ubc.magic.thingbroker.dao;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Content;

/**
 * Event Data Data Access Object for storing event content.
 * 
 * @author Ricardo Almeida, Mike Blackstock
 *
 */
public class EventDataDAO {
	private final String CONTENT_COLLECTION = "eventcontent";

	private final MongoOperations mongoOperations;
	
	public EventDataDAO(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}
	public void create(Content data) {
		mongoOperations.save(data,"eventcontent");
	}
	
	Content retrieve(String contentId) {
		return mongoOperations.findById(contentId, Content.class, CONTENT_COLLECTION);
	}
	
	public Content retrieve(Content data) {
		Query q = new Query(Criteria.where("contentId").is(data.getContentId()));
		return mongoOperations.findOne(q, Content.class, CONTENT_COLLECTION);
	}
	
    public void delete(Event event) {
		Query q = new Query(Criteria.where("contentId").is(event.getEventId()));
		mongoOperations.remove(q, "eventcontent");
    }
}
