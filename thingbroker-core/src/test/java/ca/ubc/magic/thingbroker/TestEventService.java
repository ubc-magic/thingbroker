package ca.ubc.magic.thingbroker;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.EventService;
import ca.ubc.magic.thingbroker.services.interfaces.EventService.Filter;
import ca.ubc.magic.thingbroker.services.interfaces.ThingService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:core-context.xml")
public class TestEventService {

	@Autowired
	private ThingService thingService;
	@Autowired
	private EventService eventService;
	
	List<Thing> testThings;
	Thing t1;
	Thing t2;
	Thing t3;	

	@Before
	public void setup() throws Exception {
		testThings = new ArrayList<Thing>();
		
		Thing t = new Thing();
		t.setName("test-thing-1");
		t = thingService.storeThing(t);
		testThings.add(t);
		
		t = new Thing();
		t.setName("test-thing-2");
		t = thingService.storeThing(t);
		testThings.add(t);

		t = new Thing();
		t.setName("test-thing-3");
		t = thingService.storeThing(t);
		testThings.add(t);

		// set up following relationship for tests
		t1 = testThings.get(0);
		t2 = testThings.get(1);
		t3 = testThings.get(2);
		
		String[] array = new String[] {t2.getThingId(), t3.getThingId()};
		
		thingService.followThings(t1, array);
	}
	
	@After
	public void teardown() throws UnsupportedEncodingException, Exception {

		for (Thing thing: testThings) {
			thingService.delete(thing.getThingId());
		}
	}
	
	@Test
	public void testEventsHistory() {
		
		// now send some events to t1
		Event event = new Event();
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("test", 1234);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t1.getThingId());
		eventService.create(event, null, true);
		
