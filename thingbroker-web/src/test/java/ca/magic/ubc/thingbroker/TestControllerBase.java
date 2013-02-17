package ca.magic.ubc.thingbroker;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.After;
import org.junit.Before;
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
 * Base class for controller integration tests
 * 
 * @author mike
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:servlet-context.xml")
public abstract class TestControllerBase {

	   public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
	        return mapper.writeValueAsBytes(object);
	    }
	 
		@Autowired
		private WebApplicationContext wac;

		protected MockMvc mockMvc;

		@Before
		public void setup() throws Exception {
			this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
			
			// delete all things
			teardown();
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
			
		}
		
		@After
		public void teardown() throws UnsupportedEncodingException, Exception {
			// get all of the things and delete them
			String output = this.mockMvc.perform(get("/things"))
					.andExpect(status().isOk())
					.andExpect(content().contentType(MediaType.APPLICATION_JSON))
					.andReturn().getResponse().getContentAsString();
	        ObjectMapper mapper = new ObjectMapper();

			JsonNode things = mapper.readTree(output);
			
			for (Iterator<JsonNode> iter = things.getElements(); iter.hasNext(); ) {
			    JsonNode node = iter.next();
			    String id = node.get("thingId").asText();
				System.out.println("deleting thing:"+id);
				System.out.println("/things/"+id);

				this.mockMvc.perform(delete("/things/"+id));
			}
		}

}
