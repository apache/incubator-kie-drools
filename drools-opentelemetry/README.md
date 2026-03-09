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
# Drools OpenTelemetry

`drools-opentelemetry` instruments Drools rule execution with OpenTelemetry traces and metrics by attaching Drools event listeners to a session.

It currently provides:

- Rule-firing spans (per fired rule)
- Match and fact lifecycle span events
- Rule execution counters and duration histogram

## What this module instruments

When instrumentation is enabled, the module registers these listeners:

- `TracingAgendaEventListener`
- `TracingRuleRuntimeEventListener`
- `MetricsAgendaEventListener`

The entry point is:

- `org.drools.opentelemetry.DroolsOpenTelemetry`

Supported target type:

- Any Drools session implementing `org.kie.api.event.rule.RuleRuntimeEventManager` (for example a stateful `KieSession`)

## Dependency setup (Maven)

At minimum, add:

```xml
<dependency>
  <groupId>org.drools</groupId>
  <artifactId>drools-opentelemetry</artifactId>
  <version>${drools.version}</version>
</dependency>
```

To export telemetry, add OpenTelemetry SDK + exporter dependencies as well:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>io.opentelemetry</groupId>
      <artifactId>opentelemetry-bom</artifactId>
      <version>${otel.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>org.drools</groupId>
    <artifactId>drools-opentelemetry</artifactId>
    <version>${drools.version}</version>
  </dependency>
  <dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-sdk</artifactId>
  </dependency>
  <dependency>
    <groupId>io.opentelemetry</groupId>
    <artifactId>opentelemetry-exporter-otlp</artifactId>
  </dependency>
</dependencies>
```

## Initialize OpenTelemetry

You can use either:

- Global OpenTelemetry (`GlobalOpenTelemetry`) and call `instrument(session)`, or
- An explicit `OpenTelemetry` instance and call `instrument(session, openTelemetry, ...)`.

### Option A: Use preconfigured global OpenTelemetry

If your app already configures global OpenTelemetry (for example with an OpenTelemetry Java agent or your own bootstrap), you can use the simple API:

```java
DroolsOpenTelemetry.instrument(kieSession);
```

### Option B: Build and pass an explicit OpenTelemetry instance

Example with OTLP exporters:

```java
import java.time.Duration;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;

Resource resource = Resource.getDefault().toBuilder()
        .put("service.name", "drools-app")
        .build();

SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
        .setResource(resource)
        .addSpanProcessor(
                BatchSpanProcessor.builder(
                        OtlpGrpcSpanExporter.builder().build())
                        .build())
        .build();

SdkMeterProvider meterProvider = SdkMeterProvider.builder()
        .setResource(resource)
        .registerMetricReader(
                PeriodicMetricReader.builder(
                        OtlpGrpcMetricExporter.builder().build())
                        .setInterval(Duration.ofSeconds(30))
                        .build())
        .build();

OpenTelemetry openTelemetry = OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .setMeterProvider(meterProvider)
        .build();

DroolsOpenTelemetry.instrument(kieSession, openTelemetry);
```

At shutdown, close providers so buffered telemetry is flushed:

```java
meterProvider.close();
tracerProvider.close();
```

## Instrument a session

Instrument once, immediately after session creation and before inserting facts/firing rules.

```java
import org.drools.opentelemetry.DroolsOpenTelemetry;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

KieServices ks = KieServices.Factory.get();
KieContainer kContainer = ks.getKieClasspathContainer();
KieSession kieSession = kContainer.newKieSession("ksession-rules");

// Uses GlobalOpenTelemetry.get()
DroolsOpenTelemetry.instrument(kieSession);

kieSession.insert(new Order("A-100", 250.0));
kieSession.fireAllRules();
kieSession.dispose();
```

### Selectively enable tracing and/or metrics

```java
// tracing + metrics (default behavior)
DroolsOpenTelemetry.instrument(session, openTelemetry, true, true);

// tracing only
DroolsOpenTelemetry.instrument(session, openTelemetry, true, false);

// metrics only
DroolsOpenTelemetry.instrument(session, openTelemetry, false, true);
```

## Telemetry emitted by this module

### Traces

Rule-firing span:

- Span name: `rule: <ruleName>`
- Kind: `INTERNAL`
- Starts on `beforeMatchFired`
- Ends on `afterMatchFired`
- Status on end: `OK`

Span attributes on rule-firing span:

- `drools.rule.name` (string)
- `drools.rule.package` (string)
- `drools.match.fact.count` (long)

Additional span events (added only when a valid current span exists):

- `match.created` with attribute `drools.rule.name`
- `match.cancelled` with attribute `drools.rule.name`
- `fact.inserted` with attributes:
  - `drools.fact.class`
  - `drools.rule.name` (only when available from event)
- `fact.updated` with attributes:
  - `drools.fact.class`
  - `drools.rule.name` (only when available from event)
- `fact.deleted` with attributes:
  - `drools.fact.class`
  - `drools.rule.name` (only when available from event)

### Metrics

Counters/histogram:

- `drools.rules.fired` (`{rules}`)
  - Description: total number of rules fired
  - Attributes: `drools.rule.name`, `drools.rule.package`
- `drools.rules.firing.duration` (`ms`)
  - Description: duration of rule firing in milliseconds
  - Attributes: `drools.rule.name`, `drools.rule.package`
- `drools.matches.created` (`{matches}`)
  - Description: total number of matches created
  - Attributes: `drools.rule.name`
- `drools.matches.cancelled` (`{matches}`)
  - Description: total number of matches cancelled
  - Attributes: `drools.rule.name`

## Practical usage notes

- Call instrumentation once per session. Calling it multiple times adds duplicate listeners and duplicates telemetry.
- Instrument before rule execution starts. Events that happen before instrumentation are not captured.
- If you use `instrument(session)` (global mode), global OpenTelemetry must already be configured.
- Tracing and metrics depend on your OpenTelemetry SDK/exporter pipeline; without SDK/exporters, no data is exported.

## Current implementation details and limitations

- Tracer scope name: `org.drools.opentelemetry`
- Tracer scope version in this branch: `999-SNAPSHOT`
- Meter scope name: `org.drools.opentelemetry`
- Listener state for in-flight rule spans/durations is tracked by thread id.
- `match.*` and `fact.*` events are recorded on `Span.current()` only when a valid span context exists.

## Troubleshooting

No spans or metrics visible:

- Verify OpenTelemetry SDK/exporters are configured and reachable.
- Verify instrumentation is called before `insert(...)` / `fireAllRules()`.
- Verify you did not accidentally instrument a different session instance.

Unexpected duplicate telemetry:

- Ensure `DroolsOpenTelemetry.instrument(...)` is not called more than once for the same session.

Missing `match.*` / `fact.*` events:

- These events require a valid active current span context at event time.
