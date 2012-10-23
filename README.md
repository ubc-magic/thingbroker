THING BROKER


# VERSION

Build version: 0.0.5
STS: 3.1.0

# INTRODUCTION

The term the "Internet of Things" is an evolving term used to refer to the technology and implementation of a global network of uniquely identifiable objects (things) and their on line representations. Things can be identified using RFID tags, optical codes, and other means; they can talk to each other as well as applications and online services. The concept of the Web of Things (WoT) considers how to fully integrate things not only on the internet, but the Web.

The Thing Broker enables easy application development by providing an easy to use RESTFul interface to things for applications for the WoT that can be deployed in a stand alone system, or as a platform as a service. The Thing Broker provides a data model supporting the notion of "things" as a way to encapsulate multiple sensor/actuator endpoints that are hosted on the same device, in the same environment, or on the same person.

# COMPILE FROM SOURCE

Thing Broker was written in java, using Springsource (STS 3.1.0). Download and install the development kit from Springsource. 

You will need two projects: "thing-broker-core" contains the core functionality of the Thing Broker, while "thing-broker-web" implements the REST API. The latter depends on the former, so you need to use maven to build both projects. Make sure to compile "thing-broker-core" first, otherwise "thing-broker-web" will not compile.

# RUN YOUR OWN INSTANCE

You will either need Tomcat 6+ or a Springsource server to run Thing Broker. For Tomcat: whether you compiled from source or you downloaded the latest "war" file. Place this file on your "webapps" directory of your Tomcat 6+ installation directory, and restart your Tomcat server.

# USAGE 

The Thing Broker can be accessed through a RESTFul interface running on either Tomcat or a Springsource server, generally running on port 8080. On the examples below we will use an instance running on the server at "http://kimberly.magic.ubc.ca". This will vary if you deployed your own instance. Currently, Thing Broker has only been implemented with JSON. That is, most data and "thing" definitions are accepted by the server only as JSON objects, and all responses will be in JSON format. 

The Thing Broker abstract entities as "things" with a name, decription and type. "Things" can have state and events related to them. Generally, developers would register a "thing" with the Thing Broker, and add data, metadata or related events to it. Then if needed they would query this information using the REST API.

## Response objects

A "thing" object is represented by the JSON object:

```
{
    thingId: "88c67d4e-178e-4c50-98b2-3e2179e32636",
    name: "Ricardo-Galaxy-Nexus",
    description: "Description for a thing",
    type: "smartphone",
    metadata: null,
    followers: [ ],
    following: [ ],
    state: null
}
```

An "event" response is commonly represented by a collection of events ordered by timestamp:

```
[
{
    eventId: "b7ba0f87-d54f-4531-af93-d78277b305ee",
    thingId: "123",
    serverTimestamp: 1349912663503,
    info: null,
    data: [
    "1abe3a85-7376-4a15-968b-72dca3fef2e6"
    ]
},
{
    eventId: "62e5d0fe-a854-4d72-bbec-6a211f720671",
    thingId: "123",
    serverTimestamp: 1349911561470,
    info: null,
    data: [
    "9dafb29b-21c8-4266-a6d6-8fe5ae128470"
]
},
{
    eventId: "5c114a70-7fb2-42ae-9685-9738d1008651",
    thingId: "123",
    serverTimestamp: 1349911236601,
    info: "{"website":"www.blueyou.com.br"}",
    data: [ ]
}
]
```

# API METHODS

You can use the publicly available endpoint of the Thing Broker at:

```
http://kimberly.magic.ubc.ca/thingbroker-web/
```

If you are running your own instance exchange the location of the broker to your server. The following methods are available for creating and manipulating "things" on the Thing Broker.

## (POST) - /thing

This method creates a thing on the Thing Broker. There are no required JSON parameters, but you MUST provide an empty JSON object; that is, at least "{}"

### Resource URL

