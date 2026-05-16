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
# Drools Replay

`drools-replay` records Drools session activity into an in-memory log and lets you replay that log for time-travel debugging.

It provides:

- Recording of rule match lifecycle events and fact insert/update/delete events
- An ordered `ExecutionLog` you can dump, filter, or query
- A `TimeTravelDebugger` that can step forward/backward and reconstruct working memory state

## Core types

- `org.drools.replay.recorder.RuleExecutionRecorder` - attaches to a session and records events
- `org.drools.replay.recorder.ExecutionLog` - thread-safe ordered event log
- `org.drools.replay.debug.TimeTravelDebugger` - replay and state reconstruction

## Dependency setup (Maven)

```xml
<dependency>
  <groupId>org.drools</groupId>
  <artifactId>drools-replay</artifactId>
  <version>${drools.version}</version>
</dependency>
```

## Record a session

Attach the recorder immediately after session creation and before inserting facts or firing rules.

```java
import org.drools.replay.recorder.RuleExecutionRecorder;
import org.drools.replay.recorder.ExecutionLog;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

KieServices ks = KieServices.Factory.get();
KieContainer kContainer = ks.getKieClasspathContainer();
KieSession kieSession = kContainer.newKieSession("ksession-rules");

RuleExecutionRecorder recorder = new RuleExecutionRecorder();
recorder.attach(kieSession);

kieSession.insert(new Order("A-100", 250.0));
kieSession.fireAllRules();

ExecutionLog log = recorder.getLog();
System.out.println(log.dump());

kieSession.dispose();
```

## Control recording

You can temporarily disable recording without detaching the listeners.

```java
recorder.disable();
// ... run operations without recording
recorder.enable();
```

Detach when you are done:

```java
recorder.detach(kieSession);
```

## Explore the ExecutionLog

`ExecutionLog` is a thread-safe ordered list of `ExecutionEvent` instances.

```java
int total = log.size();
List<ExecutionEvent> all = log.getEvents();
List<ExecutionEvent> inserts = log.getEventsByType(EventType.FACT_INSERTED);
List<RuleMatchEvent> byRule = log.getRuleEvents("myRule");
```

## Time-travel debugging

The debugger replays the log to reconstruct working memory at any point.

```java
import org.drools.replay.debug.TimeTravelDebugger;
import org.drools.replay.debug.StateSnapshot;

TimeTravelDebugger debugger = new TimeTravelDebugger(log);

ExecutionEvent e1 = debugger.stepForward();
ExecutionEvent e2 = debugger.stepForward();

StateSnapshot snapshot = debugger.getStateSnapshot();

debugger.jumpTo(5);
debugger.stepBackward();
```

Useful queries:

```java
List<RuleMatchEvent> fired = debugger.getRuleFireHistory("myRule");
List<ExecutionEvent> factHistory = debugger.getFactHistory("com.example.Order");
```

## What gets recorded

The recorder captures these event types:

- Match lifecycle: `MATCH_CREATED`, `MATCH_CANCELLED`, `BEFORE_RULE_FIRED`, `AFTER_RULE_FIRED`
- Fact lifecycle: `FACT_INSERTED`, `FACT_UPDATED`, `FACT_DELETED`

Each event is assigned a monotonically increasing sequence number in the order received.

## Practical notes

- Attach once per session. Attaching multiple times will duplicate events.
- Recording is in-memory; large runs can grow the log quickly.
- `TimeTravelDebugger` reconstructs state by replaying all fact events from the start up to the current cursor.

