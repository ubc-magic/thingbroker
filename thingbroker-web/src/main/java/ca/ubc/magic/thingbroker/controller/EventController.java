package ca.ubc.magic.thingbroker.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.ubc.magic.thingbroker.config.Constants;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Content;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.StatusMessage;
import ca.ubc.magic.thingbroker.services.interfaces.EventService;
import ca.ubc.magic.utils.Messages;

@Controller
public class EventController {
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);

	private final EventService eventService;
	private final Messages messages;

	public EventController(EventService eventService, Messages messages) {
		this.eventService = eventService;
		this.messages = messages;
	}

	/**
	 * Send a new event to the thing.
	 * 
	 * @param thingId
	 * @param keepStored
	 * @param content
	 * @return
	 */
	@RequestMapping(value = "/things/{thingId}/events", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Object postEvent(@PathVariable String thingId,
			@RequestParam(value="keep-stored", required=false) Boolean keepStored,
			@RequestBody HashMap<String, Object> content) {
		
		keepStored = keepStored == null?true:keepStored;
		
        try {
		   Event event = new Event();
		   event.setServerTimestamp(System.currentTimeMillis());
		   event.setThingId(thingId);
		   event.setInfo(content);
		   return eventService.create(event, null, keepStored);
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

	/**
	 * Send a new event to the thing containing content
	 * 
	 * @param thingId
	 * @param keepStored
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/things/{thingId}/events", method = RequestMethod.POST, consumes = "multipart/form-data", produces = "application/json")
	@ResponseBody
	public Object postEventWithMultipartContent(@PathVariable String thingId,
			@RequestParam("keep-stored") boolean keepStored,
			MultipartHttpServletRequest request) {
		try {
			Event event = new Event();
			event.setServerTimestamp(System.currentTimeMillis());
			event.setThingId(thingId);
			return eventService.create(event, getEventDataArray(request),keepStored);
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

	/**
	 * Update the event with the specified id
	 * 
	 * @param eventId
	 * @param info
	 * @param params
	 * @return
	 */
	@RequestMapping(value="/events/{eventId}",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Object updateEvent(@PathVariable String eventId, @RequestBody Map<String,Object> info,@RequestParam Map<String, String> params) {
		try {
			if(params != null) {
			 String serverTimestamp = params.get("serverTimestamp");
			 if(serverTimestamp != null) {
				Event updatedEvent = new Event(eventId);
				updatedEvent.setServerTimestamp(Long.valueOf(serverTimestamp));
				updatedEvent.setInfo(info);
				return eventService.update(updatedEvent, null);	
			 }
			}
			throw new ThingBrokerException(Constants.CODE_SERVER_TIMESTAMP_NOT_PROVIDED,messages.getMessage("SERVER_TIMESTAMP_NOT_PROVIDED"));
		} catch (ThingBrokerException ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(ex.getExceptionCode(), ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
	}
	
	/**
	 * Update the event with the specified ID by adding content to it.
	 * 
	 * @param eventId
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/events/{eventId}", method = RequestMethod.POST, consumes = "multipart/form-data", produces = "application/json")
	@ResponseBody
	public Object updateEventWithMultipartContent(@PathVariable String eventId, MultipartHttpServletRequest request) {
		try {
			return eventService.update(new Event(eventId), getEventDataArray(request));
		} catch (ThingBrokerException ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(ex.getExceptionCode(), ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug(ex.getMessage());
			return new StatusMessage(Constants.CODE_INTERNAL_ERROR,ex.getMessage());
		}
	}
	
	/**
	 * Update an event with content with the specified info
	 * 
	 * @param eventId
	 * @param content
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/events/{eventId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Object addEventInfoData(@PathVariable String eventId, @RequestBody HashMap<String, Object> content,@RequestParam Map<String, String> params) {
        try {
        	if(params != null) {
   			   String serverTimestamp = params.get("serverTimestamp");
   			   if(serverTimestamp != null) {
   				  Event updatedEvent = new Event(eventId);
   			      updatedEvent.setServerTimestamp(Long.valueOf(serverTimestamp));	
 		          return eventService.addDataToInfoField(updatedEvent, content); 
   			   }
        	}
			throw new ThingBrokerException(Constants.CODE_SERVER_TIMESTAMP_NOT_PROVIDED,messages.getMessage("SERVER_TIMESTAMP_NOT_PROVIDED"));
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
	
	/**
	 * Get events from the thing and its followers
	 * 
	 * @param thingId
	 * @param params
	 * @return
	 */
	@RequestMapping(value = "/things/{thingId}/events", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Object getThingEvents(@PathVariable String thingId, @RequestParam Map<String, String> params) {
		try {
			Event event = new Event();
			event.setThingId(thingId);
			int waitTime = params.get("waitTime") == null?10:Integer.parseInt(params.get("waitTime"));
			boolean followingOnly = params.get("followingOnly") == null?false:Boolean.parseBoolean(params.get("followingOnly"));
			
			return eventService.getEvents(thingId, params, waitTime, followingOnly);
			
//			return eventService.retrieveByCriteria(event, params);
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
	

	/**
	 * Get a single event
	 * 
	 * @param eventId
	 * @return
	 */
	@RequestMapping(value = "/events/{eventId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getThingEvent(@PathVariable String eventId) {
		try {
			Event event = new Event(eventId);
			Event response = eventService.retrieve(event);
			return (response != null) ? response : "{}";
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
	
	
	/**
	 * Get the content description for the content associated with the event
	 * 
	 * @param eventId
	 * @return
	 */
	@RequestMapping(value = "/events/{eventId}/contents-description", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Object getEventContentsDescription(@PathVariable String eventId) {
		try {
			Event event = eventService.retrieve(new Event(eventId));
			List<Content> contents = new ArrayList<Content>();
			if(event.getContent() != null && event.getContent().size() > 0) { 
			  for(String dataId : event.getContent()) {
				 contents.add(eventService.retrieveEventDataInfo(new Content(dataId)));
			  }
			}
			return contents;
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
	
	private Content [] getEventDataArray(MultipartHttpServletRequest request) {
		List<Content> dataList = new ArrayList<Content>();
		final Map<String, MultipartFile> content = request.getFileMap();
		for (MultipartFile file : content.values()) {
			Content data = new Content();
			try {
				data.setData(file.getBytes());
				data.setName(file.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();
			}
			data.setMimeType(file.getContentType());
			dataList.add(data);
		}
		Content[] dataArray = new Content[dataList.size()];
		return dataList.toArray(dataArray);
	}
}
