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

package org.kie.kogito.jobs.quarkus.common;

import java.util.Optional;

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Processes;
import org.kie.kogito.uow.UnitOfWork;
import org.kie.kogito.uow.UnitOfWorkManager;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CallbackJobsServiceResourceTest {

    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String TIMER_ID = "TIMER:1:8be48533-beed-4c7b-ad85-bd7b543e7925";
    private static final int LIMIT = 1;

    @Mock
    private Processes processes;

    @Mock
    private Application application;

    @Mock
    private UnitOfWorkManager unitOfWorkManager;

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private Process<?> process;

    @Mock
    private ProcessInstances<?> instances;

    @Mock
    private ProcessInstance<?> processInstance;

    private CallbackJobsServiceResource resource;

    @BeforeEach
    void setUp() {
        resource = new CallbackJobsServiceResource();
        resource.application = application;
        resource.processes = processes;
    }

    @Test
    void triggerTimerProcessIdNotPresent() {
        triggerTimerProcessIdOrProcessInstanceIdNotPresent(null, PROCESS_INSTANCE_ID);
    }

    @Test
    void triggerTimerProcessInstanceIdNotPresent() {
        triggerTimerProcessIdOrProcessInstanceIdNotPresent(PROCESS_ID, null);
    }

    private void triggerTimerProcessIdOrProcessInstanceIdNotPresent(String processId, String processInstanceId) {
        Response response = resource.triggerTimer(processId, processInstanceId, TIMER_ID, LIMIT);
        assertThat(response.getStatus()).isEqualTo(BAD_REQUEST.getStatusCode());
        assertThat(response.getEntity()).isEqualTo("Process id and Process instance id must be given");
    }

    @Test
    void triggerTimerProcessNotFound() {
        Response response = resource.triggerTimer(PROCESS_ID, PROCESS_INSTANCE_ID, TIMER_ID, LIMIT);
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.getStatusCode());
        assertThat(response.getEntity()).isEqualTo("Process with id " + PROCESS_ID + " not found");
    }

    @Test
    void triggerTimerProcessInstanceNotFound() {
        doReturn(process).when(processes).processById(PROCESS_ID);
        doReturn(instances).when(process).instances();
        doReturn(unitOfWorkManager).when(application).unitOfWorkManager();
        doReturn(unitOfWork).when(unitOfWorkManager).newUnitOfWork();
        doReturn(Optional.empty()).when(instances).findById(PROCESS_INSTANCE_ID);

        Response response = resource.triggerTimer(PROCESS_ID, PROCESS_INSTANCE_ID, TIMER_ID, LIMIT);
        assertThat(response.getStatus()).isEqualTo(NOT_FOUND.getStatusCode());
        assertThat(response.getEntity()).isEqualTo("Process instance with id " + PROCESS_INSTANCE_ID + " not found");
    }

    @Test
    void triggerTimerOK() {
        doReturn(process).when(processes).processById(PROCESS_ID);
        doReturn(instances).when(process).instances();
        doReturn(unitOfWorkManager).when(application).unitOfWorkManager();
        doReturn(unitOfWork).when(unitOfWorkManager).newUnitOfWork();
        doReturn(Optional.of(processInstance)).when(instances).findById(PROCESS_INSTANCE_ID);

        Response response = resource.triggerTimer(PROCESS_ID, PROCESS_INSTANCE_ID, TIMER_ID, LIMIT);
        assertThat(response.getStatus()).isEqualTo(OK.getStatusCode());
        verify(processInstance).send(any());
    }
}
