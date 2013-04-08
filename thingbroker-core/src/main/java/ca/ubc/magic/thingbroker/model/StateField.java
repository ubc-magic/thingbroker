package ca.ubc.magic.thingbroker.model;

import java.io.Serializable;


public class StateField implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long timestamp;
	private String name;
	private Object value;
	private Integer index;
	private Object metadata;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Object getMetadata() {
		return metadata;
	}

	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}

}
