package ca.ubc.magic.thingbroker.controller.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.controller.config.SpringMongoConfig;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;

public class EventDataDAO {
	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
	private static MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoStorageTemplate");
	
	public static void create(EventData data) {
		mongoOperation.save(data,"eventcontent");
	}
	
	public static EventData retrieve(EventData data) {
		Query q = new Query(Criteria.where("contentId").is(data.getContentId()));
		return mongoOperation.findOne(q, EventData.class, "eventcontent");
	}
	
    public static void delete(Event event) {
		Query q = new Query(Criteria.where("contentId").is(event.getEventId()));
		mongoOperation.remove(q, "eventcontent");
    }
}
