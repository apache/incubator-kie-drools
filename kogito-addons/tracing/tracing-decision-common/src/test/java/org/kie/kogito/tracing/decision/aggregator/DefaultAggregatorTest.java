/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.tracing.decision.aggregator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.cloudevents.v1.CloudEventImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNModel;
import org.kie.kogito.conf.StaticConfigBean;
import org.kie.kogito.tracing.decision.DecisionTestUtils;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType;
import org.kie.kogito.tracing.decision.event.message.InternalMessageType;
import org.kie.kogito.tracing.decision.event.message.MessageCategory;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEventType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.DECISION_SERVICE_DECISION_ID;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.EVALUATE_ALL_EXECUTION_ID;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.EVALUATE_ALL_JSON_RESOURCE;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.EVALUATE_DECISION_SERVICE_EXECUTION_ID;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.EVALUATE_DECISION_SERVICE_JSON_RESOURCE;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.FIRST_DECISION_NODE_ID;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.LAST_DECISION_NODE_ID;
import static org.kie.kogito.tracing.decision.DecisionTestUtils.createDMNModel;

class DefaultAggregatorTest {

    private static DMNModel model;
    private static StaticConfigBean configBean;

    @BeforeAll
    static void initModel() {
        model = createDMNModel();
        configBean = new StaticConfigBean();
    }

    @Test
    void test_Aggregate_NullList_NotEnoughData() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, null, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNotEnoughData(traceEvent);
    }

    @Test
    void test_Aggregate_EmptyList_NotEnoughData() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, Collections.emptyList(), configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNotEnoughData(traceEvent);
    }

    @Test
    void test_Aggregate_EvaluateAll_ValidList_Working() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE);
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEvent(traceEvent, 2, 2, 2);
    }

    @Test
    void test_Aggregate_EvaluateAll_NullModel_DmnModelNotFound() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE);
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(null, EVALUATE_ALL_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEvent(traceEvent, 2, 2, 2);
        assertTraceEventInternalMessage(traceEvent, InternalMessageType.DMN_MODEL_NOT_FOUND);
    }

    @Test
    void test_Aggregate_EvaluateAll_ListWithOnlyFirstEvent_NoExecutionSteps() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .limit(1).collect(Collectors.toList());
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEvent(traceEvent, 2, 2, 0);
    }

    @Test
    void test_Aggregate_EvaluateAll_ListWithMissingFirstBeforeEvaluateDecisionEvent_NoExecutionStepHierarchy() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION && FIRST_DECISION_NODE_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 2, 2, 6);
    }

    @Test
    void test_Aggregate_EvaluateAll_ListWithMissingFirstAfterEvaluateDecisionEvent_NoExecutionStepHierarchy() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.AFTER_EVALUATE_DECISION && FIRST_DECISION_NODE_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 2, 2, 5);
    }

    @Test
    void test_Aggregate_EvaluateAll_ListWithMissingLastBeforeEvaluateDecisionEvent_NoExecutionStepHierarchy() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION && LAST_DECISION_NODE_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 2, 2, 6);
    }

    @Test
    void test_Aggregate_EvaluateAll_ListWithMissingLastAfterEvaluateDecisionEvent_NoExecutionStepHierarchy() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_ALL_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.AFTER_EVALUATE_DECISION && LAST_DECISION_NODE_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_ALL_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_ALL_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 2, 2, 5);
    }

    @Test
    void test_Aggregate_EvaluateDecisionService_ValidList_Working() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE);
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEvent(traceEvent, 1, 1, 1);
    }

    @Test
    void test_Aggregate_EvaluateDecisionService_NullModel_DmnModelNotFound() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE);
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(null, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEvent(traceEvent, 1, 1, 1);
        assertTraceEventInternalMessage(traceEvent, InternalMessageType.DMN_MODEL_NOT_FOUND);
    }

    @Test
    void test_Aggregate_EvaluateDecisionService_ListWithOnlyFirstEvent_NoExecutionSteps() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE).stream()
                .limit(1).collect(Collectors.toList());
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEvent(traceEvent, 1, 0, 0);
    }

    @Test
    void test_Aggregate_EvaluateDecisionService_ListWithMissingBeforeEvaluateDecisionEvent_NoExecutionStepHierarchy() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.BEFORE_EVALUATE_DECISION && DECISION_SERVICE_DECISION_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 1, 1, 3);
    }

    @Test
    void test_Aggregate_EvaluateDecisionService_ListWithMissingAfterEvaluateDecisionEvent_NoExecutionStepHierarchy() {
        final DefaultAggregator aggregator = new DefaultAggregator();
        final List<EvaluateEvent> events = DecisionTestUtils.readEvaluateEventsFromJsonResource(EVALUATE_DECISION_SERVICE_JSON_RESOURCE).stream()
                .filter(e -> !(e.getType() == EvaluateEventType.AFTER_EVALUATE_DECISION && DECISION_SERVICE_DECISION_ID.equals(e.getNodeId())))
                .collect(Collectors.toList());
        CloudEventImpl<TraceEvent> cloudEvent = aggregator.aggregate(model, EVALUATE_DECISION_SERVICE_EXECUTION_ID, events, configBean);
        TraceEvent traceEvent = assertValidCloudEventAndGetData(cloudEvent, EVALUATE_DECISION_SERVICE_EXECUTION_ID);
        assertTraceEventWithNoExecutionStepsHierarchy(traceEvent, 1, 1, 2);
    }

    private static TraceEvent assertValidCloudEventAndGetData(CloudEventImpl<TraceEvent> cloudEvent, String executionId) {
        assertEquals(executionId, cloudEvent.getAttributes().getId());
        assertEquals(TraceEvent.class.getName(), cloudEvent.getAttributes().getType());
        assertTrue(cloudEvent.getData().isPresent());
        return cloudEvent.getData().get();
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
                m -> m.getLevel() == type.getLevel() && m.getCategory() == MessageCategory.INTERNAL && type.name().equals(m.getType())
        ));
    }
}
