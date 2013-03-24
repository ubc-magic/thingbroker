/*
* Ajax-based layer for supporting NUI interaction between displays
* Author: Roberto Calderon
*



 */

/*Nerve-wreking global variable*/
var thingBrokerUrl = 'http://kimberly.magic.ubc.ca:8080/thingbroker';


/*Global Functions*/

function setUniqueId(thingId) {   
   var device_id = getCookie("device_id");
   if (device_id == undefined){
      var device_id = (new Date).getTime();
      setCookie("device_id", device_id, 365);
   }   
   return thingId + device_id;
}

function getCookie(c_name){
   var i,x,y,ARRcookies=document.cookie.split(";");
   for (i=0;i<ARRcookies.length;i++) {
     x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
     y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
     x=x.replace(/^\s+|\s+$/g,"");
     if (x==c_name) {
       return unescape(y);
     }
   }
};
function setCookie(c_name,value,exdays){
   var exdate=new Date();
   exdate.setDate(exdate.getDate() + exdays);
   var c_value=escape(value) + ((exdays==null) ? "" : "; expires="+exdate.toUTCString());
   document.cookie=c_name + "=" + c_value;
}

/*ThingBroker jQuery plugin*/

(function($) {
  
  $.fn.thing = function(params) {
    var now = (new Date).getTime();
    params = $.extend({
        url:thingBrokerUrl,
	thingId: this.attr('id'),
        thingName: this.attr('id'),
        thingType: "Web Object",
        //thingIdUnique: this.attr('id')+(new Date).getTime(),
        thingIdUnique: setUniqueId(this.attr('id')),
        listen: false,
        eventCallback: null,
        event_key: '',
        event_value: '',
	container: true, //thread topics based on display_id cookie
        timestamp: (new Date).getTime(), //timestamp of the latest event by the object
        debug: false
    },params);

    /*************/   
    //traverse all nodes when instantiated
    this.each(function(){
      var obj = $(this);
      params = containerSafeThing(params);
      params.thingType = this.tagName;
      //Send a jQuery event, to a thing.
      if (!params.listen && (params.remove || params.append || params.src || params.prepend) ) {
        if (params.to != null ) { 
           params.thingId = params.to.replace('#', '');
           params = containerSafeThing(params);
        }
        if (params.remove){params.event_key = 'remove';params.event_value = params.remove;};
	if (params.append){params.event_key = 'append';params.event_value = params.append;};
	if (params.prepend){params.event_key = 'prepend';params.event_value = params.prepend;};
	if (params.src){params.event_key = 'src';params.event_value = params.src;};	
	sendEvent(params,obj);
      } 

      //Follow a thing.
      else if (params.follow){
        followThing(params.follow.replace('#', ''));
      } 
      
      //Listen for events
      else if (params.listen) {
	setTimeout(function() {
	  registerThing(params);
          followThing(params.thingId); //following parent
          setTimeout (function() { getEvents(params, obj); }, 1000); //give server enough time to set follow
        }, 100); //let's wait for everything to load.	
      } else {
        registerThing(params); //setting up 
      }      
      return this;
    }); 
    /*************/

    function getEvents(params, obj) {
      if (params.debug) 
     	console.log("Getting event for "+params.thingIdUnique);
      $.ajax({
        type: "GET",
        crossDomain: true,
        url: params.url+"/things/"+params.thingIdUnique+"/events?waitTime=30&after="+params.timestamp,
        dataType: "JSON",   
        success: function(json) {
      	  updateElement(json, params, obj);
        },
        error: function(json) {connectionError(json)},
      });    
    };

    function sendEvent(params, obj) {
      if (params.debug) 
      	console.log("Sending event "+params.event_key+" for "+params.thingId);
      $.ajax({
  	type: "POST",
        url: params.url+"/things/"+params.thingId+"/events?keep-stored=true",
	data: '{"'+params.event_key+'": "'+encodeURIComponent(params.event_value)+'"}',
        contentType: "application/json",
	dataType: "JSON",
        error: function(json) {connectionError(json)},
      });
    };

    function registerThing(params) {
      if (params.debug) 
        console.log("Registering parent "+params.thingId);
      $.ajax({
        type: "POST",
        crossDomain: true,
        url: params.url+"/things",
	data: '{"thingId": "'+params.thingId+'","name": "'+params.thingName+'","type": "'+params.thingType+'"}',
        contentType: "application/json",
	dataType: "JSON",
        error: function(json) {connectionError(json)},
      });

      if (params.debug) 
        console.log("Registering "+params.thingId+" as "+params.thingIdUnique);
      $.ajax({
        type: "POST",
        crossDomain: true,
        url: params.url+"/things",
	data: '{"thingId": "'+params.thingIdUnique+'","name": "'+params.thingName+'","type": "'+params.thingType+'"}',
        contentType: "application/json",
	dataType: "JSON",
        error: function(json) {connectionError(json)},
      });
    };

    function connectionError(json) {
      console.log("Thingbroker Connection Error.");
      console.log(json);
    }

    function followThing(thingIdToFollow) {
      if (params.debug) 
        console.log("Setting "+params.thingIdUnique+" to follow "+thingIdToFollow);
      $.ajax({
        type: "POST",
        crossDomain: true,
        url: params.url+"/things/"+params.thingIdUnique+"/follow",
	data: '["'+thingIdToFollow+'"]',
        contentType: "application/json",
	dataType: "JSON",
        error: function(json) {connectionError(json)},
      });       
    }

    function updateElement(json,params, obj) {
       $.each(json, function(index, jsonobj){	 
	  if(jsonobj.info == null) {
             return false;
          }
          if (params.eventCallback != null) {
             var callback = params.eventCallback;
             callback(json);
          }
 	 
          $.each(jsonobj.info, function(key,value){
             var valueObject = $(decodeURIComponent(value));
	     if (key == 'append') {
                obj.append(valueObject);		
	     }
	     if (key == 'prepend') {
                obj.prepend(valueObject);
	     }
	     if (key == 'remove') {
                if ( jQuery(obj).is('div') ) {
                   var txt = valueObject.find("p").remove().html();
	           $("p:contains('"+txt+"')").remove();	  
                }
                if ( jQuery(obj).is('ul') ) {
                   var txt = valueObject.find("li").remove().html();
	           $("li:contains('"+txt+"')").remove();	  
                }
             }
 	     if (key == 'src') {
                obj.attr("src", value);
             }
             params.timestamp = jsonobj.serverTimestamp;//update object with latest timestamp   
          });
       });
       getEvents(params, obj);
    };

    //if a cookie "display_id" is set: change thingid to add such id, unless functionality toggled false.
    function containerSafeThing(params) { 
       if (params.container) {
          var display = '';
          var thingbroker_url = '';
          if ( location.href.indexOf("?") !== -1) {
             var urlparams = location.href.split('?')[1].split('&');
             data = {};
             for (x in urlparams) {
                data[urlparams[x].split('=')[0]] = urlparams[x].split('=')[1];
             }
             display = data.display_id;
             thingbroker_url = decodeURIComponent(data.thingbroker_url);
          } else {
             display = getCookie("display_id");
          }
          if (display!==null && display!=="" && display!==undefined) {
	    params.thingId = params.thingId + display;
            if (params.debug)
              console.log("Setting container safe thingId name "+params.thingId);
	  } 
          if (thingbroker_url!==null && thingbroker_url!=="" && thingbroker_url!==undefined) {
            params.url = thingbroker_url;
            if (params.debug)
              console.log("Setting container safe thingbroker_url "+params.url);
          }

      }
      return params;
    };
  }  
})(jQuery);


