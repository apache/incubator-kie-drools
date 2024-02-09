### Start test Kafka instance via Docker Compose

There's a useful [docker-compose.yml](docker-compose.yml) in the root that starts a dedicated Kafka instance for quick tests.

Simply start it with this command from the root of the repo:

```
docker-compose up -d
```

To shutdown the instances.

```
docker-compose down
```

### Package and Run in JVM mode

```
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

To stop the quarkus process, press Ctrl+C

## Test with Kafka

Send Event with JSON format to "events" topic.

```sh
echo '{"type":"temperature","value":35}' | kafka-console-producer.sh --broker-list localhost:9092 --topic events
```

You will see the result in "alerts" topic via Kafdrop ( http://localhost:9000 ).

```json
{"severity":"warning","message":"Event [type=temperature, value=35]"}
```

Note that this example doesn't expose REST endpoints.