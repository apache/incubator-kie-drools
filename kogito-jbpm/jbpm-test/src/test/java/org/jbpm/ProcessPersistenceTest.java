package org.jbpm;

import java.util.List;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.test.JbpmJUnitTestCase;

/**
 * This is a sample file to test a process.
 */
public class ProcessPersistenceTest extends JbpmJUnitTestCase {
	
	public ProcessPersistenceTest() {
		super(true);
	}

	public void testProcess() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("hello.bpmn");
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello", "EndProcess");
		ksession.dispose();
	}

	public void testProcess2() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
		TaskService taskService = getTaskService(ksession);

		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		System.out.println("Started process instance " + processInstance.getId());

		assertProcessInstanceActive(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "Start", "Task 1");
		
		// dispose and recreate session
		ksession = restoreSession(ksession, false);
		
		// let john execute Task 1
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		TaskSummary task = list.get(0);
		System.out.println("John is executing task " + task.getName());
		taskService.start(task.getId(), "john");
		taskService.complete(task.getId(), "john", null);

		ksession = createKnowledgeSession("humantask.bpmn");
		processInstance = ksession.startProcess("com.sample.bpmn.hello");
		System.out.println("Started process instance " + processInstance.getId());

		assertProcessInstanceActive(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "Start", "Task 1");

		assertNodeTriggered(processInstance.getId(), "Task 2");
		
		// dispose and recreate session
		ksession = restoreSession(ksession, false);
		
		// let mary execute Task 2
		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = list.get(0);
		System.out.println("Mary is executing task " + task.getName());
		taskService.start(task.getId(), "mary");
		taskService.complete(task.getId(), "mary", null);

		assertNodeTriggered(processInstance.getId(), "End");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		
		ksession.dispose();
	}
}