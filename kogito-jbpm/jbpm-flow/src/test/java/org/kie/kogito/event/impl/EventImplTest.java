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
package org.kie.kogito.event.impl;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.Model;
import org.kie.kogito.event.CloudEventExtensionConstants;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Signal;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventConsumer;
import org.kie.kogito.services.event.EventConsumerFactory;
import org.kie.kogito.services.event.EventMarshaller;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EventImplTest {

    private static class DummyEvent {

        private String dummyField;

        public DummyEvent() {
        }

        public DummyEvent(String dummyField) {
            this.dummyField = dummyField;
        }

        public String getDummyField() {
            return dummyField;
        }
    }

    private static class DummyModel implements Model {
        private DummyEvent dummyEvent;

        @Override
        public void fromMap(Map<String, Object> params) {
            this.dummyEvent = (DummyEvent) params.get("dummyEvent");
        }

        @Override
        public Map<String, Object> toMap() {
            return Collections.singletonMap("dummyEvent", dummyEvent);
        }

        public DummyModel(DummyEvent dummyEvent) {
            this.dummyEvent = dummyEvent;
        }

        @Override
        public void update(Map<String, Object> params) {
            fromMap(params);
        }
    }

    private static class DummyCloudEvent extends AbstractProcessDataEvent<DummyEvent> {

        public DummyCloudEvent() {
        }

        public DummyCloudEvent(DummyEvent dummyEvent) {
            super("", dummyEvent, "1", null, null, null, null, null, null);
        }
    }

    private static EventConsumerFactory factory;
    private static EventMarshaller marshaller;

    @BeforeAll
    static void init() {
        factory = new DefaultEventConsumerFactory();
        marshaller = new DefaultEventMarshaller();
    }

    private Process<DummyModel> process;
    private ProcessInstance<DummyModel> processInstance;
    private ProcessInstances<DummyModel> processInstances;
    private Application application;

    @BeforeEach
    void setup() {

        application = mock(Application.class);
        when(application.unitOfWorkManager())
                .thenReturn(new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));

        process = mock(Process.class);
        processInstances = mock(ProcessInstances.class);
        processInstance = mock(ProcessInstance.class);

        when(process.instances()).thenReturn(processInstances);
        when(processInstances.findById(anyString())).thenReturn(Optional.of(processInstance));
        when(process.createInstance(Mockito.any(DummyModel.class))).thenReturn(processInstance);

    }

    @Test
    void testSigCloudEvent() {
        EventConsumer<DummyModel> consumer = factory.get(DummyModel::new, DummyEvent.class, DummyCloudEvent.class, Optional.of(true));
        final String trigger = "dummyTopic";
        final String payload = "{ \"specversion\": \"0.3\"," +
                "\"id\": \"21627e26-31eb-43e7-8343-92a696fd96b1\"," +
                "\"source\": \"\"," +
                "\"type\": \"dummyTopic\"," +
                "\"time\": \"2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]\"," +
                "\"" + CloudEventExtensionConstants.PROCESS_REFERENCE_ID + "\": \"1\"," +
                "\"" + CloudEventExtensionConstants.PROCESS_INSTANCE_ID + "\": \"1\"," +
                "\"data\": {\"dummyField\" : \"pepe\"}}";

        consumer.consume(application, process, payload, trigger);
        ArgumentCaptor<Signal> signal = ArgumentCaptor.forClass(Signal.class);
        verify(processInstance, times(1)).send(signal.capture());
        assertEquals("1", signal.getValue().referenceId());
        assertTrue(signal.getValue().payload() instanceof DummyEvent);
        assertEquals("pepe", ((DummyEvent) signal.getValue().payload()).getDummyField());
    }

    @Test
    void testCloudEvent() {
        EventConsumer<DummyModel> consumer =
                factory.get(DummyModel::new, DummyEvent.class, DummyCloudEvent.class, Optional.empty());
        final String trigger = "dummyTopic";
        final String payload = "{ \"specversion\": \"0.3\"," +
                "\"id\": \"21627e26-31eb-43e7-8343-92a696fd96b1\"," +
                "\"source\": \"\"," +
                "\"type\": \"dummyTopic\"," +
                "\"time\": \"2019-10-01T12:02:23.812262+02:00[Europe/Warsaw]\"," +
                "\"" + CloudEventExtensionConstants.PROCESS_INSTANCE_ID + "\": \"1\"," +
                "\"data\": {\"dummyField\" : \"pepe\"}}";

        consumer.consume(application, process, payload, trigger);
        verify(processInstance, times(1)).start(trigger, "1");
    }

    @Test
    void testDataEvent() {
        EventConsumer<DummyModel> consumer =
                factory.get(DummyModel::new, DummyEvent.class, DummyCloudEvent.class, Optional.of(false));
        final String trigger = "dummyTopic";
        final String payload = "{\"dummyField\" : \"pepe\"}";
        consumer.consume(application, process, payload, trigger);
        verify(processInstance, times(1)).start(trigger, null);
    }

    @Test
    void testDataMarshaller() {
        DummyEvent dataEvent = new DummyEvent("pepe");
        assertEquals(
                "{\"dummyField\":\"pepe\"}",
                marshaller.marshall(dataEvent, DummyCloudEvent::new, Optional.of(false)));
    }

    @Test
    void testCloudEventMarshaller() {
        DummyEvent dataEvent = new DummyEvent("pepe");
        String jsonString = marshaller.marshall(dataEvent, DummyCloudEvent::new, Optional.empty());
        assertTrue(jsonString.contains("\"dummyField\":\"pepe\""));
        assertTrue(jsonString.contains("\"" + CloudEventExtensionConstants.PROCESS_INSTANCE_ID + "\":\"1\""));
    }

    @Test
    void testCloudEventPayloadException() {
        EventConsumer<DummyModel> consumer = factory.get(DummyModel::new, DummyEvent.class, DummyCloudEvent.class, Optional.empty());
        final String trigger = "dummyTopic";
        final String payload = "{ a = b }";
        assertThrows(IllegalStateException.class, () -> consumer.consume(application, process, payload, trigger));
    }
}
