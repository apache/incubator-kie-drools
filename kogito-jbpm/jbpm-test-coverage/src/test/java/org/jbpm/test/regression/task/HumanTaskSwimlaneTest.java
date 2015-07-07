/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.test.regression.task;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import qa.tools.ikeeper.annotation.BZ;

public class HumanTaskSwimlaneTest extends JbpmTestCase {

    private static final String SWIMLANE_SAME_GROUPS =
            "org/jbpm/test/regression/task/HumanTaskSwimlane-sameGroups.bpmn2";
    private static final String SWIMLANE_SAME_GROUPS_ID =
            "org.jbpm.test.regression.task.HumanTaskSwimlane-sameGroups";

    private static final String SWIMLANE_DIFFERENT_GROUPS =
            "org/jbpm/test/regression/task/HumanTaskSwimlane-differentGroups.bpmn2";
    private static final String SWIMLANE_DIFFERENT_GROUPS_ID =
            "org.jbpm.test.regression.task.HumanTaskSwimlane-differentGroups";

    private TaskService taskService;

    @Test
    @BZ("997139")
    public void testSameGroups() {
        createRuntimeManager(SWIMLANE_SAME_GROUPS);
        KieSession ksession = getRuntimeEngine().getKieSession();
        taskService = getRuntimeEngine().getTaskService();

        ProcessInstance pi = ksession.startProcess(SWIMLANE_SAME_GROUPS_ID);

        long task1 = getActiveTask(pi).getId();
        taskService.claim(task1, "john");
        taskService.start(task1, "john");
        taskService.complete(task1, "john", null);

        Task task2 = getActiveTask(pi);
        Assertions.assertThat(task2.getTaskData().getStatus()).isEqualTo(Status.Reserved);
        Assertions.assertThat(task2.getTaskData().getActualOwner().getId()).isEqualTo("john");
        Assertions.assertThat(task2.getPeopleAssignments().getPotentialOwners().get(0).getId()).isEqualTo("users");
    }

    @Test
    @BZ("997139")
    public void testDifferentGroups() {
        createRuntimeManager(SWIMLANE_DIFFERENT_GROUPS);
        KieSession ksession = getRuntimeEngine().getKieSession();
        taskService = getRuntimeEngine().getTaskService();

        ProcessInstance pi = ksession.startProcess(SWIMLANE_DIFFERENT_GROUPS_ID);

        long task1 = getActiveTask(pi).getId();
        taskService.claim(task1, "john");
        taskService.start(task1, "john");
        taskService.complete(task1, "john", null);

        Task task2 = getActiveTask(pi);
        Assertions.assertThat(task2.getTaskData().getStatus()).isEqualTo(Status.Ready);
        Assertions.assertThat(task2.getTaskData().getActualOwner()).isNull();
        Assertions.assertThat(task2.getPeopleAssignments().getPotentialOwners().get(0).getId()).isEqualTo("sales");
    }

    private Task getActiveTask(ProcessInstance pi) {
        List<Long> taskIds = taskService.getTasksByProcessInstanceId(pi.getId());
        for (Long taskId : taskIds) {
            Task task = taskService.getTaskById(taskId);
            if (!task.getTaskData().getStatus().equals(Status.Completed)) {
                return task;
            }
        }
        return null;
    }

}
