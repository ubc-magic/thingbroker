/*
* Ajax-based layer for supporting NUI interaction between displays
* Author: Roberto Calderon
*

EVENTS
(#id).thing("append": "<p>helloworld</p>", to: "#id2")
(#id).thing("prepend": "<p>helloworld</p>", to: "#id2")
(#id).thing("remove": "<p>helloworld</p>", to: "#id2")
(#id).thing("src": "http://localhost/img.jpg", to: "#id2")

LISTENER THING
(#id).thing(listen:true)

OTHER METHODS
(#id).thing("follow": "#id2")

**********************************************************

THINGBROKER API METHODS (BUNDLED WITH THE JQUERY PLUGIN)

$.ThingBroker({url: "http:url/thingbroker"}).postThing("thingId")
$.ThingBroker().postEvent("thingId", {key:value})
$.ThingBroker().getEvents("thingId")
$.ThingBroker().getThing("thingId")

 */

/*Nerve-wreking global variable*/
//'http://kimberly.magic.ubc.ca:8080/thingbroker',
var thingBrokerUrl = 'http://localhost:8080/thingbroker';

(function($) {
  
  $.fn.thing = function(params) {
    var now = (new Date).getTime();
    params = $.extend({
        url:thingBrokerUrl,
	thingId: this.attr('id'),
        thingName: this.attr('id'),
        thingType: "Web Object",
        thingIdUnique: this.attr('id')+(new Date).getTime(),
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
        console.log("Registering parent"+params.thingId);
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

    //if a cookie "display_id" is set: change thingid to add such id, unless functionality toggled false.
    function containerSafeThing(params) { 
      if (params.container) {
        var display=getCookie("display_id");
        if (display!=null && display!="") {	  
	  params.thingId = params.thingId + display;
          if (params.debug)
             console.log("Setting container safe thingId name "+params.thingId);
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
        debug: false
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
     if (params.debug)
        console.log("Sending event for "+thingId);
     var response = {}
     thingId = thingId.replace('#', '');
     thingId = containerSafeThingId(thingId);

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


  function containerSafeThingId(thingId) { 
      var display=getCookie("display_id");
      if (display!=null && display!="") {	  
	thingId = thingId + display;
        if (params.debug)
           console.log("Setting container safe thingId name "+thingId);
      }    
    return thingId;
  };

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

  return {
    postThing: postThing,
    postEvent: postEvent,
    getEvents: getEvents,
    getThing: getThing
  }
};
