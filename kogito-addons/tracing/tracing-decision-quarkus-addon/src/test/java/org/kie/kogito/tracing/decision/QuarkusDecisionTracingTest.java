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
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import io.cloudevents.json.Json;
import io.cloudevents.v1.CloudEventImpl;
import io.reactivex.subscribers.TestSubscriber;
import io.vertx.core.eventbus.EventBus;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.Application;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QuarkusDecisionTracingTest {

    private static final String TEST_EXECUTION_ID = "7c50581e-6e5b-407b-91d6-2ffb1d47ebc0";

    @Test
    public void test_ListenerAndCollector_UseRealEvents_Working() {
        final String modelResource = "/Traffic Violation.dmn";
        final String modelNamespace = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
        final String modelName = "Traffic Violation";

        final DMNRuntime runtime = DMNKogito.createGenericDMNRuntime(new java.io.InputStreamReader(
                QuarkusDecisionTracingTest.class.getResourceAsStream(modelResource)
        ));

        EventBus eventBus = mock(EventBus.class);

        QuarkusDecisionTracingListener listener = new QuarkusDecisionTracingListener(eventBus);
        runtime.addListener(listener);

        final Map<String, Object> driver = new HashMap<>();
        driver.put("Age", 25);
        driver.put("Points", 10);
        final Map<String, Object> violation = new HashMap<>();
        violation.put("Type", "speed");
        violation.put("Actual Speed", 105);
        violation.put("Speed Limit", 100);
        final Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("Driver", driver);
        contextVariables.put("Violation", violation);

        final DecisionModel model = new DmnDecisionModel(runtime, modelNamespace, modelName, () -> TEST_EXECUTION_ID);
        final DMNContext context = model.newContext(contextVariables);
        model.evaluateAll(context);

        ArgumentCaptor<EvaluateEvent> eventCaptor = ArgumentCaptor.forClass(EvaluateEvent.class);

        verify(eventBus, times(14)).send(eq("kogito-tracing-decision_EvaluateEvent"), eventCaptor.capture());

        TestSubscriber<String> subscriber = new TestSubscriber<>();

        final DecisionModels mockedDecisionModels = mock(DecisionModels.class);
        when(mockedDecisionModels.getDecisionModel(modelNamespace, modelName)).thenReturn(model);
        final Application mockedApplication = mock(Application.class);
        when(mockedApplication.decisionModels()).thenReturn(mockedDecisionModels);

        QuarkusTraceEventEmitter eventEmitter = new QuarkusTraceEventEmitter();
        QuarkusDecisionTracingCollector collector = new QuarkusDecisionTracingCollector(mockedApplication, eventEmitter);
        eventEmitter.getEventPublisher().subscribe(subscriber);
        eventCaptor.getAllValues().forEach(collector::onEvent);

        subscriber.assertValueCount(1);

        CloudEventImpl<JsonNode> cloudEvent = Json.decodeValue(subscriber.values().get(0), CloudEventImpl.class, JsonNode.class);
        assertEquals(TEST_EXECUTION_ID, cloudEvent.getAttributes().getId());
    }
}