http://kimberly.magic.ubc.ca/thingbroker-web/thing

### URL Parameters

 none

### JSON Parameters

     thingId: (not required) Specifies an alpha-numeric id.If not provided, thingId will receive a UUID as the identifier
     name: (not required) Specifies a thing's name.
     description: (not required) Specifies a short description of the thing.
     type: (not required) Specifies a user-defined thing type. Example Values: sensor, display, mobile.

### Example request

POST http://kimberly.magic.ubc.ca/thingbroker-web/thing

Content-type: application/JSON
Body: {"thingId": "123", "name": "test","description":"This is a thing","type":"Random thing"}

```
{
    "thingId": "123",
    "name": "test",
    "description": "This is a thing",
    "type": "Random thing",
    "metadata": null,
    "followers": [],
    "following": [],
    "state": null
}
```

## (PUT) - /thing

This method updates a thing on the Thing Broker. The only required JSON parameter is ThingId.

### Resource URL

http://kimberly.magic.ubc.ca/thingbroker-web/thing

### URL Parameters

 none

### JSON Parameters

     thingId: (required) Specifies an alpha-numeric id that identifies a thing
     name: (not required) Specifies a thing's name.
     description: (not required) Specifies a short description of the thing.
     type: (not required) Specifies a user-defined thing type. Example Values: sensor, display, mobile.

### Example request

PUT http://kimberly.magic.ubc.ca/thingbroker-web/thing

Content-type: application/JSON
Body: {"thingId": "123", "name": "test","description":"This is a thing","type":"Random thing"}

```
{
    "thingId": "123",
    "name": "test",
    "description": "This is a thing",
    "type": "Random thing",
    "metadata": null,
    "followers": [],
    "following": [],
    "state": null
}
```

## (DELETE) - /thing

This method removes a thing and all its events from Thing Broker. 

### Resource URL

http://kimberly.magic.ubc.ca/thingbroker-web/thing

### URL Parameters

 thingId: (required) Specifies an alpha-numeric id that identifies a thing

### JSON Parameters

 none

### Example request

DELETE http://kimberly.magic.ubc.ca/thingbroker-web/thing?thingId="123"

Content-type: application/JSON

**Response:** A JSON that represents the thing it was removed.

```
{
    "thingId": "123",
    "name": "test",
    "description": "This is a thing",
    "type": "Random thing",
    "metadata": null,
    "followers": [],
    "following": [],
    "state": null
}
```

## (GET) thing/search

This method will search for a thing on the current Thing Broker instance. It will return a thing object.

### Resource URL

http://kimberly.magic.ubc.ca/thingbroker-web/thing/search

### URL Parameters

    thingId: Search for a thing whose id matches "thingId"
    name: Search for all things whose name matches "name"
    type: Seach for all things whose type matches "type"

**Note: It's also possible to compose a search with more than one parameter**

### JSON Parameters

none

### Example request

GET http://kimberly.magic.ubc.ca/thingbroker-web/thing/search?thingId=123

GET http://kimberly.magic.ubc.ca/thingbroker-web/thing/search?name=test

GET http://kimberly.magic.ubc.ca/thingbroker-web/thing/search?type=test

GET http://kimberly.magic.ubc.ca/thingbroker-web/thing/search?thingId=123&name=test


```
{
    thingId: "123",
    name: "test",
    description: "This is a thing",
    type: "Random thing",
    metadata: null,
    followers: [ ],
    following: [ ],
    state: null
}
```

## (POST) events/event/thing/{thingId}

This method will add events to a thing, 

### Resource URL

http://kimberly.magic.ubc.ca/thingbroker-web/events/event/thing/{thingId}

### URL Parameters

    keep-stored: When set to "true" the events will be stored for future reference.

### JSON Parameters

You must provide a JSON object containing a collection of key-value pairs. You can provide as many key-value pairs, like so:

    {"video_url": "http://www.youtube.com/watch?v=QH2-TGUlwu4"}

