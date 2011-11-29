package org.jbpm;

import java.util.List;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.test.JbpmJUnitTestCase;

/**
 * This is a sample file to test a process.
 */
public class ProcessPersistenceHumanTaskTest extends JbpmJUnitTestCase {

	public ProcessPersistenceHumanTaskTest() {
		super(true);
		setPersistence(true);
	}

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
		
		ksession.dispose();
	}
	
	public void testTransactions() throws Exception {
		StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
		TaskService taskService = getTaskService(ksession);
		
		UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		ut.rollback();

		assertNull(ksession.getProcessInstance(processInstance.getId()));
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		assertEquals(0, list.size());

		ksession.dispose();
	}

}