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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.Application;
import org.kie.kogito.process.Processes;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessInstanceManagementRestControllerTest {

    public static final String PROCESS_ID = "processId";
    public static final String PROCESS_INSTANCE_ID = "processInstanceId";
    public static final String NODE_ID = "nodeId";
    public static final String NODE_INSTANCE_ID = "nodeInstanceId";
    public static final String MESSAGE = "message";

    private ProcessInstanceManagementRestController tested;

    @Mock
    private Processes processes;

    @Mock
    private Application application;

    @Mock
    private Object body;

    @BeforeEach
    void setUp() {
        tested = spy(new ProcessInstanceManagementRestController(processes, application));
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
    void getInstanceInError() {
        tested.getInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(tested).doGetInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    void getWorkItemsInProcessInstance() {
        tested.getWorkItemsInProcessInstance(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(tested).doGetWorkItemsInProcessInstance(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    void retriggerInstanceInError() {
        tested.retriggerInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(tested).doRetriggerInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    void skipInstanceInError() {
        tested.skipInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(tested).doSkipInstanceInError(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    void triggerNodeInstanceId() {
        tested.triggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
        verify(tested).doTriggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_ID);
    }

    @Test
    void retriggerNodeInstanceId() {
        tested.retriggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_INSTANCE_ID);
        verify(tested).doRetriggerNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_INSTANCE_ID);
    }

    @Test
    void cancelNodeInstanceId() {
        tested.cancelNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_INSTANCE_ID);
        verify(tested).doCancelNodeInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID, NODE_INSTANCE_ID);
    }

    @Test
    void cancelProcessInstanceId() {
        tested.cancelProcessInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID);
        verify(tested).doCancelProcessInstanceId(PROCESS_ID, PROCESS_INSTANCE_ID);
    }

    @Test
    void testGetProcessNodes() {
        tested.getProcessNodes(PROCESS_ID);
        verify(tested).doGetProcessNodes(PROCESS_ID);
    }
}