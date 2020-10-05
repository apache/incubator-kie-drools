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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
import io.reactivex.subscribers.TestSubscriber;
import io.vertx.core.eventbus.EventBus;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.Application;
import org.kie.kogito.conf.ConfigBean;
import org.kie.kogito.conf.StaticConfigBean;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DMNKogito;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class QuarkusDecisionTracingTest {

    static final String MODEL_NAMESPACE = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
    static final String MODEL_NAME = "Traffic Violation";

    private static final String MODEL_RESOURCE = "/Traffic Violation.dmn";
    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(JsonFormat.getCloudEventJacksonModule());
    private static final String TEST_EXECUTION_ID = "7c50581e-6e5b-407b-91d6-2ffb1d47ebc0";
    private static final String TEST_SERVICE_URL = "localhost:8080";

    @Test
    public void test_ListenerAndCollector_UseRealEvents_Working() throws IOException {
        final DMNRuntime runtime = DMNKogito.createGenericDMNRuntime(new java.io.InputStreamReader(
                QuarkusDecisionTracingTest.class.getResourceAsStream(MODEL_RESOURCE)
        ));

        ConfigBean configBean = new StaticConfigBean(TEST_SERVICE_URL);
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

        final DecisionModel model = new DmnDecisionModel(runtime, MODEL_NAMESPACE, MODEL_NAME, () -> TEST_EXECUTION_ID);
        final DMNContext context = model.newContext(contextVariables);
        model.evaluateAll(context);

        ArgumentCaptor<EvaluateEvent> eventCaptor = ArgumentCaptor.forClass(EvaluateEvent.class);

        verify(eventBus, times(14)).send(eq("kogito-tracing-decision_EvaluateEvent"), eventCaptor.capture());

        TestSubscriber<String> subscriber = new TestSubscriber<>();

        final DecisionModels mockedDecisionModels = mock(DecisionModels.class);
        when(mockedDecisionModels.getDecisionModel(MODEL_NAMESPACE, MODEL_NAME)).thenReturn(model);
        final Application mockedApplication = mock(Application.class);
        when(mockedApplication.decisionModels()).thenReturn(mockedDecisionModels);

        QuarkusTraceEventEmitter eventEmitter = new QuarkusTraceEventEmitter();
        QuarkusDecisionTracingCollector collector = new QuarkusDecisionTracingCollector(mockedApplication, eventEmitter, configBean);
        eventEmitter.getEventPublisher().subscribe(subscriber);
        eventCaptor.getAllValues().forEach(collector::onEvent);

        subscriber.assertValueCount(1);

        CloudEvent cloudEvent = MAPPER.readValue(subscriber.values().get(0), CloudEvent.class);
        assertEquals(TEST_EXECUTION_ID, cloudEvent.getId());
        assertNotNull(cloudEvent.getData());
        TraceEvent traceEvent = MAPPER.readValue(cloudEvent.getData(), TraceEvent.class);
        assertNotNull(traceEvent);
        assertEquals(TEST_SERVICE_URL, traceEvent.getHeader().getResourceId().getServiceUrl());
    }
}
