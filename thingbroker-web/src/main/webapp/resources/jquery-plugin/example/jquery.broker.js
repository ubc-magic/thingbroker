/*
*
* Ajax-based layer for supporting NUI interaction between displays
* Author: Roberto Calderon
* Date: Saturday 28th, 2012. Sao Carlos, Brazil.
*
*/

/*
*
* NOTES:
* name has to be unique at the broker. There can only be one thing named like that.
* one should be able to post to a thing through it's name, hence name has to be unique
*
*/




(function($) {
  
  $.fn.broker = function(params) {
    var now = (new Date).getTime();
    params = $.extend({
	url:'http://kimberly.magic.ubc.ca:8080/thingbroker/',
	name: this.attr('class'),
	topic: this.attr('class'), //TODO: change "topic" to "name"
        listen: false,	
        event_key: '',
	event_value: '',
	container: true, //true: thread topics based on display_id cookie, you can turn this off (false)
        timestamp: (new Date).getTime(), //timestamp of the latest event by the object
    },params);    
    
    //traverse all nodes
    this.each(function(){
      var obj = $(this);

      if (!params.listen && (params.remove || params.append || params.src || params.prepend || params.before) ) {
        params.topic = params.to;
        params = containerSafeTopic(params, params.topic);
        if (params.remove){params.event_key = 'remove';params.event_value = params.remove;};
	if (params.append){params.event_key = 'append';params.event_value = params.append;};
	if (params.prepend){params.event_key = 'prepend';params.event_value = params.prepend;};
	if (params.before){params.event_key = 'before';params.event_value = params.before;};
	if (params.src){params.event_key = 'src';params.event_value = params.src;};
	subscribe(params);
	sendEvent(params,obj);
      } else if (params.listen) {
	setTimeout(function() {
	  params = containerSafeTopic(params, params.topic);
	  subscribe(params);
	  getEvents(params, obj);
        }, 100); //let's wait for everything to load.	
      }
      
      return this;
    }); 

    function getEvents(params, obj) {
      $.ajax({
        type: "GET",
        crossDomain: true,
	url: params.url+"/events/thing/"+params.topic+"?requester="+params.topic+"&timeout=20&after="+params.timestamp,
        dataType: "JSON",   
        success: function(json) {
      	  updateElement(json, params, obj);
        }
      });    
    };

    function sendEvent(params, obj) {
      $.ajax({
  	type: "POST",
        url: params.url+"/events/event/thing/"+params.topic+"?keep-stored=true",
	data: '{"'+params.event_key+'": "'+encodeURIComponent(params.event_value)+'"}',
        contentType: "application/json",
	dataType: "JSON",
      });    
    };

    function subscribe(params) {
      $.ajax({
        type: "POST",
        crossDomain: true,
        url: params.url+"/thing",
	data: '{"thingId": "'+params.topic+'"}',
        contentType: "application/json",
	dataType: "JSON",
      });
    };

    function updateElement(json,params, obj) {
       $.each(json, function(index, jsonobj){
          $.each(jsonobj.info, function(key,value){
	     if (key == 'append') {
		if ( jQuery(obj).is('ul') ) {
		   obj.append("<li>"+decodeURI(value)+"</li>");
		}
		if ( jQuery(obj).is('div') ) {
		   obj.append("<p>"+decodeURI(value)+"</p>");
		}
	     }
	     if (key == 'before') {
		if ( jQuery(obj).is('ul') ) {
		   obj.before("<li>"+decodeURI(value)+"</li>");
		}
		if ( jQuery(obj).is('div') ) {
		   obj.before("<p>"+decodeURI(value)+"</p>");
		}
	     }
	     if (key == 'prepend') {
		if ( jQuery(obj).is('ul') ) {
		   obj.prepend("<li>"+decodeURI(value)+"</li>");
		}
		if ( jQuery(obj).is('div') ) {
		   obj.prepend("<p>"+decodeURI(value)+"</p>");
		}
	     }
	     if (key == 'remove') {                
	        if ( jQuery(obj).is('ul') ) {
	           $("li:contains('"+decodeURI(value)+"')").remove();	  
	        }
 	        if ( jQuery(obj).is('div') ) {	   
	           $("p:contains('"+decodeURI(value)+"')").remove();	  
	        }
             }
 	     if (key == 'src') {
                obj.attr("src", value);
             }



             params.timestamp = jsonobj.serverTimestamp;//update object with latest timestamp   
          });
       });
       getEvents(params,obj); //loop until the end of time searching for events
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

    //if a cookie "display_id" is set change topic to add such id, unless functionality toggled false.
    function containerSafeTopic(params, topic) { 
      if (params.container) {
        var display=getCookie("display_id");
        if (display!=null && display!="") {	  
	  params.topic = params.topic + display;
 	  params.client = params.client + display;
	}
      }      
      return params;
    };


  }  
})(jQuery);
