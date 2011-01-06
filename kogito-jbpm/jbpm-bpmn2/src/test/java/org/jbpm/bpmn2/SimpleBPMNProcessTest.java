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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.definition.process.Process;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessStartedEvent;
import org.drools.event.process.ProcessVariableChangedEvent;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.process.core.datatype.impl.type.ObjectDataType;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.jbpm.JbpmTestCase;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.ruleflow.core.RuleFlowProcess;

import bitronix.tm.TransactionManagerServices;
import bitronix.tm.resource.jdbc.PoolingDataSource;

import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;

public class SimpleBPMNProcessTest extends JbpmTestCase {

	private PoolingDataSource ds1;
	private EntityManagerFactory emf;
	private static Server h2Server;
    
    static {
    	try {
			DeleteDbFiles.execute("", "JPADroolsFlow", true);
			h2Server = Server.createTcpServer(new String[0]);
			h2Server.start();
		} catch (SQLException e) {
			throw new RuntimeException("can't start h2 server db",e);
		}
    }
    
    protected void setUp() {
    	ds1 = new PoolingDataSource();
        ds1.setClassName("bitronix.tm.resource.jdbc.lrc.LrcXADataSource");
    	ds1.setUniqueName("jdbc/testDS1");
    	ds1.setMaxPoolSize(5);
    	ds1.setAllowLocalTransactions(true);
    	ds1.getDriverProperties().setProperty("driverClassName", "org.h2.Driver");
    	ds1.getDriverProperties().setProperty("url", "jdbc:h2:tcp://localhost/JPADroolsFlow");
    	ds1.getDriverProperties().setProperty("user", "sa");
    	ds1.getDriverProperties().setProperty("password", "");
        ds1.init();
        emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );
    }

    protected void tearDown() {
    	emf.close();
        ds1.close();
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

    public void testDataStore() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-DataStore.xml");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Evaluation");
        Definitions def = (Definitions) processInstance.getProcess().getMetaData().get("Definitions");
        assertNotNull(def.getDataStores());
        assertTrue(def.getDataStores().size() == 1);
        DataStore dataStore = def.getDataStores().get(0);
        assertEquals("employee", dataStore.getId());
        assertEquals("employeeStore", dataStore.getName());
        assertEquals(String.class.getCanonicalName(), ((ObjectDataType) dataStore.getType()).getClassName());
    }

    public void testAssociation() throws Exception {
    	KnowledgeBase kbase = createKnowledgeBase("BPMN2-Association.xml");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Evaluation");
        Definitions def = (Definitions) processInstance.getProcess().getMetaData().get("Definitions");
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
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.startProcess("UserProcess");
        // we can't test further as user tasks are asynchronous.
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
        ksession = restoreSession(ksession);
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
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("ActorId", "mary");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        ksession = restoreSession(ksession);
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
		ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		// No
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession);
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
		ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		// No
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession);
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
		ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession);
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
		ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		Thread.sleep(800);
		ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.fireAllRules();
        ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Timer
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		Thread.sleep(800);
		ksession = restoreSession(ksession);
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
		ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		// Condition
		processInstance = ksession.startProcess("com.sample.test");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
		ksession = restoreSession(ksession);
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
		ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Message-YesMessage", "YesValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		ksession = restoreSession(ksession);
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
		ksession = restoreSession(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
		receiveTaskHandler.setKnowledgeRuntime(ksession);
		ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
		ksession = restoreSession(ksession);
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

    public void testTimerBoundaryEvent() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEvent.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        Thread.sleep(1000);
        ksession = restoreSession(ksession);
        System.out.println("Firing timer");
        ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testTimerBoundaryEventInterrupting() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterrupting.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        Thread.sleep(1000);
        ksession = restoreSession(ksession);
        System.out.println("Firing timer");
        ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testAdHocSubProcess() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
//        ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
		        ((PackageBuilderConfiguration) conf).getSemanticModules());
		List<Process> processes = processReader.read(SimpleBPMNProcessTest.class.getResourceAsStream("/BPMN2-AdHocSubProcess.bpmn2"));
		assertNotNull(processes);
		assertEquals(1, processes.size());
		RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		assertNotNull(p);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
