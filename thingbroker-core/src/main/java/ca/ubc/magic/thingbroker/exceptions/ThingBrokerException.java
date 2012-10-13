package ca.ubc.magic.thingbroker.exceptions;

import org.springframework.core.NestedRuntimeException;

@SuppressWarnings("serial")
public class ThingBrokerException extends NestedRuntimeException {

	public ThingBrokerException(String msg) {
		super(msg);
	}

	public ThingBrokerException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ThingBrokerException(Throwable cause) {
		super(cause != null ? cause.getMessage() : null, cause);
	}
}