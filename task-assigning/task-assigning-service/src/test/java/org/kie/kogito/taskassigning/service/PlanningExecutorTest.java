/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.service;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.kogito.taskassigning.ClientServices;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.process.service.client.ProcessServiceClient;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class PlanningExecutorTest extends RunnableBaseTest<PlanningExecutor> {

    private static final String SERVICE1_URL = "http://service1.cloud.com:8280";
    private static final String PROCESS1_ID = "PROCESS1_ID";
    private static final String PROCESS1_INSTANCE_ID_1 = "PROCESS1_INSTANCE_ID_1";
    private static final String PROCESS1_INSTANCE_ID_2 = "PROCESS1_INSTANCE_ID_2";
    private static final String TASK1_NAME = "TASK1_NAME";
    private static final String TASK1_INSTANCE_ID_1 = "TASK1_INSTANCE_ID_1";
    private static final String TASK1_INSTANCE_ID_2 = "TASK1_INSTANCE_ID_2";

    private static final String SERVICE2_URL = "http://service2.cloud.com:8280";
    private static final String PROCESS2_ID = "PROCESS2_ID";
    private static final String PROCESS2_INSTANCE_ID_1 = "PROCESS2_INSTANCE_ID_1";
    private static final String TASK2_NAME = "TASK2_NAME";
    private static final String TASK2_INSTANCE_ID_1 = "TASK2_INSTANCE_ID_1";

    private static final String USER1 = "USER1";
    private static final String USER2 = "USER2";
    private static final String USER3 = "USER3";

    private static final String SERVICE_ERROR = "SERVICE_ERROR";

    @Mock
    private ClientServices clientServices;

    @Mock
    private TaskAssigningConfig config;

    private CountDownLatch resultApplied;

    @Captor
    private ArgumentCaptor<PlanningExecutionResult> resultCaptor;

    @Mock
    private Consumer<PlanningExecutionResult> resultConsumer;

    @Override
    protected PlanningExecutor createRunnableBase() {
        return spy(new PlanningExecutorMock(clientServices, config));
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void start() throws Exception {
        CompletableFuture<Void> future = startRunnableBase();

        PlanningItem service1Item1 = new PlanningItem(
                mockTask(SERVICE1_URL,
                        PROCESS1_ID,
                        PROCESS1_INSTANCE_ID_1,
                        TASK1_NAME,
                        TASK1_INSTANCE_ID_1),
                USER1);

        PlanningItem service1Item2 = new PlanningItem(
                mockTask(SERVICE1_URL,
                        PROCESS1_ID,
                        PROCESS1_INSTANCE_ID_2,
                        TASK1_NAME,
                        TASK1_INSTANCE_ID_2),
                USER2);

        PlanningItem service2Item1 = new PlanningItem(
                mockTask(SERVICE2_URL,
                        PROCESS2_ID,
                        PROCESS2_INSTANCE_ID_1,
                        TASK2_NAME,
                        TASK2_INSTANCE_ID_1),
                USER3);

        List<PlanningItem> planningItems = Arrays.asList(service1Item1, service1Item2, service2Item1);

        ProcessServiceClient service1Client = mock(ProcessServiceClient.class);
        ProcessServiceClient service2Client = mock(ProcessServiceClient.class);

        URL service1Url = new URL(SERVICE1_URL);
        URL service2Url = new URL(SERVICE2_URL);
        doReturn(service1Client).when(runnableBase).createProcessServiceClient(clientServices, config, service1Url);
        doReturn(service2Client).when(runnableBase).createProcessServiceClient(clientServices, config, service2Url);

        doThrow(new RuntimeException(SERVICE_ERROR)).when(service2Client).transitionTask(PROCESS2_ID,
                PROCESS2_INSTANCE_ID_1,
                TASK2_NAME,
                TASK2_INSTANCE_ID_1,
                "claim",
                USER3,
                Collections.emptyList());

        resultApplied = new CountDownLatch(1);

        runnableBase.start(planningItems, resultConsumer);

        resultApplied.await();
        runnableBase.destroy();
        future.get();

        assertThat(runnableBase.isDestroyed()).isTrue();
        verify(runnableBase).createProcessServiceClient(clientServices, config, service1Url);
        verify(runnableBase).createProcessServiceClient(clientServices, config, service2Url);

        verify(resultConsumer).accept(resultCaptor.capture());
        PlanningExecutionResult result = resultCaptor.getValue();
        assertThat(result).isNotNull();
        assertThat(result.getItems())
                .isNotNull()
                .hasSize(3);

        assertSuccessfulInvocation(result.getItems().get(0), service1Item1);
        assertSuccessfulInvocation(result.getItems().get(1), service1Item2);
        assertUnSuccessfulInvocation(result.getItems().get(2), service2Item1, SERVICE_ERROR);
        verify(service1Client).close();
        verify(service2Client).close();
    }

    private void assertSuccessfulInvocation(PlanningExecutionResultItem resultItem, PlanningItem expectedPlanningItem) {
        assertThat(resultItem.hasError()).isFalse();
        assertThat(resultItem.getItem()).isSameAs(expectedPlanningItem);
    }

    private void assertUnSuccessfulInvocation(PlanningExecutionResultItem resultItem,
            PlanningItem expectedPlanningItem,
            String expectedError) {
        assertThat(resultItem.hasError()).isTrue();
        assertThat(resultItem.getItem()).isSameAs(expectedPlanningItem);
        assertThat(resultItem.getError()).isNotNull();
        assertThat(resultItem.getError().getMessage()).isEqualTo(expectedError);
    }

    private class PlanningExecutorMock extends PlanningExecutor {

        public PlanningExecutorMock(ClientServices clientServices, TaskAssigningConfig config) {
            super(clientServices, config);
        }

        @Override
        void applyResult(PlanningExecutionResult result) {
            super.applyResult(result);
            resultApplied.countDown();
        }
    }

    private static Task mockTask(String serviceUrl, String processId, String processInstanceId,
            String taskName, String taskInstanceId) {
        return Task.newBuilder()
                .processId(processId)
                .processInstanceId(processInstanceId)
                .name(taskName)
                .id(taskInstanceId)
                .endpoint(buildEndpoint(serviceUrl, processId, processInstanceId, taskName, taskInstanceId))
                .build();
    }

    private static String buildEndpoint(String serviceUrl, String processId, String processInstanceId,
            String taskName, String taskInstanceId) {
        return serviceUrl + "/" + processId + "/" + processInstanceId + "/" + taskName + "/" + taskInstanceId;
    }
}
