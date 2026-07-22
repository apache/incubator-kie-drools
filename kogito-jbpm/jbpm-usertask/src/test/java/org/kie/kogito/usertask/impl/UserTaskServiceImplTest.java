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
package org.kie.kogito.usertask.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.Application;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskFilter;
import org.kie.kogito.usertask.UserTaskInstances;
import org.kie.kogito.usertask.UserTasks;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.view.UserTaskView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserTaskServiceImplTest {

    private UserTaskServiceImpl service;
    private UserTaskInstances instances;
    private IdentityProvider identity;
    private Application application;
    private UserTasks userTasks;

    @BeforeEach
    public void setup() {
        instances = mock(UserTaskInstances.class);
        userTasks = mock(UserTasks.class);
        application = mock(Application.class);

        when(application.get(UserTasks.class)).thenReturn(userTasks);
        when(userTasks.instances()).thenReturn(instances);

        service = new UserTaskServiceImpl(application);
        identity = mock(IdentityProvider.class);
    }

    @Test
    public void testListTasksWithEmptyResult() {
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.emptyList());

        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).isEmpty();
        verify(instances).findByIdentity(eq(identity), eq(filter));
    }

    @Test
    public void testListTasksWithSingleTask() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        UserTaskView info = result.get(0);
        assertThat(info.getId()).isEqualTo("task1");
        assertThat(info.getTaskName()).isEqualTo("hr_interview");
        assertThat(info.getActualOwner()).isEqualTo("recruiter");
        assertThat(info.getProcessInfo().getProcessId()).isEqualTo("hiring");
        assertThat(info.getProcessInfo().getProcessInstanceId()).isEqualTo("pi1");
        assertThat(info.getStatus().getName()).isEqualTo("Reserved");
    }

    @Test
    public void testListTasksWithMultipleTasks() {
        DefaultUserTaskInstance task1 = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        DefaultUserTaskInstance task2 = createTask("task2", "it_interview", "developer", "hiring", "pi2", "InProgress");
        when(instances.findByIdentity(any(), any())).thenReturn(Arrays.asList(task1, task2));

        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(2);

        UserTaskView info1 = result.get(0);
        assertThat(info1.getId()).isEqualTo("task1");
        assertThat(info1.getTaskName()).isEqualTo("hr_interview");
        assertThat(info1.getStatus().getName()).isEqualTo("Reserved");

        UserTaskView info2 = result.get(1);
        assertThat(info2.getId()).isEqualTo("task2");
        assertThat(info2.getTaskName()).isEqualTo("it_interview");
        assertThat(info2.getStatus().getName()).isEqualTo("InProgress");
    }

    @Test
    public void testListTasksWithProcessIdFilter() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("hiring")
                .build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProcessInfo().getProcessId()).isEqualTo("hiring");
    }

    @Test
    public void testListTasksWithTaskNameFilter() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder()
                .taskName("hr_interview")
                .build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTaskName()).isEqualTo("hr_interview");
    }

    @Test
    public void testListTasksWithStatusFilter() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder()
                .statuses(List.of("Reserved"))
                .build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus().getName()).isEqualTo("Reserved");
    }

    @Test
    public void testListTasksWithAllFilters() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("hiring")
                .processInstanceId("pi1")
                .statuses(List.of("Reserved"))
                .taskName("hr_interview")
                .build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        UserTaskView info = result.get(0);
        assertThat(info.getProcessInfo().getProcessId()).isEqualTo("hiring");
        assertThat(info.getProcessInfo().getProcessInstanceId()).isEqualTo("pi1");
        assertThat(info.getStatus().getName()).isEqualTo("Reserved");
        assertThat(info.getTaskName()).isEqualTo("hr_interview");
        assertThat(info.getActualOwner()).isEqualTo("recruiter");
    }

    @Test
    public void testListTasksConvertsUserTaskIdCorrectly() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        task.setUserTaskId("ut-123");
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserTaskId()).isEqualTo("ut-123");
    }

    @Test
    public void testListTasksConvertsTaskDescriptionCorrectly() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        task.setTaskDescription("Interview with HR department");
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTaskDescription()).isEqualTo("Interview with HR department");
    }

    @Test
    public void testListTasksConvertsTaskPriorityCorrectly() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        task.setTaskPriority("High");
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTaskPriority()).isEqualTo("High");
    }

    @Test
    public void testListTasksConvertsProcessVersionCorrectly() {
        DefaultUserTaskInstance task = createTask("task1", "hr_interview", "recruiter", "hiring", "pi1", "Reserved");
        task.setProcessInfo(org.kie.kogito.usertask.model.ProcessInfo.builder()
                .withProcessId("hiring")
                .withProcessInstanceId("pi1")
                .withProcessVersion("1.0")
                .build());
        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProcessInfo().getProcessVersion()).isEqualTo("1.0");
    }

    @Test
    public void testListTasksHandlesNullValues() {
        DefaultUserTaskInstance task = new DefaultUserTaskInstance();
        task.setId("task1");
        task.setTaskName("task");
        task.setProcessInfo(org.kie.kogito.usertask.model.ProcessInfo.builder()
                .withProcessId("process")
                .withProcessInstanceId("instance")
                .build());
        task.setStatus(UserTaskState.of("Ready"));
        // Leave other fields null

        when(instances.findByIdentity(any(), any())).thenReturn(Collections.singletonList(task));

        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskView> result = service.listTasks(identity, filter);

        assertThat(result).hasSize(1);
        UserTaskView info = result.get(0);
        assertThat(info.getId()).isEqualTo("task1");
        assertThat(info.getUserTaskId()).isNull();
        assertThat(info.getTaskDescription()).isNull();
        assertThat(info.getTaskPriority()).isNull();
        assertThat(info.getActualOwner()).isNull();
        assertThat(info.getProcessInfo().getProcessVersion()).isNull();
    }

    private DefaultUserTaskInstance createTask(String id, String taskName, String owner,
            String processId, String processInstanceId, String status) {
        DefaultUserTaskInstance task = new DefaultUserTaskInstance();
        task.setId(id);
        task.setTaskName(taskName);
        task.setActualOwner(owner);
        task.setProcessInfo(org.kie.kogito.usertask.model.ProcessInfo.builder()
                .withProcessId(processId)
                .withProcessInstanceId(processInstanceId)
                .build());
        task.setStatus(UserTaskState.of(status));
        return task;
    }
}
