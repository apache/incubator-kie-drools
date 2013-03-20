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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.drools.compiler.compiler.PackageBuilderConfiguration;
import org.drools.core.impl.KnowledgeBaseFactoryServiceImpl;
import org.drools.core.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNExtensionsSemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import org.jbpm.workflow.instance.node.ForEachNodeInstance.ForEachJoinNodeInstance;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.api.definition.process.Process;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SimpleBPMNProcessTest extends JbpmBpmn2TestCase {

    private Logger logger = LoggerFactory.getLogger(SimpleBPMNProcessTest.class);
   
    protected void setUp() { 
		String testName = getName();
		String[] testFailsWithPersistence = {

		};
		for (String testNameBegin : testFailsWithPersistence) {
			if (testName.startsWith(testNameBegin)) {
				persistence = false;
			}
		}
        super.setUp();
    }

	public void testImport() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-Import.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		ProcessInstance processInstance = ksession.startProcess("Import");
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		ksession.dispose();
	}
	
	public void testDataObject() throws Exception {
		KnowledgeBase kbase = createKnowledgeBase("BPMN2-DataObject.bpmn2");
		StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("employee", "UserId-12345");
		ProcessInstance processInstance = ksession.startProcess("Evaluation",
				params);
		assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
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
		ksession.dispose();
	}
    
	private KnowledgeBase createKnowledgeBase(String process) throws Exception {
		KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new KnowledgeBaseFactoryServiceImpl());
		KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
		((PackageBuilderConfiguration) conf).initSemanticModules();
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNExtensionsSemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
		List<Process> processes = processReader.read(SimpleBPMNProcessTest.class.getResourceAsStream("/" + process));
		for (Process p : processes) {
			RuleFlowProcess ruleFlowProcess = (RuleFlowProcess) p;
			logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(ruleFlowProcess));
			kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
					XmlBPMNProcessDumper.INSTANCE.dump(ruleFlowProcess))),
					ResourceType.BPMN2);
		}
		kbuilder.add(ResourceFactory.newReaderResource(new InputStreamReader(SimpleBPMNProcessTest.class.getResourceAsStream("/" + process))), ResourceType.BPMN2);
		if (!kbuilder.getErrors().isEmpty()) {
			for (KnowledgeBuilderError error : kbuilder.getErrors()) {
				logger.error(error.toString());
				System.out.println(error.toString());
			}
			throw new IllegalArgumentException("Errors while parsing knowledge base");
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
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNSemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNDISemanticModule());
		((PackageBuilderConfiguration) conf).addSemanticModule(new BPMNExtensionsSemanticModule());
		// ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
		XmlProcessReader processReader = new XmlProcessReader(((PackageBuilderConfiguration) conf).getSemanticModules(),
				getClass().getClassLoader());
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(conf);
		kbuilder.add(ResourceFactory.newReaderResource(new InputStreamReader(
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
