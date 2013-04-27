package ca.ubc.magic.thingbroker.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.ubc.magic.thingbroker.dao.ApplicationDao;
import ca.ubc.magic.thingbroker.model.Application;

/**
 * @author mike
 *
 */
@Controller
@RequestMapping("/applications")
public class ApplicationController {

	private ApplicationDao applicationDao;
	
	public ApplicationController(ApplicationDao applicationDao) {
		this.applicationDao = applicationDao;
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes="application/json", produces = "application/json")
	@ResponseBody 
	public Object createApp(@RequestBody Application app) {
		return applicationDao.create(app);
	}
	
	@RequestMapping(value="{appId}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Application getApplication(@PathVariable String appId) {
		return applicationDao.find(appId);
	}
	
	@RequestMapping(method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Application> getAllApplications(@RequestParam(required=false) Integer offset,
			@RequestParam(required=false) Integer limit) {
		offset = offset==null?0:offset;
		limit = limit==null?100:limit;
		return applicationDao.findAll(offset, limit);
	}
	
	@RequestMapping(method = RequestMethod.PUT, produces = "application/json")
	@ResponseBody
	public Application updateApplication(@RequestBody Application app) {
		return applicationDao.update(app);
	}
	
	@RequestMapping(value="{appId}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public void deleteApplication(@PathVariable String appId) {
		applicationDao.delete(appId);
	}
}
