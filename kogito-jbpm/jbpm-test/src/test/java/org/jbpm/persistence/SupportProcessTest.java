/*
 * Copyright 2013 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.test.JbpmJUnitTestCase;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

/**
 *
 * @author salaboy
 */
public class SupportProcessTest extends JbpmJUnitTestCase{
    
    public SupportProcessTest() {
        super(true);
        setPersistence(true);
    }
  
    @Test
    public void simpleSupportProcessTest() {
        StatefulKnowledgeSession ksession = createKnowledgeSession("support.bpmn");
        TaskServiceEntryPoint taskService = getTaskService(ksession);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", "salaboy");
        ProcessInstance processInstance = ksession.startProcess("support.process", params);
        
        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertProcessVarExists(processInstance, "customer");
        // Configure Release
        List<TaskSummary> tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertNodeTriggered(processInstance.getId(), "Create Support");
        
        assertEquals(1, tasksAssignedToSalaboy.size());
        assertEquals("Create Support", tasksAssignedToSalaboy.get(0).getName());


        TaskSummary createSupportTask = tasksAssignedToSalaboy.get(0);

        taskService.start(createSupportTask.getId(), "salaboy");

        Map<String, Object> taskContent = taskService.getTaskContent(createSupportTask.getId());

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

        
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        
    }
}