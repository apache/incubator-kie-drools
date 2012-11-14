/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2;

import java.io.StringReader;
import java.util.*;

import junit.framework.TestCase;

import org.drools.*;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.kie.definition.process.Process;
import org.kie.event.process.DefaultProcessEventListener;
import org.kie.event.process.ProcessStartedEvent;
import org.drools.impl.EnvironmentFactory;
import org.kie.io.ResourceFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.ProcessBaseFactoryService;
import org.jbpm.process.instance.event.DefaultSignalManagerFactory;
import org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.junit.AfterClass;

public class StandaloneBPMNProcessTest extends TestCase {
    
    public void tearDown() {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(null);
    }
	
    public void testMinimalProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MinimalProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Minimal");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testMinimalProcessWithGraphical() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MinimalProcessWithGraphical.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Minimal");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testMinimalProcessWithDIGraphical() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MinimalProcessWithDIGraphical.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Minimal");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testCompositeProcessWithDIGraphical() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-CompositeProcessWithDIGraphical.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Composite");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

    public void testScriptTask() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ScriptTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("ScriptTask");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

    public void testDataObject() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-DataObject.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "UserId-12345");
        ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

	public void testEvaluationProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "UserId-12345");
		ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testEvaluationProcess2() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess2.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "UserId-12345");
		ProcessInstance processInstance = ksession.startProcess("com.sample.evaluation", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

//	public void testEvaluationProcess3() throws Exception {
//		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess3.bpmn2");
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
//		ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("employee", "john2");
//		ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
//		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
//	}
	
    public void testUserTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-UserTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testLane() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-Lane.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("ActorId", "mary");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("mary", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

	private StatefulKnowledgeSession restoreSession(StatefulKnowledgeSession ksession, boolean b) {
		return ksession;
	}

	public void testExclusiveSplit() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "First");
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
	public void testExclusiveSplitDefault() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitDefault.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "NotFirst");
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
    public void testInclusiveSplit() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-InclusiveSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 15);
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
    
    public void testInclusiveSplitDefault() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-InclusiveSplitDefault.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", -5);
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
    
