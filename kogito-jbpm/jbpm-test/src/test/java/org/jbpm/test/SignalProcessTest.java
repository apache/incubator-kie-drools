package org.jbpm.test;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;

@RunWith(Parameterized.class)
public class SignalProcessTest extends JbpmJUnitBaseTestCase {
	
	@Parameters(name="Persistence={1} - data source={0}")
    public static Collection<Object[]> parameters() {
        Object[][] locking = new Object[][] { 
                { true, true }, 
                { false, false }                
                };
        return Arrays.asList(locking);
    };
	
	public SignalProcessTest(boolean dataSource, boolean persistence) {
		super(dataSource, persistence);
	}

	@Test
    public void testDoubleSignalProcess() {
	    createRuntimeManager("signal/sample_doublesignal.bpmn2");
	    RuntimeEngine runtimeEngine = getRuntimeEngine();
	    KieSession ksession = runtimeEngine.getKieSession();

        ProcessInstance processInstance = ksession.startProcess("com.sample.signal");
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("Signal1", "", processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("Signal1", "", processInstance.getId());
        
        // check whether the process instance has completed successfully
        assertProcessInstanceNotActive(processInstance.getId(), ksession);
        
    }
	
	@Test
    public void testDoubleMessageProcess() {
	    createRuntimeManager("signal/sample_doublemessagesignal.bpmn2");
	    RuntimeEngine runtimeEngine = getRuntimeEngine();
	    KieSession ksession = runtimeEngine.getKieSession();

        ProcessInstance processInstance = ksession.startProcess("com.sample.msg");
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("Message-TestMessage", "", processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("Message-TestMessage", "", processInstance.getId());
        
        // check whether the process instance has completed successfully
        assertProcessInstanceNotActive(processInstance.getId(), ksession);
        
    }

}
