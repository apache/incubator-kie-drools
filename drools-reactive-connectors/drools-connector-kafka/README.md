# Drools Kafka Connector

Reactive connector that consumes messages from Apache Kafka topics, deserializes them into typed facts, and feeds them into Drools Rule Unit `DataStream`s for reactive rule evaluation.

## Maven Dependency

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-connector-kafka</artifactId>
    <version>${drools.version}</version>
</dependency>
```

## Features

- Consumes from one or more Kafka topics
- Configurable consumer group, offset management, and auto-commit
- Built-in `JsonFactDeserializer` (Jackson-based) or bring your own `FactDeserializer`
- Three firing strategies: `PER_MESSAGE`, `MICRO_BATCH`, `EXTERNAL`
- Synchronous offset commits after processing (configurable)
- Daemon polling thread with graceful shutdown via `consumer.wakeup()`

## Usage Example

### Basic Kafka-to-Rules Pipeline

```java
// 1. Define your fact type
public class StockTick {
    private String symbol;
    private double price;
    private long timestamp;
    // getters, setters
}

// 2. Define your Rule Unit
public class StockMonitorUnit implements RuleUnitData {
    private DataStream<StockTick> ticks = DataSource.createStream();

    public DataStream<StockTick> getTicks() { return ticks; }
    public void setTicks(DataStream<StockTick> ticks) { this.ticks = ticks; }
}

// 3. Configure and start the connector
KafkaConnectorConfig config = KafkaConnectorConfig.builder()
        .bootstrapServers("broker1:9092,broker2:9092")
        .topics("stock-ticks")
        .groupId("stock-monitor-rules")
        .firingStrategy(FiringStrategy.PER_MESSAGE)
        .pollTimeout(Duration.ofMillis(500))
        .build();

JsonFactDeserializer<StockTick> deserializer = new JsonFactDeserializer<>(StockTick.class);

KafkaReactiveConnector<StockTick> connector =
        new KafkaReactiveConnector<>(config, deserializer);

// 4. Create rule unit and start
StockMonitorUnit unit = new StockMonitorUnit();
RuleUnitInstance<StockMonitorUnit> instance =
        RuleUnitProvider.get().createRuleUnitInstance(unit);

connector.start(unit.getTicks(), instance);
// Rules fire automatically on every tick from Kafka

// 5. Monitor
ConnectorHealth health = connector.health();
System.out.printf("Received: %d, Processed: %d, Failed: %d%n",
        health.getMessagesReceived(),
        health.getMessagesProcessed(),
        health.getMessagesFailed());

// 6. Shutdown
connector.close();
instance.close();
```

### Micro-Batch Mode (Higher Throughput)

```java
KafkaConnectorConfig config = KafkaConnectorConfig.builder()
        .bootstrapServers("localhost:9092")
        .topics("events")
        .groupId("batch-processor")
        .firingStrategy(FiringStrategy.MICRO_BATCH)
        .batchSize(200)
        .pollTimeout(Duration.ofSeconds(1))
        .build();
```

### External Firing (Daemon Mode)

```java
KafkaConnectorConfig config = KafkaConnectorConfig.builder()
        .bootstrapServers("localhost:9092")
        .topics("events")
        .groupId("daemon-processor")
        .firingStrategy(FiringStrategy.EXTERNAL)
        .build();

connector.start(unit.getEvents());  // only inserts, no auto-fire

// Fire continuously in a separate thread
executor.submit(() -> instance.fire());  // or fireUntilHalt() on classic KieSession
```

### Multiple Topics

```java
KafkaConnectorConfig config = KafkaConnectorConfig.builder()
        .bootstrapServers("localhost:9092")
        .topics("orders", "payments", "refunds")
        .groupId("order-rules")
        .build();
```

### Custom Consumer Properties

```java
KafkaConnectorConfig config = KafkaConnectorConfig.builder()
        .bootstrapServers("localhost:9092")
        .topics("secure-topic")
        .groupId("secured")
        .autoCommit(false)
        .property("security.protocol", "SSL")
        .property("ssl.truststore.location", "/path/to/truststore.jks")
        .property("max.poll.records", "500")
        .build();
```

## Configuration Reference

| Property | Default | Description |
|----------|---------|-------------|
| `bootstrapServers` | `localhost:9092` | Kafka broker addresses |
| `topics` | (required) | One or more topic names |
| `groupId` | `drools-reactive` | Consumer group ID |
| `autoCommit` | `false` | Enable Kafka auto-commit (manual sync commit if false) |
| `firingStrategy` | `PER_MESSAGE` | When to fire rules |
| `pollTimeout` | `1000ms` | `KafkaConsumer.poll()` timeout |
| `batchSize` | `100` | Max records per micro-batch |
| `properties` | (empty) | Additional Kafka consumer properties |

## Classes

| Class | Description |
|-------|-------------|
| `KafkaReactiveConnector<T>` | Main connector; manages Kafka consumer and polling thread |
| `KafkaConnectorConfig` | Kafka-specific configuration with builder |
| `JsonFactDeserializer<T>` | Jackson-based JSON byte[] to fact deserializer |
