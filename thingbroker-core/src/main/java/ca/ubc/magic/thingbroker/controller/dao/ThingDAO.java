package ca.ubc.magic.thingbroker.controller.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.controller.config.SpringMongoConfig;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.utils.Utils;

public class ThingDAO {
   
	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
	private static MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoDBTemplate");
	
	public static void create(Thing thing) {
		mongoOperation.save(thing,"things");
	}
	
	public static Thing putMetadata(Thing thing) {
		Map<String,String> searchParams = new HashMap<String, String>();
		searchParams.put("thingId", thing.getThingId());
		Thing thingToUpdate = retrieve(searchParams);
		if(thingToUpdate != null) {
		  thingToUpdate.getMetadata().putAll(thing.getMetadata());
		  mongoOperation.save(thingToUpdate,"things");
		  return thingToUpdate;
		}
		else {
			throw new ThingBrokerException(Utils.getMessage("THING_NOT_FOUND"));
		}
	}
	
	public static void putSubscription(Thing thing, List<String> groups) {
		
	}
	
	public static Thing retrieve(Map<String,String> searchParams) {
		if(searchParams == null || searchParams.size() == 0) {
			return null;
		}
		Criteria c = null;
		for(String key : searchParams.keySet()) {
			if(c == null) {
			  c = new Criteria(key).is(searchParams.get(key));
			}
			else {
			   c.andOperator(Criteria.where(key).is(searchParams.get(key)));
			}
		}
		Query q = new Query(c);
		return mongoOperation.findOne(q, Thing.class, "things");
	}
	
	public static List<Thing> retrieveByName(Thing thing) {
		Query q = new Query(Criteria.where("name").is(thing.getName()));
		return mongoOperation.find(q, Thing.class, "things");
	}
	
	public static List<Thing> retrieveByType(Thing thing) {
		Query q = new Query(Criteria.where("type").is(thing.getType()));
		return mongoOperation.find(q, Thing.class, "things");
	}
	
	public static Map<String,Object> retrieveMetada(Thing thing) {
		Query q = new Query(Criteria.where("thingId").is(thing.getThingId()));
		Thing t = mongoOperation.findOne(q, Thing.class, "things");
		return t.getMetadata();
	}
	
    public static void update(Thing thing) {
        mongoOperation.save(thing,"things");
    }
    
    public static void delete(Thing thing) {
		Query q = new Query(Criteria.where("thingId").is(thing.getThingId()));
		mongoOperation.remove(q, "things");
    }
    
}
