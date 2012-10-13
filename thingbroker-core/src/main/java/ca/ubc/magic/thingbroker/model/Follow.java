package ca.ubc.magic.thingbroker.model;

import java.util.List;

public class Follow {
	private Long id;   //follow id
    private List<String> followers; //list of followers
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<String> getFollowers() {
		return followers;
	}
	public void setFollowers(List<String> followers) {
		this.followers = followers;
	}   
}
