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

import org.drools.audit.event.AgendaOperationEvent;
import org.drools.audit.event.AuditEvent;
import org.drools.audit.event.AuditEventType;
import org.drools.audit.event.FactOperationEvent;
import org.drools.audit.event.RuleFiredEvent;
import org.drools.audit.event.SessionOperationEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuditEventModelTest {

    @Test
    void ruleFiredEventImmutability() {
        Map<String, String> decls = Map.of("$person", "Person{name='Alice'}");
        List<Long> handles = List.of(1L, 2L, 3L);
        RuleFiredEvent event = new RuleFiredEvent(
                AuditEventType.RULE_FIRED, "session-1", 1,
                "discountRule", "com.shop.rules",
                decls, handles, 100);

        assertThat(event.getId()).isNotNull();
        assertThat(event.getType()).isEqualTo(AuditEventType.RULE_FIRED);
        assertThat(event.getTimestamp()).isBeforeOrEqualTo(Instant.now());
        assertThat(event.getSessionId()).isEqualTo("session-1");
        assertThat(event.getSequenceNumber()).isEqualTo(1);
        assertThat(event.getRuleName()).isEqualTo("discountRule");
        assertThat(event.getPackageName()).isEqualTo("com.shop.rules");
        assertThat(event.getDeclarations()).containsEntry("$person", "Person{name='Alice'}");
        assertThat(event.getFactHandleIds()).containsExactly(1L, 2L, 3L);
        assertThat(event.getSalience()).isEqualTo(100);
    }

    @Test
    void factOperationEventCapture() {
        FactOperationEvent event = new FactOperationEvent(
                AuditEventType.FACT_INSERTED, "session-2", 5,
                42L, "com.model.Order",
                "Order{id=42, total=100.0}",
                null, null);

        assertThat(event.getType()).isEqualTo(AuditEventType.FACT_INSERTED);
        assertThat(event.getFactHandleId()).isEqualTo(42L);
        assertThat(event.getObjectClassName()).isEqualTo("com.model.Order");
        assertThat(event.getObjectRepresentation()).contains("Order{id=42");
        assertThat(event.getPreviousObjectRepresentation()).isNull();
        assertThat(event.getTriggeringRuleName()).isNull();
    }

    @Test
    void factUpdateCapturesPreviousState() {
        FactOperationEvent event = new FactOperationEvent(
                AuditEventType.FACT_UPDATED, "session-3", 10,
                42L, "com.model.Order",
                "Order{id=42, total=150.0}",
                "Order{id=42, total=100.0}",
                "applyDiscountRule");

        assertThat(event.getObjectRepresentation()).contains("150.0");
        assertThat(event.getPreviousObjectRepresentation()).contains("100.0");
        assertThat(event.getTriggeringRuleName()).isEqualTo("applyDiscountRule");
    }

    @Test
    void agendaOperationEvent() {
        AgendaOperationEvent event = new AgendaOperationEvent(
                AuditEventType.AGENDA_GROUP_PUSHED, "session-4", 3,
                "validation-group");

        assertThat(event.getType()).isEqualTo(AuditEventType.AGENDA_GROUP_PUSHED);
        assertThat(event.getGroupName()).isEqualTo("validation-group");
    }

    @Test
    void sessionOperationEvent() {
        SessionOperationEvent event = new SessionOperationEvent(
                AuditEventType.SESSION_FIRE_ALL_RULES, "session-5", 100,
                42, 150);

        assertThat(event.getRulesFiredCount()).isEqualTo(42);
        assertThat(event.getDurationMillis()).isEqualTo(150);
    }

    @Test
    void auditEventComparableOrdering() {
        RuleFiredEvent e1 = new RuleFiredEvent(AuditEventType.RULE_FIRED, "session-a", 1,
                "r1", "p", Collections.emptyMap(), Collections.emptyList(), 0);
        RuleFiredEvent e2 = new RuleFiredEvent(AuditEventType.RULE_FIRED, "session-a", 2,
                "r2", "p", Collections.emptyMap(), Collections.emptyList(), 0);
        RuleFiredEvent e3 = new RuleFiredEvent(AuditEventType.RULE_FIRED, "session-a", 3,
                "r3", "p", Collections.emptyMap(), Collections.emptyList(), 0);

        List<AuditEvent> events = new java.util.ArrayList<>(List.of(e3, e1, e2));
        Collections.sort(events);

        assertThat(events.get(0).getSequenceNumber()).isEqualTo(1);
        assertThat(events.get(1).getSequenceNumber()).isEqualTo(2);
        assertThat(events.get(2).getSequenceNumber()).isEqualTo(3);
    }

    @Test
    void auditEventEquality() {
        RuleFiredEvent e1 = new RuleFiredEvent(AuditEventType.RULE_FIRED, "s", 1,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0);
        RuleFiredEvent e2 = new RuleFiredEvent(AuditEventType.RULE_FIRED, "s", 1,
                "r", "p", Collections.emptyMap(), Collections.emptyList(), 0);

        assertThat(e1).isNotEqualTo(e2);
        assertThat(e1).isEqualTo(e1);
    }

    @Test
    void eventTypeCategories() {
        assertThat(AuditEventType.RULE_FIRED.getCategory()).isEqualTo("rule");
        assertThat(AuditEventType.FACT_INSERTED.getCategory()).isEqualTo("fact");
        assertThat(AuditEventType.AGENDA_GROUP_PUSHED.getCategory()).isEqualTo("agenda");
        assertThat(AuditEventType.SESSION_CREATED.getCategory()).isEqualTo("session");
    }

    @Test
    void toStringContainsRelevantInfo() {
        RuleFiredEvent event = new RuleFiredEvent(
                AuditEventType.RULE_FIRED, "sess-1", 1,
                "myRule", "org.pkg",
                Collections.emptyMap(), Collections.emptyList(), 0);
        String str = event.toString();
        assertThat(str).contains("RULE_FIRED");
        assertThat(str).contains("myRule");
        assertThat(str).contains("org.pkg");
    }
}
