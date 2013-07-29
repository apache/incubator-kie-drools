package org.jbpm.test;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

/**
 * This is a sample file to test a process.
 */
public class ProcessTest extends JbpmJUnitBaseTestCase {

	@Test
    public void testProcess() {
	    createRuntimeManager("hello.bpmn");
	    RuntimeEngine runtimeEngine = getRuntimeEngine();
	    KieSession ksession = runtimeEngine.getKieSession();

        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        // check whether the process instance has completed successfully
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello", "EndProcess");
    }

}
