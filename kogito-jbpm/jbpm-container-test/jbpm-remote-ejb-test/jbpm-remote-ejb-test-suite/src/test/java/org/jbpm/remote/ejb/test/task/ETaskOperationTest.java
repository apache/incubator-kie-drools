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

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskData;


public class ETaskOperationTest extends RemoteEjbTest {

    /**
     * This test must use another user because the task will remain in the database
     * interfering with other tests. The task will not be deleted because the work item
     * is accessed directly.
     */
    @Test()
    public void testCompleteWorkItem() {
        ProcessInstance pi = startHumanTaskProcess("root", "Root's task 1");

        Long taskId = ejb.getTasksByProcessInstanceId(pi.getId()).get(0);
        Task task = ejb.getTask(taskId);
        Assertions.assertThat(task).isNotNull();

        ejb.completeWorkItem(task.getTaskData().getWorkItemId());

        ProcessInstanceDesc log = ejb.getProcessInstanceById(pi.getId());
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    /**
     * See ETaskOperationTest.testCompleteWorkItem() for reason why another user is used.
     */
    @Test()
    public void testAbortWorkItem() {
        ProcessInstance pi = startHumanTaskProcess("root", "Root's task 2");

        Long taskId = ejb.getTasksByProcessInstanceId(pi.getId()).get(0);
        Task task = ejb.getTask(taskId);
        Assertions.assertThat(task).isNotNull();

        TaskData taskData = task.getTaskData();
        ejb.abortWorkItem(taskData.getWorkItemId());

        ProcessInstanceDesc log = ejb.getProcessInstanceById(pi.getId());
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test()
    public void testExecuteTaskOpStartAndComplete() {
        ProcessInstance pi = startHumanTaskProcess(userId, userId + "'s task 1");

        Long taskId = ejb.getTasksByProcessInstanceId(pi.getId()).get(0);

        ejb.start(taskId, userId);

        Task task = ejb.getTask(taskId);
        Assertions.assertThat(task).isNotNull();
        Assertions.assertThat(task.getTaskData().getStatus()).isEqualTo(Status.InProgress);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        ejb.complete(taskId, userId, params);

        ProcessInstanceDesc log = ejb.getProcessInstanceById(pi.getId());
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test()
    public void testReleaseAndClaimTask() {
        ProcessInstance pi = startHumanTaskProcess(userId, userId + "'s task 2");

        Long taskId = ejb.getTasksByProcessInstanceId(pi.getId()).get(0);
        checkTaskStatusAndActualOwner(taskId, Status.Reserved, userId);

        ejb.release(taskId, userId);
        checkTaskStatusAndActualOwner(taskId, Status.Ready, null);

        ejb.claim(taskId, userId);
        checkTaskStatusAndActualOwner(taskId, Status.Reserved, userId);

        // and try to finish the process
        ejb.start(taskId, userId);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        ejb.complete(taskId, userId, params);

        ProcessInstanceDesc log = ejb.getProcessInstanceById(pi.getId());
        Assertions.assertThat(log).isNotNull();
        Assertions.assertThat(log.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    private ProcessInstance startHumanTaskProcess(String assigneeName, String taskName) {
        Map<String, Object> params = new HashMap<>();
        params.put("assigneeName", assigneeName);
        params.put("taskName", taskName);

        return ejb.startAndGetProcess("designer.human-task", params);
    }

    private TaskData checkTaskStatusAndActualOwner(Long taskId, Status status, String user) {
        TaskData taskData = checkTaskStatus(taskId, status);
        if (user != null) {
            Assertions.assertThat(taskData.getActualOwner()).isNotNull();
            Assertions.assertThat(taskData.getActualOwner().getId()).isEqualTo(user);
        } else {
            Assertions.assertThat(taskData.getActualOwner()).isNull();
        }
        return taskData;
    }

    private TaskData checkTaskStatus(Long taskId, Status status) {
        Task task = ejb.getTask(taskId);
        Assertions.assertThat(task.getId()).isEqualTo(taskId);

        TaskData taskData = task.getTaskData();
        Assertions.assertThat(taskData.getStatus()).isEqualTo(status);

        return taskData;
    }

}
