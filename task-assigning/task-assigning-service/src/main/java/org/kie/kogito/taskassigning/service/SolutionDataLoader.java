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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.user.service.api.User;
import org.kie.kogito.taskassigning.user.service.api.UserServiceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STARTED;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STOPPED;
import static org.kie.kogito.taskassigning.service.TaskStatus.READY;
import static org.kie.kogito.taskassigning.service.TaskStatus.RESERVED;

public class SolutionDataLoader extends RunnableBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionDataLoader.class);
    private static final String SERVICE_ACCESS_ERROR = "An error was produced during solution data loading." +
            " It was not possible to access the %s." +
            " Next attempt will be in a period of %s, error: %s";

    //TODO upcoming iteration, parametrize this value
    private static final int PAGE_SIZE = 2;

    private TaskServiceConnector taskServiceConnector;
    private UserServiceConnector userServiceConnector;
    private final Duration retryInterval;

    private Semaphore startPermit = new Semaphore(0);
    private Consumer<Result> resultConsumer;
    private int remainingRetries;

    public static class Result {

        private List<UserTaskInstance> tasks;
        private List<User> users;
        private List<Throwable> errors;

        public Result(List<UserTaskInstance> tasks, List<User> users) {
            this.tasks = tasks;
            this.users = users;
        }

        public Result(List<Throwable> errors) {
            this.errors = errors;
        }

        public List<UserTaskInstance> getTasks() {
            return tasks;
        }

        public List<User> getUsers() {
            return users;
        }

        public boolean hasErrors() {
            return errors != null && !errors.isEmpty();
        }

        public List<Throwable> getErrors() {
            return errors;
        }
    }

    public SolutionDataLoader(TaskServiceConnector taskServiceConnector,
                              UserServiceConnector userServiceConnector,
                              Duration retryInterval) {
        this.taskServiceConnector = taskServiceConnector;
        this.userServiceConnector = userServiceConnector;
        this.retryInterval = retryInterval;
    }

    public void start(Consumer<Result> resultConsumer, int retries) {
        if (!status.compareAndSet(STOPPED, STARTED)) {
            throw new IllegalStateException("start method can only be invoked when the status is STOPPED");
        }
        this.resultConsumer = resultConsumer;
        this.remainingRetries = retries;
        startPermit.release();
    }

    @Override
    public void destroy() {
        super.destroy();
        startPermit.release();
    }

    @Override
    public void run() {
        while (isAlive()) {
            try {
                startPermit.acquire();
                if (isAlive()) {
                    Result result = loadData();
                    if (result.hasErrors() && hasRemainingRetries()) {
                        decreaseRemainingRetries();
                        Thread.sleep(retryInterval.toMillis());
                        startPermit.release();
                    } else if (isAlive() && status.compareAndSet(STARTED, STOPPED)) {
                        applyResult(result);
                    }
                }
            } catch (InterruptedException e) {
                super.destroy();
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean hasRemainingRetries() {
        return remainingRetries > 0;
    }

    private void decreaseRemainingRetries() {
        remainingRetries--;
    }

    protected Result loadData() {
        List<UserTaskInstance> tasks = null;
        List<User> users = null;
        try {
            tasks = taskServiceConnector.findAllTasks(Arrays.asList(READY.value(), RESERVED.value()), PAGE_SIZE);
            if (isAlive()) {
                users = userServiceConnector.findAllUsers();
            }
            return new Result(tasks, users);
        } catch (Exception e) {
            String msg;
            if (tasks == null) {
                msg = String.format(SERVICE_ACCESS_ERROR, " Task Service", retryInterval, e.getMessage());
            } else {
                msg = String.format(SERVICE_ACCESS_ERROR, " User Service", retryInterval, e.getMessage());
            }
            LOGGER.warn(msg);
            LOGGER.debug(msg, e);
            return new Result(Collections.singletonList(e));
        }
    }

    protected void applyResult(Result result) {
        resultConsumer.accept(result);
    }
}
