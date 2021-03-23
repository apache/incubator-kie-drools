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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.index.service.client.graphql.UserTaskInstance;
import org.kie.kogito.taskassigning.service.TaskData;
import org.kie.kogito.taskassigning.service.messaging.UserTaskEvent;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.service.TestUtil.parseZonedDateTime;

class TaskUtilTest {

    private static final String TASK_ID = "TASK_ID";
    private static final String TASK_NAME = "TASK_NAME";
    private static final String TASK_DESCRIPTION = "TASK_DESCRIPTION";
    private static final String TASK_PRIORITY = "0";
    private static final String REFERENCE_NAME = "REFERENCE_NAME";
    private static final String ENDPOINT = "ENDPOINT";

    private static final ZonedDateTime START_DATE = parseZonedDateTime("2021-02-10T10:00:00.000+01:00");
    private static final ZonedDateTime LAST_UPDATE = parseZonedDateTime("2021-02-10T11:00:00.000+01:00");
    private static final ZonedDateTime COMPLETED_DATE = parseZonedDateTime("2021-02-10T12:00:00.000+01:00");

    private static final String STATE = "STATE";
    private static final String ACTUAL_OWNER = "ACTUAL_OWNER";
    private static final List<String> POTENTIAL_USERS = Arrays.asList("POTENTIAL_USER_1", "POTENTIAL_USER_2");
    private static final List<String> POTENTIAL_GROUPS = Arrays.asList("POTENTIAL_GROUP_1", "POTENTIAL_GROUP_2");
    private static final List<String> EXCLUDED_USERS = Arrays.asList("EXCLUDED_USER_1", "EXCLUDED_USER_2");
    private static final List<String> ADMIN_USERS = Arrays.asList("ADMIN_USER_1", "ADMIN_USER_2");
    private static final List<String> ADMIN_GROUPS = Arrays.asList("ADMIN_GROUP_1", "ADMIN_GROUP_2");

    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";

