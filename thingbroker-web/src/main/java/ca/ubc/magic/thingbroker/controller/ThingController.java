package ca.ubc.magic.thingbroker.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ubc.magic.thingbroker.config.Constants;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.StatusMessage;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.ThingService;
import ca.ubc.magic.utils.Utils;

@Controller
@RequestMapping("/thing")
public class ThingController {
	
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);
	
	private ThingService thingService;
	
	public ThingController() {

	}
	
	public ThingController(ThingService thingService) {
		this.thingService = thingService;
	}
	
	@RequestMapping(value = "/",method = RequestMethod.POST, consumes="application/json", produces = "application/json")
	@ResponseBody
	public Object registerThing(@RequestBody Thing thing) {
		if(thing.getThingId() == null || thing.getThingId().equals("")) {
		  thing.setThingId(UUID.randomUUID().toString());
		}
		else {
			Map<String, String> searchParam = new HashMap<String, String>();
			searchParam.put("thingId",thing.getThingId());
			Thing storedThing = thingService.getThing(searchParam);
			if(storedThing != null) {
			   return new StatusMessage(Constants.CODE_THING_ALREADY_REGISTERED, Utils.getMessage("THING_ALREADY_REGISTERED"));
			}
			thingService.storeThing(thing);
		}
		return thing;
	}
	
	@RequestMapping(value = "/",method = RequestMethod.PUT, consumes="application/json", produces = "application/json")
	@ResponseBody
	public Object updateThing(@RequestBody Thing thing) {
		Map<String, String> searchParam = new HashMap<String, String>();
		searchParam.put("thingId",thing.getThingId());
		Thing storedThing = thingService.getThing(searchParam);
		if(storedThing != null) {
		   thingService.storeThing(thing);
		   return thing;
		}
		return new StatusMessage(Constants.CODE_THING_NOT_FOUND, Utils.getMessage("THING_NOT_FOUND"));
	}
	
	@RequestMapping(value = "/search",method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Thing retrieveThing(@RequestParam Map<String, String> params) {
		return thingService.getThing(params);
	}
	
	@RequestMapping(value = "/{thingId}/metadata",method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String,Object> retrieveThingMetadata(@PathVariable String thingId) {
		return thingService.getThingMetadata(new Thing(thingId));
	}
	
	@RequestMapping(value = "/{thingId}/metadata",method = RequestMethod.POST, consumes ="application/json", produces = "application/json")
	@ResponseBody
	public Object addThingMetadata(@PathVariable String thingId, @RequestBody Map<String,Object> metadata) {
		try {
		  Thing thing = new Thing(thingId);
		  thing.setMetadata(metadata);
		  return thingService.addMetadata(thing);
		}
		catch(ThingBrokerException ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_THING_NOT_FOUND,Constants.THING_NOT_FOUND_MESSAGE);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_SERVER_ERROR,ex.getMessage());
		}
	}
	
	@RequestMapping(value = "/{thingId}/follow",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public StatusMessage followThings(@PathVariable String thingId, @RequestBody String[] thingsToFollow) {
		try {
		    thingService.followThings(new Thing(thingId), thingsToFollow);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_THING_NOT_FOUND,Constants.THING_NOT_FOUND_MESSAGE);
		}
		return new StatusMessage(Constants.CODE_OK,Constants.FOLLOWING_REGISTRATION_SUCCESSFUL_MESSAGE);
	}

	@RequestMapping(value = "/{thingId}/unfollow",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public StatusMessage unfollowThings(@PathVariable String thingId, @RequestBody String[] thingsToFollow) {
		try {
		    thingService.unfollowThings(new Thing(thingId), thingsToFollow);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_THING_NOT_FOUND,Constants.THING_NOT_FOUND_MESSAGE);
		}
		return new StatusMessage(Constants.CODE_OK,Constants.UNFOLLOWING_REGISTRATION_SUCCESSFUL_MESSAGE);
	}
}
