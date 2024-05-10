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

# Common Utils For Kogito

Add the needed utils dependency in the _pom.xml_ file:

### Framework agnostic
```xml
<dependency>
  <groupId>org.kie.kogito</groupId>
  <artifactId>kogito-test-utils</artifactId>
  <scope>test</scope>
</dependency>
```

### Quarkus
```xml
<dependency>
  <groupId>org.kie.kogito</groupId>
  <artifactId>kogito-quarkus-test-utils</artifactId>
  <scope>test</scope>
</dependency>
```

### Spring Boot
```xml
<dependency>
  <groupId>org.kie.kogito</groupId>
  <artifactId>kogito-spring-boot-test-utils</artifactId>
  <scope>test</scope>
</dependency>
```

## Infinispan Test Containers Support

### Usage in a Quarkus test:

Example:

```java
@QuarkusTest
@QuarkusTestResource(InfinispanQuarkusTestResource.class)
public class MyTest {
   // ...
}
```

The property _quarkus.infinispan-client.hosts_ will be automatically populated with a random port.

In case we want to run the container only if some requirements are met, we need to use it this way:

```java
@QuarkusTestResource(value = InfinispanQuarkusTestResource.Conditional.class)
```

### Usage in Spring Boot test:


```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ContextConfiguration(initializers = InfinispanSpringBootTestResource.class)
public class MyTest {    
    // ...
}
```

And add the Infinispan properties in the _application.properties_:

```
# Infinispan
infinispan.remote.sasl-mechanism=PLAIN
infinispan.remote.auth-server-name=infinispan
infinispan.remote.use-auth=true
infinispan.remote.auth-realm=default
infinispan.remote.auth-username=admin
infinispan.remote.auth-password=admin
```

The property _infinispan.remote.server-list_ will be automatically populated with a random port.

In case we want to run the container only if some requirements are met, we need to use it this way:

```java

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ContextConfiguration(initializers = InfinispanSpringBootTestResource.Conditional.class)
public class MyTest {    
    // ...
}
```

## Keycloak Test Containers Support

### Usage in a Quarkus test:

Example:

```java
@QuarkusTest
@QuarkusTestResource(KeycloakQuarkusTestResource.class)
public class MyTest {
   // ...
}
```

### Usage in a Spring Boot test:

Example:

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ContextConfiguration(initializers = KeycloakSpringBootTestResource.class)
public class MyTest {
    // ...
}
```

## Kafka Test Containers Support

### Usage in a Quarkus test:

Example:

```java
@QuarkusTest
@QuarkusTestResource(KafkaQuarkusTestResource.class)
public class MyTest {
   
   @ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
   private String kafkaBootstrapServers;
   // ...
}
```

### Usage in a Spring Boot test:

Example:

```java
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = DemoApplication.class)
@ContextConfiguration(initializers = KafkaSpringBootTestResource.class)
public class MyTest {
    // ...
}
```

## Kafka Client

Add the Kafka Client dependency in the _pom.xml_ file:

```xml
<dependency>
  <groupId>org.apache.kafka</groupId>
  <artifactId>kafka-clients</artifactId>
  <scope>test</scope>
</dependency>
```

And make use of it:

- In Spring:

```java
@Autowired
private KafkaTestClient kafkaClient;
``` 

- In Kafka:

```java
@ConfigProperty(name = KafkaQuarkusTestResource.KOGITO_KAFKA_PROPERTY)
private String kafkaBootstrapServers;

@Test
public void myTest() {
    KafkaClient kafkaClient = new KafkaClient(kafkaBootstrapServers);
    // ...
}
```
