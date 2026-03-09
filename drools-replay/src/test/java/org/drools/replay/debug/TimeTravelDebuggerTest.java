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
package org.drools.replay.debug;

import java.util.List;

import org.drools.replay.event.EventType;
import org.drools.replay.event.ExecutionEvent;
import org.drools.replay.event.FactDeletedEvent;
import org.drools.replay.event.FactInsertedEvent;
import org.drools.replay.event.RuleMatchEvent;
import org.drools.replay.recorder.ExecutionLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeTravelDebuggerTest {

    private ExecutionLog log;
    private TimeTravelDebugger debugger;

    @BeforeEach
    void setUp() {
        log = new ExecutionLog();
        log.addEvent(new FactInsertedEvent(1, "com.example.Order", "abc1", "Order{id=1}", null));
        log.addEvent(new FactInsertedEvent(2, "com.example.Customer", "def2", "Customer{name=John}", null));
        log.addEvent(new RuleMatchEvent(3, EventType.BEFORE_RULE_FIRED, "applyDiscount", "com.example", List.of("Order{id=1}")));
        log.addEvent(new RuleMatchEvent(4, EventType.AFTER_RULE_FIRED, "applyDiscount", "com.example", List.of("Order{id=1}")));
        log.addEvent(new FactDeletedEvent(5, "com.example.Order", "abc1", "Order{id=1}", "cleanup"));

        debugger = new TimeTravelDebugger(log);
    }

    @Test
    void shouldStartBeforeFirstEvent() {
        assertThat(debugger.getCurrentPosition()).isEqualTo(-1);
        assertThat(debugger.hasNext()).isTrue();
        assertThat(debugger.hasPrevious()).isFalse();
    }

    @Test
    void shouldStepForward() {
        ExecutionEvent first = debugger.stepForward();
        assertThat(first).isNotNull();
        assertThat(first.getType()).isEqualTo(EventType.FACT_INSERTED);
        assertThat(debugger.getCurrentPosition()).isEqualTo(0);
    }

    @Test
    void shouldStepBackward() {
        debugger.stepForward();
        debugger.stepForward();
        assertThat(debugger.getCurrentPosition()).isEqualTo(1);

        ExecutionEvent prev = debugger.stepBackward();
        assertThat(prev).isNotNull();
        assertThat(debugger.getCurrentPosition()).isEqualTo(0);
    }

    @Test
    void shouldReturnNullAtEnd() {
        for (int i = 0; i < 5; i++) {
            debugger.stepForward();
        }
        assertThat(debugger.stepForward()).isNull();
        assertThat(debugger.hasNext()).isFalse();
    }

    @Test
    void shouldJumpToPosition() {
        ExecutionEvent event = debugger.jumpTo(3);
        assertThat(event).isNotNull();
        assertThat(event.getType()).isEqualTo(EventType.AFTER_RULE_FIRED);
        assertThat(debugger.getCurrentPosition()).isEqualTo(3);
    }

    @Test
    void shouldThrowOnInvalidJump() {
        assertThatThrownBy(() -> debugger.jumpTo(99))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void shouldReconstructStateAfterInserts() {
        debugger.jumpTo(1);
        StateSnapshot snapshot = debugger.getStateSnapshot();

        assertThat(snapshot.getActiveFacts()).hasSize(2);
        assertThat(snapshot.getActiveFacts()).containsKey("abc1");
        assertThat(snapshot.getActiveFacts()).containsKey("def2");
        assertThat(snapshot.getRulesFiredSoFar()).isEmpty();
    }

    @Test
    void shouldReconstructStateAfterRuleFiring() {
        debugger.jumpTo(3);
        StateSnapshot snapshot = debugger.getStateSnapshot();

        assertThat(snapshot.getActiveFacts()).hasSize(2);
        assertThat(snapshot.getRulesFiredSoFar()).containsExactly("applyDiscount");
    }

    @Test
    void shouldReconstructStateAfterDeletion() {
        debugger.jumpTo(4);
        StateSnapshot snapshot = debugger.getStateSnapshot();

        assertThat(snapshot.getActiveFacts()).hasSize(1);
        assertThat(snapshot.getActiveFacts()).containsKey("def2");
        assertThat(snapshot.getActiveFacts()).doesNotContainKey("abc1");
        assertThat(snapshot.getRulesFiredSoFar()).containsExactly("applyDiscount");
    }

    @Test
    void shouldGetRuleFireHistory() {
        List<RuleMatchEvent> history = debugger.getRuleFireHistory("applyDiscount");
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getRuleName()).isEqualTo("applyDiscount");
    }

    @Test
    void shouldGetFactHistory() {
        List<ExecutionEvent> history = debugger.getFactHistory("com.example.Order");
        assertThat(history).hasSize(2);
    }

    @Test
    void shouldGetEventsInRange() {
        List<ExecutionEvent> range = debugger.getEventsInRange(1, 3);
        assertThat(range).hasSize(3);
    }
}