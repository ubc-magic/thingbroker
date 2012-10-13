package ca.ubc.magic.utils;

import java.io.IOException;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Utils<T> {
	private static final Log logger = LogFactory.getLog(Utils.class);
	private static MessageSource msg = getMessageSource();
	private static Locale locale = Locale.US;

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

	private static MessageSource getMessageSource() {
		if (msg == null) {
			ApplicationContext app = new ClassPathXmlApplicationContext(
					"META-INF/spring/app-context.xml");
			msg = (MessageSource) app.getBean("msgObj");
		}
		return msg;
	}
	
	public static String getMessage(String msgTag) {
		return msg.getMessage(msgTag, null, locale);
	}
	
	public static Locale getLocale() {
		return locale;
	}

	public static void setLocale(Locale locale) {
		Utils.locale = locale;
	}
}
