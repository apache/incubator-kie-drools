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
import org.kie.kogito.event.EventMarshaller;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Signal;
import org.kie.kogito.services.event.AbstractProcessDataEvent;
import org.kie.kogito.services.event.EventConsumer;
import org.kie.kogito.services.event.EventConsumerFactory;
import org.kie.kogito.services.event.impl.DefaultEventMarshaller;
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
        public DummyModel fromMap(Map<String, Object> params) {
            this.dummyEvent = (DummyEvent) params.get("dummyEvent");
            return this;
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
            this(dummyEvent, null);
        }

        public DummyCloudEvent(DummyEvent dummyEvent, String referenceId) {
            super("dummyTopic", dummyEvent, "1", "1", "1", "1", "1", "1", null);
            super.kogitoReferenceId = referenceId;
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
        EventConsumer<DummyModel> consumer = factory.get(DummyModel::new, true);
        final String trigger = "dummyTopic";
        consumer.consume(application, process, new DummyCloudEvent(new DummyEvent("pepe"), "1"), trigger);
        ArgumentCaptor<Signal> signal = ArgumentCaptor.forClass(Signal.class);
        verify(processInstance, times(1)).send(signal.capture());
        assertEquals("1", signal.getValue().referenceId());
        assertTrue(signal.getValue().payload() instanceof DummyEvent);
        assertEquals("pepe", ((DummyEvent) signal.getValue().payload()).getDummyField());
    }

    @Test
    void testCloudEvent() {
        EventConsumer<DummyModel> consumer =
                factory.get(DummyModel::new, true);
        final String trigger = "dummyTopic";
        consumer.consume(application, process, new DummyCloudEvent(new DummyEvent("pepe")), trigger);
        verify(processInstance, times(1)).start(trigger, "1");
    }

    @Test
    void testDataEvent() {
        EventConsumer<DummyModel> consumer =
                factory.get(DummyModel::new, false);
        final String trigger = "dummyTopic";
        consumer.consume(application, process, new DummyEvent("pepe"), trigger);
        verify(processInstance, times(1)).start(trigger, null);
    }

    @Test
    void testDataMarshaller() {
        DummyEvent dataEvent = new DummyEvent("pepe");
        assertEquals(
                "{\"dummyField\":\"pepe\"}",
                marshaller.marshall(dataEvent));
    }

    @Test
    void testEventMarshaller() {
        DummyEvent dataEvent = new DummyEvent("pepe");
        String jsonString = marshaller.marshall(dataEvent);
        assertTrue(jsonString.contains("\"dummyField\":\"pepe\""));
    }

    @Test
    void testEventPayloadException() {
        EventConsumer<DummyModel> consumer = factory.get(DummyModel::new, true);
        final String trigger = "dummyTopic";
        final String payload = "{ a = b }";
        assertThrows(ClassCastException.class, () -> consumer.consume(application, process, payload, trigger));
    }
}
