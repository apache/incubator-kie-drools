# Kogito runtime events publisher with MongoDB

This MongoDB events publisher is created to support strong consistency for Kogito process data and Kogito runtime events.

Kogito process data and runtime events generated in the same unit of work can be saved to MongoDB in the same transaction with this publisher.

Once the events are inserted to MongoDB, they will be deleted immediately. Then Debezium will read the events from the MongoDB operation log, and publish to Kafka.

This MongoDB events publisher is not supposed to be enabled with other Kafka events publishers.

To enable this events publisher, make sure dependency for the Kafka events publisher is not added:

1. Quarkus:
```xml
<dependency>
  <groupId>org.kie.kogito</groupId>
  <artifactId>kogito-addons-quarkus-events-smallrye</artifactId>
</dependency>
```

2. Springboot:
```xml
<dependency>
  <groupId>org.kie</groupId>
  <artifactId>kie-addons-springboot-events-kafka</artifactId>
</dependency>
```

Then add the following dependency for MongoDB events publisher:

1. Quarkus:
```xml
<dependency>
  <groupId>org.kie</groupId>
  <artifactId>kie-addons-quarkus-events-mongodb</artifactId>
</dependency>
```

2. Springboot:
```xml
<dependency>
  <groupId>org.kie</groupId>
  <artifactId>kie-addons-springboot-events-mongodb</artifactId>
</dependency>
```