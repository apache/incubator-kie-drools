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
package org.kie.kogito.index.addon.api;

import java.util.Optional;

import javax.enterprise.inject.Instance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Application;
import org.kie.kogito.addon.source.files.SourceFilesProvider;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.index.test.TestUtils;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstanceExecutionException;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.kie.kogito.svg.ProcessSvgService;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KogitoAddonRuntimeClientImplTest {

    private static int ACTIVE = 1;
    private static int ERROR = 5;
    private static String SERVICE_URL = "http://runtimeURL.com";
    private static String PROCESS_INSTANCE_ID = "pId";
    private static final String NODE_ID = "nodeId";
    private static String TASK_ID = "taskId";
    private static String JOB_ID = "jobId";

    public static final String NODE_ID_ERROR = "processInstanceIdError";

    private KogitoAddonRuntimeClientImpl client;

    @Mock
    Instance<ProcessSvgService> processSvgServiceInstance;

    @Mock
    private ProcessSvgService processSvgService;

    @Mock
    private SourceFilesProvider sourceFilesProvider;

    @Mock
    Instance<Processes> processesInstance;

    @Mock
    private Processes processes;

    @Mock
    private AbstractProcess process;

    @Mock
    private ProcessInstances instances;

    @Mock
    private org.kie.kogito.process.ProcessInstance processInstance;

    @Mock
    private ProcessError error;

    @Mock
    private Object variables;

    @Mock
    Instance<Application> applicationInstance;

    @Mock
    private Application application;

    @BeforeEach
    public void setup() {
        lenient().when(processSvgServiceInstance.isResolvable()).thenReturn(true);
        lenient().when(processSvgServiceInstance.get()).thenReturn(processSvgService);
        lenient().when(processesInstance.isResolvable()).thenReturn(true);
        lenient().when(processesInstance.get()).thenReturn(processes);
        lenient().when(processes.processById(anyString())).thenReturn(process);
        lenient().when(process.instances()).thenReturn(instances);
        lenient().when(instances.findById(anyString())).thenReturn(Optional.of(processInstance));
        lenient().when(processInstance.error()).thenReturn(Optional.of(error));
        lenient().when(processInstance.variables()).thenReturn(variables);
        lenient().when(processInstance.id()).thenReturn(PROCESS_INSTANCE_ID);
        lenient().when(processInstance.status()).thenReturn(org.kie.kogito.process.ProcessInstance.STATE_ERROR);
        lenient().when(error.failedNodeId()).thenReturn(NODE_ID_ERROR);
        lenient().when(error.errorMessage()).thenReturn("Test error message");
        lenient().when(application.unitOfWorkManager()).thenReturn(new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));
        lenient().when(applicationInstance.isResolvable()).thenReturn(true);
        lenient().when(applicationInstance.get()).thenReturn(application);

        client = spy(new KogitoAddonRuntimeClientImpl(processSvgServiceInstance, sourceFilesProvider, processesInstance, applicationInstance));
    }

    private org.kie.kogito.process.ProcessInstance mockProcessInstanceStatusActive() {
        return doAnswer((v) -> {
            when(processInstance.status()).thenReturn(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
            return null;
        }).when(processInstance);
    }

    private org.kie.kogito.process.ProcessInstance mockProcessInstanceStatusError() {
        return doAnswer((v) -> {
            when(processInstance.status()).thenReturn(org.kie.kogito.process.ProcessInstance.STATE_ERROR);
            return null;
        }).when(processInstance);
    }

    private ProcessError mockProcessInstanceStatusActiveOnError() {
        return doAnswer((v) -> {
            when(processInstance.status()).thenReturn(org.kie.kogito.process.ProcessInstance.STATE_ACTIVE);
            return null;
        }).when(error);
    }

    @Test
    void testAbortProcessInstanceSuccess() {
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        mockProcessInstanceStatusActive().abort();
        client.abortProcessInstance(SERVICE_URL, pI);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).abort();
    }

    @Test
    void testAbortProcessInstanceError() {
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        mockProcessInstanceStatusError().abort();
        assertThrows(ProcessInstanceExecutionException.class,
                () -> client.abortProcessInstance(SERVICE_URL, pI));
        verify(processInstance, times(2)).error();
        verify(processInstance, times(1)).abort();
    }

    @Test
    void testRetryProcessInstance() {
        mockProcessInstanceStatusActiveOnError().retrigger();
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.retryProcessInstance(SERVICE_URL, pI);
        verify(processInstance, times(1)).error();
        verify(error, times(1)).retrigger();
        verify(error, times(0)).skip();
    }

    @Test
    void testSkipProcessInstance() {
        mockProcessInstanceStatusActiveOnError().skip();
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.skipProcessInstance(SERVICE_URL, pI);
        verify(processInstance, times(1)).error();
        verify(error, times(0)).retrigger();
        verify(error, times(1)).skip();
    }

    @Test
    void testTriggerNodeInstance() {
        mockProcessInstanceStatusActive().triggerNode(NODE_ID);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.triggerNodeInstance(SERVICE_URL, pI, NODE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).triggerNode(NODE_ID);
    }

    @Test
    void testRetriggerNodeInstance() {
        mockProcessInstanceStatusActive().retriggerNodeInstance(NODE_ID);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.retriggerNodeInstance(SERVICE_URL, pI, NODE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).retriggerNodeInstance(NODE_ID);
    }

    @Test
    void testCancelNodeInstance() {
        mockProcessInstanceStatusActive().cancelNodeInstance(NODE_ID);
        ProcessInstance pI = createProcessInstance(PROCESS_INSTANCE_ID, ACTIVE);
        client.cancelNodeInstance(SERVICE_URL, pI, NODE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).cancelNodeInstance(NODE_ID);
    }

    private ProcessInstance createProcessInstance(String processInstanceId, int status) {
        return TestUtils.getProcessInstance("travels", processInstanceId, status, null, null);
    }

}
