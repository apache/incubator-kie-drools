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
# Drools gRPC

`drools-grpc` exposes Drools rule evaluation over gRPC with both stateless and stateful APIs.

It provides:

- A standalone gRPC server (`DroolsGrpcServer`)
- A high-performance gRPC service (`DroolsRuleService`)
- Session pooling and stateful session lifecycle management
- Bidirectional streaming for real-time operations
- Optional TLS/mTLS, token auth, and in-process metrics
- Spring Boot and Quarkus auto-configuration

## Core types

- `org.drools.grpc.DroolsGrpcServer` - standalone server builder
- `org.drools.grpc.DroolsRuleServiceImpl` - gRPC service implementation
- `org.drools.grpc.session.SessionManager` - session pool and lifecycle
- `org.drools.grpc.util.FactConverter` - JSON <-> fact conversion

Proto service definition:

- `drools-grpc/src/main/proto/drools_rule_service.proto`

## Dependency setup (Maven)

```xml
<dependency>
  <groupId>org.drools</groupId>
  <artifactId>drools-grpc</artifactId>
  <version>${drools.version}</version>
</dependency>
```

## Standalone server

```java
import org.drools.grpc.DroolsGrpcServer;
import org.drools.grpc.security.AuthInterceptor;
import org.drools.grpc.security.TlsConfig;
import org.kie.api.KieBase;

KieBase defaultBase = ...;
KieBase fraudBase = ...;

DroolsGrpcServer server = DroolsGrpcServer.builder(defaultBase)
        .port(50051)
        .addKieBase("fraud", fraudBase)
        .sessionPoolSize(10)
        .enableReflection(true)
        .enableMetrics(true)
        .authInterceptor(AuthInterceptor.staticToken("my-secret"))
        .tlsConfig(TlsConfig.builder()
                .certChainFile(new File("server.crt"))
                .privateKeyFile(new File("server.key"))
                .build())
        .build();

server.start();
server.blockUntilShutdown();
```

Notes:

- The default KieBase is registered under an empty name (`""`).
- Clients route to a named KieBase by setting `kie_base_name` in requests.

## Stateless usage (Execute)

The `Execute` RPC inserts facts, fires rules, and returns results in one call.

Request fields:

- `kie_base_name` (optional)
- `facts` (typed JSON)
- `max_rules` (0 = fire all)

Result includes `rules_fired`, `result_facts`, and `rules_fired_names`.

## Stateful usage (session lifecycle)

Typical flow:

1. `CreateSession`
2. `InsertFact` / `UpdateFact` / `DeleteFact`
3. `FireAllRules`
4. `GetFacts` (optional)
5. `DisposeSession`

Facts are referenced by `fact_handle_id`, which is the Drools `FactHandle.toExternalForm()`.

## Bidirectional streaming

`StreamingSession` provides a long-lived, bidirectional channel for real-time operations.

Supported operations (via `StreamEventType`) include:

- Fact insert/update/delete
- Fire rules or `fireUntilHalt` / `halt`
- Query facts
- CEP entry point insert
- Batch insert
- Live query subscribe/unsubscribe
- Set/get globals
- Set agenda focus
- Heartbeat and session lifecycle

Server responses (`StreamResultType`) include:

- Operation acks with `sequence_id`
- Rule activation and rule firing events
- Facts inserted/updated/retracted by rules
- Live query result changes
- Heartbeat with session metrics

Each client event should set a `sequence_id` so responses can be correlated.

## Fact format

Facts are serialized as typed JSON:

```json
{
  "type": "com.example.Order",
  "json": "{\"id\":\"A-100\",\"total\":250.0}"
}
```

The server uses Jackson to deserialize/serialize facts based on the fully qualified class name.

## TLS and mTLS

Use `TlsConfig` with `DroolsGrpcServer.Builder`:

```java
TlsConfig tls = TlsConfig.builder()
        .certChainFile(new File("server.crt"))
        .privateKeyFile(new File("server.key"))
        .trustCertFile(new File("ca.crt")) // enables mTLS
        .clientAuth(TlsConfig.ClientAuthMode.REQUIRE)
        .build();
```

TLS requires the Netty server transport (used automatically when TLS is configured).

## Token authentication

Enable bearer token auth:

```java
builder.authInterceptor(AuthInterceptor.staticToken("my-secret"));
```

Clients must send `authorization: Bearer <token>` metadata.

## Metrics

Enable in-process metrics collection:

```java
builder.enableMetrics(true);
```

The `MetricsInterceptor` tracks per-method call counts, failures, and durations.
You can read a snapshot from `server.getMetricsInterceptor().getSnapshot()`.

## Spring Boot auto-configuration

`DroolsGrpcAutoConfiguration` wires up the server automatically when `KieBase` is present.

Configuration properties (prefix `drools.grpc.*`):

- `drools.grpc.port`
- `drools.grpc.session-pool-size`
- `drools.grpc.reflection-enabled`
- `drools.grpc.metrics-enabled`
- `drools.grpc.auth.enabled`
- `drools.grpc.auth.static-token`
- `drools.grpc.tls.enabled`
- `drools.grpc.tls.cert-chain-path`
- `drools.grpc.tls.private-key-path`
- `drools.grpc.tls.trust-cert-path`
- `drools.grpc.tls.client-auth` (`NONE`, `OPTIONAL`, `REQUIRE`)

## Quarkus integration

`DroolsGrpcQuarkusProducer` provides CDI producers and lifecycle hooks. It uses:

- `drools.grpc.port`
- `drools.grpc.session-pool-size`
- `drools.grpc.reflection-enabled`
- `drools.grpc.metrics-enabled`

## Practical notes

- Stateful sessions are pooled; `disposeSession` returns a session to the pool.
- `fireUntilHalt` runs in a daemon thread per session; always call `halt` or `disposeSession`.
- For streaming, use `CreateSession` or `CREATE_SESSION_EVENT` before other operations.
- For multi-KieBase routing, set `kie_base_name` on requests (empty = default).

