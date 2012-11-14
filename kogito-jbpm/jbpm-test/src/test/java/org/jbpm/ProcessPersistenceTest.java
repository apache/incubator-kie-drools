package org.jbpm;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Test;

/**
 * This is a sample file to test a process.
 */
public class ProcessPersistenceTest extends JbpmJUnitTestCase {
	
	public ProcessPersistenceTest() {
		super(true);
		setPersistence(true);
	}

	@Test
	public void testProcess() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("hello.bpmn");
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello", "EndProcess");
	}

	@Test
	public void testTransactions() throws Exception {
		StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
		
		UserTransaction ut = (UserTransaction) new InitialContext().lookup( "java:comp/UserTransaction" );
        ut.begin();
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		ut.rollback();

		assertNull(ksession.getProcessInstance(processInstance.getId()));
	}
	
}