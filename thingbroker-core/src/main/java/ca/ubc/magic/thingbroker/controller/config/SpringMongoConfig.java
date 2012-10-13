package ca.ubc.magic.thingbroker.controller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
 
import com.mongodb.Mongo;

@Configuration
public class SpringMongoConfig {
 
	public static final int THING_BROKER_DB = 1;
	public static final int THING_BROKER_CONTENT_STORAGE = 2;
	
	public @Bean
	MongoDbFactory mongoThingBrokerDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new Mongo(), "thingbroker");
	}
	
	public @Bean
	MongoTemplate mongoDBTemplate() throws Exception {
		 return new MongoTemplate(mongoThingBrokerDbFactory());
	}
	
	public @Bean
	MongoTemplate mongoStorageTemplate() throws Exception {
    	return new MongoTemplate(mongoThingBrokerContetStorageFactory());
	}
	
	public @Bean
	MongoDbFactory mongoThingBrokerContetStorageFactory() throws Exception {
		return new SimpleMongoDbFactory(new Mongo(), "tbcontentstorage");
	}
	
	public @Bean
	MongoTemplate mongoThingBrokerContentTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoThingBrokerDbFactory());
		return mongoTemplate;
	}
 
}