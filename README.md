# reakt [![Build Status](https://travis-ci.org/advantageous/reakt.svg)](https://travis-ci.org/advantageous/reakt) [![Join the chat at https://gitter.im/advantageous/reakt](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/advantageous/reakt?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[Reakt website](http://advantageous.github.io/reakt)

***Reactive interfaces for Java.***

Reakt is reactive interfaces for Java which includes: 
 * [Promises](https://github.com/advantageous/reakt/wiki/Promise),
 * [Streams](https://github.com/advantageous/reakt/wiki/Stream), 
 * [Callbacks](https://github.com/advantageous/reakt/wiki/Callback), 
 * [Async Results](https://github.com/advantageous/reakt/wiki/Result) with [Expected](https://github.com/advantageous/reakt/wiki/Expected)
 * [Circuit Breakers](https://github.com/advantageous/reakt/wiki/Breaker)
 
The emphasis is on defining interfaces that enable lambda expressions, 
and fluent APIs for asynchronous programming for Java.

Note: This mostly just provides the interfaces not the implementations. There are some starter implementations but the idea is that anyone can implement this. It is all about interfaces. There will be adapters for Vertx, RxJava, Reactive Streams, etc. There is support for ***Guava Async*** (used by Cassandra) and the ***QBit*** microservices lib. [Elekt](http://advantageous.github.io/elekt/) uses Reakt for its reactive leadership election.



## Have a question?
[Reakt Mailing List](https://groups.google.com/forum/#!forum/reakt)

## Getting started
#### Using from maven

Reakt is published in the [maven public repo](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22io.advantageous.reakt%22).

```xml
<dependency>
    <groupId>io.advantageous.reakt</groupId>
    <artifactId>reakt</artifactId>
    <version>2.5.0.RELEASE</version>
</dependency>
```

#### Using from gradle
```xml
compile 'io.advantageous.reakt:reakt:2.5.0.RELEASE'
```

#### Fluent Promise API
```java
  Promise<Employee> promise = promise()
                .then(e -> saveEmployee(e))
                .catchError(error -> logger.error("Unable to lookup employee", error));

  employeeService.lookupEmployee(33, promise);
```

Or you can handle it in one line. 

#### Fluent Promise API example 2
```java


  employeeService.lookupEmployee(33, 
        promise().then(e -> saveEmployee(e))
                 .catchError(error -> logger.error("Unable to lookup ", error))
        );
```


Promises are both a callback and a Result; however, you can work
with Callbacks directly. 

#### Using Result and callback directly
```java
        employeeService.lookupEmployee(33, result -> {
            result.then(e -> saveEmployee(e))
                  .catchError(error -> logger.error("Unable to lookup", error));
        });
```

In both of these examples, lookupEmployee would look like:

#### Using Result and callback directly
```java

   public void lookupEmployee(long employeeId, Callback<Employee> callback){...}

```

You can use Promises to transform into other promises. 

#### Transforming into another type of promise using thenMap
```java

        Promise<Employee> employeePromise = Promises.<Employee>blockingPromise();

        Promise<Sheep> sheepPromise = employeePromise
                .thenMap(employee1 -> new Sheep(employee1.getId()));
```

The `thenMap` will return a new type of Promise. 

You can find more examples in the [reakt wiki](https://github.com/advantageous/reakt/wiki).

We also support working with streams. 


## Promise concepts

This has been adapted from this [article on ES6 promises](http://www.html5rocks.com/en/tutorials/es6/promises/).
A promise can be:

* fulfilled The callback/action relating to the promise succeeded 
* rejected  The callback/action relating to the promise failed 
* pending   The callback/action has not been fulfilled or rejected yet 
* completed The callback/action has been fulfilled/resolved or rejected

Java is not single threaded, meaning that two bits of code can run at 
the same time, so the design of this promise and streaming library takes
that into account. 

There are three types of promises:
* Callback promises
* Blocking promises (for testing and legacy integration)
* Replay promises (allow promises to be handled on the same thread as caller)

Replay promises are the most like their JS cousins. Replay promises are usually
managed by the Reakt `Reactor` and supports environments like Vert.x and QBit.
See the wiki for more details on Replay promises.

It is common to make async calls to store data in 
a NoSQL store or to call a remote REST interface or deal with a 
distributed cache or queue. Also Java is strongly typed so the library
that mimics JS promises is going to look a bit different. We tried to 
use similar terminology where it makes sense. 

Events and Streams are great for things that can happen multiple times 
on the same object — keyup, touchstart, or event a 
user action stream from Kafka, etc. 

With those events you don't really care about what happened before 
when you attached the listener. 

But often times when dealing with services and data repositories,
you want to handle a response with a specific next action, 
and a different action if there was an error
or timeout from the responses. You essentially want to call and handle
a response asynchronously and that is what promises allow.

This is not our first time to bat with Promises. QBit has had Promises for
a few years now. We just called them CallbackBuilders instead. 
We wanted to use more standard terminology and wanted to use the same 
terminology and modeling on projects that do not use QBit like Conekt, 
Vert.x, RxJava, and reactive streams.

At their most basic level, promises are like event listeners except:

A promise can only succeed or fail once. A promise cannot succeed or 
fail twice, neither can it switch from 
success to failure. Once it enters its `completed` state, then it is done.



## Bridges

[Reakt Guava Bridge](http://advantageous.github.io/reakt-guava/) which 
allows libs that use Guava async support to now have a modern Java feel.


#### Cassandra Reakt example

```java

register(session.executeAsync("SELECT release_version FROM system.local"), 
  promise().thenExpect(expected -> 
     gui.setMessage("Cassandra version is " +
         expected.get().one().getString("release_version"))
  ).catchError(error -> 
     gui.setMessage("Error while reading Cassandra version: " 
     + error.getMessage())
  )
);
     
```

***QBit 1*** ships with a bridge and ***QBit 2***will use ***Reakt*** as its 
primary reactive callback mechanism. 

***Conekt***, a slimmed down fork of Vert.x, will also use ***Reakt***. 

See [QBit](https://github.com/advantageous/qbit) microservices lib 
for more details.

See our wiki for more details on [Reakt](https://github.com/advantageous/reakt/wiki).


## Further reading

[What is Microservices Architecture?](http://www.mammatustech.com/microservices-architecture)

[QBit Java Micorservices lib tutorials](https://github.com/MammatusTech/qbit-microservices-examples/wiki)

The Java microservice lib. QBit is a reactive programming lib for building microservices - JSON, HTTP, WebSocket, and REST. QBit uses reactive programming to build elastic REST, and WebSockets based cloud friendly, web services. SOA evolved for mobile and cloud. ServiceDiscovery, Health, reactive StatService, events, Java idiomatic reactive programming for Microservices.

[Find more tutorial on QBit](https://github.com/MammatusTech/qbit-microservices-examples/wiki).


[Reactive Programming](http://rick-hightower.blogspot.com/2015/03/reactive-programming-service-discovery.html), [Java Microservices](http://rick-hightower.blogspot.com/2015/03/java-microservices-architecture.html), [Rick Hightower](http://www.linkedin.com/in/rickhigh)



[High-speed microservices consulting firm and authors of QBit with lots of experience with Vertx - Mammatus Technology](http://www.mammatustech.com/)

[Highly recommended consulting and training firm who specializes in microservices architecture and mobile development that are already very familiar with QBit and Vertx as well as iOS and Android - About Objects](http://www.aboutobjects.com/)

[Java Microservices Architecture](http://www.mammatustech.com/java-microservices-architecture)

[Microservice Service Discovery with Consul] (http://www.mammatustech.com/Microservice-Service-Discovery-with-Consul)

[Microservices Service Discovery Tutorial with Consul](http://www.mammatustech.com/consul-service-discovery-and-health-for-microservices-architecture-tutorial)

[Reactive Microservices]
(http://www.mammatustech.com/reactive-microservices)

[High Speed Microservices]
(http://www.mammatustech.com/high-speed-microservices)

[Java Microservices Consulting](http://www.mammatustech.com/java-microservices-consulting)

[Microservices Training](http://www.mammatustech.com/java-reactive-microservice-training)


[Reactive Microservices Tutorial, using the Reactor]
(https://github.com/MammatusTech/qbit-microservices-examples/wiki/Reactor-tutorial--%7C-reactively-handling-async-calls-with-QBit-Reactive-Microservices)

[QBit is mentioned in the Restlet blog](http://restlet.com/blog/2015/09/04/this-week-in-api-land-20/)

[All code is written using JetBrains Idea - the best IDE ever!](https://www.jetbrains.com/idea/)

