package org.jbpm;

import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Test;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

/**
 * This is a sample file to test a process.
 */
public class ProcessTest extends JbpmJUnitTestCase {

	@Test
    public void testProcess() {
        StatefulKnowledgeSession ksession = createKnowledgeSession("hello.bpmn");
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        // check whether the process instance has completed successfully
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello", "EndProcess");
    }

}