package org.jbpm.bpmn2;

import org.drools.KnowledgeBase;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.junit.Ignore;

@Ignore
public class SignalEventsTest extends JbpmBpmn2TestCase {

	public SignalEventsTest() {
		super(true);
	}
	
	public void testSignal() {
		 KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchSignalSingle.bpmn2", "BPMN2-IntermediateThrowEventSignal.bpmn2");
	        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
	        TestWorkItemHandler handler = new TestWorkItemHandler();
	        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
	        ksession.addEventListener(new DefaultProcessEventListener() {

	            @Override
	            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
	                System.out.println("After node triggered " + event.getNodeInstance().getNodeName());
	            }

	            @Override
	            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
	                System.out.println("Before node triggered " + event.getNodeInstance().getNodeName());
	            }
	           
	        });
	        
	        ProcessInstance processInstance = ksession.startProcess("BPMN2-IntermediateCatchSignalSingle");
	        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
	        
	        ProcessInstance processInstance2 = ksession.startProcess("SignalIntermediateEvent");
	        assertProcessInstanceCompleted(processInstance2.getId(), ksession);
	        
	        assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
}
