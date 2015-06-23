/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.test.tasks.identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

/**
 * This JUnit test is testing JBossUserGroupCallbackImpl with "," included in a Group ID of User Task
 */
public class JBossUserGroupCallbackTest extends JbpmJUnitBaseTestCase {

	public JBossUserGroupCallbackTest() {
		super(true, true);
	}
	
	@BeforeClass
	public static void setupOnce() {
		System.setProperty("org.jbpm.ht.user.separator", "#");
	}
	
	@AfterClass
	public static void cleanupOnce() {
		System.clearProperty("org.jbpm.ht.user.separator");
	}
	
	@Before
	public void configure() {
        Properties properties = new Properties();
        properties.setProperty("krisv", "krisvgg");
        properties.setProperty("mary", "maryg,g");
        properties.setProperty("john", "johngg");
        
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
	}
    
    @Test
    public void testProcess() throws Exception {

        RuntimeManager manager = createRuntimeManager(Strategy.PROCESS_INSTANCE, "default", "CustomSeparatorGroupIdUserTaskTest.bpmn");
        RuntimeEngine runtime = getRuntimeEngine();
        KieSession ksession = runtime.getKieSession();
                                                        
        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance pi = ksession.startProcess("com.sample.bpmn.hello", params);
        System.out.println("A process instance started : pid = " + pi.getId());

        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, pi.getState());
        
        TaskService taskService = runtime.getTaskService();

        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        System.out.println("Listing if the there are any tasks for john to complete: list= " + list);
        Assert.assertEquals(1, list.size());
        
        for (TaskSummary taskSummary : list) {
            System.out.println("john starts a task : taskId = " + taskSummary.getId());
            taskService.start(taskSummary.getId(), "john");
            System.out.println("john started the task : taskId = " + taskSummary.getId() + ", which had assigned to Group/Owner: " + taskService.getTaskById(taskSummary.getId()).getPeopleAssignments().getPotentialOwners());
            taskService.complete(taskSummary.getId(), "john", null);
            System.out.println("john completed the task .");
        }
    
        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        Assert.assertEquals(1, taskList.size());
        for (TaskSummary taskSummary : taskList) {
            System.out.println("mary starts a task : taskId = " + taskSummary.getId() + ", which had assigned to Group/Owner: " + taskService.getTaskById(taskSummary.getId()).getPeopleAssignments().getPotentialOwners());
            taskService.start(taskSummary.getId(), "mary");
            System.out.println("mary started the task : taskId = " + taskSummary.getId());
            taskService.complete(taskSummary.getId(), "mary", null);
            System.out.println("mary completed the task .");
        }
        
		assertProcessInstanceCompleted(pi.getId());
		System.out.println("Process Instance with id: '" + pi.getId() + "' , got completed successfully.");
		
        manager.disposeRuntimeEngine(runtime);


    }


}