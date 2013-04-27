/**
 * 
 */
package ca.ubc.magic.thingbroker.exceptions;

import ca.ubc.magic.thingbroker.config.Constants;
/**
 * @author mike
 *
 */
@SuppressWarnings("serial")
public class AppNotFoundException extends ThingBrokerException {
	public AppNotFoundException(String msg) {
		super(Constants.CODE_BAD_APPLICATION_ID, msg);
	}
}
