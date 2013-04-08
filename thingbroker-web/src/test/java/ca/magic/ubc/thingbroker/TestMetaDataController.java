/**
 * 
 */
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
 * Integration test for thing meta data controller.
 * 
 * @author 
 * @author Ricardo Almeida, Mike Blackstock
 *
 */
public class TestMetaDataController extends TestControllerBase {

	@Test
	public void testMetaData() throws Exception {
		
		// get all of the things and delete them
		String output = this.mockMvc.perform(get("/things?name=test-thing1"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();

		JsonNode things = mapper.readTree(output);
		String thingId1 = things.get(0).get("thingId").asText();
	
		// get all of the things and delete them
		output = this.mockMvc.perform(get("/things/"+thingId1+"/metadata"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		JsonNode metaData = mapper.readTree(output);
		//empty
		assertEquals(0,metaData.size());
		
		output = this.mockMvc.perform(post("/things/"+thingId1+"/metadata")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"data\":\"test-meta-data\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();
		metaData = mapper.readTree(output);
		// should return only meta data, but currently returns thing!
		assertEquals(1,metaData.get("metadata").size());
		assertEquals("test-meta-data",metaData.get("metadata").get("data").asText());

	}
}
