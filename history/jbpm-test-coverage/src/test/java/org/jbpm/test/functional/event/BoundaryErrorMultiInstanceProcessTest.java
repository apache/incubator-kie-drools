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
package org.jbpm.test.functional.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

import static org.junit.Assert.*;


public class BoundaryErrorMultiInstanceProcessTest extends JbpmTestCase{
    
    public BoundaryErrorMultiInstanceProcessTest() {
        super(true, true);
    }
  
    @Test
    public void simpleSupportProcessTest() {
        createRuntimeManager("org/jbpm/test/functional/event/BoundaryErrorMultiInstanceProcess.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();
  
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approvers", new String[]{"salaboy", "mary"});
        ProcessInstance processInstance = ksession.startProcess("boundary-catch-error-event", params);
        
        assertProcessInstanceActive(processInstance.getId());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());

        TaskSummary dataEntryTask = tasks.get(0);

        taskService.start(dataEntryTask.getId(), "john");        
        taskService.complete(dataEntryTask.getId(), "john", null);
        
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        assertEquals(1, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(1, tasks.size());

        // signal to terminate subprocess
        ksession.signalEvent("Terminate", null, processInstance.getId());
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        assertEquals(0, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(0, tasks.size());
        
        // second round ...
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        dataEntryTask = tasks.get(0);

        taskService.start(dataEntryTask.getId(), "john");        
        taskService.complete(dataEntryTask.getId(), "john", null);
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        assertEquals(1, tasks.size());
        
        TaskSummary maryTask = tasks.get(0);

        taskService.start(maryTask.getId(), "mary");        
        taskService.complete(maryTask.getId(), "mary", null);
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(1, tasks.size());
        
        TaskSummary salaboyTask = tasks.get(0);

        taskService.start(salaboyTask.getId(), "salaboy");        
        taskService.complete(salaboyTask.getId(), "salaboy", null);
        
        
        assertProcessInstanceCompleted(processInstance.getId());
        
    }
}
