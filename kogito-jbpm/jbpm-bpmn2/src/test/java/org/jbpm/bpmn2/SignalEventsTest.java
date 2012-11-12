package org.jbpm.bpmn2;

import java.util.ArrayList;
import java.util.List;

import org.kie.KnowledgeBase;
import org.kie.event.process.DefaultProcessEventListener;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;


public class SignalEventsTest extends JbpmBpmn2TestCase {

	public SignalEventsTest() {
		super(true);
	}

	public void testSignalBetweenProcesses() {
		 KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchSignalSingle.bpmn2", "BPMN2-IntermediateThrowEventSignal.bpmn2");
	        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
	        TestWorkItemHandler handler = new TestWorkItemHandler();
	        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
	       
	        ProcessInstance processInstance = ksession.startProcess("BPMN2-IntermediateCatchSignalSingle");
	        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
	        
	        ProcessInstance processInstance2 = ksession.startProcess("SignalIntermediateEvent");
	        assertProcessInstanceCompleted(processInstance2.getId(), ksession);
	        
	        assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testSignalToStartProcess() {
		 KnowledgeBase kbase = createKnowledgeBase("BPMN2-SignalStart.bpmn2", "BPMN2-IntermediateThrowEventSignal.bpmn2");
	        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
	        TestWorkItemHandler handler = new TestWorkItemHandler();
	        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
	        final List<String> startedProcesses = new ArrayList<String>();
	        ksession.addEventListener(new DefaultProcessEventListener() {

				@Override
				public void beforeProcessStarted(ProcessStartedEvent event) {
					startedProcesses.add(event.getProcessInstance().getProcessId());
				}
	        });
	        
	        ProcessInstance processInstance2 = ksession.startProcess("SignalIntermediateEvent");
	        assertProcessInstanceCompleted(processInstance2.getId(), ksession);
	        assertEquals(2, startedProcesses.size());
	}
	
	public void testSignalBoundaryEvent() {
		 KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn", "BPMN2-IntermediateThrowEventSignal.bpmn2");
	        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
	        TestWorkItemHandler handler = new TestWorkItemHandler();
	        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
	        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");
	        
	        ProcessInstance processInstance2 = ksession.startProcess("SignalIntermediateEvent");
	        assertProcessInstanceCompleted(processInstance2.getId(), ksession);
	        
	        
	        assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
}