//	public void testExclusiveSplitXPath() throws Exception {
//        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath.bpmn2");
//        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
//        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
//        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
//            .parse(new ByteArrayInputStream(
//                "<myDocument><chapter1>BlaBla</chapter1><chapter2>MoreBlaBla</chapter2></myDocument>".getBytes()));
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("x", document);
//        params.put("y", "SomeString");
//        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
//        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
//    }

	public void testEventBasedSplit() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		// No
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("No", "NoValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testEventBasedSplitBefore() throws Exception {
		// signaling before the split is reached should have no effect
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		// No
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        ksession.signalEvent("No", "NoValue", processInstance.getId());
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
	}
	
	public void testEventBasedSplitAfter() throws Exception {
		// signaling the other alternative after one has been selected should have no effect
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // No
		ksession.signalEvent("No", "NoValue", processInstance.getId());
	}
	
	public void testEventBasedSplit2() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit2.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		Thread.sleep(800);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Timer
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		Thread.sleep(800);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
//	public void testEventBasedSplit3() throws Exception {
//		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit3.bpmn2");
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
//		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
//		Person jack = new Person();
//		jack.setName("Jack");
//		// Yes
//		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
//		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
//		ksession = restoreSession(ksession);
//		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
//		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
//        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
//		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//		// Condition
//		processInstance = ksession.startProcess("com.sample.test");
//		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
//		ksession = restoreSession(ksession);
//		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
//		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
//        ksession.insert(jack);
//		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//	}
	
	public void testEventBasedSplit4() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit4.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Message-YesMessage", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		// No
		processInstance = ksession.startProcess("com.sample.test");
		ksession.signalEvent("Message-NoMessage", "NoValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testEventBasedSplit5() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit5.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
		// Yes
		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		receiveTaskHandler.setKnowledgeRuntime(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		receiveTaskHandler.setKnowledgeRuntime(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
		// No
		processInstance = ksession.startProcess("com.sample.test");
		receiveTaskHandler.messageReceived("NoMessage", "NoValue");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		receiveTaskHandler.messageReceived("YesMessage", "YesValue");
	}
	
	public void testCallActivity() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error: kbuilder.getErrors()) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "oldValue");
		ProcessInstance processInstance = ksession.startProcess("ParentProcess", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals("new value", ((WorkflowProcessInstance) processInstance).getVariable("y"));
	}

	public void testSubProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-SubProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("SubProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testMultiInstanceLoopCharacteristicsProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultiInstanceLoopCharacteristicsProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> myList = new ArrayList<String>();
		myList.add("First Item");
		myList.add("Second Item");
		params.put("list", myList);
		ProcessInstance processInstance = ksession.startProcess("MultiInstanceLoopCharacteristicsProcess", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

    public void testEscalationBoundaryEvent() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-EscalationBoundaryEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("EscalationBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

    public void testEscalationBoundaryEventInterrupting() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-EscalationBoundaryEventInterrupting.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("EscalationBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        // TODO: check for cancellation of task
    }

    public void testErrorBoundaryEvent() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventInterrupting.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("ErrorBoundaryEvent");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

//    public void testTimerBoundaryEvent() throws Exception {
//        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDuration.bpmn2");
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
//        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
//        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
//        Thread.sleep(1000);
//		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//    }
//
//    public void testTimerBoundaryEventInterrupting() throws Exception {
//        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterrupting.bpmn2");
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
//        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
//        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
//        Thread.sleep(1000);
//		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//    }

//    public void testAdHocSubProcess() throws Exception {
//		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
//		((PackageBuilderConfiguration) conf).initSemanticModules();
//		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
//		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
////        ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
//		XmlProcessReader processReader = new XmlProcessReader(
//	        ((PackageBuilderConfiguration) conf).getSemanticModules());
//		RuleFlowProcess p = (RuleFlowProcess)
//		    processReader.read(StandaloneBPMNProcessTest.class.getResourceAsStream("/BPMN2-AdHocSubProcess.bpmn2"));
//		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
////		System.out.println(XmlBPMNProcessDumper.INSTANCE.dump(p));
//		kbuilder.add(ResourceFactory.newReaderResource(
//            new StringReader(XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
//		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-AdHocSubProcess.drl"), ResourceType.DRL);
//		if (!kbuilder.getErrors().isEmpty()) {
//			for (KnowledgeBuilderError error: kbuilder.getErrors()) {
//				System.err.println(error);
//			}
//			throw new IllegalArgumentException("Errors while parsing knowledge base");
//		}
//		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
//        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
//        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
//        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
//        WorkItem workItem = workItemHandler.getWorkItem();
//        assertNull(workItem);
//        ksession = restoreSession(ksession);
//        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
//        ksession.fireAllRules();
//        System.out.println("Signaling Hello2");
//        ksession.signalEvent("Hello2", null, processInstance.getId());
//        workItem = workItemHandler.getWorkItem();
//        assertNotNull(workItem);
//        ksession = restoreSession(ksession);
//        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
//        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
//    }

//    public void testAdHocSubProcessAutoComplete() throws Exception {
//		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
//		((PackageBuilderConfiguration) conf).initSemanticModules();
//		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
//		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
////      ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
//		XmlProcessReader processReader = new XmlProcessReader(
//	        ((PackageBuilderConfiguration) conf).getSemanticModules());
//		RuleFlowProcess p = (RuleFlowProcess)
//		    processReader.read(StandaloneBPMNProcessTest.class.getResourceAsStream("/BPMN2-AdHocSubProcessAutoComplete.bpmn2"));
//		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
////		System.out.println(XmlBPMNProcessDumper.INSTANCE.dump(p));
//		kbuilder.add(ResourceFactory.newReaderResource(
//            new StringReader(XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
//		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-AdHocSubProcess.drl"), ResourceType.DRL);
//		if (!kbuilder.getErrors().isEmpty()) {
//			for (KnowledgeBuilderError error: kbuilder.getErrors()) {
//				System.err.println(error);
//			}
//			throw new IllegalArgumentException("Errors while parsing knowledge base");
//		}
//		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
//		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
//        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
//        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
//        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
//        WorkItem workItem = workItemHandler.getWorkItem();
//        assertNull(workItem);
//        ksession = restoreSession(ksession);
//        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
//        ksession.fireAllRules();
//        workItem = workItemHandler.getWorkItem();
//        assertNotNull(workItem);
//        ksession = restoreSession(ksession);
//        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
//        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
//		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//    }
    
    public void testIntermediateCatchEventSignal() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
        // now signal process instance
		ksession.signalEvent("MyMessage", "SomeValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

    public void testIntermediateCatchEventMessage() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventMessage.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("Message-HelloMessage", "SomeValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testIntermediateCatchEventTimer() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

//    public void testIntermediateCatchEventCondition() throws Exception {
//        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
//        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
//        ksession = restoreSession(ksession);
//        // now activate condition
//        Person person = new Person();
//        person.setName("Jack");
//        ksession.insert(person);
//		assertProcessInstanceCompleted(processInstance.getId(), ksession);
//    }

    public void testErrorEndEventProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ErrorEndEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("ErrorEndEvent");
		assertProcessInstanceAborted(processInstance.getId(), ksession);
    }

    public void testEscalationEndEventProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-EscalationEndEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("EscalationEndEvent");
		assertProcessInstanceAborted(processInstance.getId(), ksession);
    }

    public void testEscalationIntermediateThrowEventProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventEscalation.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("EscalationIntermediateThrowEvent");
		assertProcessInstanceAborted(processInstance.getId(), ksession);
    }

    public void testCompensateIntermediateThrowEventProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventCompensate.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("CompensateIntermediateThrowEvent");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testCompensateEndEventProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-CompensateEndEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("CompensateEndEvent");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testServiceTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ServiceProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
            ksession.startProcess("ServiceProcess", params);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertEquals("Hello john!", processInstance.getVariable("s"));
    }

    public void testSendTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-SendTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
            ksession.startProcess("SendTask", params);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testReceiveTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ReceiveTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance)
            ksession.startProcess("ReceiveTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        ksession = restoreSession(ksession, true);
        receiveTaskHandler.messageReceived("HelloMessage", "Hello john!");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

//    public void testConditionalStart() throws Exception {
//        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ConditionalStart.bpmn2");
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//        Person person = new Person();
//        person.setName("jack");
//        ksession.insert(person);
//        ksession.fireAllRules();
//        person = new Person();
//        person.setName("john");
//        ksession.insert(person);
//        ksession.fireAllRules();
//    }
    
//    public void testTimerStart() throws Exception {
//        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerStart.bpmn2");
//		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//		final List<Long> list = new ArrayList<Long>();
//		ksession.addEventListener(new ProcessEventListener() {
//			public void beforeProcessStarted(ProcessStartedEvent event) {
//			}
//			public void beforeProcessCompleted(ProcessCompletedEvent event) {
//			}
//			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
//			}
//			public void beforeNodeLeft(ProcessNodeLeftEvent event) {
//			}
//			public void afterProcessStarted(ProcessStartedEvent event) {
//				list.add(event.getProcessInstance().getId());
//			}
//			public void afterProcessCompleted(ProcessCompletedEvent event) {
//			}
//			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
//			}
//			public void afterNodeLeft(ProcessNodeLeftEvent event) {
//			}
//		});
//		Thread.sleep(250);
//		assertEquals(0, list.size());
//        for (int i = 0; i < 5; i++) {
//	        Thread.sleep(500);
//        }
//        assertEquals(5, list.size());
//    }
    
    public void testSignalStart() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-SignalStart.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		final List<Long> list = new ArrayList<Long>();
		ksession.addEventListener(new DefaultProcessEventListener() {
			public void afterProcessStarted(ProcessStartedEvent event) {
				list.add(event.getProcessInstance().getId());
			}
		});
        ksession.signalEvent("MySignal", "NewValue");
		assertEquals(1, list.size());
    }
    
    public void testSignalEnd() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-SignalEndEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ksession.startProcess("SignalEndEvent", params);
    }
    
    public void testMessageStart() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MessageStart.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		final List<Long> list = new ArrayList<Long>();
		ksession.addEventListener(new DefaultProcessEventListener() {
			public void afterProcessStarted(ProcessStartedEvent event) {
				list.add(event.getProcessInstance().getId());
			}
		});
        ksession.signalEvent("Message-HelloMessage", "NewValue");
        assertEquals(1, list.size());
    }
    
    public void testMessageEnd() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MessageEndEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("MessageEndEvent", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testMessageIntermediateThrow() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("MessageIntermediateEvent", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testSignalIntermediateThrow() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventSignal.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("SignalIntermediateEvent", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    public void testNoneIntermediateThrow() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventNone.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("NoneIntermediateEvent", null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
	private KnowledgeBase createKnowledgeBase(String process) throws Exception {
		KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new ProcessBaseFactoryService());
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
//		ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
	        ((PackageBuilderConfiguration) conf).getSemanticModules(), getClass().getClassLoader());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
		List<Process> processes = processReader.read(SimpleBPMNProcessTest.class.getResourceAsStream("/" + process));
        for (Process p : processes) {
            RuleFlowProcess ruleFlowProcess = (RuleFlowProcess) p;
            kbuilder.add(ResourceFactory.newReaderResource(
                    new StringReader(XmlBPMNProcessDumper.INSTANCE.dump(ruleFlowProcess))), ResourceType.BPMN2);
        }
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error: kbuilder.getErrors()) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

	protected StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
	    Properties defaultProps = new Properties();
	    defaultProps.setProperty("drools.processSignalManagerFactory", DefaultSignalManagerFactory.class.getName());
	    defaultProps.setProperty("drools.processInstanceManagerFactory", DefaultProcessInstanceManagerFactory.class.getName());
	    SessionConfiguration sessionConfig = new SessionConfiguration(defaultProps);
	    
		return kbase.newStatefulKnowledgeSession(sessionConfig, EnvironmentFactory.newEnvironment());
	}
	
	public void assertProcessInstanceCompleted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public void assertProcessInstanceAborted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public void assertProcessInstanceActive(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNotNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public static class TestWorkItemHandler implements WorkItemHandler {
		
	    private List<WorkItem> workItems = new ArrayList<WorkItem>();
	    
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItems.add(workItem);
        }
        
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
        
        public WorkItem getWorkItem() {
        	if (workItems.size() == 0) {
        		return null;
        	}
        	if (workItems.size() == 1) {
        		WorkItem result = workItems.get(0);
        		this.workItems.clear();
        		return result;
        	} else {
        		throw new IllegalArgumentException("More than one work item active");
        	}
        }
        
        public List<WorkItem> getWorkItems() {
        	List<WorkItem> result = new ArrayList<WorkItem>(workItems);
        	workItems.clear();
        	return result;
        }
        
	}
}
