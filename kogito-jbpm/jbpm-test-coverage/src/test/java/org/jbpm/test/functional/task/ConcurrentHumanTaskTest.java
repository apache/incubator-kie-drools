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

package org.jbpm.test.functional.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.core.timer.TimerServiceRegistry;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.test.JbpmTestCase;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;

public class ConcurrentHumanTaskTest extends JbpmTestCase {

	public ConcurrentHumanTaskTest() {
		super(true, true);
	}

	private static final int THREADS = 2;

	@Test
	public void testConcurrentInvocationsIncludingUserTasks() throws Exception {
		for (int i = 0; i < THREADS; i++) {
			ProcessRunner pr = new ProcessRunner(i, getEmf());
			Thread t = new Thread(pr, i + "-process-runner");
			t.start();						
		}
		
		Thread.sleep(5000);
		
		AuditLogService logService = new JPAAuditLogService(getEmf());
		
		List<? extends ProcessInstanceLog> logs = logService.findProcessInstances("com.sample.humantask.concurrent");
		assertEquals(2, logs.size());
		
		for (ProcessInstanceLog log : logs) {
			assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());
		}
		
		logService.dispose();
	}
}

class ProcessRunner implements Runnable {

	private int i;
	private EntityManagerFactory emf;

	public ProcessRunner(int i, EntityManagerFactory emf) {
		this.i = i;
		this.emf = emf;
	}

	private RuntimeManager getRuntimeManager(String process, int i) {
		Properties properties = new Properties();
		properties.setProperty("krisv", "");
		properties.setProperty("sales-rep", "sales");
		properties.setProperty("john", "PM");

		KnowledgeBuilder knowledgeBuilder = createKBuilder(process, ResourceType.BPMN2);
		KieBase kieBase = knowledgeBuilder.newKnowledgeBase();

		UserGroupCallback userGroupCallback = new JBossUserGroupCallbackImpl( properties);
		// load up the knowledge base
		TimerServiceRegistry.getInstance();
		RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
				.userGroupCallback(userGroupCallback).persistence(true)
				.entityManagerFactory(emf).knowledgeBase(kieBase).get();
		return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "id-" + i);
	}

	private KnowledgeBuilder createKBuilder(String resource, ResourceType resourceType) {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(resource), resourceType);
		if (kbuilder.hasErrors()) {
			int errors = kbuilder.getErrors().size();
			if (errors > 0) {
				System.out.println("Found " + errors + " errors");
				for (KnowledgeBuilderError error : kbuilder.getErrors()) {
					System.out.println(error.getMessage());
				}
			}
			throw new IllegalArgumentException("Application process definition has errors, see log for more details");
		}
		return kbuilder;
	}

	@Override
    public void run() {
        System.out.println(" building runtime: " + i);
        RuntimeManager manager = getRuntimeManager("org/jbpm/test/functional/task/ConcurrentHumanTask.bpmn", i);
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", "krisv");
        params.put("description", "Need a new laptop computer");
        ProcessInstance pi = ksession.startProcess("com.sample.humantask.concurrent", params);

        System.out.println(" starting runtime: " + i);
        HumanTaskResolver htr = new HumanTaskResolver(pi.getId(), manager);
        Thread t = new Thread(htr, i + "-ht-resolver");
        t.start();
    }
}

class HumanTaskResolver implements Runnable {

	private final long pid;
	private final RuntimeManager runtime;

	public HumanTaskResolver(long pid, RuntimeManager runtime) {
		this.pid = pid;
		this.runtime = runtime;
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>" + pid);
	}

	@Override
    public void run() {
        System.out.println(pid + " running tasks");
        // "sales-rep" reviews request
        TaskService taskService1 = getTaskService();

        List<TaskSummary> tasks1 = taskService1.getTasksAssignedAsPotentialOwner("sales", "en-UK");
        TaskSummary task1 = selectTaskForProcessInstance(tasks1);
        System.out.println("Sales-rep executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");
        taskService1.claim(task1.getId(), "sales-rep");
        taskService1.start(task1.getId(), "sales-rep");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("comment", "Agreed, existing laptop needs replacing");
        results.put("outcome", "Accept");
        taskService1.complete(task1.getId(), "sales-rep", results);

        TaskService taskService2 = getTaskService();

        // "krisv" approves result
        List<TaskSummary> tasks2 = taskService2.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        TaskSummary task2 = selectTaskForProcessInstance(tasks2);
        System.out.println("krisv executing task " + task2.getName() + "(" + task2.getId() + ": " + task2.getDescription() + ")");
        taskService2.start(task2.getId(), "krisv");
        results = new HashMap<String, Object>();
        results.put("outcome", "Agree");
        taskService2.complete(task2.getId(), "krisv", results);

        TaskService taskService3 = getTaskService();

        // "john" as manager reviews request
        List<TaskSummary> tasks3 = taskService3.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task3 = selectTaskForProcessInstance(tasks3);
        System.out.println("john executing task " + task3.getName() + "(" + task3.getId() + ": " + task3.getDescription() + ")");
        taskService3.claim(task3.getId(), "john");
        taskService3.start(task3.getId(), "john");
        results = new HashMap<String, Object>();
        results.put("outcome", "Agree");
        taskService3.complete(task3.getId(), "john", results);

        TaskService taskService4 = getTaskService();

        // "sales-rep" gets notification
        List<TaskSummary> tasks4 = taskService4.getTasksAssignedAsPotentialOwner("sales-rep", "en-UK");
        TaskSummary task4 = selectTaskForProcessInstance(tasks4);
        System.out.println("sales-rep executing task " + task4.getName() + "(" + task4.getId() + ": " + task4.getDescription() + ")");
        taskService4.start(task4.getId(), "sales-rep");
        Task task = taskService4.getTaskById(task4.getId());
        Content content = taskService4.getContentById(task.getTaskData().getDocumentContentId());
        Object result = ContentMarshallerHelper.unmarshall(content.getContent(), null);
        Assert.assertNotNull(result);
        taskService4.complete(task4.getId(), "sales-rep", null);

        System.out.println("Process instance completed");

        runtime.close();
    }

	public TaskService getTaskService() {
		return runtime.getRuntimeEngine(ProcessInstanceIdContext.get(pid)).getTaskService();
	}

	protected TaskSummary selectTaskForProcessInstance(List<TaskSummary> tasks) {
		for (TaskSummary ts : tasks) {
			if (ts.getProcessInstanceId().longValue() == pid) {
				return ts;
			}
		}

		return null;
	}
}
