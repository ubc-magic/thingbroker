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
import java.nio.charset.Charset;
import java.util.LinkedHashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import ca.ubc.magic.thingbroker.controller.ThingController;

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
		System.out.println(output);
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
