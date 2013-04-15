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

import java.util.LinkedHashMap;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

/**
 * Integration test for testing the API.  The XML file loads up the core modules.
 * We can then call the controllers as if an application would.
 * 
 * @author mike
 *
 */
public class TestThingController extends TestControllerBase {

	@Test
	public void testGetThings() throws Exception {
		
		String output = this.mockMvc.perform(get("/things"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		
        ObjectMapper mapper = new ObjectMapper();
        JsonNode things = mapper.readTree(output);
		assertEquals(3, things.size());
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
		// actually should return a list of things.
		this.mockMvc.perform(get("/things?name=test-thing").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].name").value("test-thing"));
	}
	
}
