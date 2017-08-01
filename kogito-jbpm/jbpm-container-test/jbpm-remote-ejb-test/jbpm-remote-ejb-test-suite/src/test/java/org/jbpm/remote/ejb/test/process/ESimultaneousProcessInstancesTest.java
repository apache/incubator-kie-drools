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

package org.jbpm.remote.ejb.test.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;

import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.junit.Test;

import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.model.Status;

public class ESimultaneousProcessInstancesTest extends RemoteEjbTest {

    @Test()
    public void testSimultaneousProcesses() {
        ProcessInstance pi1 = ejb.startAndGetProcess(ProcessDefinitions.HUMAN_TASK);
        ProcessInstance pi2 = ejb.startAndGetProcess(ProcessDefinitions.HUMAN_TASK);
        ProcessInstance pi3 = ejb.startAndGetProcess(ProcessDefinitions.HUMAN_TASK);

        // check that all the process instances are running
        Assertions.assertThat(pi1.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        Assertions.assertThat(pi2.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        Assertions.assertThat(pi3.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // start and complete the task for p2
        long taskId = ejb.getTasksByProcessInstanceId(pi2.getId()).get(0);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        ejb.start(taskId, userId);
        ejb.complete(taskId, userId, params);

        // check that p2 is completed
        ProcessInstanceDesc instanceDesc = ejb.getProcessInstanceById(pi2.getId());
        Assertions.assertThat(instanceDesc).isNotNull();
        Assertions.assertThat(instanceDesc.getState().intValue()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        // other processes shouldn't be affected
        pi1 = ejb.getProcessInstance(pi1.getId());
        Assertions.assertThat(pi1.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        pi3 = ejb.getProcessInstance(pi3.getId());
        Assertions.assertThat(pi3.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // abort p1 to test that the tasks will be removed
        ejb.abortProcessInstance(pi1.getId());
        instanceDesc = ejb.getProcessInstanceById(pi1.getId());
        Assertions.assertThat(instanceDesc).isNotNull();
        Assertions.assertThat(instanceDesc.getState().intValue()).isEqualTo(ProcessInstance.STATE_ABORTED);

        // p1 was aborted so there shouldn't be any tasks to do
        List<Long> taskIds = ejb.getTasksByProcessInstanceId(pi1.getId());
        Assertions.assertThat(taskIds).hasSize(1);
        UserTaskInstanceDesc taskDesc = ejb.getTaskById(taskIds.get(0));
        Assertions.assertThat(taskDesc.getStatus()).isEqualTo(Status.Exited.name());

        // p3 still shouldn't be affected by any of the previous operations
        pi3 = ejb.getProcessInstance(pi3.getId());
        Assertions.assertThat(pi3.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        // start and complete the task for p3
        taskId = ejb.getTasksByProcessInstanceId(pi3.getId()).get(0);
        params = new HashMap<>();
        params.put("userId", userId);
        ejb.start(taskId, userId);
        ejb.complete(taskId, userId, params);

        // check that p3 is completed
        instanceDesc = ejb.getProcessInstanceById(pi3.getId());
        Assertions.assertThat(instanceDesc).isNotNull();
        Assertions.assertThat(instanceDesc.getState().intValue()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        taskIds = ejb.getTasksByProcessInstanceId(pi3.getId());
        Assertions.assertThat(taskIds).hasSize(1);
        taskDesc = ejb.getTaskById(taskIds.get(0));
        Assertions.assertThat(taskDesc.getStatus()).isEqualTo(Status.Completed.name());
    }

}
