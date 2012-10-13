package ca.ubc.magic.thingbroker.model;

import java.io.Serializable;
import java.util.Map;

public class State implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Map<String,StateInformation> stateInformation;

	public Map<String, StateInformation> getStateInformation() {
		return stateInformation;
	}

	public void setStateInformation(Map<String, StateInformation> stateInformation) {
		this.stateInformation = stateInformation;
	}
}
