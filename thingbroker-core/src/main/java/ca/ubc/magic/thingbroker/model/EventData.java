package ca.ubc.magic.thingbroker.model;

import java.util.UUID;

import org.springframework.data.annotation.Id;

public class EventData {
	@Id
	private String contentId;
    private byte [] data;
    private String name;
    private String mimeType;
    
    public EventData() {
    	contentId = UUID.randomUUID().toString();
    }
    
    public EventData(String contentId) {
    	this.contentId = contentId;
    }

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}   
}