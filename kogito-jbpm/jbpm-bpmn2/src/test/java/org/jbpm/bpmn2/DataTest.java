/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.xml.ProcessHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DataTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };

    private static final Logger logger = LoggerFactory.getLogger(DataTest.class);

    private StatefulKnowledgeSession ksession;
    
    public DataTest(boolean persistence) {
        super(persistence);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    @After
    public void dispose() {
        if (ksession != null) {
            ksession.dispose();
            ksession = null;
        }
    }

    @Test
    public void testImport() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-Import.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Import");
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testDataObject() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-DataObject.bpmn2");
        ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "UserId-12345");
        ProcessInstance processInstance = ksession.startProcess("Evaluation",
                params);
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testDataStore() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-DataStore.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testAssociation() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-Association.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Evaluation");
        List<Association> associations = (List<Association>) processInstance.getProcess().getMetaData().get(ProcessHandler.ASSOCIATIONS);
        assertNotNull(associations);
        assertTrue(associations.size() == 1);
        Association assoc = associations.get(0);
        assertEquals("_1234", assoc.getId());
        assertEquals("_1", assoc.getSourceRef());
        assertEquals("_2", assoc.getTargetRef());
        
    }

    @Test
    public void testEvaluationProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler(
                "RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "UserId-12345");
        ProcessInstance processInstance = ksession.startProcess("Evaluation",
                params);
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testEvaluationProcess2() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess2.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "UserId-12345");
        ProcessInstance processInstance = ksession.startProcess(
                "com.sample.evaluation", params);
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testEvaluationProcess3() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess3.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler(
                "RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "john2");
        ProcessInstance processInstance = ksession.startProcess("Evaluation",
                params);
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testXpathExpression() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-XpathExpression.bpmn2");
        ksession = createKnowledgeSession(kbase);
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
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testDataInputAssociations() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testDataInputAssociationsWithStringObject() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations-string-object.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    /**
     * TODO testDataInputAssociationsWithLazyLoading
     */
    @Test
    @Ignore
    public void testDataInputAssociationsWithLazyLoading()
            throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-DataInputAssociations-lazy-creating.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new WorkItemHandler() {

                    public void abortWorkItem(WorkItem manager,
                            WorkItemManager mgr) {

                    }

                    public void executeWorkItem(WorkItem workItem,
                            WorkItemManager mgr) {
                        Object coIdParamObj = workItem.getParameter("coId");
                        assertEquals("mydoc", ((Element) coIdParamObj).getNodeName());
                        assertEquals("mynode", ((Element) workItem.getParameter("coId")).getFirstChild().getNodeName());
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

    @Test
    public void testDataInputAssociationsWithString() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-DataInputAssociations-string.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testDataInputAssociationsWithStringWithoutQuotes()
            throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-DataInputAssociations-string-no-quotes.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testDataInputAssociationsWithXMLLiteral() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataInputAssociations-xml-literal.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    /**
     * TODO testDataInputAssociationsWithTwoAssigns
     */
    @Test
    @Ignore
    public void testDataInputAssociationsWithTwoAssigns() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-DataInputAssociations-two-assigns.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testDataOutputAssociationsforHumanTask() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataOutputAssociations-HumanTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testDataOutputAssociations() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataOutputAssociations.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testDataOutputAssociationsXmlNode() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-DataOutputAssociations-xml-node.bpmn2");
        ksession = createKnowledgeSession(kbase);
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

}
