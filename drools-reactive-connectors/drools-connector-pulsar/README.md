# Drools Pulsar Connector

Reactive connector that consumes messages from Apache Pulsar topics, deserializes them into typed facts, and feeds them into Drools Rule Unit `DataStream`s for reactive rule evaluation.

## Maven Dependency

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-connector-pulsar</artifactId>
    <version>${drools.version}</version>
</dependency>
```

## Features

- Consumes from one or more Pulsar topics via batch receive
- Supports all Pulsar subscription types: Exclusive, Shared, Failover, Key_Shared
- Automatic message acknowledgment after processing
- Native pause/resume via Pulsar consumer API
- Built-in Pulsar client lifecycle management (auto-close on shutdown)
- Pluggable `FactDeserializer` for any serialization format

## Usage Example

### Basic Pulsar-to-Rules Pipeline

```java
// 1. Define your fact type
public class SensorReading {
    private String sensorId;
    private double temperature;
    private long timestamp;
    // getters, setters
}

// 2. Define your Rule Unit
public class TemperatureAlertUnit implements RuleUnitData {
    private DataStream<SensorReading> readings = DataSource.createStream();
    private DataStream<Alert> alerts = DataSource.createStream();

    public DataStream<SensorReading> getReadings() { return readings; }
    public DataStream<Alert> getAlerts() { return alerts; }
}

// 3. Create a FactDeserializer (JSON example)
FactDeserializer<SensorReading> deserializer = (topic, data) -> {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(data, SensorReading.class);
};

// 4. Configure the Pulsar connector
PulsarConnectorConfig config = PulsarConnectorConfig.builder()
        .serviceUrl("pulsar://localhost:6650")
        .topics("persistent://public/default/sensor-readings")
        .subscriptionName("temperature-rules")
        .subscriptionType(PulsarSubscriptionType.SHARED)
        .firingStrategy(FiringStrategy.PER_MESSAGE)
        .pollTimeout(Duration.ofSeconds(1))
        .build();

PulsarReactiveConnector<SensorReading> connector =
        new PulsarReactiveConnector<>(config, deserializer);

// 5. Create rule unit and start
TemperatureAlertUnit unit = new TemperatureAlertUnit();
RuleUnitInstance<TemperatureAlertUnit> instance =
        RuleUnitProvider.get().createRuleUnitInstance(unit);

connector.start(unit.getReadings(), instance);
// Rules fire on every sensor reading from Pulsar

// 6. Shutdown
connector.close();
instance.close();
```

### Shared Subscription (Load Balancing Across Consumers)

```java
PulsarConnectorConfig config = PulsarConnectorConfig.builder()
        .serviceUrl("pulsar://broker:6650")
        .topics("events")
        .subscriptionName("rule-workers")
        .subscriptionType(PulsarSubscriptionType.SHARED)
        .firingStrategy(FiringStrategy.MICRO_BATCH)
        .build();

// Multiple instances of this connector share the workload
```

### Key-Based Routing

```java
PulsarConnectorConfig config = PulsarConnectorConfig.builder()
        .serviceUrl("pulsar://broker:6650")
        .topics("orders")
        .subscriptionName("order-rules")
        .subscriptionType(PulsarSubscriptionType.KEY_SHARED)
        .build();

// Messages with the same key always go to the same consumer
```

### Failover Subscription (High Availability)

```java
PulsarConnectorConfig config = PulsarConnectorConfig.builder()
        .serviceUrl("pulsar://broker:6650")
        .topics("critical-events")
        .subscriptionName("ha-rules")
        .subscriptionType(PulsarSubscriptionType.FAILOVER)
        .build();

// One active consumer; automatic failover on disconnect
```

## Configuration Reference

| Property | Default | Description |
|----------|---------|-------------|
| `serviceUrl` | `pulsar://localhost:6650` | Pulsar broker service URL |
| `topics` | (required) | One or more topic names (fully qualified) |
| `subscriptionName` | `drools-reactive` | Subscription name |
| `subscriptionType` | `EXCLUSIVE` | `EXCLUSIVE`, `SHARED`, `FAILOVER`, or `KEY_SHARED` |
| `firingStrategy` | `PER_MESSAGE` | When to fire rules |
| `pollTimeout` | `1000ms` | Batch receive timeout |
| `batchSize` | `100` | Max records per micro-batch |

## Classes

| Class | Description |
|-------|-------------|
| `PulsarReactiveConnector<T>` | Main connector; manages Pulsar client, consumer, and polling thread |
| `PulsarConnectorConfig` | Pulsar-specific configuration with builder |
| `PulsarSubscriptionType` | Enum: `EXCLUSIVE`, `SHARED`, `FAILOVER`, `KEY_SHARED` |
