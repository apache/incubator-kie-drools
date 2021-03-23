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

package org.kie.kogito.taskassigning.service.util;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.service.TaskData;
import org.kie.kogito.taskassigning.service.messaging.UserTaskEvent;

import com.fasterxml.jackson.databind.JsonNode;

public class TaskUtil {

    private TaskUtil() {
    }

    public static List<TaskData> fromUserTaskInstances(List<UserTaskInstance> userTaskInstances) {
        return fromMappedType(userTaskInstances, UserTaskInstanceTaskData::new);
    }

    public static List<TaskData> fromUserTaskEvents(List<UserTaskEvent> userTaskEvents) {
        return fromMappedType(userTaskEvents, UserTaskEventTaskData::new);
    }

    private static <T> List<TaskData> fromMappedType(List<T> values, Function<T, TaskData> mapper) {
        return Optional.ofNullable(values)
                .orElse(Collections.emptyList())
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }

    public static Task fromTaskData(TaskData taskData) {
        return Task.newBuilder()
                .id(taskData.getId())
                .name(taskData.getName())
                .state(taskData.getState())
                .description(taskData.getDescription())
                .referenceName(taskData.getReferenceName())
                .priority(taskData.getPriority())
                .processInstanceId(taskData.getProcessInstanceId())
                .processId(taskData.getProcessId())
                .rootProcessInstanceId(taskData.getRootProcessInstanceId())
                .rootProcessId(taskData.getRootProcessId())
                .potentialUsers(taskData.getPotentialUsers())
                .potentialGroups(taskData.getPotentialGroups())
                .adminUsers(taskData.getAdminUsers())
                .adminGroups(taskData.getAdminGroups())
                .excludedUsers(taskData.getExcludedUsers())
                .started(taskData.getStarted())
                .completed(taskData.getCompleted())
                .lastUpdate(taskData.getLastUpdate())
                //TODO Upcoming iteration
                //.inputData(taskInstance.getInputs())
                .endpoint(taskData.getEndpoint())
                .build();
    }

    /**
     * Avoid falling into https://issues.redhat.com/browse/KOGITO-4537.
     * TODO, remove all the usages of this method when fixed.
     */
    private static String sanitizeStringValue(String priority) {
        if (priority != null && priority.isEmpty()) {
            return null;
        }
        return priority;
    }

    private static <T> Set<T> toSet(Collection<T> collection) {
        return collection != null ? new HashSet<>(collection) : new HashSet<>();
    }

    private static class UserTaskEventTaskData implements TaskData {
        private UserTaskEvent userTaskEvent;

        private UserTaskEventTaskData(UserTaskEvent userTaskEvent) {
            this.userTaskEvent = userTaskEvent;
        }

        @Override
        public String getId() {
            return userTaskEvent.getTaskId();
        }

        @Override
        public String getName() {
            return userTaskEvent.getName();
        }

        @Override
        public String getState() {
            return userTaskEvent.getState();
        }

        @Override
        public String getDescription() {
            return sanitizeStringValue(userTaskEvent.getDescription());
        }

        @Override
        public String getReferenceName() {
            return userTaskEvent.getReferenceName();
        }

        @Override
        public String getPriority() {
            return sanitizeStringValue(userTaskEvent.getPriority());
        }

        @Override
        public String getProcessInstanceId() {
            return userTaskEvent.getProcessInstanceId();
        }

        @Override
        public String getProcessId() {
            return userTaskEvent.getProcessId();
        }

        @Override
        public String getRootProcessInstanceId() {
            return userTaskEvent.getRootProcessInstanceId();
        }

        @Override
        public String getRootProcessId() {
            return userTaskEvent.getRootProcessId();
        }

        @Override
        public String getActualOwner() {
            return userTaskEvent.getActualOwner();
        }

        @Override
        public Set<String> getPotentialUsers() {
            return toSet(userTaskEvent.getPotentialUsers());
        }

        @Override
        public Set<String> getPotentialGroups() {
            return toSet(userTaskEvent.getPotentialGroups());
        }

        @Override
        public Set<String> getAdminUsers() {
            return toSet(userTaskEvent.getAdminUsers());
        }

        @Override
        public Set<String> getAdminGroups() {
            return toSet(userTaskEvent.getAdminGroups());
        }

        @Override
        public Set<String> getExcludedUsers() {
            return toSet(userTaskEvent.getExcludedUsers());
        }

        @Override
        public ZonedDateTime getStarted() {
            return userTaskEvent.getStarted();
        }

        @Override
        public ZonedDateTime getCompleted() {
            return userTaskEvent.getCompleted();
        }

        @Override
        public ZonedDateTime getLastUpdate() {
            return userTaskEvent.getLastUpdate();
        }

        @Override
        public JsonNode getInputs() {
            return userTaskEvent.getInputs();
        }

        @Override
        public String getEndpoint() {
            return userTaskEvent.getEndpoint();
        }
    }

    private static class UserTaskInstanceTaskData implements TaskData {
        private UserTaskInstance userTaskInstance;

        private UserTaskInstanceTaskData(UserTaskInstance userTaskInstance) {
            this.userTaskInstance = userTaskInstance;
        }

        @Override
        public String getId() {
            return userTaskInstance.getId();
        }

        @Override
        public String getName() {
            return userTaskInstance.getName();
        }

        @Override
        public String getState() {
            return userTaskInstance.getState();
        }

        @Override
        public String getDescription() {
            return sanitizeStringValue(userTaskInstance.getDescription());
        }

        @Override
        public String getReferenceName() {
            return userTaskInstance.getReferenceName();
        }

        @Override
        public String getPriority() {
            return sanitizeStringValue(userTaskInstance.getPriority());
        }

        @Override
        public String getProcessInstanceId() {
            return userTaskInstance.getProcessInstanceId();
        }

        @Override
        public String getProcessId() {
            return userTaskInstance.getProcessId();
        }

        @Override
        public String getRootProcessInstanceId() {
            return userTaskInstance.getRootProcessInstanceId();
        }

        @Override
        public String getRootProcessId() {
            return userTaskInstance.getRootProcessId();
        }

        @Override
        public String getActualOwner() {
            return userTaskInstance.getActualOwner();
        }

        @Override
        public Set<String> getPotentialUsers() {
            return toSet(userTaskInstance.getPotentialUsers());
        }

        @Override
        public Set<String> getPotentialGroups() {
            return toSet(userTaskInstance.getPotentialGroups());
        }

        @Override
        public Set<String> getAdminUsers() {
            return toSet(userTaskInstance.getAdminUsers());
        }

        @Override
        public Set<String> getAdminGroups() {
            return toSet(userTaskInstance.getAdminGroups());
        }

        @Override
        public Set<String> getExcludedUsers() {
            return toSet(userTaskInstance.getExcludedUsers());
        }

        @Override
        public ZonedDateTime getStarted() {
            return userTaskInstance.getStarted();
        }

        @Override
        public ZonedDateTime getCompleted() {
            return userTaskInstance.getCompleted();
        }

        @Override
        public ZonedDateTime getLastUpdate() {
            return userTaskInstance.getLastUpdate();
        }

        @Override
        public JsonNode getInputs() {
            return userTaskInstance.getInputs();
        }

        @Override
        public String getEndpoint() {
            return userTaskInstance.getEndpoint();
        }
    }
}
