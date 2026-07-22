/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jbpm.usertask.jpa.springboot;

import java.util.*;
import java.util.function.Function;

import org.jbpm.usertask.jpa.JPAUserTaskInstances;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * SpringBoot JPA UserTask Filter Tests
 * Tests filtering functionality for user tasks by taskName, status, processId, and processInstanceId
 */
public abstract class BaseSpringBootJPAUserTaskInstancesTest {

    @Autowired
    JPAUserTaskInstances userTaskInstances;

    private Function<UserTaskInstance, UserTaskInstance> connect;
    private Function<UserTaskInstance, UserTaskInstance> disconnect;

    @BeforeEach
    public void init() {
        connect = Mockito.mock(Function.class);
        disconnect = Mockito.mock(Function.class);

        when(connect.apply(any(UserTaskInstance.class))).thenAnswer(i -> i.getArgument(0));
        when(disconnect.apply(any(UserTaskInstance.class))).thenAnswer(i -> i.getArgument(0));

        userTaskInstances.setReconnectUserTaskInstance(connect);
        userTaskInstances.setDisconnectUserTaskInstance(disconnect);
    }

    @Test
    public void testFindByIdentityWithTaskNameFilter() {
        // Create tasks with different names
        UserTaskInstance task1 = createTaskWithName("task1", "Approve Request", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "Approve Document", "InProgress", "Homer");
        UserTaskInstance task3 = createTaskWithName("task3", "Reject Application", "Reserved", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .taskName("Approve Request")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(1)
                .extracting(UserTaskInstance::getTaskName)
                .containsExactly("Approve Request");

        // Cleanup
        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
    }

    @Test
    public void testFindByIdentityWithTaskNameFilterUpperCase() {
        UserTaskInstance task1 = createTaskWithName("task1", "approve request", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "APPROVE REQUEST", "Reserved", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .taskName("APPROVE REQUEST")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(1)
                .extracting(UserTaskInstance::getTaskName)
                .containsExactly("APPROVE REQUEST");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
    }

    @Test
    public void testFindByIdentityWithStatusFilter() {
        UserTaskInstance task1 = createTaskWithName("task1", "Task 1", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "Task 2", "InProgress", "Homer");
        UserTaskInstance task3 = createTaskWithName("task3", "Task 3", "Completed", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .statuses(Collections.singletonList("Reserved"))
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(1)
                .extracting(UserTaskInstance::getStatus)
                .containsExactly(org.kie.kogito.usertask.lifecycle.UserTaskState.of("Reserved"));

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
    }

    @Test
    public void testFindByIdentityWithMultipleStatusFilter() {
        UserTaskInstance task1 = createTaskWithName("task1", "Task 1", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "Task 2", "InProgress", "Homer");
        UserTaskInstance task3 = createTaskWithName("task3", "Task 3", "Completed", "Homer");
        UserTaskInstance task4 = createTaskWithName("task4", "Task 4", "Suspended", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .statuses(Arrays.asList(
                        "Reserved",
                        "InProgress"))
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(2)
                .extracting(UserTaskInstance::getStatus)
                .containsExactlyInAnyOrder(
                        org.kie.kogito.usertask.lifecycle.UserTaskState.of("Reserved"),
                        org.kie.kogito.usertask.lifecycle.UserTaskState.of("InProgress"));

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
        userTaskInstances.remove(task4);
    }

    @Test
    public void testFindByIdentityWithProcessIdFilter() {
        UserTaskInstance task1 = createTaskWithProcessInfo("task1", "Task 1", "Reserved", "Homer", "hiring", "inst1");
        UserTaskInstance task2 = createTaskWithProcessInfo("task2", "Task 2", "Reserved", "Homer", "onboarding", "inst2");
        UserTaskInstance task3 = createTaskWithProcessInfo("task3", "Task 3", "Reserved", "Homer", "hiring", "inst3");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .processId("hiring")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(2)
                .extracting(UserTaskInstance::getId)
                .containsExactlyInAnyOrder("task1", "task3");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
    }

    @Test
    public void testFindByIdentityWithProcessInstanceIdFilter() {
        UserTaskInstance task1 = createTaskWithProcessInfo("task1", "Task 1", "Reserved", "Homer", "hiring", "inst1");
        UserTaskInstance task2 = createTaskWithProcessInfo("task2", "Task 2", "Reserved", "Homer", "hiring", "inst1");
        UserTaskInstance task3 = createTaskWithProcessInfo("task3", "Task 3", "Reserved", "Homer", "hiring", "inst2");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .processInstanceId("inst1")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(2)
                .extracting(UserTaskInstance::getId)
                .containsExactlyInAnyOrder("task1", "task2");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
    }

    @Test
    public void testFindByIdentityWithCombinedFilters() {
        UserTaskInstance task1 = createTaskWithProcessInfo("task1", "Approve Request", "Reserved", "Homer", "hiring", "inst1");
        UserTaskInstance task2 = createTaskWithProcessInfo("task2", "Approve Document", "InProgress", "Homer", "hiring", "inst1");
        UserTaskInstance task3 = createTaskWithProcessInfo("task3", "Approve Request", "Reserved", "Homer", "onboarding", "inst2");
        UserTaskInstance task4 = createTaskWithProcessInfo("task4", "Review Request", "Reserved", "Homer", "hiring", "inst1");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .taskName("Approve Request")
                .statuses(Collections.singletonList("Reserved"))
                .processId("hiring")
                .processInstanceId("inst1")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(1)
                .extracting(UserTaskInstance::getId)
                .containsExactly("task1");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
        userTaskInstances.remove(task4);
    }

    @Test
    public void testFindByIdentityWithNullFilter() {
        UserTaskInstance task1 = createTaskWithName("task1", "Task 1", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "Task 2", "InProgress", "Homer");

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), null);

        assertThat(result).hasSize(2);

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
    }

    @Test
    public void testFindByIdentityWithEmptyResultFilter() {
        UserTaskInstance task1 = createTaskWithName("task1", "Approve Request", "Reserved", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .taskName("reject")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result).isEmpty();

        userTaskInstances.remove(task1);
    }

    // Helper methods for filter tests

    private UserTaskInstance createTaskWithName(String id, String taskName, String status, String actualOwner) {
        DefaultUserTaskInstance instance = new DefaultUserTaskInstance();
        instance.setId(id);
        instance.setTaskName(taskName);
        instance.setStatus(org.kie.kogito.usertask.lifecycle.UserTaskState.of(status));
        instance.setActualOwner(actualOwner);
        instance.setAdminUsers(Collections.singleton(actualOwner));

        // Initialize deadline and reassignment collections to avoid NPE
        instance.setNotStartedDeadlines(Collections.emptyList());
        instance.setNotCompletedDeadlines(Collections.emptyList());
        instance.setNotStartedReassignments(Collections.emptyList());
        instance.setNotCompletedReassignments(Collections.emptyList());

        instance.setInstances(userTaskInstances);
        userTaskInstances.create(instance);
        return instance;
    }

    private UserTaskInstance createTaskWithProcessInfo(String id, String taskName, String status, String actualOwner,
            String processId, String processInstanceId) {
        DefaultUserTaskInstance instance = new DefaultUserTaskInstance();
        instance.setId(id);
        instance.setTaskName(taskName);
        instance.setStatus(org.kie.kogito.usertask.lifecycle.UserTaskState.of(status));
        instance.setActualOwner(actualOwner);
        instance.setAdminUsers(Collections.singleton(actualOwner));

        org.kie.kogito.usertask.model.ProcessInfo processInfo = org.kie.kogito.usertask.model.ProcessInfo.builder()
                .withProcessId(processId)
                .withProcessInstanceId(processInstanceId)
                .build();
        instance.setProcessInfo(processInfo);

        // Initialize deadline and reassignment collections to avoid NPE
        instance.setNotStartedDeadlines(Collections.emptyList());
        instance.setNotCompletedDeadlines(Collections.emptyList());
        instance.setNotStartedReassignments(Collections.emptyList());
        instance.setNotCompletedReassignments(Collections.emptyList());

        instance.setInstances(userTaskInstances);
        userTaskInstances.create(instance);
        return instance;
    }
}
