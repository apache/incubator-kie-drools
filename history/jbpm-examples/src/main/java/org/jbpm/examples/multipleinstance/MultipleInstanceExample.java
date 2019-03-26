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

package org.jbpm.examples.multipleinstance;

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
import org.kie.api.task.model.TaskSummary;

public class MultipleInstanceExample {
	
	public static final void main(String[] args) {
		try {
            RuntimeManager manager = getRuntimeManager("multipleinstance/multipleinstance.bpmn");        
            RuntimeEngine runtime = manager.getRuntimeEngine(null);
            KieSession ksession = runtime.getKieSession();

			// start a new process instance
			Map<String, Object> params = new HashMap<String, Object>();
			List<String> list = new ArrayList<String>();
			list.add("krisv");
			list.add("john doe");
			list.add("superman");
			params.put("list", list);
			ksession.startProcess("com.sample.multipleinstance", params);

            TaskService taskService = runtime.getTaskService();
    		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK");
    		for (TaskSummary task: tasks) {
	            System.out.println("Sales-rep executing task " + task.getName() + "(" + task.getId() + ": " + task.getDescription() + ")");
	            taskService.start(task.getId(), "sales-rep");
	            taskService.complete(task.getId(), "sales-rep", null);
    		}
            
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
            .addAsset(KieServices.Factory.get().getResources().newClassPathResource(process), ResourceType.BPMN2)
            .get();
        return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
    }
    
}
