package org.jbpm;

import java.util.List;

import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Test;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

/**
 * This is a sample file to test a process.
 */
public class ProcessHumanTaskTest extends JbpmJUnitTestCase {
	
	public ProcessHumanTaskTest() {
		super(true);
	}

	@Test
	public void testProcess() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
		TaskService taskService = getTaskService(ksession);
		
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");

		assertProcessInstanceActive(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "Start", "Task 1");
		
		// let john execute Task 1
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		TaskSummary task = list.get(0);
		System.out.println("John is executing task " + task.getName());
		taskService.start(task.getId(), "john");
		taskService.complete(task.getId(), "john", null);

		assertNodeTriggered(processInstance.getId(), "Task 2");
		
		// let mary execute Task 2
		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = list.get(0);
		System.out.println("Mary is executing task " + task.getName());
		taskService.start(task.getId(), "mary");
		taskService.complete(task.getId(), "mary", null);

		assertNodeTriggered(processInstance.getId(), "End");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

}