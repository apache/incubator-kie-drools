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
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.event.Event;

import org.eclipse.microprofile.context.ManagedExecutor;
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

    @Mock
    private TaskAssigningConfig config;

    @Mock
    private TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer;

    @Mock
    private ManagedExecutor managedExecutor;

    @Mock
    private UserServiceConnector userServiceConnector;

    @Mock
    private Event<UserServiceAdapter.StartExecution> startExecutionEvent;

    @Mock
    private Event<TaskAssigningService.FailFastRequestEvent> failFastRequestEvent;

    private UserServiceAdapter adapter;

    @Captor
    private ArgumentCaptor<Runnable> executionCaptor;

    @Captor
    private ArgumentCaptor<Duration> nextStartTimeCaptor;

    @Captor
    private ArgumentCaptor<UserDataEvent> eventCaptor;

    @Captor
    private ArgumentCaptor<TaskAssigningService.FailFastRequestEvent> failFastRequestEventCaptor;

    @BeforeEach
    void setUp() {
        lenient().doReturn(SYNC_INTERVAL).when(config).getUserServiceSyncInterval();
        adapter = spy(new UserServiceAdapter(config, taskAssigningServiceEventConsumer,
                managedExecutor, userServiceConnector, startExecutionEvent, failFastRequestEvent));
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
    void startWithSyncDisabled() {
        doReturn(Duration.ZERO).when(config).getUserServiceSyncInterval();
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
        verify(startExecutionEvent).fire(any());
        adapter.executeQuery(new UserServiceAdapter.StartExecution());

        verify(adapter, times(2)).scheduleExecution(nextStartTimeCaptor.capture(), any());
        assertThat(nextStartTimeCaptor.getAllValues().get(0)).isEqualTo(SYNC_INTERVAL);
        assertThat(nextStartTimeCaptor.getAllValues().get(1)).isEqualTo(SYNC_INTERVAL);
        verify(taskAssigningServiceEventConsumer).accept(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isNotNull();
        assertThat(eventCaptor.getValue().getData()).isSameAs(users);
    }

    @Test
    void executionWithFailure() {
        String serviceFailure = "User service failed";
        doThrow(new RuntimeException(serviceFailure))
                .when(userServiceConnector)
                .findAllUsers();
        adapter.start();
        verify(adapter).scheduleExecution(any(), executionCaptor.capture());
        executionCaptor.getValue().run();
        verify(startExecutionEvent).fire(any());
        adapter.executeQuery(new UserServiceAdapter.StartExecution());

        verify(adapter, times(1)).scheduleExecution(nextStartTimeCaptor.capture(), any());
        verify(taskAssigningServiceEventConsumer, never()).accept(eventCaptor.capture());
        verify(failFastRequestEvent).fire(failFastRequestEventCaptor.capture());
        assertThat(failFastRequestEventCaptor.getValue().getCause())
                .isNotNull()
                .hasMessageContaining("An error was produced during users information synchronization, error: %s", serviceFailure);
    }

    @Test
    @Timeout(2)
    void scheduleExecution() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AtomicBoolean wasExecuted = new AtomicBoolean();
        ManagedExecutor realManagedExecutor = ManagedExecutor.builder()
                .maxAsync(1)
                .maxQueued(1)
                .build();

        UserServiceAdapter realAdapter = new UserServiceAdapter(config, taskAssigningServiceEventConsumer,
                realManagedExecutor, userServiceConnector, startExecutionEvent, failFastRequestEvent);
        realAdapter.scheduleExecution(Duration.parse("PT1S"), () -> {
            wasExecuted.set(true);
            countDownLatch.countDown();
        });
        countDownLatch.await();
        assertThat(wasExecuted).isTrue();
        realManagedExecutor.shutdown();
    }

    @Test
    void destroy() {
        adapter.start();
        verify(adapter).scheduleExecution(any(), executionCaptor.capture());
        adapter.destroy();
        executionCaptor.getValue().run();
        verify(startExecutionEvent).fire(any());
        adapter.executeQuery(new UserServiceAdapter.StartExecution());
        verify(taskAssigningServiceEventConsumer, never()).accept(any());
    }
}
