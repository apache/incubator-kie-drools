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

package org.jbpm.examples.junit;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.TestCase;

import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.event.process.DefaultProcessEventListener;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.kie.io.ResourceFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BPMN2JUnitTests extends TestCase {

    public void testMinimalProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MinimalProcess.bpmn2");
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

    public void testImport() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-Import.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Import");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

    public void testRuleTask() throws Exception {
		System.out.println("Loading process BPMN2-RuleTask.bpmn2"); 
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("junit/BPMN2-RuleTask.bpmn2"), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("junit/BPMN2-RuleTask.drl"), ResourceType.DRL);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		List<String> list = new ArrayList<String>();
		ksession.setGlobal("list", list);
		ProcessInstance processInstance = ksession.startProcess("RuleTask");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession.fireAllRules();
		assertTrue(list.size() == 1);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
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

	public void testEvaluationProcess3() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess3.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "john2");
		ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
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
	
	public void testExclusiveSplitXPathAdvanced() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath-advanced.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element hi = doc.createElement("hi");
        Element ho = doc.createElement("ho");
        hi.appendChild(ho);
        Attr attr = doc.createAttribute("value");
        ho.setAttributeNode(attr);
        attr.setValue("a");
        params.put("x", hi);
        params.put("y", "Second");
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
	
	public void testExclusiveSplitXPathAdvanced2() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath-advanced-vars-not-signaled.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element hi = doc.createElement("hi");
        Element ho = doc.createElement("ho");
        hi.appendChild(ho);
        Attr attr = doc.createAttribute("value");
        ho.setAttributeNode(attr);
        attr.setValue("a");
        params.put("x", hi);
        params.put("y", "Second");
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
	
	public void testExclusiveSplitXPathAdvancedWithVars() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath-advanced-with-vars.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element hi = doc.createElement("hi");
        Element ho = doc.createElement("ho");
        hi.appendChild(ho);
        Attr attr = doc.createAttribute("value");
        ho.setAttributeNode(attr);
        attr.setValue("a");
        params.put("x", hi);
        params.put("y", "Second");
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

	public void testExclusiveSplitPriority() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitPriority.bpmn2");
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
        ksession.fireAllRules();
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
        ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testEventBasedSplit3() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit3.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		Person jack = new Person();
		jack.setName("Jack");
		// Yes
		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		// Condition
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.insert(jack);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
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
		System.out.println("Loading process BPMN2-CallActivity.bpmn2"); 
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("junit/BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("junit/BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
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
		ksession.addEventListener(new DefaultProcessEventListener() {
			public void afterProcessStarted(ProcessStartedEvent event) {
				System.out.println(event);
			}
			public void beforeVariableChanged(ProcessVariableChangedEvent event) {
				System.out.println(event);
			}
			public void afterVariableChanged(ProcessVariableChangedEvent event) {
				System.out.println(event);
			}
		});
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

    public void testTimerBoundaryEventDuration() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDuration.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testTimerBoundaryEventCycle1() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle1.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testTimerBoundaryEventCycle2() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle2.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        Thread.sleep(1000);
		assertProcessInstanceActive(processInstance.getId(), ksession);
        Thread.sleep(1000);
		assertProcessInstanceActive(processInstance.getId(), ksession);
		ksession.abortProcessInstance(processInstance.getId());
        Thread.sleep(1000);
    }

    public void testTimerBoundaryEventInterrupting() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterrupting.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        System.out.println("Firing timer");
        ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testAdHocSubProcess() throws Exception {
		System.out.println("Loading process BPMN2-AdHocSubProcess.bpmn2"); 
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("junit/BPMN2-AdHocSubProcess.bpmn2"), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("junit/BPMN2-AdHocSubProcess.drl"), ResourceType.DRL);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNull(workItem);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.fireAllRules();
        System.out.println("Signaling Hello2");
        ksession.signalEvent("Hello2", null, processInstance.getId());
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
    }

    public void testAdHocSubProcessAutoComplete() throws Exception {
		System.out.println("Loading process BPMN2-AdHocSubProcessAutoComplete.bpmn2"); 
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("junit/BPMN2-AdHocSubProcessAutoComplete.bpmn2"), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("junit/BPMN2-AdHocSubProcess.drl"), ResourceType.DRL);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNull(workItem);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.fireAllRules();
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    public void testAdHocProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-AdHocProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("AdHocProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        System.out.println("Triggering node");
        ksession.signalEvent("Task1", null, processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);
        ksession.signalEvent("User1", null, processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);
		ksession.insert(new Person());
		ksession.signalEvent("Task3", null, processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

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

    public void testIntermediateCatchEventTimerDuration() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testIntermediateCatchEventTimerCycle1() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle1.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testIntermediateCatchEventTimerCycle2() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle2.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
		assertProcessInstanceActive(processInstance.getId(), ksession);
        Thread.sleep(1000);
		assertProcessInstanceActive(processInstance.getId(), ksession);
		ksession.abortProcessInstance(processInstance.getId());
        Thread.sleep(1000);
    }

    public void testIntermediateCatchEventCondition() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

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

    public void testConditionalStart() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ConditionalStart.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Person person = new Person();
        person.setName("jack");
        ksession.insert(person);
        ksession.fireAllRules();
        person = new Person();
        person.setName("john");
        ksession.insert(person);
        ksession.fireAllRules();
    }
    
    public void testTimerStart() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerStart.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		final List<Long> list = new ArrayList<Long>();
		ksession.addEventListener(new DefaultProcessEventListener() {
			public void afterProcessStarted(ProcessStartedEvent event) {
				list.add(event.getProcessInstance().getId());
			}
		});
		Thread.sleep(250);
		assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
	        ksession.fireAllRules();
	        Thread.sleep(500);
        }
        assertEquals(5, list.size());
    }
    
    public void testTimerStartCron() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerStartCron.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		final List<Long> list = new ArrayList<Long>();
		ksession.addEventListener(new DefaultProcessEventListener() {
			public void afterProcessStarted(ProcessStartedEvent event) {
				list.add(event.getProcessInstance().getId());
			}
		});
		Thread.sleep(500);
        for (int i = 0; i < 5; i++) {
	        ksession.fireAllRules();
	        Thread.sleep(1000);
        }
        assertEquals(6, list.size());
    }
    
    public void testSignalStart() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-SignalStart.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		final List<Long> list = new ArrayList<Long>();
		ksession.addEventListener(new DefaultProcessEventListener() {
			public void afterProcessStarted(ProcessStartedEvent event) {
				list.add(event.getProcessInstance().getId());
			}
		});
        ksession.signalEvent("MyStartSignal", "NewValue");
		assertEquals(1, list.size());
    }
    
    public void testSignalStartDynamic() throws Exception {
    	KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        KnowledgeBase kbase2 = createKnowledgeBase("BPMN2-SignalStart.bpmn2");
        kbase.addKnowledgePackages(kbase2.getKnowledgePackages());
		final List<Long> list = new ArrayList<Long>();
		ksession.addEventListener(new DefaultProcessEventListener() {
			public void afterProcessStarted(ProcessStartedEvent event) {
				list.add(event.getProcessInstance().getId());
			}
		});
        ksession.signalEvent("MyStartSignal", "NewValue");
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
        ksession.signalEvent("Message-HelloMessage", "NewValue");
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

	public void testXpathExpression() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-XpathExpression.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Document document = DocumentBuilderFactory.newInstance()
			.newDocumentBuilder().parse(new ByteArrayInputStream(
				"<instanceMetadata><user approved=\"false\" /></instanceMetadata>".getBytes()));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceMetadata", document);
		ProcessInstance processInstance = ksession.startProcess("XPathProcess", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testOnEntryExitScript() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-OnEntryExitScriptProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new SystemOutWorkItemHandler());
		List<String> myList = new ArrayList<String>();
		ksession.setGlobal("list", myList);
		ProcessInstance processInstance = ksession.startProcess("OnEntryExitScriptProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals(4, myList.size());
	}
	
	public void testXORGateway() throws Exception {
	    KnowledgeBase kbase = createKnowledgeBase("BPMN2-gatewayTest.bpmn2");
	    StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
	    Document document = DocumentBuilderFactory.newInstance()
	    .newDocumentBuilder().parse(new ByteArrayInputStream(
	            "<instanceMetadata><user approved=\"false\" /></instanceMetadata>".getBytes()));
	    Map<String, Object> params = new HashMap<String, Object>();
	    params.put("instanceMetadata", document);
	    params.put("startMessage", DocumentBuilderFactory.newInstance()
	            .newDocumentBuilder().parse(new ByteArrayInputStream(
	                    "<task subject='foobar2'/>".getBytes())).getFirstChild());
	    ProcessInstance processInstance = ksession.startProcess("process", params);
	    assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
	private KnowledgeBase createKnowledgeBase(String process) throws Exception {
		System.out.println("Loading process " + process); 
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource("junit/" + process), ResourceType.BPMN2);
		return kbuilder.newKnowledgeBase();
	}
	
	private StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
		return kbase.newStatefulKnowledgeSession();
	}
	
	private void assertProcessInstanceActive(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNotNull(ksession.getProcessInstance(processInstanceId));
	}
	
	private void assertProcessInstanceCompleted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	private void assertProcessInstanceAborted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	private StatefulKnowledgeSession restoreSession(StatefulKnowledgeSession ksession, boolean b) {
		return ksession;
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
