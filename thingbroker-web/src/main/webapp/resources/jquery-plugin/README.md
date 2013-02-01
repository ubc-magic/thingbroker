JQUERY BROKER PLUGIN

## VERSION

Plugin version: 0.1.0
Broker dependency: < 1.4.0

## CHANGE LOG
2012.08.01-Roberto: First Version of this Requirements Document.
2012.08.09-Roberto: First Version of the plugin.
2012.10.29-Roberto: Second Version of the plugin using the new Thing Broker

## INTRODUCTION

This plugin serves to augment existent web content with real-time capabilities pertaining to Natural User Interfaces. The broker.jquery plugin connects web document objects (divisions, paragraphs, lists) together without the need of server-dependent background processes. This allows for client-to-client interaction and web-browser to web-browser data exchange that is transparent to the developer. The plugin leverages JQuery's wide range of hooks and functions to manipulate data.

## SCENARIOS

Mollie stands before a large display and wants to move content from a handheld device onto the large screen. To do so she performs a natural gesture of "throwing" the content on her device towards the screen.

## CLIENT-TO-CLIENT SOLUTION

Mollie is moving content from one web-browser (her mobile) to another web-browser (the one running a large display application), often both applications are developed in different languages (ruby, php, java) and connecting them together requires server-dependent hooks and listeners. By taking the mental model literally we propose exposing web-clients to each other through a single third party service without the need of server-dependent modifications. This allows for ANY web-browser, service, or application to modify and access shared data seamingless and to make development of small applications a snap.

## DEPENDENCIES

The current plugin depends on the Thing Broker (https://github.com/magic-liam/thingbroker). You can run a self version of the broker on your local machine, or use the provided testing version available at http://kimberly.magic.ubc.ca:8080/thingbroker-web. Downloading this project and running the examples will work out of the box, as the testing version of the Thing Broker is the default backend for this plugin.

## SECURITY CONCERNS AND SCALABILITY

This solution is targeted at prototype and small applications. In a large application having numerous inter-connected objects across many applications, the number of listening objects becomes unmanageable and insecure. Please take this into consideration when developing your applications.

## DESIGN REQUIREMENTS

* DOM objects are considered "things" on the web
* Default callbacks are provided for basic common DOM operations (e.g. append, remove, etc) in JQuery, making object-to-object manipulations transparent.

## ARCHITECTURE
```
  Large display App                                            Small display App
-----------------------           ---------------             --------------------
      DOM Object                   Non-web Client                  DOM Object
-----------------------           ---------------             --------------------
     Jquery plugin. <------------>     Broker   <---------->      Jquery plugin
-----------------------           ---------------             --------------------
      Jquery api                   DB      QUEUE                  Jquery api
------------------------          -----    ------             --------------------
      JavaScript                                                   Javascript
```
Both applications might be served from the same server, and even the same application but by binding them through a broker we inter-connect DOM objects, rather than applications, that allow for inter-object communications, e.g. one swipe on one object manipulates another object in real time.

```
[Object 1]           ----------------[Object 2]
   |                |
   | Listens to     | Posts to           | Listens to
   |                |                    |
[Topic 1]    <-------                 [Topic 2]

```

## CURRENT STATE AND CROSS-DOMAIN SUPPORT

The above diagrams are the "desired" architecture of the plugin. However, due to the current state of the MAGICBroker doing cross-domain calls is cumbersome. To solve this the plugin HAS to be used with the brokerproxy.php file. This php script works together with the plugin to redirect ajax requests to other domains and parse the response to the javascript-based ajax call. This will be deprecated on the new version of the broker, which will use Jsonp responses and will allow crossdomain calls. For the moment being, you're stuck with having to place the brokerproxy.php file on the root directory of your application. Sorry.

## API

Currently you have availability to three DOM operations with this plugin: append, remove and src. Append will append data to a brokerized DOM object, while remove will remove such data. Src is a bit tricky as is intended for updating the src value in an <img> object.


```
$('.class').broker(); Will subscribe the object with a class "class" to itself, i.e. a topic="class" and a clientID="class"

$('.class').broker({listen:true}); Will subscribe the object, set repetitive ajax calls to listen for events, and define callback functions for append, remove and src.

$('.class').broker({listen:true, url:'http://site.com'}); Will initialize the object to listen on a broker hosted on http://site.com

$('.class').broker({listen:true, url:'http://site.com', topic:'atopic'}); Will initialize the object to listen on a broker hosted on http://site.com and listen on a different object's topic (to create a listener object for another topic).

$('.class').broker({append:'hello this is a message', to: 'class2'}); Will append 'hello this is a message' to the object with class "class2" (this will generate a p object within a div element, or a li element within a ul element)

$('.class').broker({remove: 'this is a data sent previously', to: 'class2'}); Will remove child objects that contain the text "this is a data sent previously" within the object with class "class2"

$('.class').broker({src:'imagesrc', to: 'class2'}); Will update the src parameter of the img object with class "class2"
```


## IMPLEMENTED KEY-DATA PAIRS AND THEIR DOM OPERATIONS

If you're using raw events through your own custom plugin and want to communicate with objects using the Thing Broker Jquery plugin these are the implemented key-data pairs (more info at https://github.com/magic-liam/thingbroker)

```
THE EVENT {"append": "data"}
Appends "data" to a brokerized <div class'data'> as <p>data</p> 
Appends "data" to a brokerized <ul class 'data'> as <li>data</li>
```
```
THE EVENT {"remove": "data"}
Removes the element that contains the given data from the object listening to the event.
```
```
THE EVENT {"src": "data"}
Updates the src="" attribute of an image object listening to the event: <img src='data'></img>
```

