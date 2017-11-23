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

package org.jbpm.test;

import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;

import static org.junit.Assert.*;

public class JbpmJUnitBaseTestCaseTest extends JbpmJUnitBaseTestCase {

    public JbpmJUnitBaseTestCaseTest() {
        // This test aims general usages --- persistence
        super(true, true);
    }
    

    @Test
    public void testAssertNodeActive() throws Exception {
        // JBPM-4846
        RuntimeManager manager = createRuntimeManager("humantask.bpmn");
        RuntimeEngine engine = getRuntimeEngine(EmptyContext.get());
        KieSession ksession = engine.getKieSession();
        TaskService taskService = engine.getTaskService();
        
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        long processInstanceId = processInstance.getId();
        
        assertProcessInstanceActive(processInstanceId);
        assertNodeTriggered(processInstanceId, "Start", "Task 1");
        assertNodeActive(processInstanceId, ksession, "Task 1");
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        
        taskService.start(taskSummary.getId(), "john");
        taskService.complete(taskSummary.getId(), "john", null);
        
        assertNodeTriggered(processInstanceId, "Start", "Task 1", "Task 2");
        assertNodeActive(processInstanceId, ksession, "Task 2");
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        assertEquals(1, tasks.size());
        taskSummary = tasks.get(0);
        
        taskService.start(taskSummary.getId(), "mary");
        taskService.complete(taskSummary.getId(), "mary", null);
        
        assertProcessInstanceCompleted(processInstanceId);
    }
}
