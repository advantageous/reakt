# reakt
Reactive interfaces for Java.

Reactive interfaces for Java: 
 * Promises, 
 * Streams, 
 * Callbacks, 
 * Async results
 
The emphasis is on defining interfaces that enable lambda expressions, 
and fluent APIs for asynchronous programming for Java.


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



***QBit version 2*** is going to use ***Reakt***.
***Communikate***, a slimmed down fork of Vert.x, will also use ***Reakt***. 



