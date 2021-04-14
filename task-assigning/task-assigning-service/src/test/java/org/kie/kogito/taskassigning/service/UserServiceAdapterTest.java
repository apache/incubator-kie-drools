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
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.kie.kogito.taskassigning.service.event.TaskAssigningServiceEventConsumer;
import org.kie.kogito.taskassigning.service.event.UserDataEvent;
import org.kie.kogito.taskassigning.user.service.User;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfig.UserServiceSyncOnRetriesExceededStrategy.SYNC_IMMEDIATELY;
import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfig.UserServiceSyncOnRetriesExceededStrategy.SYNC_ON_NEXT_INTERVAL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceAdapterTest {

    private static final Duration SYNC_INTERVAL = Duration.parse("PT2H");
    private static final Duration SYNC_RETRY_INTERVAL = Duration.parse("PT1S");

    @Mock
    private TaskAssigningConfig config;

    @Mock
    private TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer;

    @Mock
    private ExecutorService executorService;

    @Mock
    private UserServiceConnector userServiceConnector;

    private UserServiceAdapter adapter;

    @Captor
    private ArgumentCaptor<Runnable> executionCaptor;

    @Captor
    private ArgumentCaptor<Duration> nextStartTimeCaptor;

    @Captor
    private ArgumentCaptor<UserDataEvent> eventCaptor;

    @BeforeEach
    void setUp() {
        lenient().doReturn(SYNC_INTERVAL).when(config).getUserServiceSyncInterval();
        lenient().doReturn(SYNC_RETRY_INTERVAL).when(config).getUserServiceSyncRetryInterval();
        executorService = Executors.newFixedThreadPool(1);
        adapter = spy(new UserServiceAdapter(config, taskAssigningServiceEventConsumer, executorService, userServiceConnector) {
            @Override
            void scheduleExecution(Duration nextStartTime, Runnable command) {
                //override for facilitating testing.
            }
        });
    }

    @Test
    void start() {
        adapter.start();
        verify(adapter).scheduleExecution(nextStartTimeCaptor.capture(), executionCaptor.capture());
        assertThat(nextStartTimeCaptor.getValue()).isEqualTo(SYNC_INTERVAL);
        verify(userServiceConnector, never()).findAllUsers();
        verify(taskAssigningServiceEventConsumer, never()).accept(any());
    }

    @Test
    void startWithSyncDisabledZero() {
        startWithSyncDisabled(Duration.ZERO);
    }

    private void startWithSyncDisabled(Duration userServiceSyncInterval) {
        doReturn(userServiceSyncInterval).when(config).getUserServiceSyncInterval();
        adapter.start();
        verify(adapter, never()).scheduleExecution(any(), any());

    }

    @Test
    void executionSuccessful() {
        List<User> users = new ArrayList<>();
        doReturn(users).when(userServiceConnector).findAllUsers();
        adapter.start();
        verify(adapter).scheduleExecution(any(), executionCaptor.capture());
        executionCaptor.getValue().run();
        verify(adapter, times(2)).scheduleExecution(nextStartTimeCaptor.capture(), any());
        assertThat(nextStartTimeCaptor.getAllValues().get(0)).isEqualTo(SYNC_INTERVAL);
        assertThat(nextStartTimeCaptor.getAllValues().get(1)).isEqualTo(SYNC_INTERVAL);
        verify(taskAssigningServiceEventConsumer).accept(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isNotNull();
        assertThat(eventCaptor.getValue().getData()).isSameAs(users);
    }

    @Test
    void executionFailureWithRemainingRetries() {
        doReturn(5).when(config).getUserServiceSyncRetries();
        executeWithFailure();
        assertThat(nextStartTimeCaptor.getAllValues().get(1)).isEqualTo(SYNC_RETRY_INTERVAL);
    }

    @Test
    void executionFailureWithNoRemainingRetriesSyncOnNextIntervalStrategy() {
        doReturn(0).when(config).getUserServiceSyncRetries();
        doReturn(SYNC_ON_NEXT_INTERVAL).when(config).getUserServiceSyncOnRetriesExceededStrategy();
        executeWithFailure();
        assertThat(nextStartTimeCaptor.getAllValues().get(1)).isEqualTo(SYNC_INTERVAL);
    }

    @Test
    void executionFailureWithNoRemainingRetriesSyncImmediatelyStrategy() {
        doReturn(0).when(config).getUserServiceSyncRetries();
        doReturn(SYNC_IMMEDIATELY).when(config).getUserServiceSyncOnRetriesExceededStrategy();
        executeWithFailure();
        assertThat(nextStartTimeCaptor.getAllValues().get(1)).isEqualTo(SYNC_RETRY_INTERVAL);
    }

    @Test
    @Timeout(2)
    void scheduleExecution() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean wasExecuted = new AtomicBoolean();
        ExecutorService realExecutorService = Executors.newSingleThreadExecutor();
        UserServiceAdapter realAdapter = new UserServiceAdapter(config, taskAssigningServiceEventConsumer,
                realExecutorService, userServiceConnector);
        realAdapter.scheduleExecution(Duration.parse("PT1S"), () -> {
            wasExecuted.set(true);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        assertThat(wasExecuted).isTrue();
        realExecutorService.shutdown();
    }

    private void executeWithFailure() {
        doThrow(new RuntimeException("User service failed"))
                .when(userServiceConnector)
                .findAllUsers();
        adapter.start();
        verify(adapter).scheduleExecution(any(), executionCaptor.capture());
        executionCaptor.getValue().run();
        verify(taskAssigningServiceEventConsumer, never()).accept(eventCaptor.capture());
        verify(adapter, times(2)).scheduleExecution(nextStartTimeCaptor.capture(), any());
    }
}
