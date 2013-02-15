/**
 * 
 */
package ca.magic.ubc.thingbroker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration test for testing the API.  The XML file loads up the core modules.
 * We can then call the controllers as if an application would.
 * 
 * @author mike
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:servlet-context.xml")
public class TestThingController {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
 
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}
	
	@Test
	public void testGetThings() throws Exception {
		
		this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing1\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing1"));
		
		this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing2\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing2"));
		
		this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing3\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing3"));
		
	
		String output = this.mockMvc.perform(get("/things"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		
		// now delete the things
		
	}
	
	
	@Test
	public void testFollowing() throws Exception {
		String result = this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing-a\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing-a")).andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        LinkedHashMap<String, Object> thing = (LinkedHashMap<String, Object>) mapper.readValue(result, Object.class);
        
        // final so we can access it from anonymous class below
        final String thingAId = (String)thing.get("thingId");
        System.out.println("the id is: "+thingAId);
		
        result = this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing-b\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing-b")).andReturn().getResponse().getContentAsString();
		thing = (LinkedHashMap<String, Object>) mapper.readValue(result, Object.class);
        String thingBId = (String)thing.get("thingId");
        System.out.println("the id is: "+thingBId);

        result = this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing-c\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing-c")).andReturn().getResponse().getContentAsString();	
		thing = (LinkedHashMap<String, Object>) mapper.readValue(result, Object.class);
        String thingCId = (String)thing.get("thingId");
        System.out.println("the id is: "+thingCId);
		
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
 		
        // visual check until I figure out how to use the json query language
        System.out.println(result);
        
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

        System.out.println(result);

        // get events from things we're following from 10 seconds ago
        result = this.mockMvc.perform(get("/things/"+thingAId+"/events?before=10000&followingOnly=true"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse().getContentAsString();

        System.out.println(result);
        
        // now real time events.
        
        // first, we make a query in the background, on another thread
        Runnable r = new Runnable() {
        	public void run() {
                // get all events from 10 seconds ago
                try {
                	for (int i=0; i<3; i++) {
    			        System.out.println("thread waiting");

						String result = TestThingController.this.mockMvc
								.perform(
										get("/things/" + thingAId + "/events?waitTime=10"))
								.andExpect(status().isOk())
								.andExpect(
										content().contentType(MediaType.APPLICATION_JSON))
								.andReturn().getResponse().getContentAsString();

						System.out.println("thread got event!");
						System.out.println(result);
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
        Thread.sleep(1000);
       
        // send an event
        this.mockMvc.perform(post("/things/"+thingAId+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to A!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // wait a bit for the thread to get ready
        Thread.sleep(1000);
        
        // send an event
        this.mockMvc.perform(post("/things/"+thingCId+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to C!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // wait a bit for the thread to get ready
        Thread.sleep(1000);
        
        // send an event
        this.mockMvc.perform(post("/things/"+thingBId+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to B!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        
        // now check the following only
        
        t.join();
        
        r = new Runnable() {
        	public void run() {
                // get all events from 10 seconds ago
                try {
                	for (int i=0; i<2; i++) {
    			        System.out.println("thread waiting");

						String result = TestThingController.this.mockMvc
								.perform(
										get("/things/" + thingAId
												+ "/events?waitTime=10&followingOnly=true"))
								.andExpect(status().isOk())
								.andExpect(
										content().contentType(
												MediaType.APPLICATION_JSON))
								.andReturn().getResponse().getContentAsString();

						System.out.println("thread got event!");
						System.out.println(result);
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
        t = new Thread(r);
        t.start();
        
        // wait a bit for the thread to get ready
        Thread.sleep(1000);
       
        // send an event
        this.mockMvc.perform(post("/things/"+thingAId+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to A!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // wait a bit for the thread to get ready
        Thread.sleep(1000);
        
        // send an event
        this.mockMvc.perform(post("/things/"+thingCId+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to C!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        // wait a bit for the thread to get ready
        Thread.sleep(1000);
        
        // send an event
        this.mockMvc.perform(post("/things/"+thingBId+"/events")
  				.contentType(MediaType.APPLICATION_JSON)
  				.content("{\"test\":1234,\"data\":\"event to B!\"}"))
  				.andExpect(status().isOk())
  				.andExpect(content().contentType(MediaType.APPLICATION_JSON));

        t.join();
	}
	
	
	

	@Test
	public void registerThing() throws Exception {
		LinkedHashMap<String, Object> thing = new LinkedHashMap<String, Object>();
		thing.put("name", "test-thing-123");
		
		this.mockMvc.perform(post("/things")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"name\":\"test-thing\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing"));
	}
	
	@Test
	public void getFoo() throws Exception {
		// actually should return a list of things.
		this.mockMvc.perform(get("/things?name=test-thing").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("test-thing"));
	}
	
}
