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
import java.util.ArrayList;
import java.util.Arrays;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class SolutionDataLoaderTest extends RunnableBaseTest<SolutionDataLoader> {

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
        return spy(new SolutionDataLoaderMock(taskServiceConnector, userServiceConnector, Duration.ofMillis(500)));
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void startWithSuccessfulExecution() throws Exception {
        CompletableFuture future = startRunnableBase();

        List<List<UserTaskInstance>> taskServiceResults = Arrays.asList(new ArrayList<>(), null, new ArrayList<>());
        List<List<User>> userServiceResults = Arrays.asList(null, new ArrayList<>());
        doAnswer(createExecutions(taskServiceResults)).when(taskServiceConnector).findAllTasks(anyList(), anyInt());
        doAnswer(createExecutions(userServiceResults)).when(userServiceConnector).findAllUsers();

        resultApplied = new CountDownLatch(1);
        runnableBase.start(resultConsumer, 5);
        resultApplied.await();

        runnableBase.destroy();
        future.get();
        assertThat(runnableBase.isDestroyed()).isTrue();

        verify(resultConsumer).accept(resultCaptor.capture());
        assertThat(resultCaptor.getValue().hasErrors()).isFalse();
        assertThat(resultCaptor.getValue().getTasks()).isSameAs(taskServiceResults.get(2));
        assertThat(resultCaptor.getValue().getUsers()).isSameAs(userServiceResults.get(1));
    }

    @Test
    @Timeout(TEST_TIMEOUT)
    void startWithUnsuccessfulExecution() throws Exception {
        CompletableFuture future = startRunnableBase();

        List<List<UserTaskInstance>> taskServiceResults = Arrays.asList(new ArrayList<>(),
                                                                        null,
                                                                        new ArrayList<>(),
                                                                        new ArrayList<>(),
                                                                        new ArrayList<>(),
                                                                        null);
        List<List<User>> userServiceResults = Arrays.asList(null, null, null, null);
        doAnswer(createExecutions(taskServiceResults)).when(taskServiceConnector).findAllTasks(anyList(), anyInt());
        doAnswer(createExecutions(userServiceResults)).when(userServiceConnector).findAllUsers();

        resultApplied = new CountDownLatch(1);
        runnableBase.start(resultConsumer, 5);
        resultApplied.await();

        runnableBase.destroy();
        future.get();
        assertThat(runnableBase.isDestroyed()).isTrue();

        verify(resultConsumer).accept(resultCaptor.capture());
        assertThat(resultCaptor.getValue().hasErrors()).isTrue();
    }

    private class SolutionDataLoaderMock extends SolutionDataLoader {

        public SolutionDataLoaderMock(TaskServiceConnector taskServiceConnector, UserServiceConnector userServiceConnector, Duration retryInterval) {
            super(taskServiceConnector, userServiceConnector, retryInterval);
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
