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
package org.kie.kogito.tracing.decision;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.event.AfterEvaluateAllEvent;
import org.kie.dmn.api.core.event.BeforeEvaluateAllEvent;
import org.kie.dmn.core.impl.DMNContextImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.DMNResultImpl;
import org.kie.kogito.decision.DecisionExecutionIdUtils;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEventType;
import org.kie.kogito.tracing.decision.mock.MockAfterEvaluateAllEvent;
import org.kie.kogito.tracing.decision.mock.MockBeforeEvaluateAllEvent;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.dmn.DecisionTestUtils.DECISION_SERVICE_NODE_ID;
import static org.kie.kogito.dmn.DecisionTestUtils.DECISION_SERVICE_NODE_NAME;
import static org.kie.kogito.dmn.DecisionTestUtils.MODEL_NAME;
import static org.kie.kogito.dmn.DecisionTestUtils.MODEL_NAMESPACE;
import static org.kie.kogito.dmn.DecisionTestUtils.createDMNRuntime;
import static org.kie.kogito.dmn.DecisionTestUtils.getEvaluateAllContext;
import static org.kie.kogito.dmn.DecisionTestUtils.getEvaluateAllContextForError;
import static org.kie.kogito.dmn.DecisionTestUtils.getEvaluateAllContextForWarning;
import static org.kie.kogito.dmn.DecisionTestUtils.getEvaluateDecisionServiceContext;
import static org.kie.kogito.dmn.DecisionTestUtils.getEvaluateDecisionServiceContextForWarning;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class DecisionTracingListenerTest {

    static final String MOCKED_MODEL_NAME = "mockedModelName";
    static final String MOCKED_MODEL_NAMESPACE = "mockedModelNamespace";

    static final String TEST_EXECUTION_ID_1 = "e3140fbb-49fd-4835-bb2e-682bbe02d862";
    static final String TEST_EXECUTION_ID_2 = "77408667-f218-40b0-a355-1bab047a3e9e";

    @Test
    void testListenerWithMockedEventsIsWorking() {
        DMNContextImpl context = new DMNContextImpl();
        DecisionExecutionIdUtils.inject(context, () -> TEST_EXECUTION_ID_1);

        DMNResultImpl result = new DMNResultImpl(new DMNModelImpl());
        result.setContext(context);

        BeforeEvaluateAllEvent beforeEvent = new MockBeforeEvaluateAllEvent(MOCKED_MODEL_NAMESPACE, MOCKED_MODEL_NAME, result);
        AfterEvaluateAllEvent afterEvent = new MockAfterEvaluateAllEvent(MOCKED_MODEL_NAMESPACE, MOCKED_MODEL_NAME, result);

        Consumer<EvaluateEvent> eventConsumer = mock(Consumer.class);
        DecisionTracingListener listener = new DecisionTracingListener(eventConsumer);
        listener.beforeEvaluateAll(beforeEvent);
        listener.afterEvaluateAll(afterEvent);

        ArgumentCaptor<EvaluateEvent> eventCaptor = ArgumentCaptor.forClass(EvaluateEvent.class);
        verify(eventConsumer, times(2)).accept(eventCaptor.capture());

        assertEvaluateAllEvents(eventCaptor.getAllValues(), MOCKED_MODEL_NAMESPACE, MOCKED_MODEL_NAME, TEST_EXECUTION_ID_1);
    }

    @Test
    void testListenerWithRealEvaluateAllIsWorking() {
        testWithRealEvaluateAll(getEvaluateAllContext(), 14);
    }

    @Test
    void testListenerWithRealEvaluateAllWithWarnMessageIsWorking() {
        testWithRealEvaluateAll(getEvaluateAllContextForWarning(), 14);
    }

    @Test
    void testListenerWithRealEvaluateAllWithErrorMessageIsWorking() {
        testWithRealEvaluateAll(getEvaluateAllContextForError(), 10);
    }

    @Test
    void testListenerWithRealEvaluateDecisionServiceIsWorking() {
        testWithRealEvaluateDecisionService(getEvaluateDecisionServiceContext(), 6);
    }

    @Test
    void testListenerWithRealEvaluateDecisionServiceWithWarnMessageIsWorking() {
        testWithRealEvaluateDecisionService(getEvaluateDecisionServiceContextForWarning(), 6);
    }

    @Test
    void testListenerWithRealEvaluateDecisionServiceWithEmptyContextIsWorking() {
        final Map<String, Object> contextVariables = new HashMap<>();
        testWithRealEvaluateDecisionService(contextVariables, 6);
    }

    private static void testWithRealEvaluateAll(Map<String, Object> contextVariables, int expectedEvents) {
        List<EvaluateEvent> events = testWithRealRuntime(contextVariables, expectedEvents, DecisionModel::evaluateAll);
        assertEvaluateAllEvents(events, MODEL_NAMESPACE, MODEL_NAME, TEST_EXECUTION_ID_2);
    }

    private static void testWithRealEvaluateDecisionService(Map<String, Object> contextVariables, int expectedEvents) {
        List<EvaluateEvent> events = testWithRealRuntime(contextVariables, expectedEvents, (model, context) -> model.evaluateDecisionService(context, DECISION_SERVICE_NODE_NAME));
        assertEvaluateDecisionServiceEvents(events, MODEL_NAMESPACE, MODEL_NAME, TEST_EXECUTION_ID_2);
    }

    private static List<EvaluateEvent> testWithRealRuntime(Map<String, Object> contextVariables, int expectedEvents, BiConsumer<DecisionModel, DMNContext> modelConsumer) {
        final DMNRuntime runtime = createDMNRuntime();

        Consumer<EvaluateEvent> eventConsumer = mock(Consumer.class);
        DecisionTracingListener listener = new DecisionTracingListener(eventConsumer);
        runtime.addListener(listener);

        final DecisionModel model = new DmnDecisionModel(runtime, MODEL_NAMESPACE, MODEL_NAME, () -> TEST_EXECUTION_ID_2);
        final DMNContext context = model.newContext(contextVariables);
        modelConsumer.accept(model, context);

        ArgumentCaptor<EvaluateEvent> eventCaptor = ArgumentCaptor.forClass(EvaluateEvent.class);
        verify(eventConsumer, times(expectedEvents)).accept(eventCaptor.capture());

        return eventCaptor.getAllValues();
    }

    private static void assertEvaluateAllEvents(List<EvaluateEvent> evaluateEvents, String modelNamespace, String modelName, String executionId) {
        assertThat(evaluateEvents).hasSizeGreaterThanOrEqualTo(2);

        evaluateEvents.forEach(e -> assertEventMatches(modelNamespace, modelName, executionId, e));

        EvaluateEvent beforeEvent = evaluateEvents.get(0);
        assertThat(beforeEvent.getType()).isSameAs(EvaluateEventType.BEFORE_EVALUATE_ALL);

        EvaluateEvent afterEvent = evaluateEvents.get(evaluateEvents.size() - 1);
        assertThat(afterEvent.getType()).isSameAs(EvaluateEventType.AFTER_EVALUATE_ALL);
    }

    private static void assertEvaluateDecisionServiceEvents(List<EvaluateEvent> evaluateEvents, String modelNamespace, String modelName, String executionId) {
        assertThat(evaluateEvents).hasSizeGreaterThanOrEqualTo(2);

        evaluateEvents.forEach(e -> assertEventMatches(modelNamespace, modelName, executionId, e));

        EvaluateEvent beforeEvent = evaluateEvents.get(0);
        assertThat(beforeEvent.getType()).isSameAs(EvaluateEventType.BEFORE_EVALUATE_DECISION_SERVICE);
        assertThat(beforeEvent.getNodeId()).isEqualTo(DECISION_SERVICE_NODE_ID);
        assertThat(beforeEvent.getNodeName()).isEqualTo(DECISION_SERVICE_NODE_NAME);

        EvaluateEvent afterEvent = evaluateEvents.get(evaluateEvents.size() - 1);
        assertThat(afterEvent.getType()).isSameAs(EvaluateEventType.AFTER_EVALUATE_DECISION_SERVICE);
        assertThat(afterEvent.getNodeId()).isEqualTo(DECISION_SERVICE_NODE_ID);
        assertThat(afterEvent.getNodeName()).isEqualTo(DECISION_SERVICE_NODE_NAME);
    }

    private static void assertEventMatches(String modelNamespace, String modelName, String executionId, EvaluateEvent event) {
        assertThat(event.getModelNamespace() == null && event.getModelName() == null || event.getModelNamespace() != null && event.getModelName() != null).isTrue();
        if (event.getModelNamespace() != null) {
            assertThat(event.getModelNamespace()).isEqualTo(modelNamespace);
            assertThat(event.getModelName()).isEqualTo(modelName);
        }
        assertThat(event.getExecutionId()).isEqualTo(executionId);
    }
}
