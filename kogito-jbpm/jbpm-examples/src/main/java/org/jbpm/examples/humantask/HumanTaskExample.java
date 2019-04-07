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

package org.jbpm.examples.humantask;

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

public class HumanTaskExample {

    public static final void main(String[] args) {
        try {
            RuntimeManager manager = getRuntimeManager("humantask/HumanTask.bpmn");        
            RuntimeEngine runtime = manager.getRuntimeEngine(null);
            KieSession ksession = runtime.getKieSession();

            // start a new process instance
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("userId", "krisv");
            params.put("description", "Need a new laptop computer");
            ksession.startProcess("com.sample.humantask", params);

            // "sales-rep" reviews request
            TaskService taskService = runtime.getTaskService();
    		TaskSummary task1 = taskService.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK").get(0);
            System.out.println("Sales-rep executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");
            taskService.claim(task1.getId(), "sales-rep");
            taskService.start(task1.getId(), "sales-rep");
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("comment", "Agreed, existing laptop needs replacing");
            results.put("outcome", "Accept");
            taskService.complete(task1.getId(), "sales-rep", results);

            // "krisv" approves result
            TaskSummary task2 = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK").get(0);
            System.out.println("krisv executing task " + task2.getName() + "(" + task2.getId() + ": " + task2.getDescription() + ")");
            taskService.start(task2.getId(), "krisv");
            results = new HashMap<String, Object>();
            results.put("outcome", "Agree");
            taskService.complete(task2.getId(), "krisv", results);

            // "john" as manager reviews request
            TaskSummary task3 = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK").get(0);
            System.out.println("john executing task " + task3.getName() + "(" + task3.getId() + ": " + task3.getDescription() + ")");
            taskService.claim(task3.getId(), "john");
            taskService.start(task3.getId(), "john");
            results = new HashMap<String, Object>();
            results.put("outcome", "Agree");
            taskService.complete(task3.getId(), "john", results);

            // "sales-rep" gets notification
            TaskSummary task4 = taskService.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK").get(0);
            System.out.println("sales-rep executing task " + task4.getName() + "(" + task4.getId() + ": " + task4.getDescription() + ")");
            taskService.start(task4.getId(), "sales-rep");
            Map<String, Object> content = taskService.getTaskContent(task4.getId());
            for (Map.Entry<?, ?> entry : content.entrySet()) {
                System.out.println(entry.getKey() + " = " + entry.getValue());
            }
            taskService.complete(task4.getId(), "sales-rep", null);

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
    			public List<String> getGroupsForUser(String userId) {
    				List<String> result = new ArrayList<String>();
    				if ("sales-rep".equals(userId)) {
    					result.add("sales");
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
