<!--
  Licensed to the Apache Software Foundation (ASF) under one
  or more contributor license agreements.  See the NOTICE file
  distributed with this work for additional information
  regarding copyright ownership.  The ASF licenses this file
  to you under the Apache License, Version 2.0 (the
  "License"); you may not use this file except in compliance
  with the License.  You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing,
  software distributed under the License is distributed on an
  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  KIND, either express or implied.  See the License for the
  specific language governing permissions and limitations
  under the License.
  -->

# Incubation API

This readme documents the design of the `common` component and the `application` component.

## General Expected Usage Example

Every component should provide one or more service implementations. Each service takes an identifier 
(a Path from a root) and usually a `DataContext`. The generic form of interaction should follow the pattern:


```java
   LocalId id = appRoot.get(MyComponent.class).get("componentName").subs().get("someSubComponent")
   DataContext result = svc.evaluationMethod(id, dataContext);

```

for instance, evaluating a DMN may look like this:

```java
   LocalDecisionId decisionId = appRoot.get(DecisionIds.class).get(namespaceString, nameString);
   DataContext result = svc.evaluate(decisionId, dataContext);

```

When a component admits subcomponents (e.g. tasks in processes, decision services in DMN, queries in rules, etc.),
then there should be a way to construct that path with a fluent API:

```java
   LocalDecisionId decisionServiceId = appRoot.get(DecisionIds.class).get(namespaceString, nameString).services().get(serviceString);
   DataContext result = svc.evaluate(decisionServiceId, dataContext);

```


If the types make sense, then a single `evaluate` method should be able to "parse" the provided ID, 
even for different paths; i.e. it is perfectly acceptable, even preferrable:

```java
   DataContext result = svc.evaluate(serviceId, inputDataContext);
   DataContext serviceResult = svc.evaluate(decisionServiceId, inputDataContext);

``` 

However, component developers are free to provide their own methods; multiple methods 
should be returned, especially if they represent different actions; e.g.:


```java
   LocalProcessInstanceId lpid = svc.createProcess(processId, inputDataContext);
   DataContext started = svc.start(lpid);
   DataContext aborted = svc.abort(lpid);
``` 



## Application and Paths

The `application` component provides the root of the typed builder API for paths (`appRoot`);

i.e. paths such as `/processes/my.process` or `/predictions/my.prediction` are constructed 
via `appRoot.get(ProcessIds.class).get("my.process")` or `appRoot.get(PredictionIds.class).get("my.prediction")` respectively. 
The return type of this chain of methods is always an `Id` or a `LocalId`.

The `<Component>Ids` class should implement `ComponentRoot`.

paths admit subpaths if the Component admits subcomponents; e.g. a DMN admits services, so the path

     /decisions/$id/services/$serviceId

should be allowed. The method chain should reflect the structure of that path:

    appRoot.get(DecisionIds.class).get(namespace, name).services().get(serviceId)


A `RemoteId` is also available to "install" the path on a hostname:port pair. This is not useful at the moment, 
it will be useful in a distributed setting.

In the future such a path will be also parsable starting from a String (e.g. `LocalId.parse(String)`)