### Example request

POST http://kimberly.magic.ubc.ca/thingbroker-web/events/event/thing/123?keep-stored=true

Content-type: application/JSON
Body: {"video_url": "http://www.youtube.com/watch?v=Rku5Oyf-hYU"}

**Note: If you are adding a file (image or other) on the body, make sure to change the content-type appropriately to: "Content-type: multipart/form-data". In this case, there'll be a reference id to each content in the data field of the event**

```
{
    "eventId": "7eb4e2ea-448f-4a5a-a388-0babc8b502c9",
    "thingId": "123",
    "serverTimestamp": 1349932703681,
    "info": "{\"video_url\":\"http://www.youtube.com/watch?v=Rku5Oyf-hYU\"}",
    "data": []
}
```

## (GET) events/thing/{thingId}

This method will query events from a thing, responding with an ordered collection of events. Events will be ordered by timestamp.

### Resource URL

http://kimberly.magic.ubc.ca/events/thing/{thingId}

### URL Parameters

    limit: (not required) Determines how many events will be fetched. Example Values: 24
    start: (not required) Works together with "end", returns events in an interval of time defined by "start" and "end". This parameter must be defined in Unix epoch time (milliseconds). Example Values: 1349937425
    end: (not required) Works together with "start", returns events in an interval of time defined by "start" and "end". This parameter must be defined in Unix epoch time (milliseconds). Example Values: 1349937434
    before: (not required) Returns events preceeding a time defined by this parameter. This parameter must be defined in Unix epoch time (milliseconds). Example Value: 1349937434
    after: (not required) Returns events suceeding a time defined by this parameter. This parameter must be defined in Unix epoch time (milliseconds). Example Value: 1349937420

**Note: If you don't provide url parameters it'll be returned a maximum of 25 events stored in Thing Broker

### JSON Parameters

none

### Example request

GET http://kimberly.magic.ubc.ca/thingbroker-web/events/thing/123

```
[
{
    eventId: "62e5d0fe-a854-4d72-bbec-6a211f720671",
    thingId: "123",
    serverTimestamp: 1349911561470,
    info: null,
    data: [
    "9dafb29b-21c8-4266-a6d6-8fe5ae128470"
]
},
{
    eventId: "5c114a70-7fb2-42ae-9685-9738d1008651",
    thingId: "123",
    serverTimestamp: 1349911236601,
    info: "{"website":"www.blueyou.com.br"}",
    data: [ ]
},
{
    eventId: "8f769677-a6f7-429d-b19b-db0c409e016c",
    thingId: "123",
    serverTimestamp: 1349906908431,
    info: "{"video_url":"http://www.youtube.com/watch?v=Rku5Oyf-hYU"}",
    data: [ ]
}
]
```

## (GET) events/event/content/{contentId}

This method will retrieve a piece of content by the unique content ID. Such ID is unique only to the Thing Broker instance being queried. Uniqueness between different Thing Broker servers cannot be guaranteed 

### Resource URL

http://kimberly.magic.ubc.ca/events/event/content/{contentId}

### URL Parameters

none

### JSON Parameters

none

### Example request

GET http://kimberly.magic.ubc.ca/events/event/content/9dafb29b-21c8-4266-a6d6-8fe5ae128470

The server will respond with the file whose ID matches the provided id.

```
None
```

# Error Codes

Calling a service in thing broker can return a JSON representing a status message indicating an error that occurred. All status messages are composed by a status code and a text message. The status code can be:

OK = 0

THING_NOT_FOUND = 1

THING_ALREADY_REGISTERED = 2

SENT_EVENT_TO_NON_EXISTENT_THING = 3

REQUESTER_NOT_INFORMED = 4

REQUESTER_NOT_REGISTERED = 5

INTERNAL_ERROR = 500

### Example of a status message

```

{
"code": 1,
"message": "Thing not found"
}

```

