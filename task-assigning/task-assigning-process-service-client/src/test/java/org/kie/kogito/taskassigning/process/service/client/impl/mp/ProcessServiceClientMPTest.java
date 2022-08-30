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
package org.kie.kogito.taskassigning.process.service.client.impl.mp;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessServiceClientMPTest {

    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String TASK_ID = "TASK_ID";
    private static final String WORKITEM_ID = "WORKITEM_ID";
    private static final String USER = "USER";
    private static final String GROUP1 = "GROUP1";
    private static final String GROUP2 = "GROUP2";
    private static final String PHASE1 = "PHASE1";
    private static final String PHASE2 = "PHASE2";
    private static final String EMPTY_JSON = "{}";

    @Mock
    private ProcessServiceClientRest clientRest;

    private ProcessServiceClientMP client;

    @BeforeEach
    public void setUp() {
        client = new ProcessServiceClientMP(clientRest);
    }

    @Test
    void getAvailablePhases() {
        TaskSchema taskSchema = new TaskSchema();
        taskSchema.setPhases(Arrays.asList(PHASE1, PHASE2));
        doReturn(taskSchema)
                .when(clientRest)
                .getTaskSchema(PROCESS_ID, PROCESS_INSTANCE_ID, TASK_ID, WORKITEM_ID, USER, Arrays.asList(GROUP1, GROUP2));
        Set<String> result = client.getAvailablePhases(PROCESS_ID, PROCESS_INSTANCE_ID, TASK_ID, WORKITEM_ID, USER, Arrays.asList(GROUP1, GROUP2));
        assertThat(result).containsExactlyInAnyOrder(PHASE1, PHASE2);
    }

    @Test
    void transitionTask() {
        client.transitionTask(PROCESS_ID, PROCESS_INSTANCE_ID, TASK_ID, WORKITEM_ID, PHASE1, USER, Arrays.asList(GROUP1, GROUP2));
        verify(clientRest).transitionTask(PROCESS_ID, PROCESS_INSTANCE_ID, TASK_ID, WORKITEM_ID, PHASE1, USER, Arrays.asList(GROUP1, GROUP2), EMPTY_JSON);
    }

    @Test
    void close() throws IOException {
        client.close();
        verify(clientRest).close();
    }
}
