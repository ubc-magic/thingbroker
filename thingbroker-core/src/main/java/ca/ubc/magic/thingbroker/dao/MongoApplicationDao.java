/**
 * 
 */
package ca.ubc.magic.thingbroker.dao;

import java.util.List;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import ca.ubc.magic.thingbroker.exceptions.AppNotFoundException;
import ca.ubc.magic.thingbroker.model.Application;

/**
 * @author mike
 *
 */
public class MongoApplicationDao implements ApplicationDao {
	
	private final MongoOperations mongoOperations;
	
	private String applicationCollection= "applications";
	
	public void setApplicationCollection(String collection) {
		this.applicationCollection = collection;
	}
	
	public MongoApplicationDao(MongoOperations mongoOperations) {
		this.mongoOperations = mongoOperations;
	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.dao.ApplicationDao#create(ca.ubc.magic.thingbroker.model.Application)
	 */
	@Override
	public Application create(Application app) {
		this.mongoOperations.insert(app, applicationCollection);
		return app;
	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.dao.ApplicationDao#update(ca.ubc.magic.thingbroker.model.Application)
	 */
	@Override
	public Application update(Application app) {
		this.mongoOperations.save(app, applicationCollection);
		return app;
	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.dao.ApplicationDao#delete(java.lang.String)
	 */
	@Override
	public void delete(String id) {
		Query q = new Query(Criteria.where("id").is(id));
		this.mongoOperations.remove(q, applicationCollection);
	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.dao.ApplicationDao#find(java.lang.String)
	 */
	@Override
	public Application find(String id) {
		Application a = this.mongoOperations.findById(id, Application.class, applicationCollection);
		if (a == null)
			throw new AppNotFoundException("Application: "+id+" not found", null);
		return a;
	}

	/* (non-Javadoc)
	 * @see ca.ubc.magic.thingbroker.dao.ApplicationDao#findAll(int, int)
	 */
	@Override
	public List<Application> findAll(int offset, int limit) {
		Query q = new Query().limit(limit).skip(offset);
		return this.mongoOperations.find(q, Application.class, applicationCollection);
	}

}
