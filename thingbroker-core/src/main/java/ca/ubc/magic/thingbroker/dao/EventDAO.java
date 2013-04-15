package ca.ubc.magic.thingbroker.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.model.Content;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.EventService.Filter;

/**
 * Event Data Access Object for storing events
 * 
 * @author Ricardo Almeida, Mike Blackstock
 *
 */
public class EventDAO {

	private MongoOperations mongoOperation;
	private EventDataDAO eventDataDao;

	public EventDAO(MongoOperations mongoOperation, EventDataDAO eventDataDao) {
		this.mongoOperation = mongoOperation;
		this.eventDataDao = eventDataDao;
	}

	public Event create(Event event, Content[] data) {
		if (data != null) {
			List<String> eventData = new ArrayList<String>();
			for (Content eData : data) {
				eventData.add(eData.getContentId());
				eventDataDao.create(eData);
			}
			event.setContent(eventData);
		}
		// ensure the id is null, so we don't update an old one
		event.setEventId(null);
		mongoOperation.insert(event, "events");
		return event;
	}

	public Event retrieveById(Event event) {
		Query q = new Query(Criteria.where("eventId").is(event.getEventId()));
		return mongoOperation.findOne(q, Event.class, "events");
	}

	public Set<Event> retrieveEventsFromThing(Event event,
			Map<String, String> params) {
		if (params.size() == 0) {
			Query q = new Query(Criteria.where("thingId")
					.is(event.getThingId())).limit(25); // It will provide a
														// maximum of 25 events
			q.sort().on("serverTimestamp", Order.DESCENDING);
			return new LinkedHashSet<Event>(mongoOperation.find(q, Event.class,
					"events"));
		}
		if (params.get("limit") != null) {
			Query q = new Query(Criteria.where("thingId")
					.is(event.getThingId())).limit(Integer.parseInt(params
					.get("limit")));
			q.sort().on("serverTimestamp", Order.DESCENDING);
			return new LinkedHashSet<Event>(mongoOperation.find(q, Event.class,
					"events"));
		}
		if (params.get("start") != null && params.get("end") != null) {
			Criteria c = new Criteria().andOperator(
					Criteria.where("thingId").is(event.getThingId())
							.and("serverTimestamp")
							.gte(Long.parseLong(params.get("start"))),
					Criteria.where("thingId").is(event.getThingId())
							.and("serverTimestamp")
							.lte(Long.parseLong(params.get("end"))));
			Query q = new Query(c);
			q.sort().on("serverTimestamp", Order.DESCENDING);
			return new LinkedHashSet<Event>(mongoOperation.find(q, Event.class,
					"events"));
		}
		if (params.get("before") != null) {
			Query q = new Query(Criteria.where("thingId")
					.is(event.getThingId()).and("serverTimestamp")
					.lt(Long.parseLong(params.get("before"))));
			q.sort().on("serverTimestamp", Order.DESCENDING);
			return new LinkedHashSet<Event>(mongoOperation.find(q, Event.class,
					"events"));
		}
		if (params.get("after") != null) {
			Query q = new Query(Criteria.where("thingId")
					.is(event.getThingId()).and("serverTimestamp")
					.gt(Long.parseLong(params.get("after"))));
			q.sort().on("serverTimestamp", Order.DESCENDING);
			return new LinkedHashSet<Event>(mongoOperation.find(q, Event.class,
					"events"));
		}
		return new LinkedHashSet<Event>();
	}

	public List<Event> getEvents(Thing t, Map<String, String> queryParams, Filter filter) {

		// start building query
		boolean doQuery = false;

		Set<String> thingSet = new HashSet<String>();

		// add things to get events from
		if (filter == Filter.THING_ONLY || filter == Filter.ALL)
			thingSet.add(t.getThingId());
		if (filter == Filter.ALL || filter == Filter.FOLLOWING_ONLY)
			thingSet.addAll(t.getFollowing());
		
		// first append all of the thing ids
		Criteria c = Criteria.where("thingId").in(thingSet);

		// we'll assume the start time is now unless specified
		long startTime = System.currentTimeMillis();

		if (queryParams.get("start") != null) {
			startTime = Long.parseLong(queryParams.get("start"));
			doQuery = true;
		}

		// set up criteria first
		if (queryParams.get("end") != null) {
			long endTime = Long.parseLong(queryParams.get("end"));
			c.and("serverTimestamp").gt(startTime).lte(endTime);
			doQuery = true;

		} else if (queryParams.get("before") != null) {
			long before = Long.parseLong(queryParams.get("before"));
			c.and("serverTimestamp").gt(startTime - before).lte(startTime);
			doQuery = true;

		} else if (queryParams.get("after") != null) {
			long after = Long.parseLong(queryParams.get("after"));
			c.and("serverTimestamp").gt(startTime).lte(startTime + after);
			doQuery = true;
		}

		// then modify query with offset and limit
		Query q = new Query(c);
		q.sort().on("serverTimestamp", Order.DESCENDING);
		if (queryParams.get("limit") != null) {
			int limit = Integer.parseInt(queryParams.get("limit"));
			q.limit(limit);
			doQuery = true;

		}
		if (queryParams.get("offset") != null) {
			int offset = Integer.parseInt(queryParams.get("offset"));
			q.skip(offset);
			doQuery = true;
		}
		// if we are doing a historical query, get the data, otherwise, return
		// an empty list
		if (doQuery)
			return mongoOperation.find(q, Event.class, "events");
		else
			return new ArrayList<Event>();
	}

	public void update(Event event) {
		mongoOperation.save(event, "events");
	}

	public void delete(Event event) {
		Query q = new Query(Criteria.where("eventId").is(event.getEventId()));
		mongoOperation.remove(q, "events");
	}

	public void deleteFromThing(Event event) {
		Query q = new Query(Criteria.where("thingId").is(event.getThingId()));
		mongoOperation.remove(q, "events");
	}
}
