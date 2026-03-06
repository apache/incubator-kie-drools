/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.audit;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.FactOperationEvent;
import org.drools.audit.event.RuleFiredEvent;
import org.drools.audit.event.SessionOperationEvent;
import org.drools.audit.store.InMemoryAuditStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryAuditStoreTest {

    private InMemoryAuditStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryAuditStore();
    }

    @Test
    void storeAndRetrieveBySessionId() {
        String sessionId = "session-1";
        RuleFiredEvent event = new RuleFiredEvent(
                AuditEventType.RULE_FIRED, sessionId, 1,
                "testRule", "org.test",
                Map.of("$fact", "value"), List.of(1L, 2L), 10);
        store.store(event);

        List<AuditEvent> result = store.findBySessionId(sessionId);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isInstanceOf(RuleFiredEvent.class);
        RuleFiredEvent retrieved = (RuleFiredEvent) result.get(0);
        assertThat(retrieved.getRuleName()).isEqualTo("testRule");
        assertThat(retrieved.getPackageName()).isEqualTo("org.test");
        assertThat(retrieved.getSalience()).isEqualTo(10);
        assertThat(retrieved.getFactHandleIds()).containsExactly(1L, 2L);
    }

    @Test
    void filterByEventType() {
        String sessionId = "session-2";
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, sessionId, 1,
                "rule1", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new FactOperationEvent(AuditEventType.FACT_INSERTED, sessionId, 2,
                100L, "com.Fact", "Fact{}", null, null));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, sessionId, 3,
                "rule2", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));

        List<AuditEvent> ruleEvents = store.findBySessionIdAndType(sessionId, AuditEventType.RULE_FIRED);
        assertThat(ruleEvents).hasSize(2);

        List<AuditEvent> factEvents = store.findBySessionIdAndType(sessionId, AuditEventType.FACT_INSERTED);
        assertThat(factEvents).hasSize(1);
    }

    @Test
    void findByRuleName() {
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 1,
                "targetRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 2,
                "otherRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s2", 1,
                "targetRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));

        List<AuditEvent> result = store.findByRuleName("targetRule");
        assertThat(result).hasSize(2);
    }

    @Test
    void findByTimeRange() {
        Instant before = Instant.now();
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", 1, 0, 0));
        Instant after = Instant.now();

        List<AuditEvent> result = store.findByTimeRange(before, after);
        assertThat(result).hasSize(1);

        List<AuditEvent> empty = store.findByTimeRange(
                Instant.parse("2020-01-01T00:00:00Z"),
                Instant.parse("2020-01-02T00:00:00Z"));
        assertThat(empty).isEmpty();
    }

    @Test
    void capacityEviction() {
        InMemoryAuditStore bounded = new InMemoryAuditStore(5);
        for (int i = 0; i < 10; i++) {
            bounded.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", i, 0, 0));
        }
        assertThat(bounded.count()).isEqualTo(5);

        List<AuditEvent> all = bounded.findAll();
        assertThat(all.get(0).getSequenceNumber()).isEqualTo(5);
    }

    @Test
    void deleteBySessionId() {
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", 1, 0, 0));
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s2", 1, 0, 0));
        assertThat(store.count()).isEqualTo(2);

        store.deleteBySessionId("s1");
        assertThat(store.count()).isEqualTo(1);
        assertThat(store.findBySessionId("s1")).isEmpty();
        assertThat(store.findBySessionId("s2")).hasSize(1);
    }

    @Test
    void deleteAll() {
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", 1, 0, 0));
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s2", 1, 0, 0));
        store.deleteAll();
        assertThat(store.count()).isZero();
    }

    @Test
    void countBySessionId() {
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s1", 1, 0, 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 2,
                "rule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new SessionOperationEvent(AuditEventType.SESSION_CREATED, "s2", 1, 0, 0));

        assertThat(store.countBySessionId("s1")).isEqualTo(2);
        assertThat(store.countBySessionId("s2")).isEqualTo(1);
        assertThat(store.countBySessionId("s3")).isZero();
    }

    @Test
    void eventsAreOrderedBySequenceNumber() {
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 3,
                "rule3", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 1,
                "rule1", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 2,
                "rule2", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));

        List<AuditEvent> result = store.findBySessionId("s1");
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getSequenceNumber()).isEqualTo(1);
        assertThat(result.get(1).getSequenceNumber()).isEqualTo(2);
        assertThat(result.get(2).getSequenceNumber()).isEqualTo(3);
    }

    @Test
    void invalidCapacityThrows() {
        assertThatThrownBy(() -> new InMemoryAuditStore(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new InMemoryAuditStore(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void factOperationEventCapturesDetails() {
        FactOperationEvent event = new FactOperationEvent(
                AuditEventType.FACT_UPDATED, "s1", 1,
                42L, "com.example.Person",
                "Person{name='John', age=31}",
                "Person{name='John', age=30}",
                "ageIncrementRule");
        store.store(event);

        List<AuditEvent> result = store.findBySessionId("s1");
        assertThat(result).hasSize(1);
        FactOperationEvent retrieved = (FactOperationEvent) result.get(0);
        assertThat(retrieved.getFactHandleId()).isEqualTo(42L);
        assertThat(retrieved.getObjectClassName()).isEqualTo("com.example.Person");
        assertThat(retrieved.getObjectRepresentation()).isEqualTo("Person{name='John', age=31}");
        assertThat(retrieved.getPreviousObjectRepresentation()).isEqualTo("Person{name='John', age=30}");
        assertThat(retrieved.getTriggeringRuleName()).isEqualTo("ageIncrementRule");
    }
}
