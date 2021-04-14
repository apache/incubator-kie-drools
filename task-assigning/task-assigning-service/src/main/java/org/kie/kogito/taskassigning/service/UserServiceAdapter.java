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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.kie.kogito.taskassigning.service.config.TaskAssigningConfig;
import org.kie.kogito.taskassigning.service.config.TaskAssigningConfigProperties;
import org.kie.kogito.taskassigning.service.event.TaskAssigningServiceEventConsumer;
import org.kie.kogito.taskassigning.service.event.UserDataEvent;
import org.kie.kogito.taskassigning.user.service.User;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.taskassigning.service.config.TaskAssigningConfig.UserServiceSyncOnRetriesExceededStrategy.SYNC_ON_NEXT_INTERVAL;

public class UserServiceAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceAdapter.class);

    private static final String QUERY_ERROR_RETRIES = "An error was produced during users information synchronization." +
            " Next attempt will be in a period of {}, error: {}";

    private static final String QUERY_ERROR_RETRIES_EXCEEDED = "An error was produced during users information synchronization." +
            " The configured number of retries {} was exceeded. The configured on-retries-exceeded-strategy is {}," +
            " next attempt will be in a period of {}, error: {}";

    private final TaskAssigningConfig config;

    private final TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer;

    private final ExecutorService executorService;

    private final UserServiceConnector userServiceConnector;

    private final AtomicBoolean destroyed = new AtomicBoolean();

    private int pendingRetries;

    public UserServiceAdapter(TaskAssigningConfig config,
            TaskAssigningServiceEventConsumer taskAssigningServiceEventConsumer,
            ExecutorService executorService,
            UserServiceConnector userServiceConnector) {
        this.config = config;
        this.taskAssigningServiceEventConsumer = taskAssigningServiceEventConsumer;
        this.executorService = executorService;
        this.userServiceConnector = userServiceConnector;
    }

    public void start() {
        pendingRetries = config.getUserServiceSyncRetries();
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
            scheduleExecution(nextStartTime, this::executeQuery);
        }
    }

    void scheduleExecution(Duration nextStartTime, Runnable command) {
        CompletableFuture.delayedExecutor(nextStartTime.toMillis(),
                TimeUnit.MILLISECONDS,
                executorService)
                .execute(command);
    }

    private void executeQuery() {
        if (!destroyed.get()) {
            Result result = loadUsers();
            onQueryResult(result);
        }
    }

    private void onQueryResult(Result result) {
        if (!destroyed.get()) {
            Duration nextStartTime;
            if (!result.hasError()) {
                taskAssigningServiceEventConsumer.accept(new UserDataEvent(result.getUsers(), ZonedDateTime.now()));
                pendingRetries = config.getUserServiceSyncRetries();
                nextStartTime = config.getUserServiceSyncInterval();
            } else {
                if (pendingRetries > 0) {
                    pendingRetries--;
                    nextStartTime = config.getUserServiceSyncRetryInterval();
                    LOGGER.warn(QUERY_ERROR_RETRIES, nextStartTime, result.getError().getMessage());
                } else {
                    pendingRetries = config.getUserServiceSyncRetries();
                    if (config.getUserServiceSyncOnRetriesExceededStrategy() == SYNC_ON_NEXT_INTERVAL) {
                        nextStartTime = config.getUserServiceSyncInterval();
                    } else {
                        nextStartTime = config.getUserServiceSyncRetryInterval();
                    }
                    LOGGER.warn(QUERY_ERROR_RETRIES_EXCEEDED, config.getUserServiceSyncRetries(),
                            config.getUserServiceSyncOnRetriesExceededStrategy(), nextStartTime, result.getError().getMessage());
                }
            }
            programNextExecution(nextStartTime);
        }
    }

    private boolean syncIsEnabled() {
        return !config.getUserServiceSyncInterval().isZero();
    }

    private Result loadUsers() {
        try {
            List<User> users = userServiceConnector.findAllUsers();
            return Result.successful(users);
        } catch (Exception e) {
            return Result.error(e);
        }
    }

    private static class Result {

        private List<User> users = new ArrayList<>();
        private Exception error;

        private Result() {
        }

        public static Result successful(List<User> users) {
            Result result = new Result();
            result.users = users;
            return result;
        }

        public static Result error(Exception error) {
            Result result = new Result();
            result.error = error;
            return result;
        }

        public List<User> getUsers() {
            return users;
        }

        public boolean hasError() {
            return error != null;
        }

        public Exception getError() {
            return error;
        }
    }

}
