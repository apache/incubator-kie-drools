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

package org.jbpm.test.regression.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.InternalTaskService;
import qa.tools.ikeeper.annotation.BZ;

public class HumanTaskAssignmentTest extends JbpmTestCase {

    private static final String GET_TASKS_OWNER_GROUP =
            "org/jbpm/test/regression/task/HumanTaskAssignment-getTasksOwnerGroup.bpmn2";
    private static final String GET_TASKS_OWNER_GROUP_ID =
            "org.jbpm.test.regression.task.HumanTaskAssignment-getTasksOwnerGroup";

    private static final String GET_TASKS_OWNER_USER =
            "org/jbpm/test/regression/task/HumanTaskAssignment-getTasksOwnerUser.bpmn2";
    private static final String GET_TASKS_OWNER_USER_ID =
            "org.jbpm.test.regression.task.HumanTaskAssignment-getTasksOwnerUser";

    @Test
    @BZ("1103977")
    public void testGetTasksAssignedAsPotentialOwnerGroup() {
        createRuntimeManager(GET_TASKS_OWNER_GROUP);
        KieSession ksession = getRuntimeEngine().getKieSession();
        InternalTaskService taskService = (InternalTaskService) getRuntimeEngine().getTaskService();

        long pid = ksession.startProcess(GET_TASKS_OWNER_GROUP_ID).getId();

        // Task is assigned to group HR.
        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner(null, Arrays.asList("HR"));

        Assertions.assertThat(taskList).hasSize(1);
        Assertions.assertThat(taskList.get(0).getStatus()).isEqualTo(Status.Ready);

        Long taskId = taskList.get(0).getId();
        taskService.claim(taskId, "mary");
        taskService.start(taskId, "mary");
        taskService.complete(taskId, "mary", null);

        Task task = taskService.getTaskById(taskId);
        Assertions.assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Completed);

        // Next task is assigned to group PM.
        // So task list for HR will be empty.
        taskList = taskService.getTasksAssignedAsPotentialOwner(null, Arrays.asList("HR"));
        Assertions.assertThat(taskList).hasSize(0);

        taskList = taskService.getTasksAssignedAsPotentialOwner(null, Arrays.asList("PM"));
        Assertions.assertThat(taskList).hasSize(1);
        Assertions.assertThat(taskList.get(0).getStatus()).isEqualTo(Status.Ready);

        ksession.abortProcessInstance(pid);
    }

    @Test
    @BZ("1178153")
    public void testGetTasksAssignedAsPotentialOwnerUser() {
        createRuntimeManager(GET_TASKS_OWNER_USER);
        KieSession ksession = getRuntimeEngine().getKieSession();
        InternalTaskService taskService = (InternalTaskService) getRuntimeEngine().getTaskService();

        long pid = ksession.startProcess(GET_TASKS_OWNER_USER_ID).getId();

        // Task is potentially assigned to several users - mary,john,ibek.
        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("jack", new ArrayList<String>());
        Assertions.assertThat(taskList).hasSize(0);

        taskList = taskService.getTasksAssignedAsPotentialOwner("mary", new ArrayList<String>());
        Assertions.assertThat(taskList).hasSize(1);
        Assertions.assertThat(taskList.get(0).getStatus()).isEqualTo(Status.Ready);

        ksession.abortProcessInstance(pid);
    }

}
