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
package org.kie.kogito.process.impl;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.MappableToModel;
import org.kie.kogito.Model;
import org.kie.kogito.config.ConfigBean;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.kie.kogito.uow.WorkUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Integration tests for ProcessServiceImpl signal handling.
 * Tests validation of signals for both traditional signal events and ad hoc nodes.
 */
class ProcessServiceImplSignalTest {

    private ProcessServiceImpl processService;
    private Application application;
    private Process<TestModel> process;
    private ProcessInstances<TestModel> processInstances;
    private ProcessInstance<TestModel> processInstance;
    private UnitOfWorkManager unitOfWorkManager;
    private ConfigBean configBean;

    @BeforeEach
    void setup() {
        application = mock(Application.class);
        process = mock(Process.class);
        processInstances = mock(ProcessInstances.class);
        processInstance = mock(ProcessInstance.class);
        unitOfWorkManager = mock(UnitOfWorkManager.class);
        configBean = mock(ConfigBean.class);

        when(application.unitOfWorkManager()).thenReturn(unitOfWorkManager);
        when(application.config()).thenReturn(mock(org.kie.kogito.Config.class));
        when(application.config().get(ConfigBean.class)).thenReturn(configBean);
        when(configBean.processInstanceLimit()).thenReturn((short) 100);
        when(process.instances()).thenReturn(processInstances);

        // Setup UnitOfWorkManager to execute code immediately
        org.kie.kogito.uow.UnitOfWork unitOfWork = mock(org.kie.kogito.uow.UnitOfWork.class);
        when(unitOfWorkManager.newUnitOfWork()).thenReturn(unitOfWork);
        when(unitOfWorkManager.currentUnitOfWork()).thenReturn(unitOfWork);
        doAnswer(invocation -> {
            org.kie.kogito.uow.WorkUnit<?> workUnit = invocation.getArgument(0);
            workUnit.perform();
            return null;
        }).when(unitOfWork).intercept(any());

        processService = new ProcessServiceImpl(application);
    }

    @Test
    void testSignalProcessInstance_WithTraditionalSignalEvent() {
        // Given: Process instance waiting for a signal event
        String processInstanceId = "test-signal-instance1";
        String signalName = "HelloMartin";
        Object signalData = "test-data";
        TestModel model = new TestModel();

        when(processInstances.findById(eq(processInstanceId), any(ProcessInstanceReadMode.class)))
                .thenReturn(Optional.of(processInstance));
        when(processInstances.acceptingEventType(signalName, processInstanceId))
                .thenReturn(Stream.of(processInstance));
        when(processInstance.checkError()).thenReturn(processInstance);
        when(processInstance.variables()).thenReturn(model);

        // When: Signal is sent
        Optional<TestModel> result = processService.signalProcessInstance(process, processInstanceId, signalData, signalName);

        // Then: Signal should be accepted
        assertThat(result).isPresent();
        verify(processInstance).send(any());
    }

    @Test
    void testSignalProcessInstance_WithAdHocNode() {
        // Given: Process instance with ad hoc node
        String processInstanceId = "test-signal-instance2";
        String adHocNodeName = "AdHocTask";
        Object signalData = Collections.emptyMap();
        TestModel model = new TestModel();

        when(processInstances.findById(eq(processInstanceId), any(ProcessInstanceReadMode.class)))
                .thenReturn(Optional.of(processInstance));
        when(processInstances.acceptingEventType(adHocNodeName, processInstanceId))
                .thenReturn(Stream.of(processInstance));
        when(processInstance.checkError()).thenReturn(processInstance);
        when(processInstance.variables()).thenReturn(model);

        // When: Signal is sent to trigger ad hoc node
        Optional<TestModel> result = processService.signalProcessInstance(process, processInstanceId, signalData, adHocNodeName);

        // Then: Signal should be accepted
        assertThat(result).isPresent();
        verify(processInstance).send(any());
    }

