package ca.ubc.magic.thingbroker.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ubc.magic.thingbroker.config.Constants;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Content;
import ca.ubc.magic.thingbroker.model.StatusMessage;
import ca.ubc.magic.thingbroker.services.interfaces.EventService;

/**
 * This controller deals with (event) content.
 * 
 * @author mike
 *
 */
@Controller
@RequestMapping("/content")
public class ContentController {

	private static final Logger logger = LoggerFactory.getLogger(EventController.class);

	private final EventService eventService;

	public ContentController(EventService eventService) {
		this.eventService = eventService;
	}

	@RequestMapping(value = "{contentId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getEventData(@PathVariable String contentId, @RequestParam(value="mustAttach", required=false) Boolean mustAttach, HttpServletResponse response) {
		mustAttach = mustAttach==null?false:true;
		try {
			Content data = eventService.retrieveEventData(new Content(contentId));
			if (data != null) {
				response.setContentType(data.getMimeType());
				response.setContentLength(data.getData().length);
				if (!data.getMimeType().equals("application/json")) {
					if(mustAttach) {
						response.setHeader("Content-Disposition",	"attachment; filename=\"" + data.getName() + "\"");	
					}
				}
				try {
					if(mustAttach) {
					   FileCopyUtils.copy(data.getData(),response.getOutputStream());
					}
					else {
					   response.getOutputStream().write(data.getData());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
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
		return null;
	}
	
	@RequestMapping(value = "/{contentId}/info", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Object getEventContentInfo(@PathVariable String contentId) {
		try {
			return eventService.retrieveEventDataInfo(new Content(contentId));
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
