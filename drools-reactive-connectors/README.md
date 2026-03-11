# Drools Reactive Connectors

Reactive data source connectors that bridge external streaming systems with Drools Rule Unit `DataStream`s. Facts are automatically ingested from message brokers, event streams, and database change feeds, enabling rules to fire reactively as data arrives.

## Architecture

```
                          ┌─────────────────────────────────────────────┐
                          │           Drools Rule Session               │
                          │                                             │
  External System         │   DataStream ──► EntryPoint ──► Rete Network│
  ┌──────────┐  poll/     │       ▲                            │        │
  │  Kafka   │──consume──►│       │                       fire rules    │
  │  Pulsar  │            │  FactDeserializer                  │        │
  │  DB CDC  │  bytes ──► │  (bytes → typed fact)         consequences  │
  │  JMS MQ  │            │                                             │
  └──────────┘            └─────────────────────────────────────────────┘
       │                         │
       │                    ReactiveConnector
       │                    (lifecycle, health, firing strategy)
       │
  ┌────┴──────────────────────────────────────────────────┐
  │  drools-connector-api   (SPI interfaces)              │
  │  drools-connector-kafka (Apache Kafka)                │
  │  drools-connector-pulsar(Apache Pulsar)               │
  │  drools-connector-debezium (Database CDC)             │
  │  drools-connector-jms   (JMS Message Queues)          │
  │  drools-connector-quarkus (Quarkus CDI integration)   │
  └───────────────────────────────────────────────────────┘
```

## Modules

| Module | Artifact | Description |
|--------|----------|-------------|
| [drools-connector-api](drools-connector-api/) | `org.drools:drools-connector-api` | SPI interfaces: `ReactiveConnector`, `FactDeserializer`, `ConnectorConfig`, lifecycle, health |
| [drools-connector-kafka](drools-connector-kafka/) | `org.drools:drools-connector-kafka` | Apache Kafka consumer connector |
| [drools-connector-pulsar](drools-connector-pulsar/) | `org.drools:drools-connector-pulsar` | Apache Pulsar consumer connector |
| [drools-connector-debezium](drools-connector-debezium/) | `org.drools:drools-connector-debezium` | Database CDC via embedded Debezium Engine |
| [drools-connector-jms](drools-connector-jms/) | `org.drools:drools-connector-jms` | JMS queues/topics (ActiveMQ, IBM MQ, RabbitMQ, etc.) |
| [drools-connector-quarkus](drools-connector-quarkus/) | `org.drools:drools-connector-quarkus` | Quarkus CDI lifecycle management and config binding |

## Quick Start

### 1. Add the dependency for your messaging system

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-connector-kafka</artifactId>
    <version>${drools.version}</version>
</dependency>
```

### 2. Define your fact type and a Rule Unit

```java
// Fact type
public class Transaction {
    private String accountId;
    private double amount;
    // getters, setters
}

// Rule Unit
public class FraudDetectionUnit implements RuleUnitData {
    private DataStream<Transaction> transactions = DataSource.createStream();
    private DataStream<Alert> alerts = DataSource.createStream();
    // getters, setters
}
```

### 3. Create and start the connector

```java
// Configure
KafkaConnectorConfig config = KafkaConnectorConfig.builder()
        .bootstrapServers("localhost:9092")
        .topics("payment-transactions")
        .groupId("fraud-detection")
        .firingStrategy(FiringStrategy.PER_MESSAGE)
        .build();

// Create connector with JSON deserialization
KafkaReactiveConnector<Transaction> connector = new KafkaReactiveConnector<>(
        config, new JsonFactDeserializer<>(Transaction.class));

// Create rule unit instance
FraudDetectionUnit unit = new FraudDetectionUnit();
RuleUnitInstance<FraudDetectionUnit> instance =
        RuleUnitProvider.get().createRuleUnitInstance(unit);

// Start: facts flow from Kafka → DataStream → rules fire automatically
connector.start(unit.getTransactions(), instance);

// Monitor
ConnectorHealth health = connector.health();
System.out.println("Processed: " + health.getMessagesProcessed());

// Shutdown
connector.close();
```

## Key Concepts

### Firing Strategies

| Strategy | Behavior | Use Case |
|----------|----------|----------|
| `PER_MESSAGE` | Fire rules after each fact is appended | Low-latency alerting, fraud detection |
| `MICRO_BATCH` | Accumulate a poll batch, then fire once | Higher throughput, batch correlation |
| `EXTERNAL` | Only insert facts; caller manages firing | Custom scheduling, `fireUntilHalt()` daemon |

### Connector Lifecycle

```
CREATED ──► STARTING ──► RUNNING ──► STOPPING ──► STOPPED
                           │   ▲
                           ▼   │
                         PAUSED
```

All connectors follow the same lifecycle:
- `start(dataStream, ruleUnitInstance)` -- begin consuming
- `pause()` / `resume()` -- temporarily halt/resume consumption
- `health()` -- get message counts and error state
- `close()` -- graceful shutdown

### Health Monitoring

Every connector exposes a `ConnectorHealth` snapshot:

```java
ConnectorHealth health = connector.health();
health.getState();             // RUNNING, PAUSED, STOPPED, FAILED
health.getMessagesReceived();  // total messages polled
health.getMessagesProcessed(); // successfully deserialized and appended
health.getMessagesFailed();    // deserialization or processing errors
health.getLastError();         // most recent exception, if any
```

### Custom Deserialization

Implement `FactDeserializer<T>` for any serialization format:

```java
public class AvroTransactionDeserializer implements FactDeserializer<Transaction> {
    @Override
    public void configure(Map<String, Object> config) {
        // initialize schema registry client, etc.
    }

    @Override
    public Transaction deserialize(String topic, byte[] data) {
        // Avro deserialization logic
        return transaction;
    }
}
```

A built-in `JsonFactDeserializer<T>` using Jackson is provided in the Kafka module.

## Building

```bash
# Build all connector modules
mvn clean install -pl drools-reactive-connectors -am

# Run tests only
mvn test -pl drools-reactive-connectors/drools-connector-kafka,drools-reactive-connectors/drools-connector-pulsar,drools-reactive-connectors/drools-connector-debezium,drools-reactive-connectors/drools-connector-jms,drools-reactive-connectors/drools-connector-quarkus
```

## Related

- [Drools Rule Units API](../drools-ruleunits/) -- `DataSource`, `DataStream`, `DataProcessor`
- [Quarkus Reactive Example](../drools-quarkus-extension/drools-quarkus-examples/drools-quarkus-examples-reactive/) -- existing MicroProfile Reactive Messaging example
