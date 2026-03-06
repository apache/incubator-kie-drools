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

import java.util.Collections;
import java.util.List;

import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.RuleFiredEvent;
import org.drools.audit.store.InMemoryAuditStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuditTrailServiceTest {

    private AuditTrailService service;
    private InMemoryAuditStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryAuditStore();
        service = new AuditTrailService(store);
    }

    @Test
    void configurationBuilderCreatesInMemoryService() {
        AuditTrailService built = AuditTrailConfiguration.builder()
                .inMemory()
                .maxCapacity(1000)
                .build();

        assertThat(built).isNotNull();
        assertThat(built.getStore()).isInstanceOf(InMemoryAuditStore.class);
    }

    @Test
    void manualStoreInteraction() {
        String sessionId = "manual-session";
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, sessionId, 1,
                "rule1", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, sessionId, 2,
                "rule2", "pkg", Collections.emptyMap(), Collections.emptyList(), 5));

        List<AuditEvent> trail = service.getAuditTrail(sessionId);
        assertThat(trail).hasSize(2);
        assertThat(trail.get(0).getSequenceNumber()).isEqualTo(1);
        assertThat(trail.get(1).getSequenceNumber()).isEqualTo(2);
    }

    @Test
    void filterByType() {
        String sessionId = "type-filter-session";
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, sessionId, 1,
                "rule1", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_MATCH_CREATED, sessionId, 2,
                "rule1", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));

        List<AuditEvent> fired = service.getAuditTrailByType(sessionId, AuditEventType.RULE_FIRED);
        assertThat(fired).hasSize(1);

        List<AuditEvent> created = service.getAuditTrailByType(sessionId, AuditEventType.RULE_MATCH_CREATED);
        assertThat(created).hasSize(1);
    }

    @Test
    void findByRuleName() {
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 1,
                "approvalRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s2", 1,
                "approvalRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 2,
                "otherRule", "pkg", Collections.emptyMap(), Collections.emptyList(), 0));

        List<AuditEvent> result = service.getAuditTrailByRuleName("approvalRule");
        assertThat(result).hasSize(2);
    }

    @Test
    void eventCountTracking() {
        String s1 = "count-session-1";
        String s2 = "count-session-2";

        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, s1, 1,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, s1, 2,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, s2, 1,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0));

        assertThat(service.getEventCount()).isEqualTo(3);
        assertThat(service.getEventCount(s1)).isEqualTo(2);
        assertThat(service.getEventCount(s2)).isEqualTo(1);
    }

    @Test
    void purgeSession() {
        String s1 = "purge-1";
        String s2 = "purge-2";

        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, s1, 1,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, s2, 1,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0));

        service.purgeSession(s1);
        assertThat(service.getEventCount()).isEqualTo(1);
        assertThat(service.getAuditTrail(s1)).isEmpty();
        assertThat(service.getAuditTrail(s2)).hasSize(1);
    }

    @Test
    void purgeAll() {
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s1", 1,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0));
        store.store(new RuleFiredEvent(AuditEventType.RULE_FIRED, "s2", 1,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0));

        service.purgeAll();
        assertThat(service.getEventCount()).isZero();
    }
}
