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
package org.jbpm.test.functional.gateway;

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


public class InclusiveGatewayWithHumanTasksProcessTest extends JbpmTestCase{
    
    public InclusiveGatewayWithHumanTasksProcessTest() {
        super(true, true);
    }
  
    @Test
    public void testInclusiveGatewayWithLoopAndUserTasks() {
        createRuntimeManager("org/jbpm/test/functional/gateway/InclusiveGatewayWithHumanTasksProcess.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();
  
        String userId = "john";
        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("firstXor", true);   
        params.put("secondXor", true); 
        params.put("thirdXor", true);
		ProcessInstance pi = ksession.startProcess("InclusiveWithAdvancedLoop", params);    		    	    		    		
   
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(userId, "en-UK");    		
		assertNotNull(tasks);
		assertEquals(1, tasks.size());
		//completing first task		
		taskService.start(tasks.get(0).getId(), userId);				
		taskService.complete(tasks.get(0).getId(), userId, new HashMap<String, Object>());
		
		
		//completing second task
		tasks = taskService.getTasksAssignedAsPotentialOwner(userId, "en-UK"); 
		assertNotNull(tasks);
		assertEquals(2, tasks.size());
		for(TaskSummary task : tasks) {
    		
    		if(task.getName().equals("HT Form2")) {
    			taskService.start(task.getId(), userId);
        		taskService.complete(task.getId(), userId, new HashMap<String, Object>());
        		
        		break;
    		}
		}    		    		    		
		List<TaskSummary> johntasks = taskService.getTasksAssignedAsPotentialOwner(userId, "en-UK");    		
		assertNotNull(johntasks);
		assertEquals(1, johntasks.size());
		taskService.start(johntasks.get(0).getId(), userId);				
		taskService.complete(johntasks.get(0).getId(), userId, new HashMap<String, Object>());
		
		
		johntasks = taskService.getTasksAssignedAsPotentialOwner(userId, "en-UK"); 
		assertNotNull(johntasks);
		assertEquals(1, johntasks.size());
		taskService.start(johntasks.get(0).getId(), userId);				
		taskService.complete(johntasks.get(0).getId(), userId, new HashMap<String, Object>());
		
		assertProcessInstanceCompleted(pi.getId());
        
    }
}
