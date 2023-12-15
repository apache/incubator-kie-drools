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
package org.kie.kogito.process.management;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Application;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcess;
import org.kie.kogito.process.ProcessError;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.impl.AbstractProcess;
import org.kie.kogito.services.uow.CollectingUnitOfWorkFactory;
import org.kie.kogito.services.uow.DefaultUnitOfWorkManager;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.ext.RuntimeDelegate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProcessInstanceManagementResourceTest {

    public static final String MESSAGE = "message";
    public static final String PROCESS_ID = "test";
    public static final String PROCESS_INSTANCE_ID = "xxxxx";
    public static final String NODE_ID = "abc-def";
    private static RuntimeDelegate runtimeDelegate;
    private ResponseBuilder responseBuilder;

    private Processes processes;
    @SuppressWarnings("rawtypes")
    private ProcessInstance processInstance;
    private ProcessError error;
    private Application application;
    private ProcessInstanceManagementResource resource;

    @BeforeAll
    public static void configureEnvironment() {
        runtimeDelegate = mock(RuntimeDelegate.class);
        RuntimeDelegate.setInstance(runtimeDelegate);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @BeforeEach
    public void setup() {

        responseBuilder = mock(ResponseBuilder.class);
        Response response = mock(Response.class);

        when((runtimeDelegate).createResponseBuilder()).thenReturn(responseBuilder);
        lenient().when((responseBuilder).status(any(StatusType.class))).thenReturn(responseBuilder);
        lenient().when((responseBuilder).entity(any())).thenReturn(responseBuilder);
        lenient().when((responseBuilder).build()).thenReturn(response);

        application = mock(Application.class);
        processes = mock(Processes.class);
        AbstractProcess process = mock(AbstractProcess.class);
        ProcessInstances instances = mock(ProcessInstances.class);
        processInstance = mock(ProcessInstance.class);
        error = mock(ProcessError.class);

        Instance<Processes> processesInstance = mock(Instance.class);
        lenient().when(processes.processById(anyString())).thenReturn(process);
        lenient().when(processesInstance.get()).thenReturn(processes);
        lenient().when(process.instances()).thenReturn(instances);
        lenient().when(instances.findById(anyString())).thenReturn(Optional.of(processInstance));
        lenient().when(processInstance.error()).thenReturn(Optional.of(error));
        lenient().when(processInstance.id()).thenReturn("abc-def");
        lenient().when(processInstance.status()).thenReturn(KogitoProcessInstance.STATE_ACTIVE);
        lenient().when(error.failedNodeId()).thenReturn("xxxxx");
        lenient().when(error.errorMessage()).thenReturn("Test error message");
        lenient().when(process.get()).thenReturn(mock(KogitoWorkflowProcess.class));

        lenient().when(application.unitOfWorkManager()).thenReturn(new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));
        resource = spy(new ProcessInstanceManagementResource(processesInstance, application));
    }

    @Test
    public void testGetErrorInfo() {

        Response response = resource.getInstanceInError("test", "xxxxx");
        assertThat(response).isNotNull();

        verify(responseBuilder, times(1)).status((StatusType) Status.OK);
        verify(responseBuilder, times(1)).entity(any());

        verify(processInstance, times(2)).error();
        verify(error, times(0)).retrigger();
        verify(error, times(0)).skip();

        verify(resource).doGetInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    public void testRetriggerErrorInfo() {

        doAnswer(new Answer<Void>() {

            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                when(processInstance.status()).thenReturn(KogitoProcessInstance.STATE_ACTIVE);
                return null;
            }
        }).when(error).retrigger();

        Response response = resource.retriggerInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
        assertThat(response).isNotNull();

        verify(responseBuilder, times(1)).status((StatusType) Status.OK);
        verify(responseBuilder, times(1)).entity(any());

        verify(processInstance, times(2)).error();
        verify(error, times(1)).retrigger();
        verify(error, times(0)).skip();

        verify(resource).doRetriggerInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    public void testGetProcesses() {
        resource.getProcesses();
        verify(resource).doGetProcesses();
    }

    @Test
    public void testGetProcessInfo() {
        resource.getProcessInfo(PROCESS_ID);
        verify(resource).doGetProcessInfo(PROCESS_ID);
    }

    @Test
    public void testGetProcessNodes() {
        resource.getProcessNodes(PROCESS_ID);
        verify(resource).doGetProcessNodes(PROCESS_ID);
    }

    @Test
    public void testGetWorkItemsInProcessInstance() {
        resource.getWorkItemsInProcessInstance(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(resource).doGetWorkItemsInProcessInstance(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    public void testSkipInstanceInError() {
        resource.skipInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(resource).doSkipInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    public void testTriggerNodeInstanceId() {
        resource.triggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
        verify(resource).doTriggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
    }

    @Test
    public void testRetriggerNodeInstanceId() {
        resource.retriggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
        verify(resource).doRetriggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
    }

    @Test
    public void testCancelNodeInstanceId() {
        resource.cancelNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
        verify(resource).doCancelNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
    }

    @Test
    public void testCancelProcessInstanceId() {
        resource.cancelProcessInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(resource).doCancelProcessInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    public void testBubildOkResponse(@Mock Object body) {
        Response response = resource.buildOkResponse(body);
        assertResponse(body, Status.OK);
    }

    public void assertResponse(Object body, Status status) {
        verify(responseBuilder).status((Response.StatusType) status);
        verify(responseBuilder).entity(body);
    }

    @Test
    public void testBadRequestResponse() {
        Response response = resource.badRequestResponse(MESSAGE);
        assertResponse(MESSAGE, Status.BAD_REQUEST);
    }

    @Test
    public void testNotFoundResponse() {
        Response response = resource.notFoundResponse(MESSAGE);
        assertResponse(MESSAGE, Status.NOT_FOUND);
    }
}
