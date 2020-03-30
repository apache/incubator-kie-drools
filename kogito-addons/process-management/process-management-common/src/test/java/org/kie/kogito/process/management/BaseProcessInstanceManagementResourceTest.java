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

package org.kie.kogito.process.management;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Application;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseProcessInstanceManagementResourceTest {

    public static final String PROCESS_ID = "processId";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String NODE_ID_ERROR = "processInstanceIdError";
    public static final String NODE_ID = "nodeId";
    public static final String NODE_INSTANCE_ID = "nodeInstanceId";
    private BaseProcessInstanceManagementResource tested;

    @Mock
    private Processes processes;

    @Mock
    private Application application;

    @Mock
    private ProcessInstance processInstance;

    @Mock
    private ProcessError error;

    @Mock
    private ProcessInstances instances;

    @Mock
    private Process process;

    @Mock
    private Object variables;

    @BeforeEach
    void setUp() {
        when(processes.processById(anyString())).thenReturn(process);
        when(process.instances()).thenReturn(instances);
        when(instances.findById(anyString())).thenReturn(Optional.of(processInstance));
        lenient().when(processInstance.error()).thenReturn(Optional.of(error));
        lenient().when(processInstance.variables()).thenReturn(variables);
        lenient().when(processInstance.id()).thenReturn(PROCESS_INSTANCE_ID);
        lenient().when(processInstance.status()).thenReturn(ProcessInstance.STATE_ERROR);
        lenient().when(error.failedNodeId()).thenReturn(NODE_ID_ERROR);
        lenient().when(error.errorMessage()).thenReturn("Test error message");
        lenient().when(application.unitOfWorkManager()).thenReturn(new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));

        tested = spy(new BaseProcessInstanceManagementResource(processes, application) {

            @Override
            protected Object buildOkResponse(Object body) {
                return body;
            }

            @Override
            protected Object badRequestResponse(String message) {
                return message;
            }

            @Override
            protected Object notFoundResponse(String message) {
                return message;
            }

            @Override
            public Object getInstanceInError(String processId, String processInstanceId) {
                return null;
            }

            @Override
            public Object getWorkItemsInProcessInstance(String processId, String processInstanceId) {
                return null;
            }

            @Override
            public Object retriggerInstanceInError(String processId, String processInstanceId) {
                return null;
            }

            @Override
            public Object skipInstanceInError(String processId, String processInstanceId) {
                return null;
            }

            @Override
            public Object triggerNodeInstanceId(String processId, String processInstanceId, String nodeId) {
                return null;
            }

            @Override
            public Object retriggerNodeInstanceId(String processId, String processInstanceId, String nodeInstanceId) {
                return null;
            }

            @Override
            public Object cancelNodeInstanceId(String processId, String processInstanceId, String nodeInstanceId) {
                return null;
            }

            @Override
            public Object cancelProcessInstanceId(String processId, String processInstanceId) {
                return null;
            }
        });
    }

    @Test
    void testDoGetInstanceInError() {
        Object response = tested.doGetInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(processInstance, times(2)).error();
        verify(error, times(0)).retrigger();
        verify(error, times(0)).skip();
        verify(tested).buildOkResponse(any());
        assertThat(response).isInstanceOf(Map.class);
        Map responseMap = (Map) response;
        assertThat(responseMap.get("id")).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(responseMap.get("failedNodeId")).isEqualTo(NODE_ID_ERROR);
    }

    @Test
    void testDoGetWorkItemsInProcessInstance(@Mock WorkItem workItem) {
        when(processInstance.workItems(any(SecurityPolicy.class))).thenReturn(Collections.singletonList(workItem));
        Object response = tested.doGetWorkItemsInProcessInstance(PROCESS_ID, PROCESS_INSTANCE_ID);
        assertThat(response).isInstanceOf(List.class);
        assertThat(((List)response).get(0)).isEqualTo(workItem);
    }

    @Test
    void testDoRetriggerInstanceInError() {
        mockProcessInstanceStatusActiveOnError().retrigger();
        Object response = tested.doRetriggerInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(processInstance, times(2)).error();
        verify(error, times(1)).retrigger();
        verify(error, times(0)).skip();
        assertResultOk(response);
    }

    @Test
    void testDoSkipInstanceInError() {
        mockProcessInstanceStatusActiveOnError().skip();
        Object response = tested.doSkipInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(processInstance, times(2)).error();
        verify(error, times(0)).retrigger();
        verify(error, times(1)).skip();
        assertResultOk(response);
    }

    @Test
    void testDoTriggerNodeInstanceId() {
        mockProcessInstanceStatusActive().triggerNode(NODE_ID);
        Object response = tested.doTriggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).triggerNode(NODE_ID);
        assertResultOk(response);
    }

    @Test
    void testDoRetriggerNodeInstanceId() {
        mockProcessInstanceStatusActive().retriggerNodeInstance(NODE_INSTANCE_ID);
        Object response = tested.doRetriggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_INSTANCE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).retriggerNodeInstance(NODE_INSTANCE_ID);
        assertResultOk(response);
    }

    @Test
    void testDoCancelNodeInstanceId() {
        mockProcessInstanceStatusActive().cancelNodeInstance(anyString());
        Object response = tested.doCancelNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_INSTANCE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).cancelNodeInstance(NODE_INSTANCE_ID);
        assertResultOk(response);
    }

    private void assertResultOk(Object response) {
        verify(tested).buildOkResponse(any());
        assertThat(response).isEqualTo(variables);
    }

    private ProcessInstance mockProcessInstanceStatusActive() {
        return doAnswer((v) -> {
            when(processInstance.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
            return null;
        }).when(processInstance);
    }

    private ProcessError mockProcessInstanceStatusActiveOnError() {
        return doAnswer((v) -> {
            when(processInstance.status()).thenReturn(ProcessInstance.STATE_ACTIVE);
            return null;
        }).when(error);
    }

    @Test
    void testDoCancelProcessInstanceId() {
        mockProcessInstanceStatusActive().abort();
        Object response = tested.doCancelProcessInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(processInstance, times(0)).error();
        verify(processInstance, times(1)).abort();
        assertResultOk(response);
    }
}