Because of their construction, paths can be always rendered as a string, and can always be parsed from a string into a 
typed representation (the `/prefix` and intermediated parts (such as `/services` specify the subtype). 
This makes them trivially serializable for sending over the wire and trivially unserializable into their typed representation.



## Main API (`common`)

The `common` module provides basic interfaces so that each component may specialize one or more `LocalId` 
definitions and provide one or more service implementation.

Every component provides a pair of modules:

- `kogito-api-incubation-<component>`: Paths and Identifiers
- `kogito-api-incubation-<component>-services`: Service Interfaces


### Paths and Identifiers

The first module contains specializations of `LocalId` (e.g. `DecisionId`) and/or subpaths. For instance:

- a "decision service ID" should be addressable, and that service Id is a subcomponent of a decision. 
  Then `DecisionId` should expose the method `services()` and `appRoot.get(DecisionIds.class).get("my.decision").services().get("my.service.id")` 
  should be the path constructor; it would create a `DecisionServiceId`

### DataContext

- A `DataContext` is a computational context. It contains all the data that is required by a service to perform its computation.
- A `DataContext` can be always converted into another type (or another DataContext) using the method `DataContext#as(Class<T extends DataContext>)`.

A DataContext can be always converted into another sub-type of DataContext. Converting a DataContext 
into the same DataContext is a no-op just like (Object) new Object() is a no-op. You are always able to convert 
a DataContext into an arbitrary sub-type; e.g. a MapDataContext into a POJO that implements DataContext.

```java
class Person implements DataContext, DefaultCastable { String name; }
MapDataContext ctx = MapDataContext.create();
ctx.set("name", "Paul");
Person p = ctx.as(Person.class);
String name = p.name; // "Paul"
```

`MapDataContext` provides methods `get(), set()` and wraps a map. This is the simplest and convenient way to pass data. 
However, it is suggested to create your own class. In this case your POJO should extends `DataContext` and `DefaultCastable` 
(the latter provides a default implementation of the `as()` method that is usually good for most cases)

Note: `DefaultCastable#as` uses Jackson internally, but it's an implementation detail and it may change in the future if we decide so.

### Service Interfaces

Service interfaces should follow the generic pattern :

    RETURN-TYPE VERB(IDENTIFIER [, optional DATACONTEXT])

e.g.


```java
   LocalId id = appRoot.get(MyComponent.class).get("componentName").subs().get("someSubComponent")
   DataContext result = svc.evaluationMethod(id, dataContext);

```

- Service interfaces do not extend a specific interface; component developers may define their own methods,  
  although a certain degree of consistency is expected.
- **RETURN-TYPE** should be always either an IDentifier or a DataContext. Implementations should usually take 
  the most generic implementation `DataContext` as a parameter, and should usually return `DataContext` as a return type. 
  It is allowed (as per Java spec) to return a subtype of DataContext, but this may not be required, as users can
  always convert into a more specific type.
- When a method *evaluates* the resource pointed by the IDENTIFIER and **completes** (i.e. stateless evaluation), 
  then RETURN-TYPE should always be DataContext.
- When a method *starts the evaluation* of the resource pointed by the IDENTIFIER but it may not **complete**, 
  then RETURN-TYPE may be an identifier.  (e.g. `processService.create(id)` would return a `processInstanceId`)


Asyncronous evaluation is allowed. In this case RETURN-TYPE should express the fact that evaluation is async. For instance:

```java
   LocalId id = appRoot.get(MyComponent.class).get("componentName").subs().get("someSubComponent")
   CompletableFuture<DataContext> result = asyncSvc.evaluationMethod(id, dataContext);

```

i.e. a Process engine may provide `AsyncProcessService`

```java
   CompletableFuture<LocalProcessInstanceId> futureLpid = asyncSvc.createProcess(processId, inputDataContext);
   CompletableFuture<DataContext> futureStarted = futureLpid.andThen(lpid -> asyncSvc.start(lpid));
   ...
``` 

## Implementation

Currently only for Quarkus. The implementation is being shipped as part of the extension for convenience 
(it may be refactored to its own extension in the future). Implementation should provide:

- injectable version of their component class -- used for wiring in `appRoot.get(MyComponent.class)`
- injectable versions of their services

Currently services delegate their implementation to the internal hidden API that is used for codegen. 
The internal API should be considered deprecated and in the future codegen should be moved to the new API.

Refer to the related PRs for more details.

## Caveats

### What about listeners?

Currently listeners are plugged through annotation as we currently do already in Kogito. Since internally the APIs that 
we use in codegen are being used, those will work automatically.

### What about metadata?

The API only deals with data, but what about meta-data? e.g. directives that should  
be passed to the engine, that are not part of the Payload? (e.g. HTTP headers that should be passed to the engine 
to configure a specific request)

  ```java
     var result = svc.evaluate(id, payload [ metadata ???] ) 
  ```

  This is currently under investigation.