//		System.out.println(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(
            new StringReader(XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-AdHocSubProcess.drl"), ResourceType.DRL);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error: kbuilder.getErrors()) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNull(workItem);
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.fireAllRules();
        System.out.println("Signaling Hello2");
        ksession.signalEvent("Hello2", null, processInstance.getId());
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
    }

    public void testAdHocSubProcessAutoComplete() throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
//      ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
	        ((PackageBuilderConfiguration) conf).getSemanticModules());
		List<Process> processes = processReader.read(SimpleBPMNProcessTest.class.getResourceAsStream("/BPMN2-AdHocSubProcessAutoComplete.bpmn2"));
        assertNotNull(processes);
        assertEquals(1, processes.size());
        RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
//		System.out.println(XmlBPMNProcessDumper.INSTANCE.dump(p));
		kbuilder.add(ResourceFactory.newReaderResource(
            new StringReader(XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
		kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-AdHocSubProcess.drl"), ResourceType.DRL);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error: kbuilder.getErrors()) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Errors while parsing knowledge base");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNull(workItem);
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.fireAllRules();
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    public void testAdHocProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-AdHocProcess.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("AdHocProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession);
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
		ksession = restoreSession(ksession);
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
        ksession = restoreSession(ksession);
        // now signal process instance
        ksession.signalEvent("Message-HelloMessage", "SomeValue", processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testIntermediateCatchEventTimer() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimer.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.fireAllRules();
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    public void testIntermediateCatchEventCondition() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession);
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
        ksession = restoreSession(ksession);
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
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(new ByteArrayInputStream(
                "<instanceMetadata><user approved=\"false\" id=\"58735964413\"/></instanceMetadata>".getBytes()));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("instanceMetadata", document);

                ProcessInstance processInstance = ksession.startProcess("594975243920585248", params);
                assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
    }

    
	private KnowledgeBase createKnowledgeBase(String process) throws Exception {
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNExtensionsSemanticModule());
//		ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(
	        ((PackageBuilderConfiguration) conf).getSemanticModules());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
		List<Process> processes = processReader.read(SimpleBPMNProcessTest.class.getResourceAsStream("/" + process));
		for (Process p : processes) {
		    RuleFlowProcess ruleFlowProcess = (RuleFlowProcess)p;
		    kbuilder.add(ResourceFactory.newReaderResource(
		            new StringReader(XmlBPMNProcessDumper.INSTANCE.dump(ruleFlowProcess))), ResourceType.BPMN2);
		}
//		System.out.println(XmlBPMNProcessDumper.INSTANCE.dump(p));
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
	
	private StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {
//		return kbase.newStatefulKnowledgeSession();
	    Environment env = KnowledgeBaseFactory.newEnvironment();
	    env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
	    env.set(EnvironmentName.TRANSACTION_MANAGER,
	        TransactionManagerServices.getTransactionManager());
		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
		return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);
	}
	
	private StatefulKnowledgeSession restoreSession(StatefulKnowledgeSession ksession) {
//		return ksession;
		int id = ksession.getId();
		KnowledgeBase kbase = ksession.getKnowledgeBase();
		Environment env = ksession.getEnvironment();
		KnowledgeSessionConfiguration config = ksession.getSessionConfiguration();
		return JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase, config, env);
	}
	
	private void assertProcessInstanceCompleted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	private void assertProcessInstanceAborted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	private void assertProcessInstanceActive(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNotNull(ksession.getProcessInstance(processInstanceId));
	}
	
	private static class TestWorkItemHandler implements WorkItemHandler {
	    private WorkItem workItem;
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            this.workItem = workItem;
        }
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
        public WorkItem getWorkItem() {
            WorkItem result = this.workItem;
            this.workItem = null;
            return result;
        }
        
	}
}
