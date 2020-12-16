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
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
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
import org.kie.kogito.tracing.decision.event.CloudEventUtils;
import org.kie.kogito.tracing.decision.event.evaluate.EvaluateEvent;
import org.kie.kogito.tracing.decision.event.trace.TraceEvent;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpringBootDecisionTracingTest {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(JsonFormat.getCloudEventJacksonModule());

    private static final String TEST_MODEL_RESOURCE = "/Traffic Violation.dmn";
    private static final String TEST_MODEL_NAMESPACE = "https://github.com/kiegroup/drools/kie-dmn/_A4BCA8B8-CF08-433F-93B2-A2598F19ECFF";
    private static final String TEST_MODEL_NAME = "Traffic Violation";

    private static final String TEST_EXECUTION_ID = "7c50581e-6e5b-407b-91d6-2ffb1d47ebc0";
    private static final Map<String, Object> TEST_CONTEXT_VARIABLES = new HashMap<String, Object>() {{
        put("Driver", new HashMap<String, Object>() {{
            put("Age", 25);
            put("Points", 10);
        }});
        put("Violation", new HashMap<String, Object>() {{
            put("Type", "speed");
            put("Actual Speed", 105);
            put("Speed Limit", 100);
        }});
    }};
    private static final String TEST_SERVICE_URL = "localhost:8080";
    private static final String TEST_KAFKA_TOPIC = "kogito-tracing-decision";

    @Test
    void testAsyncListenerAndCollectorWithRealEventsIsWorking() throws IOException {
        final DMNRuntime runtime = buildDMNRuntime();
        final DecisionModel model = buildDecisionModel(runtime);
        final List<EvaluateEvent> events = testListener(true, runtime, model);
        testCollector(events, model);
    }

    @Test
    void testSyncListenerAndCollectorWithRealEventsIsWorking() throws IOException {
        final DMNRuntime runtime = buildDMNRuntime();
        final DecisionModel model = buildDecisionModel(runtime);
        final List<EvaluateEvent> events = testListener(false, runtime, model);
        testCollector(events, model);
    }

    private DMNRuntime buildDMNRuntime() {
        return DMNKogito.createGenericDMNRuntime(new java.io.InputStreamReader(
                SpringBootDecisionTracingTest.class.getResourceAsStream(TEST_MODEL_RESOURCE)
        ));
    }

    private DecisionModel buildDecisionModel(DMNRuntime runtime) {
        return new DmnDecisionModel(runtime, TEST_MODEL_NAMESPACE, TEST_MODEL_NAME, () -> TEST_EXECUTION_ID);
    }

    private List<EvaluateEvent> testListener(boolean asyncEnabled, DMNRuntime runtime, DecisionModel model) {

        final ApplicationEventPublisher mockedEventPublisher = mock(ApplicationEventPublisher.class);
        final SpringBootDecisionTracingCollector mockedCollector = mock(SpringBootDecisionTracingCollector.class);

        SpringBootDecisionTracingListener listener = new SpringBootDecisionTracingListener(mockedEventPublisher, mockedCollector, asyncEnabled);
        runtime.addListener(listener);

        final DMNContext context = model.newContext(TEST_CONTEXT_VARIABLES);
        model.evaluateAll(context);

        ArgumentCaptor<EvaluateEvent> eventCaptor = ArgumentCaptor.forClass(EvaluateEvent.class);

        if (asyncEnabled) {
            verify(mockedEventPublisher, times(14)).publishEvent(eventCaptor.capture());
            verify(mockedCollector, never()).onApplicationEvent(any());
        } else {
            verify(mockedEventPublisher, never()).publishEvent(any());
            verify(mockedCollector, times(14)).onApplicationEvent(eventCaptor.capture());
        }

        return eventCaptor.getAllValues();
    }

    @SuppressWarnings("unchecked")
    private void testCollector(List<EvaluateEvent> events, DecisionModel model) throws IOException {
        final DecisionModels mockedDecisionModels = mock(DecisionModels.class);
        when(mockedDecisionModels.getDecisionModel(TEST_MODEL_NAMESPACE, TEST_MODEL_NAME)).thenReturn(model);

        final Application mockedApplication = mock(Application.class);
        when(mockedApplication.get(any())).thenReturn(mockedDecisionModels);

        final ConfigBean configBean = new StaticConfigBean(TEST_SERVICE_URL, true);

        final KafkaTemplate<String, String> mockedTemplate = mock(KafkaTemplate.class);
        final SpringBootTraceEventEmitter eventEmitter = new SpringBootTraceEventEmitter(mockedTemplate, TEST_KAFKA_TOPIC);

        SpringBootDecisionTracingCollector collector = new SpringBootDecisionTracingCollector(eventEmitter, configBean, mockedApplication);
        events.forEach(collector::onApplicationEvent);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockedTemplate).send(eq(TEST_KAFKA_TOPIC), payloadCaptor.capture());

        CloudEvent cloudEvent = CloudEventUtils
                .decode(payloadCaptor.getValue())
                .orElseThrow(() -> new IllegalStateException("Can't decode CloudEvent"));

        assertEquals(TEST_EXECUTION_ID, cloudEvent.getId());
        assertNotNull(cloudEvent.getData());

        TraceEvent traceEvent = MAPPER.readValue(cloudEvent.getData(), TraceEvent.class);

        assertNotNull(traceEvent);
        assertEquals(TEST_SERVICE_URL, traceEvent.getHeader().getResourceId().getServiceUrl());
    }
}
