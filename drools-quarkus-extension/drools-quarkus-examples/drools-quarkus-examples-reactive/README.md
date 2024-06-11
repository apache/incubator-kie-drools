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
### Start test Kafka instance via Docker Compose

There's a useful [docker-compose.yml](docker-compose.yml) in the root that starts a dedicated Kafka instance for quick tests.

Simply start it with this command from the root of the repo:

```
docker-compose up -d
```
Or, if your docker version supports `docker compose`,
```
docker compose up -d
```

To shutdown the instances.

```
docker-compose down
```
or
```
docker compose down
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