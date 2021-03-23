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

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.user.service.api.User;
import org.kie.kogito.taskassigning.user.service.api.UserServiceConnector;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class SolutionDataLoaderTest extends RunnableBaseTest<SolutionDataLoader> {

    private static final String TASK_ID = "TASK_ID";
    private static final Duration DURATION = Duration.of(10, ChronoUnit.MILLIS);
    private static final int RETRIES = 5;
    private static final int PAGE_SIZE = 10;

    @Mock
    private TaskServiceConnector taskServiceConnector;

    @Mock
    private UserServiceConnector userServiceConnector;

    @Captor
    private ArgumentCaptor<SolutionDataLoader.Result> resultCaptor;

    @Mock
    private Consumer<SolutionDataLoader.Result> resultConsumer;

    private CountDownLatch resultApplied;

    @Override
    protected SolutionDataLoader createRunnableBase() {
        return spy(new SolutionDataLoaderMock(taskServiceConnector, userServiceConnector));
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void startWithSuccessfulExecution() throws Exception {
        CompletableFuture<Void> future = startRunnableBase();

        List<List<UserTaskInstance>> taskServiceResults = Arrays.asList(new ArrayList<>(), null, Collections.singletonList(createUserTaskInstance()));
        List<List<User>> userServiceResults = Arrays.asList(null, Collections.singletonList(createExternalUser()));
        doAnswer(createExecutions(taskServiceResults)).when(taskServiceConnector).findAllTasks(anyList(), eq(PAGE_SIZE));
        doAnswer(createExecutions(userServiceResults)).when(userServiceConnector).findAllUsers();

        resultApplied = new CountDownLatch(1);
        runnableBase.start(resultConsumer, true, true, DURATION, RETRIES, PAGE_SIZE);
        resultApplied.await();

        runnableBase.destroy();
        future.get();
        assertThat(runnableBase.isDestroyed()).isTrue();

        verify(resultConsumer).accept(resultCaptor.capture());
        assertThat(resultCaptor.getValue().hasErrors()).isFalse();
        SolutionDataLoader.Result result = resultCaptor.getValue();
        assertThat(result.getTasks())
                .isNotNull()
                .hasSize(1)
                .element(0)
                .isNotNull();
        assertThat(result.getTasks().get(0).getId()).isEqualTo(TASK_ID);
        assertThat(result.getUsers()).isSameAs(userServiceResults.get(1));
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void startWithUnsuccessfulExecution() throws Exception {
        CompletableFuture<Void> future = startRunnableBase();

        List<List<UserTaskInstance>> taskServiceResults = Arrays.asList(new ArrayList<>(),
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                null);
        List<List<User>> userServiceResults = Arrays.asList(null, null, null, null);
        doAnswer(createExecutions(taskServiceResults)).when(taskServiceConnector).findAllTasks(anyList(), eq(PAGE_SIZE));
        doAnswer(createExecutions(userServiceResults)).when(userServiceConnector).findAllUsers();

        resultApplied = new CountDownLatch(1);
        runnableBase.start(resultConsumer, true, true, DURATION, RETRIES, PAGE_SIZE);
        resultApplied.await();

        runnableBase.destroy();
        future.get();
        assertThat(runnableBase.isDestroyed()).isTrue();

        verify(resultConsumer).accept(resultCaptor.capture());
        assertThat(resultCaptor.getValue().hasErrors()).isTrue();
    }

    private static UserTaskInstance createUserTaskInstance() {
        UserTaskInstance userTaskInstance = new UserTaskInstance();
        userTaskInstance.setId(TASK_ID);
        return userTaskInstance;
    }

    private static org.kie.kogito.taskassigning.user.service.api.User createExternalUser() {
        return mock(org.kie.kogito.taskassigning.user.service.api.User.class);
    }

    private class SolutionDataLoaderMock extends SolutionDataLoader {

        public SolutionDataLoaderMock(TaskServiceConnector taskServiceConnector, UserServiceConnector userServiceConnector) {
            super(taskServiceConnector, userServiceConnector);
        }

        @Override
        protected void applyResult(Result result) {
            super.applyResult(result);
            resultApplied.countDown();
        }
    }

    private <T> Answer createExecutions(List<T> results) {
        return new Answer() {
            private int invocations = 0;

            public Object answer(InvocationOnMock invocation) {
                T result = results.get(invocations++);
                if (result == null) {
                    throw new RuntimeException("Emulate an error, the loader must retry.");
                }
                return result;
            }
        };
    }
}
