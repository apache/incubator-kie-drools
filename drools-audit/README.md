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
# Drools Audit

`drools-audit` records rule and fact activity from Drools sessions into an audit trail for debugging, analytics, and compliance.

It provides:

- An `AuditTrailService` facade to start/stop auditing and query events
- An `AuditEventListener` that records agenda and working-memory changes
- Pluggable stores: in-memory or JPA, plus a simple SPI for custom stores

## Core types

- `org.drools.audit.AuditTrailService` - main API
- `org.drools.audit.AuditTrailConfiguration` - builder for configuring stores
- `org.drools.audit.listener.AuditEventListener` - attaches to a session
- `org.drools.audit.store.AuditStore` - store SPI
- `org.drools.audit.store.InMemoryAuditStore` - bounded in-memory store
- `org.drools.audit.jpa.JpaAuditStore` - JPA-backed store

## Dependency setup (Maven)

```xml
<dependency>
  <groupId>org.drools</groupId>
  <artifactId>drools-audit</artifactId>
  <version>${drools.version}</version>
</dependency>
```

## Quick start

```java
import org.drools.audit.AuditTrailConfiguration;
import org.drools.audit.AuditTrailService;
import org.drools.audit.event.AuditEvent;
import org.drools.audit.store.InMemoryAuditStore;
import org.kie.api.runtime.KieSession;

AuditTrailService auditService = AuditTrailConfiguration.builder()
        .inMemory()
        .maxCapacity(50_000)
        .build();

KieSession session = kieBase.newKieSession();
String sessionId = auditService.startAudit(session);

session.insert(new Order("A-100", 250.0));
session.fireAllRules();

List<AuditEvent> trail = auditService.getAuditTrail(sessionId);

auditService.stopAudit(session, sessionId);
session.dispose();
```

## Store configuration

**In-memory store** (default, bounded, evicts oldest entries):

```java
AuditTrailService service = AuditTrailConfiguration.builder()
        .inMemory()
        .maxCapacity(100_000)
        .build();
```

**JPA store** (durable audit trail):

```java
EntityManagerFactory emf = ...;
AuditTrailService service = AuditTrailConfiguration.builder()
        .jpa(emf)
        .build();
```

**Custom store**:

```java
AuditStore store = new MyAuditStore();
AuditTrailService service = AuditTrailConfiguration.builder()
        .store(store)
        .build();
```

## Querying audit events

```java
List<AuditEvent> bySession = auditService.getAuditTrail(sessionId);
List<AuditEvent> byType = auditService.getAuditTrailByType(sessionId, AuditEventType.RULE_FIRED);
List<AuditEvent> byRule = auditService.getAuditTrailByRuleName("myRule");
List<AuditEvent> byTime = auditService.getAuditTrailByTimeRange(from, to);
```

Counts and cleanup:

```java
long total = auditService.getEventCount();
long perSession = auditService.getEventCount(sessionId);

auditService.purgeSession(sessionId);
auditService.purgeAll();
```

## What gets recorded

The listener records these event types:

- Rule match lifecycle: `RULE_MATCH_CREATED`, `RULE_MATCH_CANCELLED`
- Rule firing: `RULE_FIRED`
- Fact lifecycle: `FACT_INSERTED`, `FACT_UPDATED`, `FACT_DELETED`
- Agenda changes: `AGENDA_GROUP_PUSHED`, `AGENDA_GROUP_POPPED`,
  `RULEFLOW_GROUP_ACTIVATED`, `RULEFLOW_GROUP_DEACTIVATED`
- Session lifecycle: `SESSION_CREATED`, `SESSION_DISPOSED`

Each event has:

- `sessionId`
- `sequenceNumber` (monotonic per session)
- `timestamp`
- Event-specific payload (rule name, package, fact details, etc.)

## Practical notes

- Call `startAudit` once per session; starting twice will attach duplicate listeners.
- In-memory store is bounded; set `maxCapacity` based on expected load.
- For compliance, prefer the JPA store or a custom durable store.
- Remember to call `stopAudit` (and dispose the session) to avoid dangling listeners.

