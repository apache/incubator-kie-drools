# Drools Quarkus Connector Integration

Quarkus CDI integration for Drools reactive connectors. Provides lifecycle management, configuration binding from `application.properties`, and declarative connector wiring via annotations.

## Maven Dependency

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-connector-quarkus</artifactId>
    <version>${drools.version}</version>
</dependency>

<!-- Add the connector(s) you need -->
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-connector-kafka</artifactId>
    <version>${drools.version}</version>
</dependency>
```

## Features

- `ReactiveConnectorManager` -- `@ApplicationScoped` CDI bean for registering, starting, pausing, and stopping connectors
- Automatic shutdown of all connectors via `@PreDestroy` when the Quarkus app stops
- `ReactiveConnectorConfig` -- SmallRye Config mapping for `drools.connector.*` properties
- `ReactiveConnectorProducer` -- CDI producer that builds typed configs from application properties
- `@ConnectTo` annotation -- CDI qualifier for declarative connector binding

## Usage Examples

### Programmatic Registration with ReactiveConnectorManager

```java
@ApplicationScoped
@Startup
public class MyRuleApp {

    @Inject
    ReactiveConnectorManager connectorManager;

    @Inject
    RuleUnit<FraudDetectionUnit> ruleUnit;

    @PostConstruct
    void init() {
        // Build connector config
        KafkaConnectorConfig config = KafkaConnectorConfig.builder()
                .bootstrapServers("localhost:9092")
                .topics("transactions")
                .groupId("fraud-rules")
                .firingStrategy(FiringStrategy.PER_MESSAGE)
                .build();

        // Create and register the connector
        KafkaReactiveConnector<Transaction> connector =
                new KafkaReactiveConnector<>(config,
                        new JsonFactDeserializer<>(Transaction.class));

        connectorManager.register("transactions", connector);

        // Create rule unit and start the connector
        FraudDetectionUnit unit = new FraudDetectionUnit();
        RuleUnitInstance<FraudDetectionUnit> instance =
                ruleUnit.createInstance(unit);

        connectorManager.start("transactions", unit.getTransactions(), instance);
    }
}

// Connectors are automatically shut down when the application stops
```

### Health Monitoring Endpoint

```java
@Path("/connectors")
@ApplicationScoped
public class ConnectorHealthResource {

    @Inject
    ReactiveConnectorManager connectorManager;

    @GET
    @Path("/{name}/health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response health(@PathParam("name") String name) {
        ConnectorHealth health = connectorManager.health(name);
        return Response.ok(Map.of(
                "state", health.getState(),
                "received", health.getMessagesReceived(),
                "processed", health.getMessagesProcessed(),
                "failed", health.getMessagesFailed()
        )).build();
    }

    @POST
    @Path("/{name}/pause")
    public Response pause(@PathParam("name") String name) {
        connectorManager.pause(name);
        return Response.ok().build();
    }

    @POST
    @Path("/{name}/resume")
    public Response resume(@PathParam("name") String name) {
        connectorManager.resume(name);
        return Response.ok().build();
    }
}
```

### Configuration-Driven Setup via application.properties

```properties
# Kafka connector named "transactions"
drools.connector.transactions.type=kafka
drools.connector.transactions.topics=payment-events
drools.connector.transactions.bootstrap-servers=broker:9092
drools.connector.transactions.group-id=fraud-rules
drools.connector.transactions.firing-strategy=per-message
drools.connector.transactions.batch-size=100
drools.connector.transactions.poll-timeout=1s
```

```java
@ApplicationScoped
public class ConfigDrivenSetup {

    @Inject
    ReactiveConnectorProducer producer;

    @Inject
    ReactiveConnectorConfig config;

    @PostConstruct
    void init() {
        // Build a typed config from application.properties
        ReactiveConnectorConfig.ConnectorInstanceConfig instanceConfig =
                config.connectors().get("transactions");

        KafkaConnectorConfig kafkaConfig = producer.buildKafkaConfig(instanceConfig);
        // Use kafkaConfig to create the connector...
    }
}
```

### @ConnectTo Annotation (Declarative Binding)

```java
public class FraudDetectionUnit implements RuleUnitData {

    @ConnectTo(connector = ConnectorType.KAFKA, name = "transactions")
    private DataStream<Transaction> transactions;

    // The connector framework can use this annotation
    // to auto-wire the DataStream at startup
}
```

## ReactiveConnectorManager API

| Method | Description |
|--------|-------------|
| `register(name, connector)` | Register a connector by logical name |
| `start(name, dataStream, ruleUnit)` | Start a registered connector, binding it to a DataStream |
| `start(name, dataStream)` | Start in EXTERNAL firing mode |
| `pause(name)` | Pause consumption |
| `resume(name)` | Resume consumption |
| `health(name)` | Get health snapshot |
| `stop(name)` | Stop and unregister a specific connector |
| `getRegisteredNames()` | List all registered connector names |
| `shutdown()` | Stop all connectors (called automatically via `@PreDestroy`) |

## Configuration Reference (application.properties)

All properties use the prefix `drools.connector.<name>.`:

| Property | Default | Description |
|----------|---------|-------------|
| `type` | `kafka` | Connector type: `kafka`, `pulsar`, `debezium` |
| `topics` | (required for kafka/pulsar) | Comma-separated topic names |
| `bootstrap-servers` | `localhost:9092` | Kafka brokers or Pulsar service URL |
| `group-id` | `drools-reactive` | Consumer group / subscription name |
| `firing-strategy` | `per-message` | `per-message`, `micro-batch`, or `external` |
| `batch-size` | `100` | Max records per micro-batch |
| `poll-timeout` | `1s` | Consumer poll timeout |
| `subscription-type` | `exclusive` | Pulsar only: `exclusive`, `shared`, `failover`, `key-shared` |
| `connector-class` | (optional) | Debezium only: connector class name |

## Classes

| Class | Description |
|-------|-------------|
| `ReactiveConnectorManager` | `@ApplicationScoped` CDI bean for connector lifecycle |
| `ReactiveConnectorProducer` | Builds typed configs from Quarkus application properties |
| `ReactiveConnectorConfig` | SmallRye `@ConfigMapping` for `drools.connector.*` |
| `ConnectTo` | CDI `@Qualifier` annotation for declarative binding |
| `ConnectorType` | Constants: `KAFKA`, `PULSAR`, `DEBEZIUM` |
