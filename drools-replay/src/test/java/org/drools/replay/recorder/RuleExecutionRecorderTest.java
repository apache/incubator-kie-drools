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
package org.drools.replay.recorder;

import java.util.Collections;
import java.util.List;

import org.drools.replay.event.EventType;
import org.drools.replay.event.ExecutionEvent;
import org.drools.replay.event.FactInsertedEvent;
import org.drools.replay.event.RuleMatchEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RuleExecutionRecorderTest {

    private RuleExecutionRecorder recorder;

    @BeforeEach
    void setUp() {
        recorder = new RuleExecutionRecorder();
    }

    @Test
    void shouldRecordFactInsertion() {
        ObjectInsertedEvent event = mock(ObjectInsertedEvent.class);
        when(event.getObject()).thenReturn("hello");
        when(event.getRule()).thenReturn(null);

        recorder.objectInserted(event);

        ExecutionLog log = recorder.getLog();
        assertThat(log.size()).isEqualTo(1);

        ExecutionEvent recorded = log.getEvent(0);
        assertThat(recorded).isInstanceOf(FactInsertedEvent.class);
        assertThat(recorded.getType()).isEqualTo(EventType.FACT_INSERTED);

        FactInsertedEvent factEvent = (FactInsertedEvent) recorded;
        assertThat(factEvent.getFactClassName()).isEqualTo("java.lang.String");
        assertThat(factEvent.getFactToString()).isEqualTo("hello");
        assertThat(factEvent.getTriggeringRule()).isNull();
    }

    @Test
    void shouldRecordRuleFiring() {
        Rule rule = mockRule("testRule", "com.example");
        Match match = mockMatch(rule);

        BeforeMatchFiredEvent event = mock(BeforeMatchFiredEvent.class);
        when(event.getMatch()).thenReturn(match);

        recorder.beforeMatchFired(event);

        ExecutionLog log = recorder.getLog();
        assertThat(log.size()).isEqualTo(1);

        ExecutionEvent recorded = log.getEvent(0);
        assertThat(recorded).isInstanceOf(RuleMatchEvent.class);
        assertThat(recorded.getType()).isEqualTo(EventType.BEFORE_RULE_FIRED);

        RuleMatchEvent ruleEvent = (RuleMatchEvent) recorded;
        assertThat(ruleEvent.getRuleName()).isEqualTo("testRule");
        assertThat(ruleEvent.getRulePackage()).isEqualTo("com.example");
    }

    @Test
    void shouldNotRecordWhenDisabled() {
        recorder.disable();

        ObjectInsertedEvent event = mock(ObjectInsertedEvent.class);
        when(event.getObject()).thenReturn("hello");
        when(event.getRule()).thenReturn(null);

        recorder.objectInserted(event);
        assertThat(recorder.getLog().size()).isZero();
    }

    @Test
    void shouldFilterByEventType() {
        ObjectInsertedEvent insertEvent = mock(ObjectInsertedEvent.class);
        when(insertEvent.getObject()).thenReturn("fact1");
        when(insertEvent.getRule()).thenReturn(null);

        Rule rule = mockRule("rule1", "pkg");
        Match match = mockMatch(rule);
        BeforeMatchFiredEvent fireEvent = mock(BeforeMatchFiredEvent.class);
        when(fireEvent.getMatch()).thenReturn(match);

        recorder.objectInserted(insertEvent);
        recorder.beforeMatchFired(fireEvent);

        assertThat(recorder.getLog().getEventsByType(EventType.FACT_INSERTED)).hasSize(1);
        assertThat(recorder.getLog().getEventsByType(EventType.BEFORE_RULE_FIRED)).hasSize(1);
    }

    @Test
    void shouldAssignIncreasingSequenceNumbers() {
        ObjectInsertedEvent event1 = mock(ObjectInsertedEvent.class);
        when(event1.getObject()).thenReturn("a");
        when(event1.getRule()).thenReturn(null);

        ObjectInsertedEvent event2 = mock(ObjectInsertedEvent.class);
        when(event2.getObject()).thenReturn("b");
        when(event2.getRule()).thenReturn(null);

        recorder.objectInserted(event1);
        recorder.objectInserted(event2);

        List<ExecutionEvent> events = recorder.getLog().getEvents();
        assertThat(events.get(0).getSequenceNumber()).isLessThan(events.get(1).getSequenceNumber());
    }

    private Rule mockRule(String name, String packageName) {
        Rule rule = mock(Rule.class);
        when(rule.getName()).thenReturn(name);
        when(rule.getPackageName()).thenReturn(packageName);
        return rule;
    }

    @SuppressWarnings("unchecked")
    private Match mockMatch(Rule rule) {
        Match match = mock(Match.class);
        when(match.getRule()).thenReturn(rule);
        when(match.getObjects()).thenReturn(Collections.singletonList("testFact"));
        when(match.getFactHandles()).thenReturn((List) Collections.singletonList(mock(FactHandle.class)));
        return match;
    }
}