/**
 * 
 */
package ca.ubc.magic.thingbroker.exceptions;

/**
 * @author mike
 *
 */
@SuppressWarnings("serial")
public class AppNotFoundException extends ThingBrokerException {
	public AppNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