		// now send some events to t2
		info = new HashMap<String, Object>();
		info.put("test", 4567);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t2.getThingId());
		eventService.create(event, null, true);		
		
		// now send some events to t3
		info = new HashMap<String, Object>();
		info.put("test", 890);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t3.getThingId());
		eventService.create(event, null, true);	
		
		// get all events to t1
		Map<String, String> params = new HashMap<String, String>();
		// one hour seconds before
		params.put("before", "3600000");
		List<Event> events = eventService.getEvents(t1.getThingId(), params, 0, Filter.ALL);
		assertEquals(3, events.size());
		assertEquals(t3.getThingId(), events.get(0).getThingId());
		assertEquals(t2.getThingId(), events.get(1).getThingId());
		assertEquals(t1.getThingId(), events.get(2).getThingId());
		
		// get only events sent to t1
		events = eventService.getEvents(t1.getThingId(), params, 0, Filter.THING_ONLY);
		assertEquals(1, events.size());
		assertEquals(t1.getThingId(), events.get(0).getThingId());

		// get only events sent to t1 following
		events = eventService.getEvents(t1.getThingId(), params, 0, Filter.FOLLOWING_ONLY);
		assertEquals(2, events.size());
		assertEquals(t3.getThingId(), events.get(0).getThingId());
		assertEquals(t2.getThingId(), events.get(1).getThingId());

	}
	
	@Test
	public void testEventsRealTime() throws InterruptedException {
		
		// first, we make a query in the background, on another thread
        Runnable r = new Runnable() {
        	public void run() {
        		String thing1Id = TestEventService.this.t1.getThingId();
        		String thing2Id = TestEventService.this.t2.getThingId();
        		String thing3Id = TestEventService.this.t3.getThingId();
				Map<String, String> params = new HashMap<String, String>();
				EventService eventService = TestEventService.this.eventService;
				
				// test getting all three events
		        System.out.println("thread waiting");
		        List<Event> events = eventService.getEvents(thing1Id, params, 3600, Filter.ALL);
				System.out.println("thread got event from "+events.get(0).getThingId());
				assertEquals(thing1Id, events.get(0).getThingId());
	
				System.out.println("thread waiting");
				events = eventService.getEvents(thing1Id, params, 3600,
						Filter.ALL);
				System.out.println("thread got event from "+events.get(0).getThingId());
				assertEquals(thing2Id, events.get(0).getThingId());

				System.out.println("thread waiting");
				events = eventService.getEvents(thing1Id, params, 3600, Filter.ALL);
				System.out.println("thread got event from "+events.get(0).getThingId());
				assertEquals(thing3Id, events.get(0).getThingId());
        	}
        };
        // now we get the thread going
        Thread t = new Thread(r);
        t.start();
        
        // wait a bit for the receiver thread to get ready
        Thread.sleep(1000);
        
		// now send some events to t1
		Event event = new Event();
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("test", 1234);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t1.getThingId());
		eventService.create(event, null, true);
		
        Thread.sleep(1000);

		// now send some events to t2
		info = new HashMap<String, Object>();
		info.put("test", 4567);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t2.getThingId());
		eventService.create(event, null, true);		
		
        Thread.sleep(1000);

		// now send some events to t3
		info = new HashMap<String, Object>();
		info.put("test", 890);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t3.getThingId());
		eventService.create(event, null, true);	
		
        Thread.sleep(1000);
        
        t.join();
		
        r = new Runnable() {
        	public void run() {
        		String thing1Id = TestEventService.this.t1.getThingId();
        		String thing2Id = TestEventService.this.t2.getThingId();
        		String thing3Id = TestEventService.this.t3.getThingId();
				Map<String, String> params = new HashMap<String, String>();
				EventService eventService = TestEventService.this.eventService;
					
				// test getting only events from following (we will send one directly too)
				System.out.println("thread waiting");
				List<Event> events = eventService.getEvents(thing1Id, params, 3600,
						Filter.FOLLOWING_ONLY);
				System.out.println("thread got event from "+events.get(0).getThingId());
				assertEquals(thing2Id, events.get(0).getThingId());

				System.out.println("thread waiting");
				events = eventService.getEvents(thing1Id, params, 3600, Filter.ALL);
				System.out.println("thread got event from "+events.get(0).getThingId());
				assertEquals(thing3Id, events.get(0).getThingId());
        	}
        };
        
        // now we get the thread going
        t = new Thread(r);
        t.start();
        
        // wait a bit for the receiver thread to get ready
        Thread.sleep(1000);
        
		// now send some events to t1
		event = new Event();
		info = new HashMap<String, Object>();
		info.put("test", 1234);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t1.getThingId());
		eventService.create(event, null, true);
		
        Thread.sleep(1000);

		// now send some events to t2
		info = new HashMap<String, Object>();
		info.put("test", 4567);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t2.getThingId());
		eventService.create(event, null, true);		
		
        Thread.sleep(1000);

		// now send some events to t3
		info = new HashMap<String, Object>();
		info.put("test", 890);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t3.getThingId());
		eventService.create(event, null, true);	
		
        Thread.sleep(1000);
        
        t.join();
		
        r = new Runnable() {
        	public void run() {
        		String thing1Id = TestEventService.this.t1.getThingId();
				Map<String, String> params = new HashMap<String, String>();
				EventService eventService = TestEventService.this.eventService;
					
				// test getting only events sent directly (send to following first)

				System.out.println("thread waiting on thing-only");
				List<Event> events = eventService.getEvents(thing1Id, params, 10,
						Filter.THING_ONLY);
				System.out.println("thread got event from "+events.get(0).getThingId());
				assertEquals(thing1Id, events.get(0).getThingId());
        	}
        };
        
        // now we get the thread going
        t = new Thread(r);
        t.start();
        
        // wait a bit for the receiver thread to get ready
        Thread.sleep(1000);
        

		// now send some events to t2
		info = new HashMap<String, Object>();
		info.put("test", 4567);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t2.getThingId());
		eventService.create(event, null, true);		
		
        Thread.sleep(1000);

		// now send some events to t3
		info = new HashMap<String, Object>();
		info.put("test", 890);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t3.getThingId());
		eventService.create(event, null, true);	
		
        Thread.sleep(1000);
        
		// now send some events to t1
		event = new Event();
		info = new HashMap<String, Object>();
		info.put("test", 1234);
		event.setEventId(null);
		event.setInfo(info);
		event.setServerTimestamp(System.currentTimeMillis());
		event.setThingId(t1.getThingId());
		eventService.create(event, null, true);
		
        Thread.sleep(1000);
        
        t.join();        
	}

}
