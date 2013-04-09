package ca.ubc.magic.thingbroker.model;

/**
 * Error message
 * 
 * @author Ricardo Almeida
 *
 */
public class StatusMessage {
	private Integer code;
	private String message;

	public StatusMessage() {
		
	}
	
	public StatusMessage(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
