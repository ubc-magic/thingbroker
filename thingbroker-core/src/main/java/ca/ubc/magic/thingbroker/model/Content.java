package ca.ubc.magic.thingbroker.model;

import java.util.UUID;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.springframework.data.annotation.Id;

/**
 * Content associated with an event.
 * 
 * @author Ricardo Almeida, Mike Blackstock
 *
 */
@JsonSerialize(include = Inclusion.NON_NULL)
public class Content {
	@Id
	private String contentId;
    private byte [] data;
    private String name;
    private String mimeType;
    
    public Content() {
    	contentId = UUID.randomUUID().toString();
    }
    
    public Content(String contentId) {
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