    @Test
    void fromUserTaskInstances() {
        UserTaskInstance userTaskInstance = new UserTaskInstance();
        userTaskInstance.setId(TASK_ID);
        userTaskInstance.setDescription(TASK_DESCRIPTION);
        userTaskInstance.setName(TASK_NAME);
        userTaskInstance.setPriority(TASK_PRIORITY);
        userTaskInstance.setProcessInstanceId(PROCESS_INSTANCE_ID);
        userTaskInstance.setProcessId(PROCESS_ID);
        userTaskInstance.setRootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID);
        userTaskInstance.setRootProcessId(ROOT_PROCESS_ID);
        userTaskInstance.setState(STATE);
        userTaskInstance.setActualOwner(ACTUAL_OWNER);
        userTaskInstance.setAdminGroups(ADMIN_GROUPS);
        userTaskInstance.setAdminUsers(ADMIN_USERS);
        userTaskInstance.setCompleted(COMPLETED_DATE);
        userTaskInstance.setStarted(START_DATE);
        userTaskInstance.setExcludedUsers(EXCLUDED_USERS);
        userTaskInstance.setPotentialGroups(POTENTIAL_GROUPS);
        userTaskInstance.setPotentialUsers(POTENTIAL_USERS);
        userTaskInstance.setReferenceName(REFERENCE_NAME);
        userTaskInstance.setLastUpdate(LAST_UPDATE);
        userTaskInstance.setEndpoint(ENDPOINT);
        List<TaskData> result = TaskUtil.fromUserTaskInstances(Collections.singletonList(userTaskInstance));
        assertThat(result).hasSize(1);
        TaskData taskData = result.get(0);
        assertExpectedTaskData(taskData);
    }

    @Test
    void fromUserTaskEvents() {
        UserTaskEvent userTaskEvent = new UserTaskEvent();
        userTaskEvent.setTaskId(TASK_ID);
        userTaskEvent.setDescription(TASK_DESCRIPTION);
        userTaskEvent.setName(TASK_NAME);
        userTaskEvent.setPriority(TASK_PRIORITY);
        userTaskEvent.setProcessInstanceId(PROCESS_INSTANCE_ID);
        userTaskEvent.setProcessId(PROCESS_ID);
        userTaskEvent.setRootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID);
        userTaskEvent.setRootProcessId(ROOT_PROCESS_ID);
        userTaskEvent.setState(STATE);
        userTaskEvent.setActualOwner(ACTUAL_OWNER);
        userTaskEvent.setAdminGroups(ADMIN_GROUPS);
        userTaskEvent.setAdminUsers(ADMIN_USERS);
        userTaskEvent.setCompleted(COMPLETED_DATE);
        userTaskEvent.setStarted(START_DATE);
        userTaskEvent.setExcludedUsers(EXCLUDED_USERS);
        userTaskEvent.setPotentialGroups(POTENTIAL_GROUPS);
        userTaskEvent.setPotentialUsers(POTENTIAL_USERS);
        userTaskEvent.setReferenceName(REFERENCE_NAME);
        userTaskEvent.setLastUpdate(LAST_UPDATE);
        userTaskEvent.setEndpoint(ENDPOINT);
        List<TaskData> result = TaskUtil.fromUserTaskEvents(Collections.singletonList(userTaskEvent));
        assertThat(result).hasSize(1);
        TaskData taskData = result.get(0);
        assertExpectedTaskData(taskData);
    }

    @Test
    void fromTaskData() {
        Task task = TaskUtil.fromTaskData(new TaskData() {
            @Override
            public String getId() {
                return TASK_ID;
            }

            @Override
            public String getName() {
                return TASK_NAME;
            }

            @Override
            public String getState() {
                return STATE;
            }

            @Override
            public String getDescription() {
                return TASK_DESCRIPTION;
            }

            @Override
            public String getReferenceName() {
                return REFERENCE_NAME;
            }

            @Override
            public String getPriority() {
                return TASK_PRIORITY;
            }

            @Override
            public String getProcessInstanceId() {
                return PROCESS_INSTANCE_ID;
            }

            @Override
            public String getProcessId() {
                return PROCESS_ID;
            }

            @Override
            public String getRootProcessInstanceId() {
                return ROOT_PROCESS_INSTANCE_ID;
            }

            @Override
            public String getRootProcessId() {
                return ROOT_PROCESS_ID;
            }

            @Override
            public String getActualOwner() {
                return ACTUAL_OWNER;
            }

            @Override
            public Set<String> getPotentialUsers() {
                return new HashSet<>(POTENTIAL_USERS);
            }

            @Override
            public Set<String> getPotentialGroups() {
                return new HashSet<>(POTENTIAL_GROUPS);
            }

            @Override
            public Set<String> getAdminUsers() {
                return new HashSet<>(ADMIN_USERS);
            }

            @Override
            public Set<String> getAdminGroups() {
                return new HashSet<>(ADMIN_GROUPS);
            }

            @Override
            public Set<String> getExcludedUsers() {
                return new HashSet<>(EXCLUDED_USERS);
            }

            @Override
            public ZonedDateTime getStarted() {
                return START_DATE;
            }

            @Override
            public ZonedDateTime getCompleted() {
                return COMPLETED_DATE;
            }

            @Override
            public ZonedDateTime getLastUpdate() {
                return LAST_UPDATE;
            }

            @Override
            public JsonNode getInputs() {
                return null;
            }

            @Override
            public String getEndpoint() {
                return ENDPOINT;
            }
        });
        assertThat(task.getId()).isEqualTo(TASK_ID);
        assertThat(task.getDescription()).isEqualTo(TASK_DESCRIPTION);
        assertThat(task.getName()).isEqualTo(TASK_NAME);
        assertThat(task.getPriority()).isEqualTo(TASK_PRIORITY);
        assertThat(task.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(task.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(task.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(task.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(task.getState()).isEqualTo(STATE);
        assertThat(task.getAdminGroups()).containsExactlyInAnyOrder(ADMIN_GROUPS.toArray(new String[0]));
        assertThat(task.getAdminUsers()).containsExactlyInAnyOrder(ADMIN_USERS.toArray(new String[0]));
        assertThat(task.getCompleted()).isEqualTo(COMPLETED_DATE);
        assertThat(task.getStarted()).isEqualTo(START_DATE);
        assertThat(task.getExcludedUsers()).containsExactlyInAnyOrder(EXCLUDED_USERS.toArray(new String[0]));
        assertThat(task.getPotentialGroups()).containsExactlyInAnyOrder(POTENTIAL_GROUPS.toArray(new String[0]));
        assertThat(task.getPotentialUsers()).containsExactlyInAnyOrder(POTENTIAL_USERS.toArray(new String[0]));
        assertThat(task.getReferenceName()).isEqualTo(REFERENCE_NAME);
        assertThat(task.getLastUpdate()).isEqualTo(LAST_UPDATE);
        assertThat(task.getEndpoint()).isEqualTo(ENDPOINT);
    }

    private void assertExpectedTaskData(TaskData taskData) {
        assertThat(taskData.getId()).isEqualTo(TASK_ID);
        assertThat(taskData.getDescription()).isEqualTo(TASK_DESCRIPTION);
        assertThat(taskData.getName()).isEqualTo(TASK_NAME);
        assertThat(taskData.getPriority()).isEqualTo(TASK_PRIORITY);
        assertThat(taskData.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(taskData.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(taskData.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(taskData.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(taskData.getState()).isEqualTo(STATE);
        assertThat(taskData.getAdminGroups()).containsExactlyInAnyOrder(ADMIN_GROUPS.toArray(new String[0]));
        assertThat(taskData.getAdminUsers()).containsExactlyInAnyOrder(ADMIN_USERS.toArray(new String[0]));
        assertThat(taskData.getCompleted()).isEqualTo(COMPLETED_DATE);
        assertThat(taskData.getStarted()).isEqualTo(START_DATE);
        assertThat(taskData.getExcludedUsers()).containsExactlyInAnyOrder(EXCLUDED_USERS.toArray(new String[0]));
        assertThat(taskData.getPotentialGroups()).containsExactlyInAnyOrder(POTENTIAL_GROUPS.toArray(new String[0]));
        assertThat(taskData.getPotentialUsers()).containsExactlyInAnyOrder(POTENTIAL_USERS.toArray(new String[0]));
        assertThat(taskData.getReferenceName()).isEqualTo(REFERENCE_NAME);
        assertThat(taskData.getLastUpdate()).isEqualTo(LAST_UPDATE);
        assertThat(taskData.getEndpoint()).isEqualTo(ENDPOINT);
    }
}
