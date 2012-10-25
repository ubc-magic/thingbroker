package ca.ubc.magic.thingbroker.controller.dao;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.controller.config.SpringMongoConfig;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Thing;

public class ThingDAO {

	private static ApplicationContext ctx = new AnnotationConfigApplicationContext(
			SpringMongoConfig.class);
	private static MongoOperations mongoOperation = (MongoOperations) ctx
			.getBean("mongoDBTemplate");

	public static Thing create(Thing thing) {
		mongoOperation.save(thing, "things");
		return thing;
	}

	public static Thing putMetadata(Thing thing) {
		mongoOperation.save(thing, "things");
		return thing;
	}

	public static List<Thing> retrieve(Map<String, String> searchParams) {
		if (searchParams == null || searchParams.size() == 0) {
			return null;
		}
		Criteria c = null;
		for (String key : searchParams.keySet()) {
			if (c == null) {
				c = new Criteria(key).is(searchParams.get(key));
			} else {
				c.andOperator(Criteria.where(key).is(searchParams.get(key)));
			}
		}
		Query q = new Query(c);
		return mongoOperation.find(q, Thing.class, "things");
	}

	public static Map<String, Object> retrieveMetadata(Thing thing) {
		Query q = new Query(Criteria.where("thingId").is(thing.getThingId()));
		Thing t = mongoOperation.findOne(q, Thing.class, "things");
		return t.getMetadata();
	}

	public static Thing update(Thing thing) throws ThingBrokerException {
		mongoOperation.save(thing, "things");
		return thing;
	}

	public static void delete(Thing thing) {
		Query q = new Query(Criteria.where("thingId").is(thing.getThingId()));
		mongoOperation.findAndRemove(q, Thing.class,"things");
		Event event = new Event();
		event.setThingId(thing.getThingId());
		EventDAO.deleteFromThing(event);
	}
}
