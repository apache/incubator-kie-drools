# Drools JMS Connector

Reactive connector that consumes messages from JMS queues and topics, deserializes them into typed facts, and feeds them into Drools Rule Unit `DataStream`s. Works with any JMS-compliant broker: Apache ActiveMQ, IBM MQ, RabbitMQ (via JMS), Apache Artemis, TIBCO EMS, etc.

## Maven Dependency

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-connector-jms</artifactId>
    <version>${drools.version}</version>
</dependency>

<!-- Add your broker's JMS client, e.g. ActiveMQ Artemis -->
<dependency>
    <groupId>org.apache.activemq</groupId>
    <artifactId>artemis-jakarta-client</artifactId>
    <version>${artemis.version}</version>
</dependency>
```

## Features

- Point-to-point queues and publish/subscribe topics
- JMS message selectors for server-side filtering
- Durable topic subscriptions (survive consumer disconnects)
- Transacted sessions with automatic commit/rollback
- Supports both `TextMessage` and `BytesMessage` payloads
- `ConnectionFactory` injection -- bring your own (via CDI, JNDI, or direct construction)

## Usage Examples

### Queue: Process Orders

```java
// 1. Define your fact type
public class Order {
    private String orderId;
    private double total;
    private String status;
    // getters, setters
}

// 2. Define your Rule Unit
public class OrderProcessingUnit implements RuleUnitData {
    private DataStream<Order> orders = DataSource.createStream();

    public DataStream<Order> getOrders() { return orders; }
}

// 3. Create a FactDeserializer
FactDeserializer<Order> deserializer = (dest, data) -> {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(data, Order.class);
};

// 4. Configure the JMS connector
JmsConnectorConfig config = JmsConnectorConfig.builder()
        .destinationName("order.processing.queue")
        .destinationType(JmsDestinationType.QUEUE)
        .firingStrategy(FiringStrategy.PER_MESSAGE)
        .pollTimeout(Duration.ofSeconds(1))
        .build();

// 5. Get a ConnectionFactory from your broker
ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

JmsReactiveConnector<Order> connector =
        new JmsReactiveConnector<>(config, deserializer, factory);

// 6. Start
OrderProcessingUnit unit = new OrderProcessingUnit();
RuleUnitInstance<OrderProcessingUnit> instance =
        RuleUnitProvider.get().createRuleUnitInstance(unit);

connector.start(unit.getOrders(), instance);
// Rules fire on every message from the queue

// 7. Shutdown
connector.close();
```

### Topic: Subscribe to Events with Filtering

```java
JmsConnectorConfig config = JmsConnectorConfig.builder()
        .destinationName("app.events")
        .destinationType(JmsDestinationType.TOPIC)
        .messageSelector("eventType = 'PAYMENT' AND amount > 1000")
        .firingStrategy(FiringStrategy.PER_MESSAGE)
        .build();

// Only messages matching the selector are delivered
```

### Durable Topic Subscription

```java
JmsConnectorConfig config = JmsConnectorConfig.builder()
        .destinationName("notifications")
        .destinationType(JmsDestinationType.TOPIC)
        .durableSubscription("my-durable-sub", "drools-client-1")
        .firingStrategy(FiringStrategy.PER_MESSAGE)
        .build();

// Subscription persists across disconnects; no messages lost
```

### Transacted Session (Commit After Rule Firing)

```java
JmsConnectorConfig config = JmsConnectorConfig.builder()
        .destinationName("critical.transactions")
        .destinationType(JmsDestinationType.QUEUE)
        .sessionTransacted(true)
        .firingStrategy(FiringStrategy.PER_MESSAGE)
        .build();

// Message is committed only after successful processing + rule firing
// On error: automatic rollback, message redelivered by broker
```

### Micro-Batch Mode

```java
JmsConnectorConfig config = JmsConnectorConfig.builder()
        .destinationName("bulk.events")
        .destinationType(JmsDestinationType.QUEUE)
        .firingStrategy(FiringStrategy.MICRO_BATCH)
        .batchSize(50)
        .pollTimeout(Duration.ofMillis(200))
        .build();

// Accumulates up to 50 messages, then fires rules once
```

## Configuration Reference

| Property | Default | Description |
|----------|---------|-------------|
| `destinationName` | (required) | JMS queue or topic name |
| `destinationType` | `QUEUE` | `QUEUE` (point-to-point) or `TOPIC` (pub/sub) |
| `messageSelector` | `null` | JMS selector expression for server-side filtering |
| `sessionTransacted` | `false` | Use transacted JMS sessions (commit/rollback) |
| `durableSubscriptionName` | `null` | Name for durable topic subscriptions (TOPIC only) |
| `clientId` | `null` | JMS client ID (required for durable subscriptions) |
| `firingStrategy` | `PER_MESSAGE` | When to fire rules |
| `pollTimeout` | `1000ms` | `MessageConsumer.receive()` timeout |
| `batchSize` | `100` | Max messages per micro-batch |

## Classes

| Class | Description |
|-------|-------------|
| `JmsReactiveConnector<T>` | Main connector; manages JMS connection, session, consumer, and polling thread |
| `JmsConnectorConfig` | JMS-specific configuration with builder |
| `JmsDestinationType` | Enum: `QUEUE` or `TOPIC` |
