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

import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;

public class ETaskQueryFilterTest extends RemoteEjbTest {

    private List<ProcessInstance> instanceList = new ArrayList<>();

    @Test
    public void testFirstResult() {
        startHumanTaskProcess(6, "john's task", "john");

        List<TaskSummary> taskList = ejb.getTasksAssignedAsPotentialOwner("john", null, null, new QueryFilter(2, 2, "t.name", true));
        logger.info("### Potential owner task list: " + taskList);
        Assertions.assertThat(taskList).hasSize(2);
        for (int i = 0; i < taskList.size(); i++) {
            Assertions.assertThat(taskList.get(i).getName()).isEqualTo("john's task " + (i + 1 + 2));
        }
    }

    @Test
    public void testMaxResults() {
        startHumanTaskProcess(4, "john's task", "john");

        List<TaskSummary> taskList = ejb.getTasksAssignedAsPotentialOwner("john", null, null, new QueryFilter(0, 2, "t.id", true));
        logger.info("### Potential owner task list: " + taskList);
        Assertions.assertThat(taskList).hasSize(2);

        taskList = ejb.getTasksOwned("john", new QueryFilter(0, 1, null, "en-UK", null));
        logger.info("### Owned task list: " + taskList);
        Assertions.assertThat(taskList.size()).isEqualTo(1);
    }

    @Test
    public void testDescendingOrder() {
        startHumanTaskProcess(3, "john's task", "john");

        List<TaskSummary> taskList = ejb.getTasksAssignedAsPotentialOwner("john", null, null, new QueryFilter(0, 0, "t.name", false));
        logger.info("### Potential owner task list: " + taskList);

        Assertions.assertThat(taskList).hasSize(3);
        for (int i = 0; i < taskList.size(); i++) {
            logger.info("### Task Name: " + taskList.get(i).getName());
            Assertions.assertThat(taskList.get(i).getName()).isEqualTo("john's task " + (3 - i));
        }
    }

    @Test
    public void testAscendingOrder() {
        startHumanTaskProcess(3, "john's task", "john");

        List<TaskSummary> taskList = ejb.getTasksAssignedAsPotentialOwner("john", null, null, new QueryFilter(0, 0, "t.name", true));
        logger.info("### Potential owner task list: " + taskList);

        Assertions.assertThat(taskList).hasSize(3);
        for (int i = 0; i < taskList.size(); i++) {
            logger.info("### Task Name: " + taskList.get(i).getName());
            Assertions.assertThat(taskList.get(i).getName()).isEqualTo("john's task " + (i + 1));
        }
    }

    private void startHumanTaskProcess(int instanceCount, String taskName, String assigneeName) {
        for (int i = 0; i < instanceCount; i++) {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("assigneeName", assigneeName);
            parameters.put("taskName", taskName + " " + (i + 1));


            ProcessInstance processInstance = ejb.startAndGetProcess("designer.human-task", parameters);
            instanceList.add(processInstance);
        }
    }
}
