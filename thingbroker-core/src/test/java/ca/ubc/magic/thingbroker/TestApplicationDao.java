package ca.ubc.magic.thingbroker;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.ubc.magic.thingbroker.dao.ApplicationDao;
import ca.ubc.magic.thingbroker.exceptions.AppNotFoundException;
import ca.ubc.magic.thingbroker.model.Application;

/**
 * 
 */

/**
 * @author mike
 *
 *
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:core-context.xml")
public class TestApplicationDao {

	@Autowired
	private ApplicationDao applicationDao;
	
	@Before
	public void setup() throws Exception {
		
		
	}
	
	
	@Test
	public void testCreateApplication() {
		Application app = new Application();
		app.setName("test-app");
		app = applicationDao.create(app);
		assertNotNull(app.getId());
		
		Application readApp = applicationDao.find(app.getId());
		assertEquals(app.getName(), readApp.getName());
	}
	
	@Test
	public void testBadApp() {
		try {
			applicationDao.find("badid");
		} catch (AppNotFoundException e) {
			return;	//success!
		}
		fail();
	}
	
	@Test
	public void testUpdate() {
		
		Application app = new Application();
		app.setName("test-app");
		app = applicationDao.create(app);
		assertNotNull(app.getId());
		Application readApp = applicationDao.find(app.getId());
		assertEquals(app.getName(), readApp.getName());
		
		app.setName("changed-name");
		app = applicationDao.update(app);
		readApp = applicationDao.find(app.getId());
		assertEquals("changed-name", app.getName());
		assertEquals(app.getName(), readApp.getName());
	}
	
	
	
	@After
	public void teardown() throws Exception {
		List<Application> apps = applicationDao.findAll(0, Integer.MAX_VALUE);
		for (Application app: apps) {
			applicationDao.delete(app.getId());
		}
	}
}
