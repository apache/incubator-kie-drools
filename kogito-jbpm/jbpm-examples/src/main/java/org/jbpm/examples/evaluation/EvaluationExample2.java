/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.examples.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JBPMHelper;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;

/**
 * This is a sample file to launch a process.
 */
public class EvaluationExample2 {

    public static final void main(String[] args) {
        try {
            RuntimeManager manager = getRuntimeManager("evaluation/Evaluation2.bpmn");        
            RuntimeEngine runtime = manager.getRuntimeEngine(null);
            KieSession ksession = runtime.getKieSession();

            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("employee", "krisv");
            params.put("reason", "Yearly performance evaluation");
            ksession.startProcess("com.sample.evaluation", params);

            // "krisv" executes his own performance evaluation
            TaskService taskService = runtime.getTaskService();
            TaskSummary task1 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK").get(0);
            System.out.println("Krisv executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");
            taskService.start(task1.getId(), "krisv");
            taskService.complete(task1.getId(), "krisv", null);

            // "john", part of the "PM" group, executes a performance evaluation
            TaskSummary task2 = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK").get(0);
            System.out.println("John executing task " + task2.getName() + "(" + task2.getId() + ": " + task2.getDescription() + ")");
            System.out.println(taskService.getTasksAssignedAsPotentialOwner("john", "en-UK").size());
            taskService.claim(task2.getId(), "john");
            taskService.start(task2.getId(), "john");
            taskService.complete(task2.getId(), "john", null);
            
            // "mary", part of the "HR" group, delegates a performance evaluation
            TaskSummary task3 = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK").get(0);
            System.out.println("Mary delegating task " + task3.getName() + "(" + task3.getId() + ": " + task3.getDescription() + ") to krisv");
            taskService.claim(task3.getId(), "mary");
            taskService.delegate(task3.getId(), "mary", "krisv");

            // "administrator" delegates the task back to mary
            System.out.println("Administrator delegating task back to mary");
            taskService.delegate(task3.getId(), "Administrator", "mary");

            // mary executing the task
            TaskSummary task3b = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK").get(0);
            System.out.println("Mary executing task " + task3b.getName() + "(" + task3b.getId() + ": " + task3b.getDescription() + ")");
            taskService.start(task3b.getId(), "mary");
            taskService.complete(task3b.getId(), "mary", null);

            System.out.println("Process instance completed");
    		
    		manager.disposeRuntimeEngine(runtime);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.exit(0);
    }

    private static RuntimeManager getRuntimeManager(String process) {
        // load up the knowledge base
    	JBPMHelper.startH2Server();
    	JBPMHelper.setupDataSource();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
            .userGroupCallback(new UserGroupCallback() {
    			public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
    				List<String> result = new ArrayList<String>();
    				if ("mary".equals(userId)) {
    					result.add("HR");
    				} else if ("john".equals(userId)) {
    					result.add("PM");
    				}
    				return result;
    			}
    			public boolean existsUser(String arg0) {
    				return true;
    			}
    			public boolean existsGroup(String arg0) {
    				return true;
    			}
    		})
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource(process), ResourceType.BPMN2)
            .get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
    }
    
}
