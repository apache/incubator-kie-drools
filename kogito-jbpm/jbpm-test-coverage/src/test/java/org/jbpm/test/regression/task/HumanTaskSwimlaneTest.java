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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.workflow.instance.node.HumanTaskNodeInstance;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

import qa.tools.ikeeper.annotation.BZ;

import static org.junit.Assert.*;

public class HumanTaskSwimlaneTest extends JbpmTestCase {

    private static final String SWIMLANE_SAME_GROUPS =
            "org/jbpm/test/regression/task/HumanTaskSwimlane-sameGroups.bpmn2";
    private static final String SWIMLANE_SAME_GROUPS_ID =
            "org.jbpm.test.regression.task.HumanTaskSwimlane-sameGroups";

    private static final String SWIMLANE_DIFFERENT_GROUPS =
            "org/jbpm/test/regression/task/HumanTaskSwimlane-differentGroups.bpmn2";
    private static final String SWIMLANE_DIFFERENT_GROUPS_ID =
            "org.jbpm.test.regression.task.HumanTaskSwimlane-differentGroups";
    
    private static final String SWIMLANE_MULTIPLE_ACTORS =
            "org/jbpm/test/regression/task/HumanTaskSwimlane-multipleActors.bpmn2";
    private static final String SWIMLANE_MULTIPLE_ACTORS_ID =
            "org.jbpm.test.regression.task.HumanTaskSwimlane-multipleActors";

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
    
    @Test
    public void testSwimlaneWithMultipleActorsAssigned() {
        createRuntimeManager(SWIMLANE_MULTIPLE_ACTORS);
        
        String user = "john";
        RuntimeEngine runtime = getRuntimeEngine();
        KieSession kSession = runtime.getKieSession();
        TaskService taskservice = runtime.getTaskService();
        
        kSession.addEventListener(new DefaultProcessEventListener(){

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                if (event.getNodeInstance().getNodeName().equals("TASK")) {
                    Object swimlaneActorId = ((HumanTaskNodeInstance) event.getNodeInstance()).getWorkItem().getParameter("SwimlaneActorId");
                    assertNull(swimlaneActorId);
                }
            }
            
        });

        Map<String, Object> map = new HashMap<String, Object>();
        
        ProcessInstance instance = kSession.startProcess(SWIMLANE_MULTIPLE_ACTORS_ID, map);
        
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Ready);
        statuses.add(Status.Reserved);
        statuses.add(Status.InProgress);

        List<TaskSummary> tasks = taskservice.getTasksByStatusByProcessInstanceId(instance.getId(), statuses, "en_US");        
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        TaskSummary task = tasks.get(0);
        assertEquals(Status.Ready, task.getStatus());
        
        
        taskservice.claim(task.getId(), user);
        taskservice.start(task.getId(), user);
        
        tasks = taskservice.getTasksByStatusByProcessInstanceId(instance.getId(), statuses, "en_US");        
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        task = tasks.get(0);
        assertEquals(Status.InProgress, task.getStatus());
                
        taskservice.complete(task.getId(), user, map);
        assertProcessInstanceCompleted(instance.getId());
    }


}
