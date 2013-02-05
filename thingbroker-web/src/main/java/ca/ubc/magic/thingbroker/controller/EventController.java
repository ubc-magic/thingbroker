package ca.ubc.magic.thingbroker.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import ca.ubc.magic.thingbroker.controller.config.Constants;
import ca.ubc.magic.thingbroker.exceptions.ThingBrokerException;
import ca.ubc.magic.thingbroker.model.Event;
import ca.ubc.magic.thingbroker.model.EventData;
import ca.ubc.magic.thingbroker.model.StatusMessage;
import ca.ubc.magic.thingbroker.services.interfaces.EventService;
import ca.ubc.magic.utils.Messages;

@Controller
@RequestMapping("/events")
public class EventController {
	private static final Logger logger = LoggerFactory.getLogger(EventController.class);

	private final EventService eventService;
	private final Messages messages;

	public EventController(EventService eventService, Messages messages) {
		this.eventService = eventService;
		this.messages = messages;
	}

	@RequestMapping(value = "/event/thing/{thingId}", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Object postEvent(@PathVariable String thingId,
			@RequestParam("keep-stored") boolean keepStored,
			@RequestBody HashMap<String, Object> content) {
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

	@RequestMapping(value = "/event/thing/{thingId}", method = RequestMethod.POST, consumes = "multipart/form-data", produces = "application/json")
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

	@RequestMapping(value="/event/{eventId}",method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
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
	
	@RequestMapping(value="/event/{eventId}", method = RequestMethod.POST, consumes = "multipart/form-data", produces = "application/json")
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
	
	@RequestMapping(value = "/event/{eventId}", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
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
	
	@RequestMapping(value = "/thing/{thingId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getThingEvents(@PathVariable String thingId,
			@RequestParam Map<String, String> params) {
		try {
			Event event = new Event();
			event.setThingId(thingId);
			return eventService.retrieveByCriteria(event, params);
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

	@RequestMapping(value = "/event/{eventId}", method = RequestMethod.GET)
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
	
	@RequestMapping(value = "/event/content/{contentId}", method = RequestMethod.GET)
	@ResponseBody
	public Object getEventData(@PathVariable String contentId, @RequestParam("mustAttach") boolean mustAttach,
			HttpServletResponse response) {
		try {
			EventData data = eventService.retrieveEventData(new EventData(contentId));
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
	
	@RequestMapping(value = "/event/content-info/{contentId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Object getEventContentInfo(@PathVariable String contentId) {
		try {
			return eventService.retrieveEventDataInfo(new EventData(contentId));
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
	
	@RequestMapping(value = "/event/{eventId}/contents-description", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Object getEventContentsDescription(@PathVariable String eventId) {
		try {
			Event event = eventService.retrieve(new Event(eventId));
			List<EventData> contents = new ArrayList<EventData>();
			if(event.getData() != null && event.getData().size() > 0) { 
			  for(String dataId : event.getData()) {
				 contents.add(eventService.retrieveEventDataInfo(new EventData(dataId)));
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
	
	private EventData [] getEventDataArray(MultipartHttpServletRequest request) {
		List<EventData> dataList = new ArrayList<EventData>();
		final Map<String, MultipartFile> content = request.getFileMap();
		for (MultipartFile file : content.values()) {
			EventData data = new EventData();
			try {
				data.setData(file.getBytes());
				data.setName(file.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();
			}
			data.setMimeType(file.getContentType());
			dataList.add(data);
		}
		EventData[] dataArray = new EventData[dataList.size()];
		return dataList.toArray(dataArray);
	}
}
