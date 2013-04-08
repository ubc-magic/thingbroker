package ca.ubc.magic.thingbroker.model;

import java.io.Serializable;
import java.util.Map;

public class State implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,StateField> stateInformation;

	public Map<String, StateField> getStateInformation() {
		return stateInformation;
	}

	public void setStateInformation(Map<String, StateField> stateInformation) {
		this.stateInformation = stateInformation;
	}
}
