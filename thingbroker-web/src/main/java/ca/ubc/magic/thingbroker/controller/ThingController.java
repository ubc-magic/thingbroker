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
import ca.ubc.magic.utils.Messages;

/**
 * This controller deals with things.
 * 
 * @author mike
 *
 */
@Controller
@RequestMapping("/things")
public class ThingController {
	
	private static final Logger logger = LoggerFactory.getLogger(ThingController.class);
	
	private ThingService thingService;
	private final Messages messages;
	
	public ThingController(ThingService thingService, Messages messages) {
		this.messages = messages;
		this.thingService = thingService;
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes="application/json", produces = "application/json")
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
	
	@RequestMapping(method = RequestMethod.PUT, consumes="application/json", produces = "application/json")
	@ResponseBody
	public Object updateThing(@RequestBody Thing thing) {
		try {
		   return thingService.update(thing);
		}
		catch(Exception ex) {
		    ex.printStackTrace();
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR, ex.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Object retrieveThing(@RequestParam Map<String, String> params) {
        try {
		  List<Thing> t = thingService.getThing(params);
		  if(t != null) {
			  return t;
		  }
		  return new StatusMessage(Constants.CODE_THING_NOT_FOUND, messages.getMessage("THING_NOT_FOUND"));
        }
		catch(Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public Object removeThing(@RequestParam("thingId") String thingId) {
		try {
		  Thing t = thingService.delete(new Thing(thingId));
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
	
}
