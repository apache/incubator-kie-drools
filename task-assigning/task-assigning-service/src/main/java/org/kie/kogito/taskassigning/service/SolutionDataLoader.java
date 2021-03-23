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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.kie.kogito.taskassigning.user.service.api.User;
import org.kie.kogito.taskassigning.user.service.api.UserServiceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STARTED;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STARTING;
import static org.kie.kogito.taskassigning.service.RunnableBase.Status.STOPPED;
import static org.kie.kogito.taskassigning.service.TaskState.READY;
import static org.kie.kogito.taskassigning.service.TaskState.RESERVED;
import static org.kie.kogito.taskassigning.service.util.TaskUtil.fromUserTaskInstances;

public class SolutionDataLoader extends RunnableBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionDataLoader.class);
    private static final String SERVICE_ACCESS_ERROR = "An error was produced during solution data loading." +
            " It was not possible to access the %s." +
            " Next attempt will be in a period of %s, error: %s";

    private TaskServiceConnector taskServiceConnector;
    private UserServiceConnector userServiceConnector;
    private Consumer<Result> resultConsumer;
    private boolean includeTasks;
    private boolean includeUsers;
    private Duration retryInterval;
    private int remainingRetries;
    private int pageSize;

    public static class Result {

        private List<TaskData> tasks = new ArrayList<>();
        private List<User> users = new ArrayList<>();
        private List<Exception> errors;

        public Result(List<TaskData> tasks, List<User> users) {
            this.tasks = tasks;
            this.users = users;
        }

        public Result(List<Exception> errors) {
            this.errors = errors;
        }

        public List<TaskData> getTasks() {
            return tasks;
        }

        public List<User> getUsers() {
            return users;
        }

        public boolean hasErrors() {
            return errors != null && !errors.isEmpty();
        }

        public List<Exception> getErrors() {
            return errors;
        }
    }

    public SolutionDataLoader(TaskServiceConnector taskServiceConnector,
            UserServiceConnector userServiceConnector) {

        this.taskServiceConnector = taskServiceConnector;
        this.userServiceConnector = userServiceConnector;
    }

    public void start(Consumer<Result> resultConsumer, boolean includeTasks, boolean includeUsers,
            Duration retryInterval, int retries, int pageSize) {
        startCheck();
        this.resultConsumer = resultConsumer;
        this.includeTasks = includeTasks;
        this.includeUsers = includeUsers;
        this.retryInterval = retryInterval;
        this.remainingRetries = retries;
        this.pageSize = pageSize;
        startPermit.release();
    }

    @Override
    public void run() {
        while (isAlive()) {
            try {
                startPermit.acquire();
                if (isAlive() && (status.compareAndSet(STARTING, STARTED) || status.get() == STARTED)) {
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
        List<TaskData> tasks = null;
        List<User> users = null;
        try {
            if (includeTasks && isAlive()) {
                tasks = fromUserTaskInstances(taskServiceConnector.findAllTasks(Arrays.asList(READY.value(), RESERVED.value()), pageSize));
            }
            if (includeUsers && isAlive()) {
                users = userServiceConnector.findAllUsers();
            }
            return new Result(tasks != null ? tasks : new ArrayList<>(),
                    users != null ? users : new ArrayList<>());
        } catch (Exception e) {
            String msg;
            if (includeTasks && tasks == null) {
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
