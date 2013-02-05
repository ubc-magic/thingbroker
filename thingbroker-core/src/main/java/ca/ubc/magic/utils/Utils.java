package ca.ubc.magic.utils;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * JSON generator and parser
 * 
 * @author mike
 *
 * @param <T>
 */
public class Utils<T> {
	private static final Log logger = LogFactory.getLog(Utils.class);

	/**
	 * Generate JSON string from a java object
	 * 
	 * @param javaObject
	 *            Java Object to be converted to JSON
	 * @return JSON string
	 */
	public static String generateJSON(Object javaObject) {
		String jsonString = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			jsonString = mapper.writeValueAsString(javaObject);
		} catch (Exception e) {
			logger.error("Error while generating JSON string, returning null:"
					+ e);
			jsonString = null;
		}
		return jsonString;
	}

	public static <T> T parseToObject(String json, Class<T> classe)
			throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, classe);
	}


}
