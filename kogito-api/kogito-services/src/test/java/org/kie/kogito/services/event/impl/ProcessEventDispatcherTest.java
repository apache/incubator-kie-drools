/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.services.event.impl;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.event.EventDispatcher;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.ProcessService;
import org.kie.kogito.services.event.DummyCloudEvent;
import org.kie.kogito.services.event.DummyEvent;
import org.kie.kogito.services.event.DummyModel;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessEventDispatcherTest {

    public static final String DUMMY_TOPIC = "dummyTopic";
    private Process<DummyModel> process;
    private Application application;
    private ProcessService processService;
    private ExecutorService executor;
    private ProcessInstance<DummyModel> processInstance;
    private ProcessInstances<DummyModel> processInstances;

    @BeforeEach
    void setup() {
        application = mock(Application.class);
        when(application.unitOfWorkManager())
                .thenReturn(new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));

        process = mock(Process.class);
        processInstances = mock(ProcessInstances.class);
        processInstance = mock(ProcessInstance.class);
        when(processInstance.id()).thenReturn("1");
        when(process.instances()).thenReturn(processInstances);
        when(processInstances.findById(Mockito.anyString())).thenReturn(Optional.empty());
        when(processInstances.findById("1")).thenReturn(Optional.of(processInstance));
        processService = mock(ProcessService.class);
        when(processService.createProcessInstance(eq(process), any(), any(), any(), any(), any())).thenReturn(processInstance);
        when(processService.signalProcessInstance(eq(process), any(), any(), any())).thenReturn(Optional.of(mock(DummyModel.class)));
        executor = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    void close() {
        executor.shutdown();
    }

    private <T> Optional<Function<T, DummyModel>> modelConverter() {
        return Optional.of(DummyModel::new);
    }

    @Test
    void testSigCloudEvent() throws Exception {
        EventDispatcher<DummyModel> dispatcher = new ProcessEventDispatcher<>(process, null, processService, executor);
        ProcessInstance<DummyModel> instance = dispatcher.dispatch(DUMMY_TOPIC, new DummyCloudEvent(new DummyEvent("pepe"), DUMMY_TOPIC, "source", "1")).toCompletableFuture().get();

        ArgumentCaptor<String> signal = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> processInstanceId = ArgumentCaptor.forClass(String.class);

        verify(processService, times(1)).signalProcessInstance(Mockito.any(Process.class), processInstanceId.capture(), Mockito.any(Object.class), signal.capture());

        assertEquals("Message-" + DUMMY_TOPIC, signal.getValue());
        assertEquals("1", processInstanceId.getValue());
        assertEquals(instance, processInstance);
    }

    @Test
    void testCloudEventNewInstanceWithoutReference() throws Exception {
        EventDispatcher<DummyModel> dispatcher = new ProcessEventDispatcher<>(process, modelConverter().get(), processService, executor);
        ProcessInstance<DummyModel> instance = dispatcher.dispatch(DUMMY_TOPIC, new DummyCloudEvent(new DummyEvent("pepe"), DUMMY_TOPIC)).toCompletableFuture().get();

        ArgumentCaptor<String> signal = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> referenceId = ArgumentCaptor.forClass(String.class);

        verify(processInstances, never()).findById(any());
        verify(processService, never()).signalProcessInstance(eq(process), any(), any(), signal.capture());
        verify(processService, times(1)).createProcessInstance(eq(process), any(), any(DummyModel.class), any(), signal.capture(), referenceId.capture());

        assertEquals(DUMMY_TOPIC, signal.getValue());
        assertEquals("1", referenceId.getValue());
        assertEquals(instance, processInstance);
    }

    @Test
    void testCloudEventNewInstanceWithReference() throws Exception {
        EventDispatcher<DummyModel> dispatcher = new ProcessEventDispatcher<>(process, modelConverter().get(), processService, executor);
        ProcessInstance<DummyModel> instance = dispatcher.dispatch(DUMMY_TOPIC, new DummyCloudEvent(new DummyEvent("pepe"), DUMMY_TOPIC, "source", "invalidReference")).toCompletableFuture().get();

        ArgumentCaptor<String> signal = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> referenceId = ArgumentCaptor.forClass(String.class);

        verify(processInstances, times(1)).findById("invalidReference");
        verify(processService, never()).signalProcessInstance(eq(process), any(), any(), signal.capture());
        verify(processService, times(1)).createProcessInstance(eq(process), any(), any(DummyModel.class), any(), signal.capture(), referenceId.capture());

        assertEquals(DUMMY_TOPIC, signal.getValue());
        assertEquals("1", referenceId.getValue());
        assertEquals(instance, processInstance);
    }

    @Test
    void testDataEvent() throws Exception {
        EventDispatcher<DummyModel> dispatcher = new ProcessEventDispatcher<>(process, modelConverter().get(), processService, executor);
        ProcessInstance<DummyModel> instance = dispatcher.dispatch(DUMMY_TOPIC, new DummyEvent("pepe")).toCompletableFuture().get();

        ArgumentCaptor<String> signal = ArgumentCaptor.forClass(String.class);
        verify(processService, times(1)).createProcessInstance(eq(process), any(), any(DummyModel.class), any(), signal.capture(), isNull());
        assertEquals(DUMMY_TOPIC, signal.getValue());
        assertEquals(instance, processInstance);
    }

    @Test
    void testIgnoredDataEvent() throws Exception {
        EventDispatcher<DummyModel> dispatcher = new ProcessEventDispatcher<>(process, null, processService, executor);
        final String payload = "{ a = b }";
        ProcessInstance<DummyModel> result = dispatcher.dispatch(DUMMY_TOPIC, payload).toCompletableFuture().get();
        assertNull(result);
    }

    @Test
    void testIgnoredCloudEvent() throws Exception {
        EventDispatcher<DummyModel> dispatcher = new ProcessEventDispatcher<>(process, modelConverter().get(), processService, executor);
        final DummyCloudEvent payload = new DummyCloudEvent(new DummyEvent("test"), "differentTopic", "differentSource");
        ProcessInstance<DummyModel> result = dispatcher.dispatch(DUMMY_TOPIC, payload).toCompletableFuture().get();
        assertNull(result);
    }
}
