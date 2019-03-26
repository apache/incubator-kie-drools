/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.remote.ejb.test.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.junit.Test;

import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.model.InternalTask;

public class EGetTaskTest extends RemoteEjbTest {

    @Test
    public void testGetTaskInstanceInfo() {
        Long pid = ejb.startProcess(ProcessDefinitions.HUMAN_TASK);

        long taskId = ejb.getTasksByProcessInstanceId(pid).get(0);

        UserTaskInstanceDesc task = ejb.getTaskById(taskId);
        System.out.println(task.getActualOwner() + "," + task.getTaskId() + "," + task.getStatus());
        Assertions.assertThat(userId).isEqualTo(task.getActualOwner());
        Assertions.assertThat(Status.Reserved.name()).isEqualTo(task.getStatus());
    }

    @Test
    public void testTaskQuery() {
        Long pid = ejb.startProcess(ProcessDefinitions.HUMAN_TASK);

        List<TaskSummary> taskList = ejb.getTasksOwned(userId, new QueryFilter(0, 5));
        Assertions.assertThat(taskList.size()).isLessThanOrEqualTo(5);

        TaskSummary task = null;
        for (TaskSummary potentialTask : taskList) {
            System.out.println("id=" + potentialTask.getProcessInstanceId());
            if (potentialTask.getProcessInstanceId() == pid) {
                task = potentialTask;
            }
        }
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getName()).isEqualTo("Hello");
        Assertions.assertThat(task.getStatus()).isEqualTo(Status.Reserved);
        Assertions.assertThat(task.getActualOwnerId()).isEqualTo(userId);
    }

    @Test()
    public void testTaskQueryWithPageSize() {
        startProcess(ProcessDefinitions.HUMAN_TASK, 5);

        List<TaskSummary> ts = ejb.getTasksAssignedAsPotentialOwner(userId, new QueryFilter(0, 2));
        Assertions.assertThat(ts.size()).isEqualTo(2);
    }

    @Test
    public void testTaskQueryWithStatusInProgress() {
        Long pid = ejb.startProcess(ProcessDefinitions.HUMAN_TASK);

        long taskId = ejb.getTasksByProcessInstanceId(pid).get(0);
        ejb.start(taskId, userId);

        List<Status> statusList = new ArrayList<>();
        statusList.add(Status.InProgress);

        List<TaskSummary> summaryList = ejb.getTasksByStatusByProcessInstanceId(pid, statusList);
        Assertions.assertThat(summaryList.size()).isEqualTo(1);
    }

    @Test
    public void testFormName() {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", "johndoe");

        Long pid = ejb.startProcess(ProcessDefinitions.HUMAN_TASK_WITH_FORM, params);

        List<Status> statusList = new ArrayList<>();
        statusList.add(Status.Reserved);

        List<TaskSummary> summaryList = ejb.getTasksByStatusByProcessInstanceId(pid, statusList, new QueryFilter(0, 1));
        Assertions.assertThat(summaryList).isNotNull().isNotEmpty();

        TaskSummary summary = summaryList.get(0);
        Task task = ejb.getTask(summary.getId());
        String formName = ((InternalTask) task).getFormName();
        Assertions.assertThat(formName).as("Expected different form name.").isEqualTo("UserNameInputTask");
    }

}
