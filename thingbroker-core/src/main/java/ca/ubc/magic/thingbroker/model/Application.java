/**
 * 
 */
package ca.ubc.magic.thingbroker.model;

import org.springframework.data.annotation.Id;

/**
 * An application that uses the thingbroker.
 * 
 * @author mike
 *
 */
public class Application {
	@Id
	private String id;
	private String name;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
