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

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.Asynchronous;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.kie.kogito.taskassigning.user.service.User;
import org.kie.kogito.taskassigning.user.service.UserServiceConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.taskassigning.service.TaskState.READY;
import static org.kie.kogito.taskassigning.service.TaskState.RESERVED;
import static org.kie.kogito.taskassigning.service.util.TaskUtil.fromUserTaskInstances;

@ApplicationScoped
public class SolutionDataLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolutionDataLoader.class);
    private static final String SERVICE_ACCESS_ERROR = "An error was produced during solution data loading." +
            " It was not possible to access the %s, error: %s";

    public static class Result {

        private final List<TaskData> tasks;
        private final List<User> users;

        public Result(List<TaskData> tasks, List<User> users) {
            this.tasks = tasks;
            this.users = users;
        }

        public List<TaskData> getTasks() {
            return tasks;
        }

        public List<User> getUsers() {
            return users;
        }
    }

    private final TaskServiceConnector taskServiceConnector;

    private final UserServiceConnector userServiceConnector;

    @Inject
    public SolutionDataLoader(TaskServiceConnector taskServiceConnector, UserServiceConnector userServiceConnector) {
        this.taskServiceConnector = taskServiceConnector;
        this.userServiceConnector = userServiceConnector;
    }

    @Asynchronous
    @Retry(maxRetries = -1,
            delay = 2000,
            maxDuration = 15,
            durationUnit = ChronoUnit.MINUTES)
    @Timeout(value = 10,
            unit = ChronoUnit.MINUTES)
    public CompletionStage<Result> loadSolutionData(boolean includeTasks, boolean includeUsers, int pageSize) {
        CompletableFuture<Result> future = new CompletableFuture<>();
        try {
            Result result = loadData(includeTasks, includeUsers, pageSize);
            future.complete(result);
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    private Result loadData(boolean includeTasks, boolean includeUsers, int pageSize) {
        List<TaskData> tasks = null;
        List<User> users = null;
        try {
            if (includeTasks) {
                tasks = fromUserTaskInstances(taskServiceConnector.findAllTasks(Arrays.asList(READY.value(), RESERVED.value()),
                        pageSize));
            }
            if (includeUsers) {
                users = userServiceConnector.findAllUsers();
            }
            return new Result(tasks != null ? tasks : new ArrayList<>(),
                    users != null ? users : new ArrayList<>());
        } catch (Exception e) {
            String msg;
            if (includeTasks && tasks == null) {
                msg = String.format(SERVICE_ACCESS_ERROR, "Task Service", e.getMessage());
            } else {
                msg = String.format(SERVICE_ACCESS_ERROR, "User Service", e.getMessage());
            }
            LOGGER.warn(msg);
            throw new TaskAssigningException(msg, e);
        }
    }
}
