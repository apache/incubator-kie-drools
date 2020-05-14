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

package org.kie.kogito.tracing.decision;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.tracing.decision.event.EvaluateEvent;
import org.kie.kogito.tracing.decision.mock.MockAfterEvaluateAllEvent;
import org.kie.kogito.tracing.decision.mock.MockBeforeEvaluateAllEvent;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.tracing.decision.mock.MockUtils.TEST_MODEL_NAME;
import static org.kie.kogito.tracing.decision.mock.MockUtils.TEST_MODEL_NAMESPACE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DecisionTracingListenerTest {

    private static final String TEST_EXECUTION_ID_1 = "e3140fbb-49fd-4835-bb2e-682bbe02d862";
    private static final String TEST_EXECUTION_ID_2 = "77408667-f218-40b0-a355-1bab047a3e9e";

    @Test
    public void test_Listener_UseMockedEvents_Working() {
        DMNContextImpl context = new DMNContextImpl();
        DecisionExecutionIdUtils.inject(context, () -> TEST_EXECUTION_ID_1);

        DMNResultImpl result = new DMNResultImpl(new DMNModelImpl());
        result.setContext(context);

        BeforeEvaluateAllEvent beforeEvent = new MockBeforeEvaluateAllEvent(TEST_MODEL_NAMESPACE, TEST_MODEL_NAME, result);
        AfterEvaluateAllEvent afterEvent = new MockAfterEvaluateAllEvent(TEST_MODEL_NAMESPACE, TEST_MODEL_NAME, result);

        Consumer<EvaluateEvent> eventConsumer = mock(Consumer.class);
        DecisionTracingListener listener = new DecisionTracingListener(eventConsumer);
        listener.beforeEvaluateAll(beforeEvent);
        listener.afterEvaluateAll(afterEvent);

        ArgumentCaptor<EvaluateEvent> eventCaptor = ArgumentCaptor.forClass(EvaluateEvent.class);
        verify(eventConsumer, times(2)).accept(eventCaptor.capture());

        assertEvaluateEvents(eventCaptor.getAllValues(), TEST_MODEL_NAMESPACE, TEST_MODEL_NAME, TEST_EXECUTION_ID_1);
    }

    @Test
    public void test_Listener_UseRealEvents_Working() {
        final String modelResource = "/Traffic Violation.dmn";
        final String modelNamespace = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
        final String modelName = "Traffic Violation";

        final DMNRuntime runtime = DMNKogito.createGenericDMNRuntime(new java.io.InputStreamReader(
                DecisionTracingListenerTest.class.getResourceAsStream(modelResource)
        ));

        Consumer<EvaluateEvent> eventConsumer = mock(Consumer.class);
        DecisionTracingListener listener = new DecisionTracingListener(eventConsumer);
        runtime.addListener(listener);

        final Map<String, Object> driver = new HashMap<>();
        driver.put("Points", 10);
        final Map<String, Object> violation = new HashMap<>();
        violation.put("Type", "speed");
        violation.put("Actual Speed", 105);
        violation.put("Speed Limit", 100);
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Driver", driver);
        contextVariables.put("Violation", violation);

        final DecisionModel model = new DmnDecisionModel(runtime, modelNamespace, modelName, () -> TEST_EXECUTION_ID_2);
        final DMNContext context = model.newContext(contextVariables);
        model.evaluateAll(context);

        ArgumentCaptor<EvaluateEvent> eventCaptor = ArgumentCaptor.forClass(EvaluateEvent.class);
        verify(eventConsumer, times(2)).accept(eventCaptor.capture());

        assertEvaluateEvents(eventCaptor.getAllValues(), modelNamespace, modelName, TEST_EXECUTION_ID_2);
    }

    private static void assertEvaluateEvents(List<EvaluateEvent> evaluateEvents, String modelNamespace, String modelName, String executionId) {
        assertEquals(2, evaluateEvents.size());
        assertTrue(evaluateEvents.get(0) instanceof org.kie.kogito.tracing.decision.event.BeforeEvaluateAllEvent);
        assertTrue(evaluateEvents.get(1) instanceof org.kie.kogito.tracing.decision.event.AfterEvaluateAllEvent);

        org.kie.kogito.tracing.decision.event.BeforeEvaluateAllEvent beforeEvaluateAllEvent =
                (org.kie.kogito.tracing.decision.event.BeforeEvaluateAllEvent) evaluateEvents.get(0);
        assertEquals(executionId, beforeEvaluateAllEvent.getExecutionId());
        assertEquals(modelName, beforeEvaluateAllEvent.getModelName());
        assertEquals(modelNamespace, beforeEvaluateAllEvent.getModelNamespace());

        org.kie.kogito.tracing.decision.event.AfterEvaluateAllEvent afterEvaluateAllEvent =
                (org.kie.kogito.tracing.decision.event.AfterEvaluateAllEvent) evaluateEvents.get(1);
        assertEquals(executionId, afterEvaluateAllEvent.getExecutionId());
        assertEquals(modelName, afterEvaluateAllEvent.getModelName());
        assertEquals(modelNamespace, afterEvaluateAllEvent.getModelNamespace());
    }

}
