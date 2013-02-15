# THING BROKER


## VERSION

Build version: 1.0
STS: 3.1.0

## INTRODUCTION

The term the "Internet of Things" is an evolving term used to refer to the technology and implementation of a global network of uniquely identifiable objects (things) and their on line representations. Things can be identified using RFID tags, optical codes, and other means; they can talk to each other as well as applications and online services. The concept of the Web of Things (WoT) considers how to fully integrate things not only on the internet, but the Web.

The Thing Broker enables easy application development by providing an easy to use RESTFul interface to things for applications for the WoT that can be deployed in a stand alone system, or as a platform as a service. The Thing Broker provides a data model supporting the notion of "things" as a way to encapsulate multiple sensor/actuator endpoints that are hosted on the same device, in the same environment, or on the same person.

## COMPILE FROM SOURCE

Thing Broker was written in java, using Springsource (STS 3.1.0). Download and install the development kit from Springsource. 

You will need two projects: "thing-broker-core" contains the core functionality of the Thing Broker, while "thing-broker-web" implements the REST API. The latter depends on the former, so you need to use maven to build both projects. Make sure to compile "thing-broker-core" first, otherwise "thing-broker-web" will not compile.

## RUN YOUR OWN INSTANCE

You will either need Tomcat 6+ or a Springsource server to run Thing Broker. For Tomcat: whether you compiled from source or you downloaded the latest "war" file. Place this file on your "webapps" directory of your Tomcat 6+ installation directory, and restart your Tomcat server.

## USAGE

See the [ThingBroker Wiki](https://github.com/ubc-magic/thingbroker/wiki) ThingBroker Wiki for information on how to use the ThingBroker in your applications.


