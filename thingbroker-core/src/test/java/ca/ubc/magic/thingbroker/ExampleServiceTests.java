package ca.ubc.magic.thingbroker;

import ca.ubc.magic.thingbroker.ExampleService;
import junit.framework.TestCase;

public class ExampleServiceTests extends TestCase {

	private ExampleService service = new ExampleService();
	
	public void testReadOnce() throws Exception {
		System.out.println("Testando 2");
		assertEquals("Hello world!", service.getMessage());
	}

}