    @Test
    void testSignalProcessInstance_InvalidSignal_ReturnsEmpty() {
        // Given: Process instance not accepting the signal
        String processInstanceId = "test-signal-instance3";
        String invalidSignal = "InvalidSignal";
        Object signalData = "test-data";

        when(processInstances.findById(eq(processInstanceId), any(ProcessInstanceReadMode.class)))
                .thenReturn(Optional.of(processInstance));
        when(processInstances.acceptingEventType(invalidSignal, processInstanceId))
                .thenReturn(Stream.empty());

        // When: Invalid signal is sent
        Optional<TestModel> result = processService.signalProcessInstance(process, processInstanceId, signalData, invalidSignal);

        // Then: Should return empty Optional
        assertThat(result).isEmpty();
    }

    @Test
    void testSignalProcessInstance_NonExistentInstance_ReturnsEmpty() {
        // Given: Non-existent process instance
        String processInstanceId = "non-existent";
        String signalName = "HelloMartin";
        Object signalData = "test-data";

        when(processInstances.findById(eq(processInstanceId), any(ProcessInstanceReadMode.class)))
                .thenReturn(Optional.empty());
        when(processInstances.acceptingEventType(signalName, processInstanceId))
                .thenReturn(Stream.empty());

        // When: Signal is sent to non-existent instance
        Optional<TestModel> result = processService.signalProcessInstance(process, processInstanceId, signalData, signalName);

        // Then: Should return empty Optional
        assertThat(result).isEmpty();
    }

    @Test
    void testSignalProcessInstance_MultipleAdHocNodes_AcceptsCorrectOne() {
        // Given: Process instance with multiple ad hoc nodes
        String processInstanceId = "test-signal-instance4";
        String adHocNode1 = "AdHocTask1";
        String adHocNode2 = "AdHocTask2";
        TestModel model = new TestModel();

        when(processInstances.findById(eq(processInstanceId), any(ProcessInstanceReadMode.class)))
                .thenReturn(Optional.of(processInstance));
        when(processInstances.acceptingEventType(adHocNode1, processInstanceId))
                .thenReturn(Stream.of(processInstance));
        when(processInstances.acceptingEventType(adHocNode2, processInstanceId))
                .thenReturn(Stream.of(processInstance));
        when(processInstance.checkError()).thenReturn(processInstance);
        when(processInstance.variables()).thenReturn(model);

        // When: Signal first ad hoc node
        Optional<TestModel> result1 = processService.signalProcessInstance(process, processInstanceId, null, adHocNode1);

        // Then: Should accept
        assertThat(result1).isPresent();

        // When: Signal second ad hoc node
        Optional<TestModel> result2 = processService.signalProcessInstance(process, processInstanceId, null, adHocNode2);

        // Then: Should accept
        assertThat(result2).isPresent();
    }

    @Test
    void testSignalProcessInstance_BothSignalAndAdHoc_AcceptsBoth() {
        // Given: Process instance with both signal event and ad hoc node
        String processInstanceId = "test-signal-instance5";
        String signalName = "HelloMartin";
        String adHocNodeName = "AdHocTask";
        TestModel model = new TestModel();

        when(processInstances.findById(eq(processInstanceId), any(ProcessInstanceReadMode.class)))
                .thenReturn(Optional.of(processInstance));
        when(processInstances.acceptingEventType(signalName, processInstanceId))
                .thenReturn(Stream.of(processInstance));
        when(processInstances.acceptingEventType(adHocNodeName, processInstanceId))
                .thenReturn(Stream.of(processInstance));
        when(processInstance.checkError()).thenReturn(processInstance);
        when(processInstance.variables()).thenReturn(model);

        // When: Signal traditional event
        Optional<TestModel> resultSignal = processService.signalProcessInstance(process, processInstanceId, "data", signalName);

        // Then: Should accept
        assertThat(resultSignal).isPresent();

        // When: Signal ad hoc node
        Optional<TestModel> resultAdHoc = processService.signalProcessInstance(process, processInstanceId, null, adHocNodeName);

        // Then: Should accept
        assertThat(resultAdHoc).isPresent();
    }

    /**
     * Test model for testing
     */
    static class TestModel implements MappableToModel<TestModel>, Model {
        @Override
        public TestModel toModel() {
            return this;
        }
    }
}
