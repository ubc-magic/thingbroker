package ca.ubc.magic.thingbroker.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ubc.magic.thingbroker.controller.config.Constants;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.StatusMessage;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.ThingService;

/**
 * This controller deals with Thing meta data.
 * 
 * @author Ricardo Almeida, Mike Blackstock
 *
 */
@Controller
@RequestMapping("/things")
public class MetaDataController {

	private static final Logger logger = LoggerFactory.getLogger(ThingController.class);
	
	private ThingService thingService;
	
	public MetaDataController(ThingService thingService) {
		this.thingService = thingService;
	}
	
	@RequestMapping(value = "/{thingId}/metadata",method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Object retrieveThingMetadata(@PathVariable String thingId) {
		try {
		  return thingService.getThingMetadata(new Thing(thingId));
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
	
}
