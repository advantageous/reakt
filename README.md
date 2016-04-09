# reakt [![Build Status](https://travis-ci.org/advantageous/reakt.svg)](https://travis-ci.org/advantageous/reakt) [![Join the chat at https://gitter.im/advantageous/reakt](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/advantageous/reakt?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[Reakt website](http://advantageous.github.io/reakt)

Reactive interfaces for Java.

Reakt is reactive interfaces for Java: 
 * Promises, 
 * Streams, 
 * Callbacks, 
 * Async results
 
The emphasis is on defining interfaces that enable lambda expressions, 
and fluent APIs for asynchronous programming for Java.

Note: This mostly just provides the interfaces not the implementations. There are some starter implementations but the idea is that anyone can implement this. It is all about interfaces. There will be adapters for Vertx, RxJava, Reactive Streams, Guava Async Futures, etc.

#### Using from maven

```xml
<dependency>
    <groupId>io.advantageous</groupId>
    <artifactId>reakt</artifactId>
    <version>0.1.0</version>
</dependency>
```

#### Using from gradle
```xml
compile 'io.advantageous:reakt:0.1.0'
```

#### Fluent Promise API
```java

  Promise<Employee> promise = promise()
                .then(e -> saveEmployee(e))
                .catchError(error -> 
                     logger.error("Unable to lookup employee", error));

  employeeService.lookupEmployee(33, promise);
```

Or you can handle it in one line. 

#### Fluent Promise API example 2
```java


  employeeService.lookupEmployee(33, 
        promise().then(e -> saveEmployee(e))
                 .catchError(error -> logger.error(
                                           "Unable to lookup ", error))
        );
```


Promises are both a callback and a Result; however, you can work
with Callbacks directly. 

#### Using Result and callback directly
```java
        employeeService.lookupEmployee(33, result -> {
            result.then(e -> saveEmployee(e))
                  .catchError(error -> {
                    logger.error("Unable to lookup", error);
            });
        });
```

In both of these examples, lookupService would look like:

#### Using Result and callback directly
```java

   public void lookup(long employeeId, Callback<Employee> callback){...}

```


## Bridges

[Reakt Guava Bridge](http://advantageous.github.io/reakt-guava/) which 
allows libs that use Guava async support to now have a modern Java feel.


#### Cassandra Reakt example

```java

register(session.executeAsync("SELECT release_version FROM system.local"), 
  promise().thenRef(ref -> 
     gui.setMessage("Cassandra version is " +
         ref.get().one().getString("release_version"))
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

