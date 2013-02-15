package ca.ubc.magic.thingbroker.controller.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Thing;

public class ThingDAO {

	private MongoOperations mongoOperation;
	private EventDAO eventDao;
	
	public ThingDAO(MongoOperations mongoOperation, EventDAO eventDao) {
		this.mongoOperation = mongoOperation;
		this.eventDao = eventDao;
	}

	public Thing create(Thing thing) {
		mongoOperation.save(thing, "things");
		return thing;
	}

	public Thing getThing(String thingId) {
		return mongoOperation.findById(thingId, Thing.class, "things");
	}

	public Thing putMetadata(Thing thing) {
		mongoOperation.save(thing, "things");
		return thing;
	}

	public List<Thing> retrieve(Map<String, String> searchParams) {
		Query q = new Query();
		if (searchParams != null && !searchParams.isEmpty()) {
			Criteria c = null;
			for (String key : searchParams.keySet()) {
				if (c == null) {
					c = new Criteria(key).is(searchParams.get(key));
				} else {
					c.andOperator(Criteria.where(key).is(searchParams.get(key)));
				}
			}
			q = new Query(c);
		}
		return mongoOperation.find(q, Thing.class, "things");
	}

	public Map<String, Object> retrieveMetadata(Thing thing) {
		Query q = new Query(Criteria.where("thingId").is(thing.getThingId()));
		Thing t = mongoOperation.findOne(q, Thing.class, "things");
		return t.getMetadata();
	}

	public Thing update(Thing thing) throws ThingBrokerException {
		mongoOperation.save(thing, "things");
		return thing;
	}

	public void delete(String thingId) {
		// we use the
		mongoOperation.remove(new Thing(thingId), "things");
		Event event = new Event();
		event.setThingId(thingId);
		//TODO: why does thingDAO depend on eventDAO = move to service above?
		eventDao.deleteFromThing(event);
	}
}
