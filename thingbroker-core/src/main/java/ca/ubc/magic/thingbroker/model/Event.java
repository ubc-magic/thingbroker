package ca.ubc.magic.thingbroker.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;

public class Event implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
    private String eventId;
	private String thingId;
	private Long serverTimestamp;
	private String info;
	private List<String> data;
    
    public Event() {
    	eventId = UUID.randomUUID().toString();
    	data = new ArrayList<String>();
    }
    
    public Event(String eventId) {
    	this.eventId = eventId;
    }
    
    public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public Long getServerTimestamp() {
		return serverTimestamp;
	}
	public void setServerTimestamp(Long serverTimestamp) {
		this.serverTimestamp = serverTimestamp;
	}
    public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	public List<String> getData() {
		return data;
	}
	public void setData(List<String> data) {
		this.data = data;
	}

	public String getThingId() {
		return thingId;
	}

	public void setThingId(String thingId) {
		this.thingId = thingId;
	}

	@Override
	public String toString() {
		return "Event [eventId=" + eventId + ", thingId=" + thingId
				+ ", serverTimestamp=" + serverTimestamp + ", data=" + data
				+ "]";
	}
} 
