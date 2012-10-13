package ca.ubc.magic.thingbroker.testes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import ca.ubc.magic.thingbroker.controller.dao.EventDAO;
import ca.ubc.magic.thingbroker.controller.dao.ThingDAO;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.Thing;

import com.mongodb.MongoException;

public class Main {

	public static void main(String[] args) throws MongoException,
			JsonParseException, JsonMappingException, IOException {
		// testEventsStorage();
		persistBrazilianNewsServiceAsAThing();
		persistWeatherServiceAsAThing();
	    //addMetadata();
		//List<GroupKey> g = new ArrayList<GroupKey>();
		//g.add(new GroupKey("1234","public"));
		//System.out.println("JSON: " + Utils.generateJSON(g));
	}
	
	private static void addMetadata() {
		Thing thing = new Thing("123");
		Map<String,Object> metadata = new HashMap<String, Object>();
		metadata.put("ip_address", "200.18.98.96");
		metadata.put("port", "443");
		metadata.put("mac_address", "ACBRYEOI");
		thing.setMetadata(metadata);
		ThingDAO.putMetadata(thing);
	}

	private static void persistWeatherServiceAsAThing() {
		Thing thing = new Thing("123");
		thing.setType("service");
		thing.setName("weather forecast");
		thing.setDescription("This is the weather forecast");
		BlueYouService service = new BlueYouService();
		Authentication authentication = new Authentication();
		authentication.setUsername("servicesconsumer");
		authentication.setPassword("blueyoudcufscar2010@gsdr");
		service.setAuthentication(authentication);
		List<String> serviceAdresses = new ArrayList<String>();
		serviceAdresses
				.add("http://200.18.98.96:8080/BlueYouServices/services/monitor/dadosclima");
		service.setServiceURLs(serviceAdresses);
		List<BlueYouService> servicesAvailable = new ArrayList<BlueYouService>();
		servicesAvailable.add(service);
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("services", servicesAvailable);
		//metadata.put("mac_address", "1246786SDU");
		thing.setMetadata(metadata);
		ThingDAO.create(thing);
	}

	private static void persistBrazilianNewsServiceAsAThing() {
		Thing thing = new Thing("1234");
		thing.setType("service");
		thing.setName("brazil news");
		thing.setDescription("This is the news channel for news from Brazil");
		BlueYouService service = new BlueYouService();
		Authentication authentication = new Authentication();
		authentication.setUsername("servicesconsumer");
		authentication.setPassword("blueyoudcufscar2010@gsdr");
		service.setAuthentication(authentication);
		List<String> serviceAdresses = new ArrayList<String>();
		serviceAdresses
				.add("http://200.18.98.96:8080/BlueYouServices/services/monitor/noticiasbrasil");
		service.setServiceURLs(serviceAdresses);
		List<BlueYouService> servicesAvailable = new ArrayList<BlueYouService>();
		servicesAvailable.add(service);
		Map<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("services", servicesAvailable);
		thing.setMetadata(metadata);
		ThingDAO.create(thing);
	}

	private static void testEventsPersistence() {
		Event event = new Event(UUID.randomUUID().toString());
		//Event event = new Event("6cdaa8b5-e9f4-40c5-9662-462ac4882333");
		event.setServerTimestamp(System.currentTimeMillis());
        event.setThingId("1234");
        List<String> groupList = new ArrayList<String>();
        groupList.add("public");
		List<String> data = new ArrayList<String>();
		data.add("2498793587");
		data.add("87468-879");
		event.setData(data);
		EventDAO.create(event,null);
	}
}

class BlueYouService {
	private Authentication authentication;
	private List<String> serviceURLs;

	public Authentication getAuthentication() {
		return authentication;
	}

	public void setAuthentication(Authentication authentication) {
		this.authentication = authentication;
	}

	public List<String> getServiceURLs() {
		return serviceURLs;
	}

	public void setServiceURLs(List<String> serviceURLs) {
		this.serviceURLs = serviceURLs;
	}
}

class Authentication {
	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}