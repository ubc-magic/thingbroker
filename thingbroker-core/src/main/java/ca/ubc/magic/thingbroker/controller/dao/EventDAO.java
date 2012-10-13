package ca.ubc.magic.thingbroker.controller.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.controller.config.SpringMongoConfig;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;

public class EventDAO {
	
	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
	private static MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoDBTemplate");
	
	public static Event create(Event event, EventData[] data) {
		if(data != null) {
		  List<String> eventData = new ArrayList<String>();
		  for(EventData eData : data) {
		      eventData.add(eData.getContentId());
		      EventDataDAO.create(eData);
		  }
		  event.setData(eventData);
		}
		mongoOperation.save(event,"events");
		return event;
	}
	
	public static Event retrieveById(Event event) {
		Query q = new Query(Criteria.where("eventId").is(event.getEventId()));
		return mongoOperation.findOne(q, Event.class, "events");
	}
	
	public static List<Event> retrieveEventsFromThing(Event event, Map<String, String> params) {
		if(params.size() == 0) {
		  Query q = new Query(Criteria.where("thingId").is(event.getThingId())).limit(25); //It will provide a maximum of 25 events
		  q.sort().on("serverTimestamp", Order.DESCENDING);
		  return mongoOperation.find(q, Event.class,"events");
		}
		if(params.get("limit") != null) {
		  Query q = new Query(Criteria.where("thingId").is(event.getThingId())).limit(Integer.parseInt(params.get("limit")));
		  q.sort().on("serverTimestamp", Order.DESCENDING);
		  return mongoOperation.find(q, Event.class,"events");
		}
		if(params.get("start") != null && params.get("end") != null) {
		  Criteria c = new Criteria().andOperator(Criteria.where("thingId").is(event.getThingId()).and("serverTimestamp").gte(Long.parseLong(params.get("start"))),
				  Criteria.where("thingId").is(event.getThingId()).and("serverTimestamp").lte(Long.parseLong(params.get("end"))));	
		  Query q = new Query(c);
		  q.sort().on("serverTimestamp", Order.DESCENDING);
	      return mongoOperation.find(q, Event.class,"events");
		}
		if(params.get("before") != null) {
		   Query q = new Query(Criteria.where("thingId").is(event.getThingId()).and("serverTimestamp").lt(Long.parseLong(params.get("before"))));
		   q.sort().on("serverTimestamp", Order.DESCENDING);
		   return mongoOperation.find(q, Event.class,"events");
		}
		if(params.get("after") != null) {
			   Query q = new Query(Criteria.where("thingId").is(event.getThingId()).and("serverTimestamp").gt(Long.parseLong(params.get("after"))));
			   q.sort().on("serverTimestamp", Order.DESCENDING);
			   return mongoOperation.find(q, Event.class,"events");
		}
		return new ArrayList<Event>();
	}
	
    public static void update(Event event) {
        mongoOperation.save(event,"events");
    }
    
    public static void delete(Event event) {
		Query q = new Query(Criteria.where("eventId").is(event.getEventId()));
		mongoOperation.remove(q, "events");
    }
    
}
