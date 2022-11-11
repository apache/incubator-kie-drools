/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.tracing.event.trace;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.kie.kogito.tracing.event.TracingTestUtils;
import org.kie.kogito.tracing.event.message.Message;
import org.kie.kogito.tracing.event.message.models.DecisionMessageTest;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.tracing.event.trace.TraceEventType.DMN;

class TraceEventTest {

    @Test
    public void testDeserialization() throws JsonProcessingException {
        String toRead = TracingTestUtils.readResourceAsString("/traceevent.json");
        TraceEvent retrieved = new ObjectMapper().readValue(toRead, TraceEvent.class);
        assertThat(retrieved).isNotNull();
    }

    @Test
    public void testSerialization() throws JsonProcessingException {
        TraceEvent traceEvent = getTraceEvent();
        String retrieved = new ObjectMapper().writeValueAsString(traceEvent);
        assertThat(retrieved).isNotNull();
    }

    private TraceEvent getTraceEvent() {
        TraceHeader header = getTraceHeader(new Random().nextInt(10));
        List<TraceInputValue> inputs =
                IntStream.range(0, 3).mapToObj(this::getTraceInputValue).collect(Collectors.toList());
        List<TraceOutputValue> outputs =
                IntStream.range(0, 3).mapToObj(this::getTraceOutputValue).collect(Collectors.toList());
        List<TraceExecutionStep> executionSteps =
                IntStream.range(0, 3).mapToObj(this::getTraceExecutionStep).collect(Collectors.toList());
        return new TraceEvent(header, inputs, outputs, executionSteps);
    }

    private TraceExecutionStep getTraceExecutionStep(int id) {
        List<Message> messages =
                IntStream.range(id + 1, id + 3).mapToObj(DecisionMessageTest::getDecisionMessage).collect(Collectors.toList());
        Map<String, String> additionalData = Collections.singletonMap("key", "value");
        List<TraceExecutionStep> children =
                IntStream.range(id + 1, id + 3).mapToObj(i -> getTraceExecutionStep()).collect(Collectors.toList());
        return new TraceExecutionStep(getTraceExecutionStepType(),
                new Random().nextLong(),
                "name-" + id,
                new IntNode(id),
                messages,
                additionalData,
                children);
    }

    private TraceExecutionStep getTraceExecutionStep() {
        List<Message> messages =
                IntStream.range(0, 3).mapToObj(DecisionMessageTest::getDecisionMessage).collect(Collectors.toList());
        Map<String, String> additionalData = Collections.singletonMap("c-key", "c-value");
        return new TraceExecutionStep(getTraceExecutionStepType(),
                new Random().nextLong(),
                "children-name",
                BooleanNode.getFalse(),
                messages,
                additionalData,
                Collections.emptyList());
    }

    private TraceExecutionStepType getTraceExecutionStepType() {
        return TraceExecutionStepType.values()[new Random().nextInt(TraceExecutionStepType.values().length)];
    }

    private TraceHeader getTraceHeader(int id) {
        Long startTimestamp = System.currentTimeMillis();
        Long duration = Long.valueOf(new Random().nextInt(10000000));
        Long endTimestamp = startTimestamp + duration;
        List<Message> messages =
                IntStream.range(id + 1, id + 3).mapToObj(DecisionMessageTest::getDecisionMessage).collect(Collectors.toList());
        return new TraceHeader(DMN,
                "execution-" + id,
                startTimestamp,
                endTimestamp,
                duration,
                getTraceResourceId(id),
                messages);
    }

    private TraceResourceId getTraceResourceId(int id) {
        return new TraceResourceId("serviceUrl-" + id,
                "modelNamespace-" + id,
                "modelName-" + id);
    }

    private TraceInputValue getTraceInputValue(int id) {
        String valueType = "type-" + id;
        TypedValue value = new UnitValue(valueType);
        List<Message> messages =
                IntStream.range(id + 1, id + 3).mapToObj(DecisionMessageTest::getDecisionMessage).collect(Collectors.toList());
        return new TraceInputValue("id-" + id,
                "name-" + id,
                value,
                messages);
    }

    private TraceOutputValue getTraceOutputValue(int id) {
        String valueType = "type-" + id;
        TypedValue value = new UnitValue(valueType);
        Map<String, TypedValue> inputs = Collections.singletonMap(valueType, value);
        List<Message> messages =
                IntStream.range(id + 1, id + 3).mapToObj(DecisionMessageTest::getDecisionMessage).collect(Collectors.toList());
        return new TraceOutputValue("id-" + id,
                "name-" + id,
                "status-" + id,
                value,
                inputs,
                messages);
    }
}
