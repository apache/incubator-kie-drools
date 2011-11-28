package org.jbpm;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.test.JbpmJUnitTestCase;

/**
 * This is a sample file to test a process.
 */
public class ProcessTest extends JbpmJUnitTestCase {

	public void testProcess() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("hello.bpmn");
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
		// check whether the process instance has completed successfully
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello", "EndProcess");
	}

}