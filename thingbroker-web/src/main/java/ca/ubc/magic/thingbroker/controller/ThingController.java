package ca.ubc.magic.thingbroker.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ubc.magic.thingbroker.controller.config.Constants;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.StatusMessage;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.ThingService;

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
	    try {
		   return thingService.storeThing(thing);
		}
		catch(ThingBrokerException ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(ex.getExceptionCode(),ex.getMessage());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
	}
	
	@RequestMapping(value = "/",method = RequestMethod.PUT, consumes="application/json", produces = "application/json")
	@ResponseBody
	public Object updateThing(@RequestBody Thing thing) {
		try {
		   return thingService.update(thing);
		}
		catch(Exception ex) {
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR, ex.getMessage());
		}
	}
	
	@RequestMapping(value = "/search",method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Object retrieveThing(@RequestParam Map<String, String> params) {
        try {
		  List<Thing> t = thingService.getThing(params);
		  return (t != null) ? t : "{}";
        }
		catch(ThingBrokerException ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(ex.getExceptionCode(),ex.getMessage());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
	}
	
	@RequestMapping(value = "/",method = RequestMethod.DELETE, consumes="application/json", produces = "application/json")
	@ResponseBody
	public Object removeThing(@RequestBody Thing thing) {
		try {
		  Thing t = thingService.delete(thing);
		  return (t != null) ? t : "{}";
		}
		catch(ThingBrokerException ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(ex.getExceptionCode(),ex.getMessage());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
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
			return new StatusMessage(ex.getExceptionCode(),ex.getMessage());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
	}
	
/*	@RequestMapping(value = "/{thingId}/follow",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Object followThings(@PathVariable String thingId, @RequestBody String[] thingsToFollow) {
		try {
		    return thingService.followThings(new Thing(thingId), thingsToFollow);
		}
		catch(ThingBrokerException ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(ex.getExceptionCode(),ex.getMessage());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
	}
*/

	@RequestMapping(value = "/{thingId}/unfollow",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Object unfollowThings(@PathVariable String thingId, @RequestBody String[] thingsToFollow) {
		try {
		    return thingService.unfollowThings(new Thing(thingId), thingsToFollow);
		}
		catch(ThingBrokerException ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(ex.getExceptionCode(),ex.getMessage());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
	}
}
