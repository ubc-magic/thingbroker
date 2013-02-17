# THING BROKER


## VERSION

Build version: 0.5.0.SNAPSHOT

## INTRODUCTION

The term the "Internet of Things" is an evolving term used to refer to the technology and implementation of a global network of uniquely identifiable objects (things) and their on line representations. Things can be identified using RFID tags, optical codes, and other means; they can talk to each other as well as applications and online services. The concept of the Web of Things (WoT) considers how to fully integrate things not only on the internet, but the Web.

The Thing Broker enables easy application development by providing an easy to use RESTFul interface to things for applications for the WoT that can be deployed in a stand alone system, or as a platform as a service. The Thing Broker provides a data model supporting the notion of "things" as a way to encapsulate multiple sensor/actuator endpoints that are hosted on the same device, in the same environment, or on the same person.

## COMPILE FROM SOURCE

Thing Broker was written in Java.  We use the Spring Tools Suite version of Eclipse
as our IDE and maven to build it on the command line.

You will need two projects: "thing-broker-core" contains the core data model and services
of the Thing Broker.
The "thingbroker-web" project implements the REST API. The latter depends on the former,
so you need to use maven to build both projects.

To compile the projects:

    cd thingbroker
    mvn package
    
Assuming a JDK and Maven is installed correctly, you should end up with the following files in
the /thingbroker-web/target directory:

    thingbroker-web-{version}.war

## RUN YOUR OWN INSTANCE

Before you can run the thing broker, install MongoDB.  See http://www.mongodb.org/

To test it out quickly, and assuming you've compiled it with maven:

    mvn tomcat:run
    
This will install tomcat and run the thing broker on port 8080.  To test hit

    http://localhost:8080/thingbroker

To get the empty list of things:

    http://localhost:8080/thingbroker/things

To deploy a server, you will need a Java web application container such as Jetty or Tomcat
and MongoDB.

1. Install and run MongoDB.  See http://www.mongodb.org/
2. Install Tomcat or Jetty
3. Rename thingbroker-web-{version}.war that you compiled to thingbroker.war
1. Place the thingbroker.war file into your "webapps" directory of your Tomcat installation (or appropriate location
for Jetty) and restart your Tomcat server.

## USAGE

See the [ThingBroker Wiki](https://github.com/ubc-magic/thingbroker/wiki) ThingBroker Wiki for information on how to use the ThingBroker in your applications.

## Summary of Changes

Feb. 16, 2013
- URLs are more consistent - see
https://github.com/ubc-magic/thingbroker/wiki/Thing-Broker-API-2.
- following and unfollowing now works for getting both historical and
real time events.  When you get events from a thing, you get events
send to the thing, and the things it is following or, you can specify
followingOnly=true to get the events from only the things you are
following.  This is to support the use cases I described.
- events now contain "info" and/or "content" rather than "data"
- null fields are no longer sent with JSON
- behavior of some of  event time query fields are consistent with the MAGIC broker (before, after, start, end)
- 'offset' added to event queries.
- integration tests to drive the controllers with fake HTTP requests.

