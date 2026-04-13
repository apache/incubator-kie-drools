# Drools Connector API

The Service Provider Interface (SPI) that all reactive connectors implement. This module defines the contracts for lifecycle management, health monitoring, deserialization, and firing strategy -- with zero dependencies on any specific messaging system.

## Maven Dependency

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-connector-api</artifactId>
    <version>${drools.version}</version>
</dependency>
```

## Interfaces and Classes

### `ReactiveConnector<T>`

The core interface that all connectors implement.

```java
public interface ReactiveConnector<T> extends AutoCloseable {
    void start(DataStream<T> target, RuleUnitInstance<?> ruleUnit);
    void start(DataStream<T> target);  // EXTERNAL firing mode
    void pause();
    void resume();
    ConnectorHealth health();
    ConnectorState getState();
    void close();
}
```

### `AbstractReactiveConnector<T>`

Base class that handles lifecycle state transitions, message counters, and firing strategy dispatch. Connector implementations extend this and implement only:

- `doStart()` -- transport-specific startup (create consumer, start polling thread)
- `doStop()` -- transport-specific shutdown (close consumer, release resources)
- `doPause()` / `doResume()` -- optional transport-specific pause/resume

### `FactDeserializer<T>`

Converts raw bytes from the messaging system into typed fact objects:

```java
public interface FactDeserializer<T> {
    T deserialize(String topic, byte[] data);
    default void configure(Map<String, Object> config) {}
    default void close() {}
}
```

### `ConnectorConfig`

Base configuration with builder pattern, inherited by all connector-specific configs:

```java
ConnectorConfig.Builder builder = new ConnectorConfig.Builder<>()
    .firingStrategy(FiringStrategy.PER_MESSAGE)  // when to fire rules
    .batchSize(100)                               // max records per micro-batch
    .batchWindow(Duration.ofMillis(500))          // micro-batch time window
    .pollTimeout(Duration.ofMillis(1000))         // consumer poll timeout
    .property("custom.key", "value");             // arbitrary properties
```

### `FiringStrategy`

Controls when rules fire after fact ingestion:

| Value | Behavior |
|-------|----------|
| `PER_MESSAGE` | `ruleUnitInstance.fire()` after every fact |
| `MICRO_BATCH` | `ruleUnitInstance.fire()` once per poll batch |
| `EXTERNAL` | No automatic firing; caller manages it |

### `ConnectorState`

Lifecycle states: `CREATED` -> `STARTING` -> `RUNNING` -> `PAUSED` -> `STOPPING` -> `STOPPED` -> `FAILED`

### `ConnectorHealth`

Immutable health snapshot with `state`, `messagesReceived`, `messagesProcessed`, `messagesFailed`, `lastError`, and an extensible `details` map.

### `ConnectorException`

Runtime exception for unrecoverable connector errors.

## Implementing a Custom Connector

```java
public class MyCustomConnector<T> extends AbstractReactiveConnector<T> {

    public MyCustomConnector(ConnectorConfig config, FactDeserializer<T> deserializer) {
        super(config, deserializer);
    }

    @Override
    protected void doStart() {
        // Create your consumer, start your polling thread
        // Use processSingle(fact) or processBatch(list) to feed facts
        // Use recordReceived(), recordProcessed(), recordFailed() for metrics
    }

    @Override
    protected void doStop() {
        // Close consumer, release resources
    }
}
```
