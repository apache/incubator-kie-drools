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
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties;
import org.kie.kogito.taskassigning.service.event.TaskAssigningServiceEventConsumer;
import org.kie.kogito.taskassigning.service.event.UserDataEvent;
import org.kie.kogito.taskassigning.user.service.User;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class UserServiceAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceAdapter.class);

    private final TaskAssigningService service;

    private final TaskAssigningConfig config;

    private final TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer;

    private final ManagedExecutor managedExecutor;

    private final UserServiceConnector userServiceConnector;

    private final Event<StartExecution> startExecutionEvent;

    private final AtomicBoolean destroyed = new AtomicBoolean();

    static class StartExecution {
    }

    @Inject
    public UserServiceAdapter(TaskAssigningService service,
            TaskAssigningConfig config,
            TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer,
            ManagedExecutor managedExecutor,
            UserServiceConnector userServiceConnector,
            Event<StartExecution> startExecutionEvent) {
        this.service = service;
        this.config = config;
        this.taskAssigningServiceEventConsumer = taskAssigningServiceEventConsumer;
        this.managedExecutor = managedExecutor;
        this.userServiceConnector = userServiceConnector;
        this.startExecutionEvent = startExecutionEvent;
    }

    public void start() {
        if (syncIsEnabled()) {
            programNextExecution(config.getUserServiceSyncInterval());
        } else {
            LOGGER.warn("A zero duration was configured for the property " +
                    TaskAssigningConfigProperties.USER_SERVICE_SYNC_INTERVAL + ": {}," +
                    " users information synchronization will be disabled.", config.getUserServiceSyncInterval());
        }
    }

    public void destroy() {
        destroyed.set(true);
    }

    private void programNextExecution(Duration nextStartTime) {
        if (!destroyed.get()) {
            scheduleExecution(nextStartTime, () -> startExecutionEvent.fire(new StartExecution()));
        }
    }

    void scheduleExecution(Duration nextStartTime, Runnable command) {
        CompletableFuture.delayedExecutor(nextStartTime.toMillis(),
                TimeUnit.MILLISECONDS,
                managedExecutor)
                .execute(command);
    }

    void executeQuery(@Observes StartExecution evt) {
        if (!destroyed.get()) {
            loadUsersData()
                    .thenAccept(this::onQuerySuccessful)
                    .exceptionally(throwable -> {
                        onQueryFailure(throwable);
                        return null;
                    });

        }
    }

    private void onQuerySuccessful(List<User> users) {
        if (!destroyed.get()) {
            taskAssigningServiceEventConsumer.accept(new UserDataEvent(users, ZonedDateTime.now()));
            programNextExecution(config.getUserServiceSyncInterval());
        }
    }

    private void onQueryFailure(Throwable throwable) {
        service.failFast(throwable);
    }

    private boolean syncIsEnabled() {
        return !config.getUserServiceSyncInterval().isZero();
    }

    @Asynchronous
    @Retry(maxRetries = -1,
            delay = 2000,
            maxDuration = 5,
            durationUnit = ChronoUnit.MINUTES)
    @Timeout(value = 10,
            unit = ChronoUnit.MINUTES)
    public CompletionStage<List<User>> loadUsersData() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();
        try {
            future.complete(userServiceConnector.findAllUsers());
        } catch (Exception e) {
            String msg = String.format("An error was produced during users information synchronization, error: %s", e.getMessage());
            LOGGER.warn(msg);
            future.completeExceptionally(new TaskAssigningException(msg, e));
        }
        return future;
    }
}
