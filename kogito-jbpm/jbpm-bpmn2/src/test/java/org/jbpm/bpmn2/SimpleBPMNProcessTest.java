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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.drools.WorkingMemory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.kie.definition.process.Process;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.kie.event.process.DefaultProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.event.process.ProcessVariableChangedEvent;
import org.drools.impl.KnowledgeBaseFactoryServiceImpl;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.kie.io.ResourceFactory;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.process.instance.impl.WorkItemImpl;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.kie.runtime.rule.FactHandle;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.instance.impl.RuleAwareProcessEventLister;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimpleBPMNProcessTest extends JbpmBpmn2TestCase {

    private Logger logger = LoggerFactory.getLogger(SimpleBPMNProcessTest.class);
    
    protected void setUp() { 
        String testName = getName();
        String [] testFailsWithPersistence = { 
            "testEventBasedSplit", "testTimerBoundaryEvent", "testIntermediateCatchEventTimer", "testTimerStart", 
            // broken, but should work?!?
            "testSignalBoundaryEvent", 
            "testEscalationBoundaryEventOnTask", "testErrorBoundaryEventOnTask",
            "testBusinessRuleTask",
            "testNullVariableInScriptTaskProcess",
            "testConditionalBoundaryEvent",
            "testMessageBoundaryEventOnTask",
            "testMessageBoundaryEvent",
            "testCallActivityWithBoundaryEvent",
            "testRuleTaskWithFacts"
        };
        for( String testNameBegin : testFailsWithPersistence ) { 
             if( testName.startsWith(testNameBegin) ) { 
                 persistence = false;
             }
        }
        super.setUp();
    }
    
    public void testSignalBoundaryEventOnTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("After node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("After node triggered " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("Before node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("Before node triggered " + event.getNodeInstance().getNodeName());
            }
           
        });
        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");
        ksession.signalEvent("MySignal", "value");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
    
    public void testSignalBoundaryEventOnTaskComplete() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("After node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("After node triggered " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("Before node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("Before node triggered " + event.getNodeInstance().getNodeName());
            }
           
        });
        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");
        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        ksession.signalEvent("MySignal", "value");
        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
    
	public void testMinimalProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MinimalProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Minimal");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testMinimalProcessImplicit() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MinimalProcessImplicit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Minimal");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testImplicitEndParallel() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ParallelSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("com.sample.test");
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

	public void testImport() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-Import.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Import");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testRuleTask() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		List<Process> processes = processReader
				.read(SimpleBPMNProcessTest.class
						.getResourceAsStream("/BPMN2-RuleTask.bpmn2"));
		assertNotNull(processes);
		assertEquals(1, processes.size());
		RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		assertNotNull(p);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		// logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
				XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(
				ResourceFactory.newClassPathResource("BPMN2-RuleTask.drl"),
				ResourceType.DRL);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		List<String> list = new ArrayList<String>();
		ksession.setGlobal("list", list);
		ProcessInstance processInstance = ksession.startProcess("RuleTask");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		restoreSession(ksession, true);
		ksession.fireAllRules();
		assertTrue(list.size() == 1);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testRuleTask2() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		List<Process> processes = processReader
				.read(SimpleBPMNProcessTest.class
						.getResourceAsStream("/BPMN2-RuleTask2.bpmn2"));
		assertNotNull(processes);
		assertEquals(1, processes.size());
		RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		assertNotNull(p);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		// logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
				XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(
				ResourceFactory.newClassPathResource("BPMN2-RuleTask2.drl"),
				ResourceType.DRL);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		List<String> list = new ArrayList<String>();
		ksession.setGlobal("list", list);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "SomeString");
		ProcessInstance processInstance = ksession.startProcess("RuleTask", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		restoreSession(ksession, true);
		ksession.fireAllRules();
		assertTrue(list.size() == 0);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testRuleTaskWithFacts() throws Exception {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
                .newKnowledgeBuilderConfiguration();
        ((PackageBuilderConfiguration) conf).initSemanticModules();
        ((PackageBuilderConfiguration) conf)
                .addSemanticModule(new BPMNSemanticModule());
        ((PackageBuilderConfiguration) conf)
                .addSemanticModule(new BPMNDISemanticModule());
        // ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
        XmlProcessReader processReader = new XmlProcessReader(
                ((PackageBuilderConfiguration) conf).getSemanticModules(),
                getClass().getClassLoader());
        List<Process> processes = processReader
                .read(SimpleBPMNProcessTest.class
                        .getResourceAsStream("/BPMN2-RuleTaskWithFact.bpmn2"));
        assertNotNull(processes);
        assertEquals(1, processes.size());
        RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
        assertNotNull(p);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder(conf);
        // logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
                XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
        kbuilder.add(
                ResourceFactory.newClassPathResource("BPMN2-RuleTask3.drl"),
                ResourceType.DRL);
        if (!kbuilder.getErrors().isEmpty()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                logger.error(error.toString());
            }
            throw new IllegalArgumentException(
                    "Errors while parsing knowledge base");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        
        final org.drools.event.AgendaEventListener agendaEventListener = new org.drools.event.AgendaEventListener() {
            public void activationCreated(ActivationCreatedEvent event, WorkingMemory workingMemory){
                ksession.fireAllRules();
            }
            public void activationCancelled(ActivationCancelledEvent event, WorkingMemory workingMemory){
            }
            public void beforeActivationFired(BeforeActivationFiredEvent event, WorkingMemory workingMemory) {
            }
            public void afterActivationFired(AfterActivationFiredEvent event, WorkingMemory workingMemory) {
            }
            public void agendaGroupPopped(AgendaGroupPoppedEvent event, WorkingMemory workingMemory) {
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event, WorkingMemory workingMemory) {
            }
            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
            }
            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
                workingMemory.fireAllRules();
            }
            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
            }
            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
            }
        };
        ((StatefulKnowledgeSessionImpl)  ksession).session.addEventListener(agendaEventListener);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "SomeString");
        ProcessInstance processInstance = ksession.startProcess("RuleTask", params);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);

        params = new HashMap<String, Object>();

        try {
            processInstance = ksession.startProcess("RuleTask", params);

            fail("Should fail");
        } catch (Exception e) {
            e.printStackTrace();
        }

        params = new HashMap<String, Object>();
        params.put("x", "SomeString");
        processInstance = ksession.startProcess("RuleTask", params);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
	
	public void testRuleTaskAcrossSessions() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(
			ResourceFactory.newClassPathResource("BPMN2-RuleTask.bpmn2"), 
			ResourceType.BPMN2);
		kbuilder.add(
			ResourceFactory.newClassPathResource("BPMN2-RuleTask.drl"),
			ResourceType.DRL);
		KnowledgeBase kbase = kbuilder.newKnowledgeBase();
		StatefulKnowledgeSession ksession1 = createKnowledgeSession(kbase);
		StatefulKnowledgeSession ksession2 = createKnowledgeSession(kbase);
		List<String> list1 = new ArrayList<String>();
		ksession1.setGlobal("list", list1);
		List<String> list2 = new ArrayList<String>();
		ksession2.setGlobal("list", list2);
		ProcessInstance processInstance1 = ksession1.startProcess("RuleTask");
		ProcessInstance processInstance2 = ksession2.startProcess("RuleTask");
		ksession1.fireAllRules();
		assertProcessInstanceCompleted(processInstance1.getId(), ksession1);
		assertProcessInstanceActive(processInstance2.getId(), ksession2);
		ksession2.fireAllRules();
		assertProcessInstanceCompleted(processInstance2.getId(), ksession2);
	}
	
	public void testDataObject() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-DataObject.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "UserId-12345");
		ProcessInstance processInstance = ksession.startProcess("Evaluation",
				params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testDataStore() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-DataStore.xml");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Evaluation");
		Definitions def = (Definitions) processInstance.getProcess()
				.getMetaData().get("Definitions");
		assertNotNull(def.getDataStores());
		assertTrue(def.getDataStores().size() == 1);
		DataStore dataStore = def.getDataStores().get(0);
		assertEquals("employee", dataStore.getId());
		assertEquals("employeeStore", dataStore.getName());
		assertEquals(String.class.getCanonicalName(),
				((ObjectDataType) dataStore.getType()).getClassName());
	}

	public void testAssociation() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-Association.xml");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Evaluation");
		Definitions def = (Definitions) processInstance.getProcess()
				.getMetaData().get("Definitions");
		assertNotNull(def.getAssociations());
		assertTrue(def.getAssociations().size() == 1);
		Association assoc = def.getAssociations().get(0);
		assertEquals("_1234", assoc.getId());
		assertEquals("_1", assoc.getSourceRef());
		assertEquals("_2", assoc.getTargetRef());
	}

	public void testUserTaskWithDataStoreScenario() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-UserTaskWithDataStore.xml");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		ksession.startProcess("UserProcess");
		// we can't test further as user tasks are asynchronous.
	}

	public void testEvaluationProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(
				"RegisterRequest", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "UserId-12345");
		ProcessInstance processInstance = ksession.startProcess("Evaluation",
				params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testEvaluationProcess2() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess2.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "UserId-12345");
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.evaluation", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testEvaluationProcess3() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess3.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler(
				"RegisterRequest", new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "john2");
		ProcessInstance processInstance = ksession.startProcess("Evaluation",
				params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testUserTask() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-UserTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
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
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ProcessInstance processInstance = ksession.startProcess("UserTask");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		WorkItem workItem = workItemHandler.getWorkItem();
		assertNotNull(workItem);
		assertEquals("john", workItem.getParameter("ActorId"));
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("ActorId", "mary");
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(),
				results);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		workItem = workItemHandler.getWorkItem();
		assertNotNull(workItem);
		assertEquals("mary", workItem.getParameter("ActorId"));
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testExclusiveSplit() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email",
				new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "First");
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testExclusiveSplitXPathAdvanced() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath-advanced.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email",
				new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element hi = doc.createElement("hi");
		Element ho = doc.createElement("ho");
		hi.appendChild(ho);
		Attr attr = doc.createAttribute("value");
		ho.setAttributeNode(attr);
		attr.setValue("a");
		params.put("x", hi);
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testExclusiveSplitXPathAdvanced2() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath-advanced-vars-not-signaled.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email",
				new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element hi = doc.createElement("hi");
		Element ho = doc.createElement("ho");
		hi.appendChild(ho);
		Attr attr = doc.createAttribute("value");
		ho.setAttributeNode(attr);
		attr.setValue("a");
		params.put("x", hi);
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testExclusiveSplitXPathAdvancedWithVars() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath-advanced-with-vars.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email",
				new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element hi = doc.createElement("hi");
		Element ho = doc.createElement("ho");
		hi.appendChild(ho);
		Attr attr = doc.createAttribute("value");
		ho.setAttributeNode(attr);
		attr.setValue("a");
		params.put("x", hi);
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testExclusiveSplitPriority() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitPriority.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email",
				new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "First");
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testExclusiveSplitDefault() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitDefault.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email",
				new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "NotFirst");
		params.put("y", "Second");
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testInclusiveSplit() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-InclusiveSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", 15);
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testInclusiveSplitDefault() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-InclusiveSplitDefault.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", -5);
		ProcessInstance processInstance = ksession.startProcess(
				"com.sample.test", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	// public void testExclusiveSplitXPath() throws Exception {
	// KnowledgeBase kbase =
	// createKnowledgeBase("BPMN2-ExclusiveSplitXPath.bpmn2");
	// StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
	// ksession.getWorkItemManager().registerWorkItemHandler("Email", new
	// SystemOutWorkItemHandler());
	// Document document =
	// DocumentBuilderFactory.newInstance().newDocumentBuilder()
	// .parse(new ByteArrayInputStream(
	// "<myDocument><chapter1>BlaBla</chapter1><chapter2>MoreBlaBla</chapter2></myDocument>".getBytes()));
	// Map<String, Object> params = new HashMap<String, Object>();
	// params.put("x", document);
	// params.put("y", "SomeString");
	// ProcessInstance processInstance =
	// ksession.startProcess("com.sample.test", params);
	// assertTrue(processInstance.getState() ==
	// ProcessInstance.STATE_COMPLETED);
	// }

	public void testEventBasedSplit() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession
				.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		// No
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ksession.signalEvent("No", "NoValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testEventBasedSplitBefore() throws Exception {
		// signaling before the split is reached should have no effect
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new DoNothingWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new DoNothingWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession
				.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new DoNothingWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new DoNothingWorkItemHandler());
		ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		// No
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new DoNothingWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new DoNothingWorkItemHandler());
		ksession.signalEvent("No", "NoValue", processInstance.getId());
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
	}

	public void testEventBasedSplitAfter() throws Exception {
		// signaling the other alternative after one has been selected should
		// have no effect
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new DoNothingWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession
				.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new DoNothingWorkItemHandler());
		ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new DoNothingWorkItemHandler());
		// No
		ksession.signalEvent("No", "NoValue", processInstance.getId());
	}

	public void testEventBasedSplit2() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit2.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession
				.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		Thread.sleep(800);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ksession.fireAllRules();
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		// Timer
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		Thread.sleep(800);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testEventBasedSplit3() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit3.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		Person jack = new Person();
		jack.setName("Jack");
		// Yes
		ProcessInstance processInstance = ksession
				.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		// Condition
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ksession.insert(jack);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testEventBasedSplit4() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit4.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		// Yes
		ProcessInstance processInstance = ksession
				.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ksession.signalEvent("Message-YesMessage", "YesValue",
				processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		// No
		processInstance = ksession.startProcess("com.sample.test");
		ksession.signalEvent("Message-NoMessage", "NoValue",
				processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testEventBasedSplit5() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit5.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Receive Task",
				receiveTaskHandler);
		// Yes
		ProcessInstance processInstance = ksession
				.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		receiveTaskHandler.setKnowledgeRuntime(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Receive Task",
				receiveTaskHandler);
		receiveTaskHandler.messageReceived("YesMessage", "YesValue");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		receiveTaskHandler.messageReceived("NoMessage", "NoValue");
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1",
				new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2",
				new SystemOutWorkItemHandler());
		receiveTaskHandler.setKnowledgeRuntime(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Receive Task",
				receiveTaskHandler);
		// No
		processInstance = ksession.startProcess("com.sample.test");
		receiveTaskHandler.messageReceived("NoMessage", "NoValue");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		receiveTaskHandler.messageReceived("YesMessage", "YesValue");
	}

	public void testCallActivity() throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-CallActivity.bpmn2"),
				ResourceType.BPMN2);
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"),
				ResourceType.BPMN2);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "oldValue");
		ProcessInstance processInstance = ksession.startProcess(
				"ParentProcess", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals("new value",
				((WorkflowProcessInstance) processInstance).getVariable("y"));
	}
	
   public void testCallActivityByName() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivityByName.bpmn2"),
                ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"),
                ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcessV2.bpmn2"),
                ResourceType.BPMN2);
        if (!kbuilder.getErrors().isEmpty()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                logger.error(error.toString());
            }
            throw new IllegalArgumentException(
                    "Errors while parsing knowledge base");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        ProcessInstance processInstance = ksession.startProcess(
                "ParentProcess", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals("new value V2",
                ((WorkflowProcessInstance) processInstance).getVariable("y"));
    }

	public void testSubProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-SubProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.addEventListener(new DefaultProcessEventListener() {
			public void afterProcessStarted(ProcessStartedEvent event) {
				logger.debug(event.toString());
			}

			public void beforeVariableChanged(ProcessVariableChangedEvent event) {
				logger.debug(event.toString());
			}

			public void afterVariableChanged(ProcessVariableChangedEvent event) {
				logger.debug(event.toString());
			}
		});
		ProcessInstance processInstance = ksession.startProcess("SubProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
	public void testSubProcessWithTerminateEndEvent() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-SubProcessWithTerminateEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        final List<String> list = new ArrayList<String>();
        ksession.addEventListener(new DefaultProcessEventListener() {
     
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                list.add(event.getNodeInstance().getNodeName());
            }
        });
        ProcessInstance processInstance = ksession.startProcess("SubProcessTerminate");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals(7, list.size());
    }
	
	public void testSubProcessWithTerminateEndEventProcessScope() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-SubProcessWithTerminateEndEventProcessScope.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        final List<String> list = new ArrayList<String>();
        ksession.addEventListener(new DefaultProcessEventListener() {
     
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                list.add(event.getNodeInstance().getNodeName());
            }
        });
        ProcessInstance processInstance = ksession.startProcess("SubProcessTerminate");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals(5, list.size());
    }

	public void testMultiInstanceLoopCharacteristicsProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultiInstanceLoopCharacteristicsProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> myList = new ArrayList<String>();
		myList.add("First Item");
		myList.add("Second Item");
		params.put("list", myList);
		ProcessInstance processInstance = ksession.startProcess(
				"MultiInstanceLoopCharacteristicsProcess", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
	public void testMultiInstanceLoopCharacteristicsProcessWithOutput() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultiInstanceLoopCharacteristicsProcessWithOutput.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> myList = new ArrayList<String>();
        List<String> myListOut = new ArrayList<String>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        params.put("listOut", myListOut);
        assertEquals(0, myListOut.size());
        ProcessInstance processInstance = ksession.startProcess(
                "MultiInstanceLoopCharacteristicsProcessWithOutput", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals(2, myListOut.size());
    }

	public void testMultiInstanceLoopCharacteristicsTaskWithOutput() throws Exception {
        KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-MultiInstanceLoopCharacteristicsTaskWithOutput.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> myList = new ArrayList<String>();
        List<String> myListOut = new ArrayList<String>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        params.put("listOut", myListOut);
        assertEquals(0, myListOut.size());
        ProcessInstance processInstance = ksession.startProcess(
                "MultiInstanceLoopCharacteristicsTask", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals(2, myListOut.size());
    }
	
	public void testMultiInstanceLoopCharacteristicsTask() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-MultiInstanceLoopCharacteristicsTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new SystemOutWorkItemHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		List<String> myList = new ArrayList<String>();
		myList.add("First Item");
		myList.add("Second Item");
		params.put("list", myList);
		ProcessInstance processInstance = ksession.startProcess(
				"MultiInstanceLoopCharacteristicsTask", params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testEscalationBoundaryEvent() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EscalationBoundaryEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession
				.startProcess("EscalationBoundaryEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testEscalationBoundaryEventInterrupting() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EscalationBoundaryEventInterrupting.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("EscalationBoundaryEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		// TODO: check for cancellation of task
	}

	public void testEscalationBoundaryEventOnTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-EscalationBoundaryEventOnTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("After node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("After node triggered " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("Before node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("Before node triggered " + event.getNodeInstance().getNodeName());
            }
           
        });
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EscalationBoundaryEventOnTask");
        
        List<WorkItem> workItems = handler.getWorkItems();
        assertEquals(2, workItems.size());
        
        WorkItem workItem = workItems.get(0);
        if (!"john".equalsIgnoreCase((String) workItem.getParameter("ActorId"))) {
            workItem = workItems.get(1);
        }
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
	
	
	public void testErrorBoundaryEvent() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventInterrupting.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("ErrorBoundaryEvent");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testErrorBoundaryEventOnTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventOnTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("After node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("After node triggered " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("Before node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("Before node triggered " + event.getNodeInstance().getNodeName());
            }
           
        });
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-ErrorBoundaryEventOnTask");
        
        List<WorkItem> workItems = handler.getWorkItems();
        assertEquals(2, workItems.size());
        
        WorkItem workItem = workItems.get(0);
        if (!"john".equalsIgnoreCase((String) workItem.getParameter("ActorId"))) {
            workItem = workItems.get(1);
        }
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

	public void testTimerBoundaryEventDuration() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDuration.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("TimerBoundaryEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		Thread.sleep(1000);
		ksession = restoreSession(ksession, true);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
   public void testTimerBoundaryEventDurationISO() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDurationISO.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        Thread.sleep(1500);
        ksession = restoreSession(ksession, true);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
   
   public void testTimerBoundaryEventDateISO() throws Exception {
       KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDateISO.bpmn2");
       StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
       ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
               new DoNothingWorkItemHandler());
       HashMap<String, Object> params = new HashMap<String, Object>();
       DateTime now = new DateTime(System.currentTimeMillis());
       now.plus(2000);
       params.put("date", now.toString());
       ProcessInstance processInstance = ksession
               .startProcess("TimerBoundaryEvent", params);
       assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
       Thread.sleep(2000);
       ksession = restoreSession(ksession, true);
       assertProcessInstanceCompleted(processInstance.getId(), ksession);
   }

	public void testTimerBoundaryEventCycle1() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle1.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("TimerBoundaryEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		Thread.sleep(1000);
		ksession = restoreSession(ksession, true);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testTimerBoundaryEventCycle2() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle2.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("TimerBoundaryEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		Thread.sleep(1000);
		assertProcessInstanceActive(processInstance.getId(), ksession);
		Thread.sleep(1000);
		assertProcessInstanceActive(processInstance.getId(), ksession);
		ksession.abortProcessInstance(processInstance.getId());
		Thread.sleep(1000);
	}
	
   public void testTimerBoundaryEventCycleISO() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycleISO.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
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
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("TimerBoundaryEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		Thread.sleep(1000);
		ksession = restoreSession(ksession, true);
		logger.debug("Firing timer");
		ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testTimerBoundaryEventInterruptingOnTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterruptingOnTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        logger.debug("Firing timer");
        ksession.fireAllRules();
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
	
	public void testSignalBoundaryEventInterrupting() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-SignalBoundaryEventInterrupting.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("SignalBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        ksession = restoreSession(ksession, true);
        ksession.signalEvent("MyMessage", null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

	public void testAdHocSubProcess() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		List<Process> processes = processReader
				.read(SimpleBPMNProcessTest.class
						.getResourceAsStream("/BPMN2-AdHocSubProcess.bpmn2"));
		assertNotNull(processes);
		assertEquals(1, processes.size());
		RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		assertNotNull(p);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		// logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
				XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-AdHocSubProcess.drl"),
				ResourceType.DRL);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ProcessInstance processInstance = ksession
				.startProcess("AdHocSubProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		WorkItem workItem = workItemHandler.getWorkItem();
		assertNull(workItem);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ksession.fireAllRules();
		logger.debug("Signaling Hello2");
		ksession.signalEvent("Hello2", null, processInstance.getId());
		workItem = workItemHandler.getWorkItem();
		assertNotNull(workItem);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
	}

	public void testAdHocSubProcessAutoComplete() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		List<Process> processes = processReader
				.read(SimpleBPMNProcessTest.class
						.getResourceAsStream("/BPMN2-AdHocSubProcessAutoComplete.bpmn2"));
		assertNotNull(processes);
		assertEquals(1, processes.size());
		RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		// logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
				XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-AdHocSubProcess.drl"),
				ResourceType.DRL);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ProcessInstance processInstance = ksession
				.startProcess("AdHocSubProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		WorkItem workItem = workItemHandler.getWorkItem();
		assertNull(workItem);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ksession.fireAllRules();
		workItem = workItemHandler.getWorkItem();
		assertNotNull(workItem);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testAdHocSubProcessAutoCompleteDynamicTask() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		List<Process> processes = processReader
				.read(SimpleBPMNProcessTest.class
						.getResourceAsStream("/BPMN2-AdHocSubProcessAutoComplete.bpmn2"));
		assertNotNull(processes);
		assertEquals(1, processes.size());
		RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		// logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
				XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-AdHocSubProcess.drl"),
				ResourceType.DRL);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("OtherTask",
				workItemHandler2);
		ProcessInstance processInstance = ksession
				.startProcess("AdHocSubProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		DynamicNodeInstance dynamicContext = (DynamicNodeInstance)
			((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next();
		DynamicUtils.addDynamicWorkItem(dynamicContext, ksession, "OtherTask", new HashMap<String, Object>());
		WorkItem workItem = workItemHandler.getWorkItem();
		assertNull(workItem);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ksession.fireAllRules();
		workItem = workItemHandler.getWorkItem();
		assertNotNull(workItem);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceActive(processInstance.getId(), ksession);
		workItem = workItemHandler2.getWorkItem();
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testAdHocSubProcessAutoCompleteDynamicSubProcess() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		List<Process> processes = processReader
				.read(SimpleBPMNProcessTest.class
						.getResourceAsStream("/BPMN2-AdHocSubProcessAutoComplete.bpmn2"));
		assertNotNull(processes);
		assertEquals(1, processes.size());
		RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		// logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
				XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-AdHocSubProcess.drl"),
				ResourceType.DRL);
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-MinimalProcess.bpmn2"),
				ResourceType.BPMN2);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("OtherTask",
				workItemHandler2);
		ProcessInstance processInstance = ksession
				.startProcess("AdHocSubProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession.fireAllRules();
		DynamicNodeInstance dynamicContext = (DynamicNodeInstance)
			((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next();
		DynamicUtils.addDynamicSubProcess(dynamicContext, ksession, "Minimal", new HashMap<String, Object>());
		ksession = restoreSession(ksession, true);
		WorkItem workItem = workItemHandler.getWorkItem();
		assertNotNull(workItem);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
//		assertProcessInstanceActive(processInstance.getId(), ksession);
//		workItem = workItemHandler2.getWorkItem();
//		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testAdHocSubProcessAutoCompleteDynamicSubProcess2() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		List<Process> processes = processReader
				.read(SimpleBPMNProcessTest.class
						.getResourceAsStream("/BPMN2-AdHocSubProcessAutoComplete.bpmn2"));
		assertNotNull(processes);
		assertEquals(1, processes.size());
		RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		// logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
				XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-AdHocSubProcess.drl"),
				ResourceType.DRL);
		kbuilder.add(ResourceFactory
				.newClassPathResource("BPMN2-ServiceProcess.bpmn2"),
				ResourceType.BPMN2);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
				workItemHandler2);
		ProcessInstance processInstance = ksession
				.startProcess("AdHocSubProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession.fireAllRules();
		DynamicNodeInstance dynamicContext = (DynamicNodeInstance)
			((WorkflowProcessInstance) processInstance).getNodeInstances().iterator().next();
		DynamicUtils.addDynamicSubProcess(dynamicContext, ksession, "ServiceProcess", new HashMap<String, Object>());
		ksession = restoreSession(ksession, true);
		WorkItem workItem = workItemHandler.getWorkItem();
		assertNotNull(workItem);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceActive(processInstance.getId(), ksession);
		workItem = workItemHandler2.getWorkItem();
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
	public void testAdHocProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-AdHocProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("AdHocProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		logger.debug("Triggering node");
		ksession.signalEvent("Task1", null, processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);
		ksession.signalEvent("User1", null, processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);
		ksession.insert(new Person());
		ksession.signalEvent("Task3", null, processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testAdHocProcessDynamicTask() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-AdHocProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("AdHocProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		logger.debug("Triggering node");
		ksession.signalEvent("Task1", null, processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);
		TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("OtherTask",
				workItemHandler2);
		DynamicUtils.addDynamicWorkItem(processInstance, ksession, "OtherTask", new HashMap<String, Object>());
		WorkItem workItem = workItemHandler2.getWorkItem();
		assertNotNull(workItem);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		ksession.signalEvent("User1", null, processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);
		ksession.insert(new Person());
		ksession.signalEvent("Task3", null, processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testAdHocProcessDynamicSubProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-AdHocProcess.bpmn2", "BPMN2-MinimalProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("AdHocProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		logger.debug("Triggering node");
		ksession.signalEvent("Task1", null, processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);
		TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("OtherTask",
				workItemHandler2);
		DynamicUtils.addDynamicSubProcess(processInstance, ksession, "Minimal", new HashMap<String, Object>());
		ksession = restoreSession(ksession, true);
		ksession.signalEvent("User1", null, processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);
		ksession.insert(new Person());
		ksession.signalEvent("Task3", null, processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testIntermediateCatchEventSignal() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("IntermediateCatchEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		// now signal process instance
		ksession.signalEvent("MyMessage", "SomeValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testIntermediateCatchEventMessage() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventMessage.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("IntermediateCatchEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession, true);
		// now signal process instance
		ksession.signalEvent("Message-HelloMessage", "SomeValue",
				processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testIntermediateCatchEventTimerDuration() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("IntermediateCatchEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		// now wait for 1 second for timer to trigger
		Thread.sleep(1000);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
   public void testIntermediateCatchEventTimerDateISO() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDateISO.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        HashMap<String, Object> params = new HashMap<String, Object>();
        DateTime now = new DateTime(System.currentTimeMillis());
        now.plus(2000);
        params.put("date", now.toString());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(2000);
        ksession.fireAllRules();
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
	
   public void testIntermediateCatchEventTimerDurationISO() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDurationISO.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1.5 second for timer to trigger
        Thread.sleep(1500);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ksession.fireAllRules();
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

	public void testIntermediateCatchEventTimerCycle1() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle1.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("IntermediateCatchEvent");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		// now wait for 1 second for timer to trigger
		Thread.sleep(1000);
		ksession = restoreSession(ksession, true);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
   public void testIntermediateCatchEventTimerCycleISO() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleISO.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("timer")) {
                    list.add(event.getProcessInstance().getId());
                }
            }

        });
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        Thread.sleep(500);
        for (int i = 0; i < 5; i++) {
            ksession.fireAllRules();
            Thread.sleep(1000);
        }
        assertEquals(6, list.size());
    }

	public void testIntermediateCatchEventTimerCycle2() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle2.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new DoNothingWorkItemHandler());
		ProcessInstance processInstance = ksession
				.startProcess("IntermediateCatchEvent");
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
		ProcessInstance processInstance = ksession
				.startProcess("IntermediateCatchEvent");
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
		ProcessInstance processInstance = ksession
				.startProcess("ErrorEndEvent");
		assertProcessInstanceAborted(processInstance.getId(), ksession);
		assertEquals("error", ((org.jbpm.process.instance.ProcessInstance)processInstance).getOutcome());
	}

	public void testEscalationEndEventProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-EscalationEndEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession
				.startProcess("EscalationEndEvent");
		assertProcessInstanceAborted(processInstance.getId(), ksession);
	}

	public void testEscalationIntermediateThrowEventProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventEscalation.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession
				.startProcess("EscalationIntermediateThrowEvent");
		assertProcessInstanceAborted(processInstance.getId(), ksession);
	}

	public void testCompensateIntermediateThrowEventProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventCompensate.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession
				.startProcess("CompensateIntermediateThrowEvent");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testCompensateEndEventProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-CompensateEndEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession
				.startProcess("CompensateEndEvent");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "StartProcess", "Task", "CompensateEvent", "CompensateEvent2", "Compensate", "EndEvent");
	}

	public void testServiceTask() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ServiceProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
				new ServiceTaskHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("s", "john");
		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
				.startProcess("ServiceProcess", params);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		assertEquals("Hello john!", processInstance.getVariable("s"));
	}

	public void testSendTask() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-SendTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
				new SendTaskHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("s", "john");
		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
				.startProcess("SendTask", params);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}

	public void testReceiveTask() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-ReceiveTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Receive Task",
				receiveTaskHandler);
		WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
				.startProcess("ReceiveTask");
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
	
    public void testTimerStartCycleISO() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerStartISO.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Thread.sleep(250);
        assertEquals(0, list.size());
        for (int i = 0; i < 6; i++) {
            ksession.fireAllRules();
            Thread.sleep(1000);
        }
        assertEquals(6, list.size());
    }	
