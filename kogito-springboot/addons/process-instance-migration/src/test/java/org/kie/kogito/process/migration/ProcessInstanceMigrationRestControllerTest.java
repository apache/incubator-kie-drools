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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Application;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.management.ProcessInstanceMigrationRestController;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessInstanceMigrationRestControllerTest {

    public static final String PROCESS_ID = "processId";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String MESSAGE = "message";
    public static final String TARGET_PROCESS_ID = "targetProcess";
    public static final String TARGET_VERSION = "1.0";

    private ProcessInstanceMigrationRestController tested;

    @Mock
    private Processes processes;

    @Mock
    private Application application;

    @Mock
    private Object body;

    @Mock
    ProcessInstances mockInstances;

    @Mock
    Process process;

    @BeforeEach
    void setUp() {
        tested = spy(new ProcessInstanceMigrationRestController(processes, application));
        lenient().when(processes.processById(anyString())).thenReturn(process);
        lenient().when(process.instances()).thenReturn(mockInstances);
    }

    @Test
    void buildOkResponse() {
        ResponseEntity responseEntity = tested.buildOkResponse(body);
        assertResponse(responseEntity, HttpStatus.OK, body);
    }

    private void assertResponse(ResponseEntity responseEntity, HttpStatus ok, Object body) {
        assertThat(responseEntity.getStatusCode()).isEqualTo(ok);
        assertThat(responseEntity.getBody()).isEqualTo(body);
    }

    @Test
    void badRequestResponse() {
        ResponseEntity responseEntity = tested.badRequestResponse(MESSAGE);
        assertResponse(responseEntity, HttpStatus.BAD_REQUEST, MESSAGE);
    }

    @Test
    void notFoundResponse() {
        ResponseEntity responseEntity = tested.notFoundResponse(MESSAGE);
        assertResponse(responseEntity, HttpStatus.NOT_FOUND, MESSAGE);
    }

    @Test
    void triggerDoMigrateAllInstances() {
        ProcessMigrationSpec processMigrationSpec = new ProcessMigrationSpec();
        processMigrationSpec.setTargetProcessId(TARGET_PROCESS_ID);
        processMigrationSpec.setTargetProcessVersion(TARGET_VERSION);

        tested.doMigrateAllInstances(PROCESS_ID, processMigrationSpec);
        verify(tested).doMigrateAllInstances(PROCESS_ID, processMigrationSpec);
        verify(mockInstances).migrateAll(TARGET_PROCESS_ID, TARGET_VERSION);
        verify(mockInstances, times(1)).migrateAll(any(), any());

    }

    @Test
    void triggerDoMigrateInstance() {
        ProcessMigrationSpec processMigrationSpec = new ProcessMigrationSpec();
        processMigrationSpec.setTargetProcessId(TARGET_PROCESS_ID);
        processMigrationSpec.setTargetProcessVersion(TARGET_VERSION);

        tested.doMigrateInstance(PROCESS_ID, processMigrationSpec, PROCESS_INSTANCE_ID);
        verify(tested).doMigrateInstance(PROCESS_ID, processMigrationSpec, PROCESS_INSTANCE_ID);
        verify(mockInstances).migrateProcessInstances(TARGET_PROCESS_ID, TARGET_VERSION, "processInstanceId");
        verify(mockInstances, times(1)).migrateProcessInstances(any(), any(), any());

    }

}
