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
package org.jbpm.test.functional.jpa;

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
import org.kie.internal.task.api.InternalTaskService;

import static org.junit.Assert.*;


public class SupportProcessTest extends JbpmTestCase{
    
    public SupportProcessTest() {
        super(true, true);
    }
  
    @Test
    public void simpleSupportProcessTest() {
        createRuntimeManager("org/jbpm/test/functional/jpa/support.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();
  
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", "salaboy");
        ProcessInstance processInstance = ksession.startProcess("support.process", params);
        
        assertProcessInstanceActive(processInstance.getId());
        assertProcessVarExists(processInstance, "customer");
        // Configure Release
        List<TaskSummary> tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertNodeTriggered(processInstance.getId(), "Create Support");
        
        assertEquals(1, tasksAssignedToSalaboy.size());
        assertEquals("Create Support", tasksAssignedToSalaboy.get(0).getName());


        TaskSummary createSupportTask = tasksAssignedToSalaboy.get(0);

        taskService.start(createSupportTask.getId(), "salaboy");

        Map<String, Object> taskContent = ((InternalTaskService) taskService).getTaskContent(createSupportTask.getId());

        assertEquals("salaboy", taskContent.get("input_customer"));
        
        Map<String, Object> output = new HashMap<String, Object>();

        output.put("output_customer", "salaboy/redhat");
        taskService.complete(createSupportTask.getId(), "salaboy", output);
        
        assertNodeTriggered(processInstance.getId(), "Resolve Support");
        
        tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(1, tasksAssignedToSalaboy.size());

        assertEquals("Resolve Support", tasksAssignedToSalaboy.get(0).getName());

        TaskSummary resolveSupportTask = tasksAssignedToSalaboy.get(0);

        taskService.start(resolveSupportTask.getId(), "salaboy");

        taskService.complete(resolveSupportTask.getId(), "salaboy", null);

        assertNodeTriggered(processInstance.getId(), "Notify Customer");
       
        tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(1, tasksAssignedToSalaboy.size());

        assertEquals("Notify Customer", tasksAssignedToSalaboy.get(0).getName());

        TaskSummary notifySupportTask = tasksAssignedToSalaboy.get(0);

        taskService.start(notifySupportTask.getId(), "salaboy");
        output = new HashMap<String, Object>();
        output.put("output_solution", "solved today");
        taskService.complete(notifySupportTask.getId(), "salaboy", output);

        
        assertProcessInstanceCompleted(processInstance.getId());
        
    }
}
