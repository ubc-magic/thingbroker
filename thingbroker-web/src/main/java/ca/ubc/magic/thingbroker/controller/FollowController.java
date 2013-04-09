/**
 * 
 */
package ca.ubc.magic.thingbroker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ubc.magic.thingbroker.config.Constants;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.StatusMessage;
import ca.ubc.magic.thingbroker.model.Thing;
import ca.ubc.magic.thingbroker.services.interfaces.ThingService;

/**
 * This controller deals with following things
 * @author mike
 *
 */
@Controller
@RequestMapping("/things")
public class FollowController {

	private static final Logger logger = LoggerFactory.getLogger(ThingController.class);
	
	private ThingService thingService;
	
	public FollowController(ThingService thingService) {
		this.thingService = thingService;
	}
	
	//TODO: get follow?

	@RequestMapping(value = "{thingId}/follow",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
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
