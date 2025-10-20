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
package org.kie.kogito.eventdriven.decision;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.decision.DecisionModel;
import org.kie.kogito.decision.DecisionModels;
import org.kie.kogito.dmn.DecisionTestUtils;
import org.kie.kogito.dmn.DmnDecisionModel;
import org.kie.kogito.dmn.rest.KogitoDMNResult;
import org.kie.kogito.event.CloudEventUnmarshallerFactory;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventEmitter;
import org.kie.kogito.event.EventReceiver;
import org.kie.kogito.event.Subscription;
import org.kie.kogito.event.cloudevents.extension.KogitoExtension;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils;
import org.kie.kogito.event.impl.CloudEventConverter;
import org.kie.kogito.event.impl.ObjectCloudEventUnmarshallerFactory;
import org.mockito.ArgumentCaptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.core.provider.ExtensionProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.kie.kogito.dmn.DecisionTestUtils.DECISION_SERVICE_NODE_NAME;
import static org.kie.kogito.dmn.DecisionTestUtils.MODEL_NAME;
import static org.kie.kogito.dmn.DecisionTestUtils.MODEL_NAMESPACE;
import static org.kie.kogito.eventdriven.decision.EventDrivenDecisionController.REQUEST_EVENT_TYPE;
import static org.kie.kogito.eventdriven.decision.EventDrivenDecisionController.RESPONSE_EVENT_TYPE;
import static org.kie.kogito.eventdriven.decision.EventDrivenDecisionController.RESPONSE_FULL_EVENT_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventDrivenDecisionControllerTest {

    private static final String MODEL_NAME_PLACEHOLDER = "%%MODEL_NAME%%";
    private static final String MODEL_NAMESPACE_PLACEHOLDER = "%%MODEL_NAMESPACE%%";
    private static final String EVALUATE_DECISION_PLACEHOLDER = "%%EVALUATE_DECISION%%";
    private static final String FULL_RESULT_PLACEHOLDER = "%%FULL_RESULT%%";
    private static final String FILTERED_CTX_PLACEHOLDER = "%%FILTERED_CTX%%";
    private static final String DATA_PLACEHOLDER = "%%DATA%%";

    private static final String CLOUDEVENT_ID = "a89b61a2-5644-487a-8a86-144855c5dce8";
    private static final String CLOUDEVENT_SOURCE = "SomeEventSource";
    private static final String CLOUDEVENT_SUBJECT = "TheSubject";
    private static final String CLOUDEVENT_TEMPLATE = "" +
            "{\n" +
            "    \"specversion\": \"1.0\",\n" +
            "    \"id\": \"" + CLOUDEVENT_ID + "\",\n" +
            "    \"source\": \"" + CLOUDEVENT_SOURCE + "\",\n" +
            "    \"type\": \"" + REQUEST_EVENT_TYPE + "\",\n" +
            "    \"subject\": \"" + CLOUDEVENT_SUBJECT + "\",\n" +
            "    \"" + KogitoExtension.KOGITO_DMN_MODEL_NAME + "\": " + MODEL_NAME_PLACEHOLDER + ",\n" +
            "    \"" + KogitoExtension.KOGITO_DMN_MODEL_NAMESPACE + "\": " + MODEL_NAMESPACE_PLACEHOLDER + ",\n" +
            "    \"" + KogitoExtension.KOGITO_DMN_EVALUATE_DECISION + "\": " + EVALUATE_DECISION_PLACEHOLDER + ",\n" +
            "    \"" + KogitoExtension.KOGITO_DMN_FULL_RESULT + "\": " + FULL_RESULT_PLACEHOLDER + ",\n" +
            "    \"" + KogitoExtension.KOGITO_DMN_FILTERED_CTX + "\": " + FILTERED_CTX_PLACEHOLDER + ",\n" +
            "    \"data\": " + DATA_PLACEHOLDER + "\n" +
            "}";

    private static final String CLOUDEVENT_IGNORED = "" +
            "{\n" +
            "    \"specversion\": \"1.0\",\n" +
            "    \"id\": \"55c5dce8-5644-487a-8a86-1448a89b61a2\",\n" +
            "    \"source\": \"SomeOtherEventSource\",\n" +
            "    \"type\": \"SomeType\",\n" +
            "    \"data\": {}\n" +
            "}";

    private static final RequestData REQUEST_DATA_EVALUATE_ALL = new RequestData(MODEL_NAME, MODEL_NAMESPACE, null, "" +
            "{\n" +
            "    \"Driver\": {\n" +
            "        \"Age\": 25,\n" +
            "        \"Points\": 13\n" +
            "    },\n" +
            "    \"Violation\": {\n" +
            "        \"Type\": \"speed\",\n" +
            "        \"Actual Speed\": 115,\n" +
            "        \"Speed Limit\": 100\n" +
            "    }\n" +
            "}");

    private static final RequestData REQUEST_DATA_EVALUATE_DECISION_SERVICE = new RequestData(MODEL_NAME, MODEL_NAMESPACE, DECISION_SERVICE_NODE_NAME, "" +
            "{\n" +
            "    \"Violation\": {\n" +
            "        \"Type\": \"speed\",\n" +
            "        \"Actual Speed\": 115,\n" +
            "        \"Speed Limit\": 100\n" +
            "    }\n" +
            "}");

    private static final String TEST_EXECUTION_ID = "11ecbb6f-fb25-4597-88c8-ac7976efe078";

    private static DMNRuntime runtime;

    private static final ObjectMapper objectMapper = CloudEventUtils.Mapper.mapper();

    private EventDrivenDecisionController controller;
    private TestEventReceiver testEventReceiver;
    private EventEmitter eventEmitterMock;
    private DecisionModel decisionModelSpy;
    private DecisionModels decisionModelsMock;

    @BeforeAll
    static void beforeAll() {
        ExtensionProvider.getInstance().registerExtension(KogitoExtension.class, KogitoExtension::new);
        runtime = DecisionTestUtils.createDMNRuntime();
    }

    @BeforeEach
    void beforeEach() {
        testEventReceiver = new TestEventReceiver();
        decisionModelsMock = mock(DecisionModels.class);
        eventEmitterMock = mock(EventEmitter.class);

        // by default there's no execution id supplier, if needed it will be overridden in the specific test
        mockDecisionModel();

        controller = new EventDrivenDecisionController(decisionModelsMock, mock(ConfigBean.class), eventEmitterMock, testEventReceiver);
        controller.subscribe();
    }

    @Test
    void testSubscribe() {
        DecisionModels decisionModelsMock = mock(DecisionModels.class);
        ConfigBean configMock = mock(ConfigBean.class);
        EventEmitter eventEmitterMock = mock(EventEmitter.class);
        EventReceiver eventReceiverMock = mock(EventReceiver.class);

        // option #1: parameters via constructor + parameterless setup
        EventDrivenDecisionController controller1 = new EventDrivenDecisionController(decisionModelsMock, configMock, eventEmitterMock, eventReceiverMock);
        controller1.subscribe();
        verify(eventReceiverMock).subscribe(any(), any());

        reset(eventReceiverMock);

        // option #2: parameterless via constructor + parameters via setup (introduced for Quarkus CDI)
        EventDrivenDecisionController controller2 = new EventDrivenDecisionController();
        controller2.init(decisionModelsMock, configMock, eventEmitterMock, eventReceiverMock);
        controller2.subscribe();
        verify(eventReceiverMock).subscribe(any(), any());
    }

    @Test
    void testHandleEventWithIgnoredCloudEvent() throws IOException {
        testEventReceiver.accept(CLOUDEVENT_IGNORED);
        verify(eventEmitterMock, never()).emit(any());
    }

    @Test
    void testHandleEventWithValidCloudEventProducingOkEvaluateAll() throws IOException {
        testAllDefaultAndFullCloudEventEmittedCombinations(REQUEST_DATA_EVALUATE_ALL, kogitoExtension -> {
            assertThat(kogitoExtension.getExecutionId()).isNull();
            verify(decisionModelSpy).evaluateAll(notNull());
            verify(decisionModelSpy, never()).evaluateDecisionService(any(), any());
            clearInvocations(decisionModelSpy);
        });

    }

    @Test
    void testHandleEventWithValidCloudEventWithExecutionIdProducingOkEvaluateAll() throws IOException {
        mockDecisionModelWithExecutionIdSupplier();
        testAllDefaultAndFullCloudEventEmittedCombinations(REQUEST_DATA_EVALUATE_ALL, kogitoExtension -> {
            assertThat(kogitoExtension.getExecutionId()).isEqualTo(TEST_EXECUTION_ID);
            verify(decisionModelSpy).evaluateAll(notNull());
            verify(decisionModelSpy, never()).evaluateDecisionService(any(), any());
            clearInvocations(decisionModelSpy);
        });
    }

    @Test
    void testHandleEventWithValidCloudEventProducingOkEvaluateDecisionService() throws IOException {
        testAllDefaultAndFullCloudEventEmittedCombinations(REQUEST_DATA_EVALUATE_DECISION_SERVICE, kogitoExtension -> {
            assertThat(kogitoExtension.getExecutionId()).isNull();
            verify(decisionModelSpy, never()).evaluateAll(any());
            verify(decisionModelSpy).evaluateDecisionService(notNull(), notNull());
            clearInvocations(decisionModelSpy);
        });

    }

    @Test
    void testHandleEventWithValidCloudEventWithExecutionIdProducingOkEvaluateDecisionService() throws IOException {
        mockDecisionModelWithExecutionIdSupplier();
        testAllDefaultAndFullCloudEventEmittedCombinations(REQUEST_DATA_EVALUATE_DECISION_SERVICE, kogitoExtension -> {
            assertThat(kogitoExtension.getExecutionId()).isEqualTo(TEST_EXECUTION_ID);
            verify(decisionModelSpy, never()).evaluateAll(any());
            verify(decisionModelSpy).evaluateDecisionService(notNull(), notNull());
            clearInvocations(decisionModelSpy);
        });

    }

    private void assertSubject(DataEvent<?> event) {
        assertThat(event.getSubject()).isNotNull()
                .isEqualTo(CLOUDEVENT_SUBJECT);
    }

    private String cloudEventOkWith(RequestData requestData, Boolean fullResult, Boolean filteredCtx) {
        return CLOUDEVENT_TEMPLATE
                .replace(MODEL_NAME_PLACEHOLDER, format(requestData.getModelName()))
                .replace(MODEL_NAMESPACE_PLACEHOLDER, format(requestData.getModelNamespace()))
                .replace(EVALUATE_DECISION_PLACEHOLDER, format(requestData.getDecision()))
                .replace(FULL_RESULT_PLACEHOLDER, fullResult == null ? "null" : fullResult.toString())
                .replace(FILTERED_CTX_PLACEHOLDER, filteredCtx == null ? "null" : filteredCtx.toString())
                .replace(DATA_PLACEHOLDER, Optional.ofNullable(requestData.getData()).orElse("null"));
    }

    private String format(String input) {
        return Optional.ofNullable(input)
                .map(i -> "\"" + i + "\"")
                .orElse("null");
    }

    private void mockDecisionModel() {
        decisionModelSpy = spy(new DmnDecisionModel(runtime, MODEL_NAMESPACE, MODEL_NAME));
        when(decisionModelsMock.getDecisionModel(eq(MODEL_NAMESPACE), eq(DecisionTestUtils.MODEL_NAME))).thenReturn(decisionModelSpy);
    }

    private void mockDecisionModelWithExecutionIdSupplier() {
        decisionModelSpy = spy(new DmnDecisionModel(runtime, MODEL_NAMESPACE, MODEL_NAME, () -> TEST_EXECUTION_ID));
        when(decisionModelsMock.getDecisionModel(eq(MODEL_NAMESPACE), eq(DecisionTestUtils.MODEL_NAME))).thenReturn(decisionModelSpy);
    }

    private <T> void testCloudEventEmitted(RequestData requestData, Boolean fullResult, Boolean filteredCtx, Class<T> responseDataClass, String expectedType,
            TriConsumer<DataEvent<T>, KogitoExtension, T> callback) throws IOException {
        try {
            ArgumentCaptor<DataEvent<T>> eventCaptor = ArgumentCaptor.forClass(DataEvent.class);

            String inputEvent = cloudEventOkWith(requestData, fullResult, filteredCtx);
            testEventReceiver.accept(inputEvent);

            verify(eventEmitterMock).emit(eventCaptor.capture());

            DataEvent<T> emittedCloudEvent = eventCaptor.getValue();

            assertThat(emittedCloudEvent.getType()).isEqualTo(expectedType);

            KogitoExtension kogitoExtension = ExtensionProvider.getInstance()
                    .parseExtension(KogitoExtension.class, emittedCloudEvent);

            if (kogitoExtension == null) {
                fail("No Kogito extension in emitted CloudEvent: " + emittedCloudEvent);
            }

            assertThat(kogitoExtension.getDmnModelName()).isEqualTo(requestData.getModelName());
            assertThat(kogitoExtension.getDmnModelNamespace()).isEqualTo(requestData.getModelNamespace());
            assertThat(kogitoExtension.getDmnEvaluateDecision()).isEqualTo(requestData.getDecision());

            assertSubject(emittedCloudEvent);

            if (callback != null) {
                callback.accept(emittedCloudEvent, kogitoExtension, emittedCloudEvent.getData());
            }
        } finally {
            reset(eventEmitterMock);
        }
    }

    private void testDefaultCloudEventEmitted(RequestData requestData, Boolean fullResult, Boolean filteredCtx, Consumer<KogitoExtension> callback)
            throws IOException {
        testCloudEventEmitted(requestData, fullResult, filteredCtx, Map.class, RESPONSE_EVENT_TYPE, (cloudEvent, kogitoExtension, data) -> {
            callback.accept(kogitoExtension);
        });
    }

    private void testFullCloudEventEmitted(RequestData requestData, Boolean fullResult, Boolean filteredCtx, Consumer<KogitoExtension> callback)
            throws IOException {
        testCloudEventEmitted(requestData, fullResult, filteredCtx, KogitoDMNResult.class, RESPONSE_FULL_EVENT_TYPE, (cloudEvent, kogitoExtension, data) -> {
            assertThat(data.getNamespace()).isNotNull();
            assertThat(data.getModelName()).isNotNull();
            assertThat(data.getDmnContext()).isNotNull();
            assertThat(data.getMessages()).isNotNull();
            assertThat(data.getDecisionResults()).isNotNull();
            callback.accept(kogitoExtension);
        });
    }

    private void testAllDefaultAndFullCloudEventEmittedCombinations(RequestData requestData, Consumer<KogitoExtension> consumer) throws IOException {
        testDefaultCloudEventEmitted(requestData, null, null, consumer);
        testDefaultCloudEventEmitted(requestData, null, false, consumer);
        testDefaultCloudEventEmitted(requestData, null, true, consumer);
        testDefaultCloudEventEmitted(requestData, false, null, consumer);
        testDefaultCloudEventEmitted(requestData, false, false, consumer);
        testDefaultCloudEventEmitted(requestData, false, true, consumer);
        testFullCloudEventEmitted(requestData, true, null, consumer);
        testFullCloudEventEmitted(requestData, true, false, consumer);
        testFullCloudEventEmitted(requestData, true, true, consumer);
    }

    @FunctionalInterface
    private interface TriConsumer<T, U, V> {

        void accept(T t, U u, V v);
    }

    private static class RequestData {

        private final String modelName;
        private final String modelNamespace;
        private final String decision;
        private final String data;

        public RequestData(String modelName, String modelNamespace, String decision, String data) {
            this.modelName = modelName;
            this.modelNamespace = modelNamespace;
            this.decision = decision;
            this.data = data;
        }

        public String getModelName() {
            return modelName;
        }

        public String getModelNamespace() {
            return modelNamespace;
        }

        public String getDecision() {
            return decision;
        }

        public String getData() {
            return data;
        }
    }

    private static class TestEventReceiver implements EventReceiver {

        private Subscription subscription;
        private CloudEventUnmarshallerFactory unmarshaller = new ObjectCloudEventUnmarshallerFactory(objectMapper);

        public void accept(String message) throws IOException {
            subscription.getConsumer().accept(subscription.getConverter().convert(message));
        }

        @Override
        public <T> void subscribe(Consumer<DataEvent<T>> consumer, Class<T> clazz) {
            subscription = new Subscription(consumer, new CloudEventConverter<>(clazz, unmarshaller));
        }
    }
}
