/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.decision.aggregator;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.conf.StaticConfigBean;
import org.kie.kogito.decision.DecisionTestUtils;
import org.kie.kogito.tracing.decision.DecisionTracingTestUtils;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType;
import org.kie.kogito.tracing.decision.event.message.InternalMessageType;
import org.kie.kogito.tracing.decision.event.message.MessageCategory;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEventType;

import io.cloudevents.CloudEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.kogito.decision.DecisionTestUtils.DECISION_SERVICE_DECISION_ID;
import static org.kie.kogito.decision.DecisionTestUtils.EVALUATE_ALL_EXECUTION_ID;
import static org.kie.kogito.decision.DecisionTestUtils.EVALUATE_DECISION_SERVICE_EXECUTION_ID;
import static org.kie.kogito.decision.DecisionTestUtils.FIRST_DECISION_NODE_ID;
import static org.kie.kogito.decision.DecisionTestUtils.LAST_DECISION_NODE_ID;
import static org.kie.kogito.decision.DecisionTestUtils.createDMNModel;
import static org.kie.kogito.tracing.decision.DecisionTracingTestUtils.EVALUATE_ALL_JSON_RESOURCE;
import static org.kie.kogito.tracing.decision.DecisionTracingTestUtils.EVALUATE_DECISION_SERVICE_JSON_RESOURCE;

class DefaultAggregatorTest {

    private static DMNModel model;
    private static StaticConfigBean configBean;

    @BeforeAll
    static void initModel() {
        model = createDMNModel();
        configBean = new StaticConfigBean();
    }

    @Test
    void testAggregateWithNullListReturnsNotEnoughData() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, null, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNotEnoughData(traceEvent);
    }

    @Test
    void testAggregateWithEmptyListReturnsNotEnoughData() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, Collections.emptyList(), configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNotEnoughData(traceEvent);
    }

    @Test
    void testAggregateOnEvaluateAllWithValidListIsWorking() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE);
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEvent(traceEvent, 2, 2, 2);
    }

    @Test
    void testAggregateOnEvaluateAllWithNullModelReturnsDmnModelNotFound() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE);
        CloudEvent cloudEvent = aggregator.aggregate(null, EVALUATE_ALL_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEvent(traceEvent, 2, 2, 2);
        assertTraceEventInternalMessage(traceEvent, InternalMessageType.DMN_MODEL_NOT_FOUND);
    }

    @Test
    void testAggregateOnEvaluateAllWithListWithOnlyFirstEventReturnsNoExecutionSteps() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .limit(1).collect(Collectors.toList());
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEvent(traceEvent, 2, 2, 0);
    }

    @Test
    void testAggregateOnEvaluateAllWithListWithMissingFirstBeforeEvaluateDecisionEventReturnsNoExecutionStepHierarchy() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION && FIRST_DECISION_NODE_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 2, 2, 6);
    }

    @Test
    void testAggregateOnEvaluateAllWithListWithMissingFirstAfterEvaluateDecisionEventReturnsNoExecutionStepHierarchy() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.AFTER_EVALUATE_DECISION && FIRST_DECISION_NODE_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 2, 2, 5);
    }

    @Test
    void testAggregateOnEvaluateAllWithListWithMissingLastBeforeEvaluateDecisionEventReturnsNoExecutionStepHierarchy() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION && LAST_DECISION_NODE_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 2, 2, 6);
    }

    @Test
    void testAggregateOnEvaluateAllWithListWithMissingLastAfterEvaluateDecisionEventReturnsNoExecutionStepHierarchy() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.AFTER_EVALUATE_DECISION && LAST_DECISION_NODE_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 2, 2, 5);
    }

    @Test
    void testAggregateOnEvaluateDecisionServiceWithValidListReturnsWorking() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE);
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEvent(traceEvent, 1, 1, 1);
    }

    @Test
    void testAggregateOnEvaluateDecisionServiceWithNullModelReturnsDmnModelNotFound() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE);
        CloudEvent cloudEvent = aggregator.aggregate(null, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEvent(traceEvent, 1, 1, 1);
        assertTraceEventInternalMessage(traceEvent, InternalMessageType.DMN_MODEL_NOT_FOUND);
    }

    @Test
    void testAggregateOnEvaluateDecisionServiceWithListWithOnlyFirstEventReturnsNoExecutionSteps() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE).stream()
                .limit(1).collect(Collectors.toList());
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEvent(traceEvent, 1, 0, 0);
    }

    @Test
    void testAggregateOnEvaluateDecisionServiceWithListWithMissingBeforeEvaluateDecisionEventReturnsNoExecutionStepHierarchy() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION && DECISION_SERVICE_DECISION_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 1, 1, 3);
    }

    @Test
    void testAggregateOnEvaluateDecisionServiceWithListWithMissingAfterEvaluateDecisionEventReturnsNoExecutionStepHierarchy() throws IOException {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTracingTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.AFTER_EVALUATE_DECISION && DECISION_SERVICE_DECISION_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEvent cloudEvent = aggregator.aggregate(model, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean).orElseThrow(IllegalStateException::new);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 1, 1, 2);
    }

    private static TraceEvent assertValidCloudEventAndGetData(CloudEvent cloudEvent, String executionId) {
        assertEquals(executionId, cloudEvent.getId());
        assertEquals(TraceEvent.class.getName(), cloudEvent.getType());

        try {
            return DecisionTestUtils.MAPPER.readValue(cloudEvent.getData().toBytes(), TraceEvent.class);
        } catch (IOException e) {
            fail(e);
            return null;
        }
    }

    private static void assertTraceEventWithNoExecutionStepsHierarchy(TraceEvent traceEvent, int inputsSize, int outputsSize, int executionStepsSize) {
        assertTraceEvent(traceEvent, inputsSize, outputsSize, executionStepsSize);
        assertTraceEventInternalMessage(traceEvent, InternalMessageType.NO_EXECUTION_STEP_HIERARCHY);
    }

    private static void assertTraceEventWithNotEnoughData(TraceEvent traceEvent) {
        assertTraceEvent(traceEvent, 0, 0, 0);
        assertTraceEventInternalMessage(traceEvent, InternalMessageType.NOT_ENOUGH_DATA);
        assertNull(traceEvent.getHeader().getStartTimestamp());
        assertNull(traceEvent.getHeader().getEndTimestamp());
        assertNull(traceEvent.getHeader().getDuration());
    }

    private static void assertTraceEvent(TraceEvent traceEvent, int inputsSize, int outputsSize, int executionStepsSize) {
        assertSame(TraceEventType.DMN, traceEvent.getHeader().getType());
        assertSame(inputsSize, traceEvent.getInputs().size());
        assertSame(outputsSize, traceEvent.getOutputs().size());
        assertSame(executionStepsSize, traceEvent.getExecutionSteps().size());
    }

    private static void assertTraceEventInternalMessage(TraceEvent traceEvent, InternalMessageType type) {
        assertTrue(traceEvent.getHeader().getMessages().stream().anyMatch(
                m -> m.getLevel() == type.getLevel() && m.getCategory() == MessageCategory.INTERNAL && type.name().equals(m.getType())));
    }
}