/*
API direct access - Bundled with the jqueryplugin
These methods require an async ajax call; because we want
the data before we can manipulate it (e.g. in processing).
Therefore, we are repeating the ajax-based methods below.
*/

jQuery.ThingBroker  = function(params) {

    params = $.extend({
        url:thingBrokerUrl,
	thingId: '',
        event: {},
        debug: false,
        container: true
    },params);

  var getEvents = function(thingId, limit) {
    if (params.debug) 
      console.log("Getting last "+limit+" events from "+thingId);
    thingId = thingId.replace('#', '');
    thingId = containerSafeThingId(thingId);
    if (limit==null)
      limit = 100;
    var eventMap = [];
    $.ajax({
         async: false,
         type: "GET",
         crossDomain: true,
 	 url: params.url+"/things/"+thingId+"/events?limit="+limit,
         dataType: "JSON",   
         success: function(json) { 
             for (i in json) {
                eventMap[i] = json[i];
             }
         },
         error: function(){console.log("Thingbroker Connection Error.")}
    });
    return eventMap;
  }

  var postThing = function(thingId) {
    if (params.debug) 
       console.log("Registering thing "+thingId);
    thingId = thingId.replace('#', '');    
    thingId = containerSafeThingId(thingId);
    var thingMap = {};
    $.ajax({
       type: "POST",
       crossDomain: true,
       url: params.url+"/things",
       data: '{"thingId": "'+thingId+'","name": "'+thingId+'"}',
       contentType: "application/json",
       dataType: "JSON",
       error: function(json) {connectionError(json)},
    });
    return thingMap;    
  }
  
  var getThing = function(thingId) {
    if (params.debug) 
       console.log("Getting thing "+thingID);
    thingId = thingId.replace('#', '');    
    thingId = containerSafeThingId(thingId);
    var thingMap = {};
    $.ajax({
       async: false,
       type: "GET",
       crossDomain: true,
       url: params.url+"/things/"+thingId,
       dataType: "JSON",   
       success: function(json) { 
          thingMap = json;
       },
       error: function(){console.log("Thingbroker Connection Error.")}
    });    
    return thingMap;
  }

  var postEvent = function(thingId, event) {
     var response = {}
     thingId = thingId.replace('#', '');
     thingId = containerSafeThingId(thingId);
     if (params.debug)
        console.log("Sending an event for "+thingId);
     $.ajax({
        async: false,
  	type: "POST",
        url: params.url+"/things/"+thingId+"/events?keep-stored=true",
        data: JSON.stringify(event),
        contentType: "application/json",
	dataType: "JSON",
        success: function(json) {
           response = json;
        },
         error: function(){console.log("Thingbroker Connection Error.")}
     });
   
     return response;
  } 

  var putEvent = function(eventId, serverTimestamp, event) {
     if (params.debug)
        console.log("Updating event "+eventId);
     $.ajax({
        async: false,
  	type: "PUT",
        url: params.url+"/events/"+eventId+"?serverTimestamp="+serverTimestamp,
        data: JSON.stringify(event),
        contentType: "application/json",
	dataType: "JSON",
        success: function(json) {
           response = json;
        },
         error: function(){console.log("Thingbroker Connection Error.")}
     });   
     return response;
  }

  function containerSafeThingId(thingId) { 
     var display = '';
     var thingbroker_url = '';
     if ( location.href.indexOf("?") !== -1) {
        var urlparams = location.href.split('?')[1].split('&');
        data = {};
        for (x in urlparams) {
           data[urlparams[x].split('=')[0]] = urlparams[x].split('=')[1];
        }
        display = data.display_id;
        thingbroker_url = decodeURIComponent(data.thingbroker_url);
     } else {
        display = getCookie("display_id");
     }
     if (display!==null && display!=="" && display!==undefined) {
        thingId = thingId + display;
        if (params.debug)
           console.log("Setting container safe thingId name "+params.thingId);
    }
    if (thingbroker_url!==null && thingbroker_url!=="" && thingbroker_url!==undefined) {
            params.url = thingbroker_url;
            if (params.debug)
              console.log("Setting container safe thingbroker_url "+params.url);
    }
    return thingId;
  };
  return {
    postThing: postThing,
    postEvent: postEvent,
    putEvent: putEvent,
    getEvents: getEvents,
    getThing: getThing
  }
};


