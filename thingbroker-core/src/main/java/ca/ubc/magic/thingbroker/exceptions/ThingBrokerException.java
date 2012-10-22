package ca.ubc.magic.thingbroker.exceptions;

import org.springframework.core.NestedRuntimeException;

@SuppressWarnings("serial")
public class ThingBrokerException extends NestedRuntimeException {

	private Integer exceptionCode;
	
	public ThingBrokerException(final int exceptionCode, String msg) {
		super(msg);
		this.exceptionCode = exceptionCode;
	}

	public ThingBrokerException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public ThingBrokerException(Throwable cause) {
		super(cause != null ? cause.getMessage() : null, cause);
	}

	public Integer getExceptionCode() {
		return exceptionCode;
	}
}