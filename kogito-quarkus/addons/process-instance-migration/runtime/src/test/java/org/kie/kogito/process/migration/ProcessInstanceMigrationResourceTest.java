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
package org.kie.kogito.process.migration;

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
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.enterprise.inject.Instance;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.ext.RuntimeDelegate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ProcessInstanceMigrationResourceTest {

    public static final String MESSAGE = "message";
    public static final String PROCESS_ID = "test";
    public static final String PROCESS_INSTANCE_ID = "testInstance";
    public static final String TARGET_PROCESS_ID = "targetProcess";
    public static final String TARGET_VERSION = "1.0";

    private static RuntimeDelegate runtimeDelegate;
    private ResponseBuilder responseBuilder;

    private Processes processes;
    @SuppressWarnings("rawtypes")
    private ProcessInstance processInstance;
    private ProcessError error;
    private Application application;
    private ProcessInstanceMigrationResource resource;

    @Mock
    ProcessInstances mockInstances;

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
        processInstance = mock(ProcessInstance.class);
        error = mock(ProcessError.class);

        Instance<Processes> processesInstance = mock(Instance.class);
        lenient().when(processes.processById(anyString())).thenReturn(process);
        lenient().when(processesInstance.get()).thenReturn(processes);
        lenient().when(process.instances()).thenReturn(mockInstances);
        lenient().when(mockInstances.findById(anyString())).thenReturn(Optional.of(processInstance));
        lenient().when(processInstance.error()).thenReturn(Optional.of(error));
        lenient().when(processInstance.id()).thenReturn("abc-def");
        lenient().when(processInstance.status()).thenReturn(KogitoProcessInstance.STATE_ACTIVE);
        lenient().when(error.failedNodeId()).thenReturn("test");
        lenient().when(error.errorMessage()).thenReturn("Test error message");
        lenient().when(process.get()).thenReturn(mock(KogitoWorkflowProcess.class));

        lenient().when(application.unitOfWorkManager()).thenReturn(new DefaultUnitOfWorkManager(new CollectingUnitOfWorkFactory()));
        resource = spy(new ProcessInstanceMigrationResource(processesInstance, application));
    }

    @Test
    public void testMigrateInstance() {
        ProcessMigrationSpec processMigrationSpec = new ProcessMigrationSpec();
        processMigrationSpec.setTargetProcessId(TARGET_PROCESS_ID);
        processMigrationSpec.setTargetProcessVersion(TARGET_VERSION);

        Response response = resource.migrateInstance(PROCESS_ID, PROCESS_INSTANCE_ID, processMigrationSpec);
        assertThat(response).isNotNull();
        verify(responseBuilder, times(1)).status((StatusType) Response.Status.OK);
        verify(responseBuilder, times(1)).entity(any());
        verify(resource).migrateInstance(PROCESS_ID, PROCESS_INSTANCE_ID, processMigrationSpec);
        verify(mockInstances).migrateProcessInstances(TARGET_PROCESS_ID, TARGET_VERSION, "testInstance");
        verify(mockInstances, times(1)).migrateProcessInstances(any(), any(), any());
    }

    @Test
    public void testMigrateAllInstance() {
        ProcessMigrationSpec processMigrationSpec = new ProcessMigrationSpec();
        processMigrationSpec.setTargetProcessId(TARGET_PROCESS_ID);
        processMigrationSpec.setTargetProcessVersion(TARGET_VERSION);

        Response response = resource.migrateAllInstances(PROCESS_ID, processMigrationSpec);
        assertThat(response).isNotNull();
        verify(responseBuilder, times(1)).status((StatusType) Response.Status.OK);
        verify(responseBuilder, times(1)).entity(any());
        verify(resource).migrateAllInstances(PROCESS_ID, processMigrationSpec);
        verify(mockInstances).migrateAll(TARGET_PROCESS_ID, TARGET_VERSION);
        verify(mockInstances, times(1)).migrateAll(any(), any());

    }

    @Test
    public void testMigrateInstanceInvocation() {
        ProcessMigrationSpec processMigrationSpec = new ProcessMigrationSpec();
        resource.doMigrateInstance(PROCESS_ID, processMigrationSpec, PROCESS_INSTANCE_ID);
        verify(resource).doMigrateInstance(PROCESS_ID, processMigrationSpec, PROCESS_INSTANCE_ID);
    }

    @Test
    public void testMigrateAllInstanceInvocation() {
        ProcessMigrationSpec processMigrationSpec = new ProcessMigrationSpec();
        resource.doMigrateAllInstances(PROCESS_ID, processMigrationSpec);
        verify(resource).doMigrateAllInstances(PROCESS_ID, processMigrationSpec);
    }

    @Test
    public void testBubildOkResponse(@Mock Object body) {
        Response response = resource.buildOkResponse(body);
        assertResponse(body, Response.Status.OK);
    }

    public void assertResponse(Object body, Response.Status status) {
        verify(responseBuilder).status((Response.StatusType) status);
        verify(responseBuilder).entity(body);
    }

    @Test
    public void testBadRequestResponse() {
        Response response = resource.badRequestResponse(MESSAGE);
        assertResponse(MESSAGE, Response.Status.BAD_REQUEST);
    }

    @Test
    public void testNotFoundResponse() {
        Response response = resource.notFoundResponse(MESSAGE);
        assertResponse(MESSAGE, Response.Status.NOT_FOUND);
    }
}
