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

package org.jbpm.test.functional.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.InternalTaskService;
import qa.tools.ikeeper.annotation.BZ;

public class HumanTaskQueryFilterTest extends JbpmTestCase {

    private static final String CONF_HUMAN_TASK =
            "org/jbpm/test/functional/task/HumanTaskQueryFilter-configurableHumanTask.bpmn2";
    private static final String CONF_HUMAN_TASK_ID =
            "org.jbpm.test.functional.task.HumanTaskQueryFilter-configurableHumanTask";

    private KieSession kieSession;
    private InternalTaskService taskService;
    private List<ProcessInstance> instanceList;

    public HumanTaskQueryFilterTest() {
        super(true, true);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        createRuntimeManager(CONF_HUMAN_TASK);
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        kieSession = runtimeEngine.getKieSession();
        taskService = (InternalTaskService) runtimeEngine.getTaskService();
        instanceList = new ArrayList<ProcessInstance>();
    }

    @Test
    public void testFirstResult() {
        startHumanTaskProcess(6, "john's task", "john");

        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", null, null,
                new QueryFilter(2, 2, "t.name", true));
        logger.debug("### Potential owner task list: " + taskList);
        Assertions.assertThat(taskList).hasSize(2);
        for (int i = 0; i < taskList.size(); i++) {
            Assertions.assertThat(taskList.get(i).getName()).isEqualTo("john's task " + (i + 1 + 2));
        }

        abortHumanTaskProcess(6);
    }

    @Test
    public void testMaxResults() {
        startHumanTaskProcess(4, "john's task", "john");

        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", null, null,
                new QueryFilter(0, 2, "t.id", true));
        Assertions.assertThat(taskList).hasSize(2);
        logger.debug("### Potential owner task list: " + taskList);

        taskList = taskService.getTasksOwned("john", null, new QueryFilter(0, 1, null, "en-UK", null));
        Assertions.assertThat(taskList).hasSize(1);
        logger.debug("### Owned task list: " + taskList);

        abortHumanTaskProcess(4);
    }

    @Test
    public void testDescendingOrder() {
        startHumanTaskProcess(3, "john's task", "john");

        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", null, null,
                new QueryFilter(0, 0, "t.name", false));
        logger.debug("### Potential owner task list: " + taskList);
        Assertions.assertThat(taskList).hasSize(3);

        for (int i = 0; i < taskList.size(); i++) {
            logger.debug("### Task Name: " + taskList.get(i).getName());
            Assertions.assertThat(taskList.get(i).getName()).isEqualTo("john's task " + (3 - i));
        }

        abortHumanTaskProcess(3);
    }

    @Test
    public void testAscendingOrder() {
        startHumanTaskProcess(3, "john's task", "john");

        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", null, null,
                new QueryFilter(0, 0, "t.name", true));
        logger.debug("### Potential owner task list: " + taskList);
        Assertions.assertThat(taskList).hasSize(3);

        for (int i = 0; i < taskList.size(); i++) {
            logger.debug("### Task Name: " + taskList.get(i).getName());
            Assertions.assertThat(taskList.get(i).getName()).isEqualTo("john's task " + (i + 1));
        }

        abortHumanTaskProcess(3);
    }

    @Test
    @BZ("1132145")
    public void testSingleResult() {
        startHumanTaskProcess(4, "john's task", "john");

        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", null, null,
                new QueryFilter(0, 0));
        logger.debug("### Potential owner task list: " + taskList);
        Assertions.assertThat(taskList).hasSize(1);

        abortHumanTaskProcess(4);
    }

    /**
     * TODO - Need to pass in parameters which do make sense.
     */
    @Test
    @BZ("1132157")
    public void testFilterParams() {
        startHumanTaskProcess(10, "john's task", "john");

        QueryFilter queryFilter = new QueryFilter("x=1,y=2", null, "t.name", true);

        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", null, null, queryFilter);
        logger.debug("### Potential owner task list: " + taskList);
        Assertions.assertThat(taskList).hasSize(1);

        abortHumanTaskProcess(10);
    }

    private void startHumanTaskProcess(int instanceCount, String taskName, String assigneeName) {
        startHumanTaskProcess(instanceCount, taskName, assigneeName, "en-UK");
    }

    private void startHumanTaskProcess(int instanceCount, String taskName, String assigneeName, String localeName) {
        for (int i = 0; i < instanceCount; i++) {
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("assigneeName", assigneeName);
            parameters.put("taskName", taskName + " " + (i + 1));
            parameters.put("localeName", localeName);
            instanceList.add(kieSession.startProcess(CONF_HUMAN_TASK_ID, parameters));
        }
    }

    private void abortHumanTaskProcess(int instanceCount) {
        for (int i = 0; i < instanceCount; i++) {
            kieSession.abortProcessInstance(instanceList.get(i).getId());
        }
        instanceList = instanceList.subList(instanceCount, instanceList.size());
    }

}