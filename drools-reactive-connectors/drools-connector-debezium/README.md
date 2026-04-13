# Drools Debezium Connector

Reactive connector that uses the embedded Debezium Engine to capture database change events (CDC) and feed them as typed facts into Drools Rule Unit `DataStream`s. Rules fire reactively on every INSERT, UPDATE, or DELETE in your database -- without requiring external Kafka infrastructure.

## Maven Dependency

```xml
<dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-connector-debezium</artifactId>
    <version>${drools.version}</version>
</dependency>

<!-- Add the Debezium connector for your database -->
<dependency>
    <groupId>io.debezium</groupId>
    <artifactId>debezium-connector-mysql</artifactId>
    <version>${debezium.version}</version>
</dependency>
```

Other available Debezium connectors: `debezium-connector-postgres`, `debezium-connector-mongodb`, `debezium-connector-sqlserver`, `debezium-connector-oracle`.

## Features

- Embedded Debezium Engine -- no external Kafka Connect required
- Typed `ChangeEvent<T>` wrapping the row data with CDC metadata (operation, table, source, timestamp)
- Supports all Debezium connectors (MySQL, PostgreSQL, MongoDB, SQL Server, Oracle)
- Automatic JSON envelope parsing (extracts `after`/`before` payloads)
- Operation types: `CREATE`, `UPDATE`, `DELETE`, `READ` (snapshot)
- Pluggable offset storage (file, memory, or custom)

## Usage Example

### React to Database Changes with Rules

```java
// 1. Define your row type (matches the DB table columns)
public class Order {
    private int id;
    private String customerId;
    private double amount;
    private String status;
    // getters, setters
}

// 2. Define a Rule Unit that receives change events
public class OrderAuditUnit implements RuleUnitData {
    private DataStream<ChangeEvent<Order>> orderChanges = DataSource.createStream();

    public DataStream<ChangeEvent<Order>> getOrderChanges() { return orderChanges; }
}

// 3. Write DRL rules against ChangeEvent
// rule "Flag large order updates"
//   when
//     $event : /orderChanges[ operation == ChangeEventOperation.UPDATE,
//                              value.amount > 10000 ]
//   then
//     // audit, alert, or compensate
// end

// 4. Configure Debezium to capture from MySQL
Properties dbzProps = new Properties();
dbzProps.setProperty("name", "order-cdc");
dbzProps.setProperty("connector.class", "io.debezium.connector.mysql.MySqlConnector");
dbzProps.setProperty("offset.storage",
        "org.apache.kafka.connect.storage.FileOffsetBackingStore");
dbzProps.setProperty("offset.storage.file.filename", "/tmp/offsets.dat");
dbzProps.setProperty("offset.flush.interval.ms", "10000");
dbzProps.setProperty("database.hostname", "localhost");
dbzProps.setProperty("database.port", "3306");
dbzProps.setProperty("database.user", "cdc_user");
dbzProps.setProperty("database.password", "secret");
dbzProps.setProperty("database.server.id", "1");
dbzProps.setProperty("topic.prefix", "myapp");
dbzProps.setProperty("schema.history.internal",
        "io.debezium.storage.file.history.FileSchemaHistory");
dbzProps.setProperty("schema.history.internal.file.filename", "/tmp/schema-history.dat");

DebeziumConnectorConfig config = DebeziumConnectorConfig.builder()
        .connectorName("order-cdc")
        .debeziumProperties(dbzProps)
        .firingStrategy(FiringStrategy.PER_MESSAGE)
        .build();

// 5. Create deserializer and connector
ChangeEventDeserializer<Order> deserializer = new ChangeEventDeserializer<>(Order.class);

DebeziumReactiveConnector<Order> connector =
        new DebeziumReactiveConnector<>(config, deserializer);

// 6. Create rule unit and start
OrderAuditUnit unit = new OrderAuditUnit();
RuleUnitInstance<OrderAuditUnit> instance =
        RuleUnitProvider.get().createRuleUnitInstance(unit);

connector.start(unit.getOrderChanges(), instance);
// Rules fire on every database change

// 7. Shutdown
connector.close();
instance.close();
```

### Using ChangeEvent in Rules

Each `ChangeEvent<T>` carries:

| Field | Type | Description |
|-------|------|-------------|
| `operation` | `ChangeEventOperation` | `CREATE`, `UPDATE`, `DELETE`, `READ`, `UNKNOWN` |
| `value` | `T` | Deserialized row data (`after` for create/update, `before` for delete) |
| `source` | `String` | Database/connector name |
| `table` | `String` | Table name |
| `timestamp` | `long` | Event timestamp in milliseconds |

### PostgreSQL Example

```java
Properties dbzProps = new Properties();
dbzProps.setProperty("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
dbzProps.setProperty("database.hostname", "localhost");
dbzProps.setProperty("database.port", "5432");
dbzProps.setProperty("database.user", "postgres");
dbzProps.setProperty("database.password", "postgres");
dbzProps.setProperty("database.dbname", "mydb");
dbzProps.setProperty("topic.prefix", "pg");
dbzProps.setProperty("plugin.name", "pgoutput");
// ... offset storage properties
```

## Configuration Reference

| Property | Default | Description |
|----------|---------|-------------|
| `connectorName` | `drools-cdc` | Logical name for this connector instance |
| `debeziumProperties` | (empty) | Full Debezium engine `Properties` |
| `firingStrategy` | `PER_MESSAGE` | When to fire rules |

## Classes

| Class | Description |
|-------|-------------|
| `DebeziumReactiveConnector<T>` | Wraps Debezium Engine; runs CDC in a daemon thread |
| `DebeziumConnectorConfig` | Config builder wrapping Debezium `Properties` |
| `ChangeEvent<T>` | Typed wrapper: operation + row value + metadata |
| `ChangeEventOperation` | Enum: `CREATE`, `UPDATE`, `DELETE`, `READ`, `UNKNOWN` |
| `ChangeEventDeserializer<T>` | Parses Debezium JSON envelope into `ChangeEvent<T>` |
