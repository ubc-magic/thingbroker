package ca.magic.ubc.thingbroker;

import static junit.framework.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
public class TestFollowController extends TestControllerBase {

	@Test
	public void testUnfollow() throws Exception {
		
		// get all of the things and delete them
		String output = this.mockMvc.perform(get("/things?name=test-thing1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

		JsonNode things = mapper.readTree(output);

		String thingId1 = things.get(0).get("thingId").asText();
		output = this.mockMvc.perform(get("/things?name=test-thing2"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		things = mapper.readTree(output);
		String thingId2 = things.get(0).get("thingId").asText();
		
        this.mockMvc.perform(post("/things/"+thingId1+"/follow")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[\""+thingId2+"\"]"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		output = this.mockMvc.perform(get("/things/"+thingId1))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		things = mapper.readTree(output);
     
		assertEquals(thingId2, things.get("following").get(0).asText());

		output = this.mockMvc.perform(get("/things/"+thingId2))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		things = mapper.readTree(output);
     
		assertEquals(thingId1, things.get("followers").get(0).asText());

        this.mockMvc.perform(post("/things/"+thingId1+"/unfollow")
				.contentType(MediaType.APPLICATION_JSON)
				.content("[\""+thingId2+"\"]"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		output = this.mockMvc.perform(get("/things/"+thingId1))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		things = mapper.readTree(output);
	     
		assertEquals(0, things.get("following").size());

	}

}
