package ca.ubc.magic.thingbroker;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ExampleConfigurationTests {
	
	@Autowired
	private ExampleService service;

	@Test
	public void testSimpleProperties() throws Exception {
		System.out.println("Testando");
		assertNotNull(service);
		System.out.println("Testando......");
	}
	
}
