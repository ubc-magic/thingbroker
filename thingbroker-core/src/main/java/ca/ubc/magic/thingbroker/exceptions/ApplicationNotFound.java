/**
 * 
 */
package ca.ubc.magic.thingbroker.exceptions;

/**
 * @author mike
 *
 */
@SuppressWarnings("serial")
public class ApplicationNotFound extends ThingBrokerException {
	public ApplicationNotFound(String msg, Throwable cause) {
		super(msg, cause);
	}
}