//Following test is commented out since it relies on fixed date 
//and it cannot be given as variable as timer should start the process
    
//    public void testTimerStartDate() throws Exception {
//        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerStartDate.bpmn2");
//        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
//        final List<Long> list = new ArrayList<Long>();
//        ksession.addEventListener(new DefaultProcessEventListener() {
//            public void afterProcessStarted(ProcessStartedEvent event) {
//                list.add(event.getProcessInstance().getId());
//            }
//        });
//        Thread.sleep(250);
//        assertEquals(0, list.size());
//        assertEquals(0, list.size());
//        ksession.fireAllRules();
//
//        Thread.sleep(40 * 1000);
//        assertEquals(1, list.size());
//    }
	
   public void testTimerStartDuration() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerStartDuration.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Thread.sleep(250);
        assertEquals(0, list.size());
        ksession.fireAllRules();

        Thread.sleep(3000);

        assertEquals(1, list.size());
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
		ksession.signalEvent("MySignal", "NewValue");
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
		ksession.signalEvent("Message-HelloMessage", "NewValue");
	}

	public void testMessageEnd() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-MessageEndEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
				new SendTaskHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "MyValue");
		ProcessInstance processInstance = ksession.startProcess(
				"MessageEndEvent", params);
		assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());
	}

	public void testMessageIntermediateThrow() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
				new SendTaskHandler());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "MyValue");
		ProcessInstance processInstance = ksession.startProcess(
				"MessageIntermediateEvent", params);
		assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());
	}

	public void testSignalIntermediateThrow() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventSignal.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "MyValue");
		ProcessInstance processInstance = ksession.startProcess(
				"SignalIntermediateEvent", params);
		assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());
	}

	public void testNoneIntermediateThrow() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventNone.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess(
				"NoneIntermediateEvent", null);
		assertEquals(ProcessInstance.STATE_COMPLETED,
				processInstance.getState());
	}

	public void testXpathExpression() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-XpathExpression.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(
						"<instanceMetadata><user approved=\"false\" /></instanceMetadata>"
								.getBytes()));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceMetadata", document);
		ProcessInstance processInstance = ksession.startProcess("XPathProcess",
				params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testOnEntryExitScript() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-OnEntryExitScriptProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new SystemOutWorkItemHandler());
		List<String> myList = new ArrayList<String>();
		ksession.setGlobal("list", myList);
		ProcessInstance processInstance = ksession
				.startProcess("OnEntryExitScriptProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals(4, myList.size());
	}
	
	public void testOnEntryExitNamespacedScript() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-OnEntryExitNamespacedScriptProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new SystemOutWorkItemHandler());
		List<String> myList = new ArrayList<String>();
		ksession.setGlobal("list", myList);
		ProcessInstance processInstance = ksession
				.startProcess("OnEntryExitScriptProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals(4, myList.size());
	}
	
	public void testOnEntryExitMixedNamespacedScript() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-OnEntryExitMixedNamespacedScriptProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new SystemOutWorkItemHandler());
		List<String> myList = new ArrayList<String>();
		ksession.setGlobal("list", myList);
		ProcessInstance processInstance = ksession
				.startProcess("OnEntryExitScriptProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals(4, myList.size());
	}
	
	public void testOnEntryExitScriptDesigner() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-OnEntryExitDesignerScriptProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
				new SystemOutWorkItemHandler());
		List<String> myList = new ArrayList<String>();
		ksession.setGlobal("list", myList);
		ProcessInstance processInstance = ksession
				.startProcess("OnEntryExitScriptProcess");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		assertEquals(4, myList.size());
	}
	
	public void testXORGateway() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-gatewayTest.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(
						"<instanceMetadata><user approved=\"false\" /></instanceMetadata>"
								.getBytes()));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceMetadata", document);
		params.put(
				"startMessage",
				DocumentBuilderFactory
						.newInstance()
						.newDocumentBuilder()
						.parse(new ByteArrayInputStream(
								"<task subject='foobar2'/>".getBytes()))
						.getFirstChild());
		ProcessInstance processInstance = ksession.startProcess("process",
				params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}

	public void testDataInputAssociations() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {
					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						assertEquals("hello world",
								workItem.getParameter("coId"));
					}
				});
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream("<user hello='hello world' />"
						.getBytes()));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceMetadata", document.getFirstChild());
		ProcessInstance processInstance = ksession.startProcess("process",
				params);
	}

	public void testDataInputAssociationsWithStringObject() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations-string-object.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						assertEquals("hello", workItem.getParameter("coId"));
					}

				});
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceMetadata", "hello");
		ProcessInstance processInstance = ksession.startProcess("process",
				params);
	}

	public void FIXMEtestDataInputAssociationsWithLazyLoading()
			throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations-lazy-creating.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						assertEquals("mydoc", ((Element) workItem
								.getParameter("coId")).getNodeName());
						assertEquals("mynode", ((Element) workItem
								.getParameter("coId")).getFirstChild()
								.getNodeName());
						assertEquals("user",
								((Element) workItem.getParameter("coId"))
										.getFirstChild().getFirstChild()
										.getNodeName());
						assertEquals("hello world",
								((Element) workItem.getParameter("coId"))
										.getFirstChild().getFirstChild()
										.getAttributes().getNamedItem("hello")
										.getNodeValue());
					}

				});
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream("<user hello='hello world' />"
						.getBytes()));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceMetadata", document.getFirstChild());
		ProcessInstance processInstance = ksession.startProcess("process",
				params);
	}

	public void FIXMEtestDataInputAssociationsWithString() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations-string.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						assertEquals("hello", workItem.getParameter("coId"));
					}

				});
		ProcessInstance processInstance = ksession
				.startProcess("process", null);
	}

	public void testDataInputAssociationsWithStringWithoutQuotes()
			throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations-string-no-quotes.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						assertEquals("hello", workItem.getParameter("coId"));
					}

				});
		ProcessInstance processInstance = ksession
				.startProcess("process", null);
	}

	public void testDataInputAssociationsWithXMLLiteral() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations-xml-literal.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						assertEquals("id", ((org.w3c.dom.Node) workItem
								.getParameter("coId")).getNodeName());
						assertEquals("some text", ((org.w3c.dom.Node) workItem
								.getParameter("coId")).getFirstChild()
								.getTextContent());
					}

				});
		ProcessInstance processInstance = ksession
				.startProcess("process", null);
	}

	public void FIXMEtestDataInputAssociationsWithTwoAssigns() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations-two-assigns.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						assertEquals("foo", ((Element) workItem
								.getParameter("Comment")).getNodeName());
						// assertEquals("mynode", ((Element)
						// workItem.getParameter("Comment")).getFirstChild().getNodeName());
						// assertEquals("user", ((Element)
						// workItem.getParameter("Comment")).getFirstChild().getFirstChild().getNodeName());
						// assertEquals("hello world", ((Element)
						// workItem.getParameter("coId")).getFirstChild().getFirstChild().getAttributes().getNamedItem("hello").getNodeValue());
					}

				});
		Document document = DocumentBuilderFactory
				.newInstance()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream("<user hello='hello world' />"
						.getBytes()));
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("instanceMetadata", document.getFirstChild());
		ProcessInstance processInstance = ksession.startProcess("process",
				params);
	}

	public void testDataOutputAssociationsforHumanTask() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataOutputAssociations-HumanTask.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						DocumentBuilderFactory factory = DocumentBuilderFactory
								.newInstance();
						DocumentBuilder builder;
						try {
							builder = factory.newDocumentBuilder();
						} catch (ParserConfigurationException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							throw new RuntimeException(e);
						}
						final Map<String, Object> results = new HashMap<String, Object>();

						// process metadata
						org.w3c.dom.Document processMetadaDoc = builder
								.newDocument();
						org.w3c.dom.Element processMetadata = processMetadaDoc
								.createElement("previoustasksowner");
						processMetadaDoc.appendChild(processMetadata);
						// org.w3c.dom.Element procElement =
						// processMetadaDoc.createElement("previoustasksowner");
						processMetadata
								.setAttribute("primaryname", "my_result");
						// processMetadata.appendChild(procElement);
						results.put("output", processMetadata);

						mgr.completeWorkItem(workItem.getId(), results);
					}

				});
		Map<String, Object> params = new HashMap<String, Object>();
		ProcessInstance processInstance = ksession.startProcess("process",
				params);
	}

	public void testDataOutputAssociations() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataOutputAssociations.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						try {
							Document document = DocumentBuilderFactory
									.newInstance()
									.newDocumentBuilder()
									.parse(new ByteArrayInputStream(
											"<user hello='hello world' />"
													.getBytes()));
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("output", document.getFirstChild());
							mgr.completeWorkItem(workItem.getId(), params);
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}

					}

				});
		ProcessInstance processInstance = ksession
				.startProcess("process", null);
	}

	public void testDataOutputAssociationsXmlNode() throws Exception {
		KnowledgeBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataOutputAssociations-xml-node.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				new WorkItemHandler() {

					public void abortWorkItem(WorkItem manager,
							WorkItemManager mgr) {

					}

					public void executeWorkItem(WorkItem workItem,
							WorkItemManager mgr) {
						try {
							Document document = DocumentBuilderFactory
									.newInstance()
									.newDocumentBuilder()
									.parse(new ByteArrayInputStream(
											"<user hello='hello world' />"
													.getBytes()));
							Map<String, Object> params = new HashMap<String, Object>();
							params.put("output", document.getFirstChild());
							mgr.completeWorkItem(workItem.getId(), params);
						} catch (Throwable e) {
							throw new RuntimeException(e);
						}

					}

				});
		ProcessInstance processInstance = ksession
				.startProcess("process", null);
	}

	public void testLinkIntermediateEvent() throws Exception {

		KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateLinkEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession
				.startProcess("linkEventProcessExample");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);

	}

	public void testLinkEventCompositeProcess() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-LinkEventCompositeProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Composite");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
	}
	
	public void testBusinessRuleTask() throws Exception {
	    Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
	    resources.put("BPMN2-BusinessRuleTask.bpmn2", ResourceType.BPMN2);
	    resources.put("BPMN2-BusinessRuleTask.drl", ResourceType.DRL);
        KnowledgeBase kbase = createKnowledgeBase(resources);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-BusinessRuleTask");
        
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
	
   public void testBusinessRuleTaskDynamic() throws Exception {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        resources.put("BPMN2-BusinessRuleTaskDynamic.bpmn2", ResourceType.BPMN2);
        resources.put("BPMN2-BusinessRuleTask.drl", ResourceType.DRL);
        KnowledgeBase kbase = createKnowledgeBase(resources);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicrule", "MyRuleFlow");
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-BusinessRuleTask", params);
        
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }
	
   public void testBusinessRuleTaskWithDataInputs() throws Exception {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        resources.put("BPMN2-BusinessRuleTaskWithDataInputs.bpmn2", ResourceType.BPMN2);
        resources.put("BPMN2-BusinessRuleTaskWithDataInput.drl", ResourceType.DRL);
        KnowledgeBase kbase = createKnowledgeBase(resources);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", new Person());
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-BusinessRuleTask", params);
        
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

    public void testNullVariableInScriptTaskProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-NullVariableInScriptTaskProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance process = ksession.startProcess("nullVariableInScriptAfterTimer");

        assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());
       
        long sleep = 1000;
        logger.debug("Sleeping " + sleep/1000 + " seconds." );
        Thread.sleep(sleep);
        
        assertTrue(ProcessInstance.STATE_ABORTED == process.getState());
    }

    public void testIntermediateCatchEventConditionFilterByProcessInstance()throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventConditionFilterByProcessInstance.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String,Object> params1 = new HashMap<String,Object>();
        params1.put("personId", Long.valueOf(1L));
        Person person1 = new Person();
        person1.setId(1L);
        WorkflowProcessInstance pi1 = (WorkflowProcessInstance) ksession.createProcessInstance("IntermediateCatchEventConditionFilterByProcessInstance", params1);
        long pi1id = pi1.getId();
        
        ksession.insert(pi1);
        FactHandle personHandle1 = ksession.insert(person1);
        
        ksession.startProcessInstance(pi1.getId());
        
        Map<String,Object> params2 = new HashMap<String,Object>();
        params2.put("personId", Long.valueOf(2L));
        Person person2 = new Person();
        person2.setId(2L);
        
        WorkflowProcessInstance pi2 = (WorkflowProcessInstance) ksession.createProcessInstance("IntermediateCatchEventConditionFilterByProcessInstance", params2);
        long pi2id = pi2.getId();
        
        ksession.insert(pi2);
        FactHandle personHandle2 = ksession.insert(person2);
        
        ksession.startProcessInstance(pi2.getId());
        
        person1.setName("John");
        ksession.update(personHandle1, person1);
        
        
        
        assertNull("First process should be completed", ksession.getProcessInstance(pi1id));
        assertNotNull("Second process should NOT be completed", ksession.getProcessInstance(pi2id));

    }
    
	public void testConditionalBoundaryEventOnTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");
        
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "User Task", "Boundary event", "Condition met", "End2");
    }
	
	public void testConditionalBoundaryEventOnTaskComplete() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");
        
        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        Person person = new Person();
        person.setName("john");
        // as the node that boundary event is attached to has been completed insert will not have any effect
        ksession.insert(person);
        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "User Task", "User Task2", "End1");
    }
	
	public void testConditionalBoundaryEventOnTaskActiveOnStartup() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);
        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");
        
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "User Task", "Boundary event", "Condition met", "End2");
    }
	
	public void testConditionalBoundaryEventInterrupting() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ConditionalBoundaryEventInterrupting.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("ConditionalBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        
        ksession = restoreSession(ksession, true);
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello", "StartSubProcess",
                "Task", "BoundaryEvent", "Goodbye", "EndProcess");
    }
	
	public void testMessageBoundaryEventOnTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundaryMessageEventOnTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
       
        ProcessInstance processInstance = ksession.startProcess("BoundaryMessageOnTask");
        ksession.signalEvent("Message-HelloMessage", "message data");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "User Task", "Boundary event",
                "Condition met", "End2");
    }
	
	public void testMessageBoundaryEventOnTaskComplete() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundaryMessageEventOnTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
       
        ProcessInstance processInstance = ksession.startProcess("BoundaryMessageOnTask");
        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        ksession.signalEvent("Message-HelloMessage", "message data");
        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "User Task", "User Task2",
                "End1");
	}
	
	public void testMultipleStartEventsRegularStart() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("MultipleStartEvents");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    public void testMultipleStartEventsStartOnTimer() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Thread.sleep(500);
        assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
            ksession.fireAllRules();
            Thread.sleep(500);
        }
        assertEquals(5, list.size());
    }
    
    public void testMultipleEventBasedStartEventsSignalStart() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultipleEventBasedStartEventProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        
        ksession.signalEvent("startSignal", null);
        
        assertEquals(1, list.size());
        WorkItem workItem = workItemHandler.getWorkItem();
        long processInstanceId = ((WorkItemImpl) workItem).getProcessInstanceId();
        
        ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
        ksession = restoreSession(ksession, true);
       
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    public void testMultipleEventBasedStartEventsStartOnTimer() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultipleEventBasedStartEventProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Thread.sleep(500);
        assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
            ksession.fireAllRules();
            Thread.sleep(500);
        }
        assertEquals(5, list.size());
    }
    
    public void testMultipleInOutgoingSequenceFlows() throws Exception {
    	System.setProperty("jbpm.enable.multi.con", "true");
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultipleInOutgoingSequenceFlows.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        
        assertEquals(0, list.size());
        
        ksession.fireAllRules();
        Thread.sleep(1500);
         
        assertEquals(1, list.size());
        System.clearProperty("jbpm.enable.multi.con");
    }
    
    public void testConditionalFlow() throws Exception {
    	System.setProperty("jbpm.enable.multi.con", "true");
        String processId = "designer.conditional-flow";
        
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ConditionalFlowWithoutGateway.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        
        WorkflowProcessInstance wpi = (WorkflowProcessInstance) ksession.startProcess(processId);

        assertProcessInstanceCompleted(wpi.getId(), ksession);
        assertNodeTriggered(wpi.getId(), "start", "script", "end1");
        System.clearProperty("jbpm.enable.multi.con");
    }
    
    public void testMultipleInOutgoingSequenceFlowsDisable() throws Exception {

    	try {
	        KnowledgeBase kbase = createKnowledgeBase("BPMN2-MultipleInOutgoingSequenceFlows.bpmn2");
	        createKnowledgeSession(kbase);
	        
	        fail("Should fail as multiple outgoing and incoming connections are disabled by default");
    	} catch (Exception e) {
			assertEquals("This type of node cannot have more than one outgoing connection!", e.getMessage());
		}

    }
    
    public void testIntermediateCatchEventTimerCycleWithError() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance.getId(), ksession);
        ((WorkflowProcessInstance)ksession.getProcessInstance(processInstance.getId())).setVariable("x", 0);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance.getId(), ksession);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        Integer xValue = (Integer) ((WorkflowProcessInstance)processInstance).getVariable("x");
        assertEquals(new Integer(2), xValue);
        
        ksession.abortProcessInstance(processInstance.getId());
        assertProcessInstanceAborted(processInstance.getId(), ksession);
    }
    
    public void testScriptTaskWithVariableByName() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("myVar", "test");
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ProcessWithVariableName.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("BPMN2-ProcessWithVariableName", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

    public void testCallActivityWithBoundaryEvent() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(ResourceFactory
                .newClassPathResource("BPMN2-CallActivityWithBoundaryEvent.bpmn2"),
                ResourceType.BPMN2);
        kbuilder.add(ResourceFactory
                .newClassPathResource("BPMN2-CallActivitySubProcessWithBoundaryEvent.bpmn2"),
                ResourceType.BPMN2);
        if (!kbuilder.getErrors().isEmpty()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                logger.error(error.toString());
            }
            throw new IllegalArgumentException(
                    "Errors while parsing knowledge base");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        ProcessInstance processInstance = ksession.startProcess(
                "ParentProcess", params);
        
        Thread.sleep(3000);
        
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals("new timer value",
                ((WorkflowProcessInstance) processInstance).getVariable("y"));
        assertNodeTriggered(processInstance.getId(), "StartProcess", "CallActivity", "Boundary event", 
                "Script Task", "end", "StartProcess2", "User Task");

    }
    
    public void testUserTaskWithBooleanOutput() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-UserTaskWithBooleanOutput.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("com.sample.boolean");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("isCheckedCheckbox", "true");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), output);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

	private KnowledgeBase createKnowledgeBase(String process) throws Exception {
		KnowledgeBaseFactory
				.setKnowledgeBaseServiceFactory(new KnowledgeBaseFactoryServiceImpl());
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNExtensionsSemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		List<Process> processes = processReader
				.read(SimpleBPMNProcessTest.class.getResourceAsStream("/"
						+ process));
		for (Process p : processes) {
			RuleFlowProcess ruleFlowProcess = (RuleFlowProcess) p;
			logger.debug(XmlBPMNProcessDumper.INSTANCE
					.dump(ruleFlowProcess));
			kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
					XmlBPMNProcessDumper.INSTANCE.dump(ruleFlowProcess))),
					ResourceType.BPMN2);
		}
		kbuilder.add(ResourceFactory
				.newReaderResource(new InputStreamReader(
						SimpleBPMNProcessTest.class.getResourceAsStream("/"
								+ process))), ResourceType.BPMN2);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
				System.out.println(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

	private KnowledgeBase createKnowledgeBaseWithoutDumper(String process)
			throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNDISemanticModule());
		((PackageBuilderConfiguration) conf)
				.addSemanticModule(new BPMNExtensionsSemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
				((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder(conf);
		kbuilder.add(ResourceFactory
				.newReaderResource(new InputStreamReader(
						SimpleBPMNProcessTest.class.getResourceAsStream("/"
								+ process))), ResourceType.BPMN2);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
			}
			throw new IllegalArgumentException(
					"Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

}
