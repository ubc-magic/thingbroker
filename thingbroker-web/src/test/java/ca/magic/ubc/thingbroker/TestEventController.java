/**
 * 
 */
package ca.magic.ubc.thingbroker;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

/**
 * @author mike
 *
 */
public class TestEventController extends TestControllerBase {
	private static final Log logger = LogFactory.getLog(TestEventController.class);

	@Test
	public void testFollowingEvents() throws Exception {
		String result = this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing-a\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing-a")).andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode thing = mapper.readTree(result);
        
        // final so we can access it from anonymous class below
        final String thingAId = thing.get("thingId").asText();
        logger.debug("the id is: "+thingAId);
		
        result = this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing-b\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing-b")).andReturn().getResponse().getContentAsString();
        thing = mapper.readTree(result);
        String thingBId = thing.get("thingId").asText();
        logger.debug("the id is: "+thingBId);
        
        result = this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing-c\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing-c")).andReturn().getResponse().getContentAsString();	
        thing = mapper.readTree(result);
        String thingCId = thing.get("thingId").asText();
        logger.debug("the id is: "+thingCId);
		
        // now follow B and C
        
        this.mockMvc.perform(post("/things/"+thingAId+"/follow")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[\""+thingBId+"\"]"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        
        this.mockMvc.perform(post("/things/"+thingAId+"/follow")
 				.contentType(MediaType.APPLICATION_JSON)
 				.content("[\""+thingCId+"\"]"))
 				.andExpect(status().isOk())
 				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
	
        // now see if we are following OK
        result = this.mockMvc.perform(get("/things/"+thingAId)
 				.contentType(MediaType.APPLICATION_JSON))
 				.andExpect(status().isOk())
 				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
 		
        thing = mapper.readTree(result);
        // A is following B and C
        assertEquals(2, thing.get("following").size());

        // visual check until I figure out how to use the json query language
        logger.debug(result);
        
        // now send an event and make sure we get em.
        this.mockMvc.perform(post("/things/"+thingCId+"/events")
 				.contentType(MediaType.APPLICATION_JSON)
 				.content("{\"test\":1234,\"data\":\"event to C!\"}"))
 				.andExpect(status().isOk())
 				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
      
        this.mockMvc.perform(post("/things/"+thingBId+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to B!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        this.mockMvc.perform(post("/things/"+thingAId+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to A!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        // get all events from 10 seconds ago
        result = this.mockMvc.perform(get("/things/"+thingAId+"/events?before=10000"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();

        logger.debug(result);

        // get events from things we're following from 10 seconds ago
        result = this.mockMvc.perform(get("/things/"+thingAId+"/events?before=10000&followingOnly=true"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();
        
        logger.debug(result);
        
 	}
	
	private Thread pollEvents(final int numEvents, final String thingId, final String filter) throws InterruptedException {
        // first, we make a long polling query in the background, on another thread for all events
        Runnable r = new Runnable() {
        	public void run() {
                // get all events from 10 seconds ago
                try {
                	for (int i=0; i<numEvents; i++) {
    			        logger.debug("thread waiting:"+filter);

						String result = TestEventController.this.mockMvc
								.perform(get("/things/" + thingId + "/events?waitTime=100&filter="+filter))
								.andExpect(status().isOk())
								.andExpect(content().contentType(MediaType.APPLICATION_JSON))
								.andReturn().getResponse().getContentAsString();

						logger.debug("thread got event!");
						logger.debug(result);
                	}
                	
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
      		
        	}
        };
        // now we get the thread going
        Thread t = new Thread(r);
        t.start();
        
        // wait a bit for the thread to get ready
        Thread.sleep(2000);	
        return t;
	
	}
	
	@Test
	public void testRealTimeEvents() throws Exception {
       // now real time events.
		
		String output = this.mockMvc.perform(get("/things?name=test-thing1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		
        ObjectMapper mapper = new ObjectMapper();
        JsonNode things = mapper.readTree(output);
		assertEquals(1, things.size());
		final String thing1Id = things.get(0).get("thingId").asText();

		output = this.mockMvc.perform(get("/things?name=test-thing2"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		
        things = mapper.readTree(output);
		assertEquals(1, things.size());
		final String thing2Id = things.get(0).get("thingId").asText();
		
		output = this.mockMvc.perform(get("/things?name=test-thing3"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		
        things = mapper.readTree(output);
		assertEquals(1, things.size());
		final String thing3Id = things.get(0).get("thingId").asText();

       // now 1 will follow 2 and 3
        
        this.mockMvc.perform(post("/things/"+thing1Id+"/follow")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[\""+thing2Id+"\",\""+thing3Id+"\"]"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        
        // first, we make a long polling query in the background, on another thread for all events
        Thread t = pollEvents(3, thing1Id, "all");
               
        // send an event
        this.mockMvc.perform(post("/things/"+thing1Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to 1!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // wait a bit for the thread to get ready
        Thread.sleep(1000);
        
        // send an event
        this.mockMvc.perform(post("/things/"+thing3Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to 3!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // wait a bit for the thread to get ready
        Thread.sleep(1000);
        
        // send an event
        this.mockMvc.perform(post("/things/"+thing2Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to 2!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        
        // now check the following only
        
        t.join();
        
        t = pollEvents(2, thing1Id, "following-only");
       
        // send an event
        this.mockMvc.perform(post("/things/"+thing1Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to 1!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // wait a bit for the thread to get ready
        Thread.sleep(1000);
        
        // send an event
        this.mockMvc.perform(post("/things/"+thing3Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to 3!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // wait a bit for the thread to get ready
        Thread.sleep(1000);
        
        // send an event
        this.mockMvc.perform(post("/things/"+thing2Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to 2!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        t.join();
        
        t = pollEvents(1, thing1Id, "thing-only");
        
        // send an event
        this.mockMvc.perform(post("/things/"+thing3Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to 3!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        logger.debug("sent event to 3");

        Thread.sleep(1000);
        

        
        // send an event
        this.mockMvc.perform(post("/things/"+thing2Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to 2!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        logger.debug("sent event to 2");
        Thread.sleep(1000);
      
        // send an event
        this.mockMvc.perform(post("/things/"+thing1Id+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":432,\"data\":\"event to 1!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
        logger.debug("sent event to 1");
        Thread.sleep(1000);


        t.join();
	}

	
}
