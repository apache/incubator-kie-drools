/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.bpmn2;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jbpm.bpmn2.core.Association;
import org.jbpm.bpmn2.core.DataStore;
import org.jbpm.bpmn2.core.Definitions;
import org.jbpm.bpmn2.xml.ProcessHandler;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemHandler;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataTest extends JbpmBpmn2TestCase {

    @Test
    public void testImport() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-Import.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Import");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testDataObject() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataObject.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation",
                params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testDataStore() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataStore.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation");
        Definitions def = (Definitions) processInstance.getProcess()
                .getMetaData().get("Definitions");
        assertNotNull(def.getDataStores());
        assertEquals(1, def.getDataStores().size());
        DataStore dataStore = def.getDataStores().get(0);
        assertEquals("employee", dataStore.getId());
        assertEquals("employeeStore", dataStore.getName());
        assertEquals(String.class.getCanonicalName(),
                ((ObjectDataType) dataStore.getType()).getClassName());

    }

    @Test
    public void testAssociation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-Association.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation");
        List<Association> associations = (List<Association>) processInstance.getProcess().getMetaData().get(ProcessHandler.ASSOCIATIONS);
        assertNotNull(associations);
        assertEquals(1, associations.size());
        Association assoc = associations.get(0);
        assertEquals("_1234", assoc.getId());
        assertEquals("_1", assoc.getSourceRef());
        assertEquals("_2", assoc.getTargetRef());

    }

    @Test
    public void testEvaluationProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EvaluationProcess.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler(
                "RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation",
                params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testEvaluationProcess2() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EvaluationProcess2.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "UserId-12345");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.evaluation", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testEvaluationProcess3() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-EvaluationProcess3.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler(
                "RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("employee", "john2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Evaluation",
                params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testXpathExpression() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-XpathExpression.bpmn2");
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(
                        "<instanceMetadata><user approved=\"false\" /></instanceMetadata>"
                                .getBytes()));
        Map<String, Object> params = new HashMap<>();
        params.put("instanceMetadata", document);
        KogitoProcessInstance processInstance = kruntime.startProcess("XPathProcess",
                params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testDataInputAssociations() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataInputAssociations.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {
                    @Override
                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    @Override
                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
                        assertEquals("hello world",
                                workItem.getParameter("coId"));
                    }
                });
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream("<user hello='hello world' />"
                        .getBytes()));
        Map<String, Object> params = new HashMap<>();
        params.put("instanceMetadata", document.getFirstChild());
        KogitoProcessInstance processInstance = kruntime.startProcess("process",
                params);

    }

    @Test
    public void testDataInputAssociationsWithStringObject() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataInputAssociations-string-object.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    @Override
                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    @Override
                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
                        assertEquals("hello", workItem.getParameter("coId"));
                    }

                });
        Map<String, Object> params = new HashMap<>();
        params.put("instanceMetadata", "hello");
        KogitoProcessInstance processInstance = kruntime.startProcess("process",
                params);

    }

    /**
     * TODO testDataInputAssociationsWithLazyLoading
     */
    @Test
    @Disabled
    public void testDataInputAssociationsWithLazyLoading()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataInputAssociations-lazy-creating.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
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
        Map<String, Object> params = new HashMap<>();
        params.put("instanceMetadata", document.getFirstChild());
        KogitoProcessInstance processInstance = kruntime.startProcess("process",
                params);

    }

    @Test
    public void testDataInputAssociationsWithString() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataInputAssociations-string.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
                        assertEquals("hello", workItem.getParameter("coId"));
                    }

                });
        KogitoProcessInstance processInstance = kruntime
                .startProcess("process");

    }

    @Test
    public void testDataInputAssociationsWithStringWithoutQuotes()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataInputAssociations-string-no-quotes.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
                        assertEquals("hello", workItem.getParameter("coId"));
                    }

                });
        KogitoProcessInstance processInstance = kruntime
                .startProcess("process");

    }

    @Test
    public void testDataInputAssociationsWithXMLLiteral() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataInputAssociations-xml-literal.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
                        assertEquals("id", ((org.w3c.dom.Node) workItem
                                .getParameter("coId")).getNodeName());
                        assertEquals("some text", ((org.w3c.dom.Node) workItem
                                .getParameter("coId")).getFirstChild()
                                        .getTextContent());
                    }

                });
        KogitoProcessInstance processInstance = kruntime
                .startProcess("process");

    }

    /**
     * TODO testDataInputAssociationsWithTwoAssigns
     */
    @Test
    @Disabled
    public void testDataInputAssociationsWithTwoAssigns() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataInputAssociations-two-assigns.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
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
        Map<String, Object> params = new HashMap<>();
        params.put("instanceMetadata", document.getFirstChild());
        KogitoProcessInstance processInstance = kruntime.startProcess("process",
                params);

    }

    @Test
    public void testDataOutputAssociationsforHumanTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataOutputAssociations-HumanTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
                        DocumentBuilderFactory factory = DocumentBuilderFactory
                                .newInstance();
                        DocumentBuilder builder;
                        try {
                            builder = factory.newDocumentBuilder();
                        } catch (ParserConfigurationException e) {
                            throw new RuntimeException(e);
                        }
                        final Map<String, Object> results = new HashMap<>();

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

                        mgr.completeWorkItem(workItem.getStringId(), results);
                    }

                });
        Map<String, Object> params = new HashMap<>();
        KogitoProcessInstance processInstance = kruntime.startProcess("process",
                params);

    }

    @Test
    public void testDataOutputAssociations() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataOutputAssociations.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
                        try {
                            Document document = DocumentBuilderFactory
                                    .newInstance()
                                    .newDocumentBuilder()
                                    .parse(new ByteArrayInputStream(
                                            "<user hello='hello world' />"
                                                    .getBytes()));
                            Map<String, Object> params = new HashMap<>();
                            params.put("output", document.getFirstChild());
                            mgr.completeWorkItem(workItem.getStringId(), params);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }

                    }

                });
        KogitoProcessInstance processInstance = kruntime
                .startProcess("process");

    }

    @Test
    public void testDataOutputAssociationsXmlNode() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-DataOutputAssociations-xml-node.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new KogitoWorkItemHandler() {

                    public void abortWorkItem(KogitoWorkItem manager,
                            KogitoWorkItemManager mgr) {

                    }

                    public void executeWorkItem(KogitoWorkItem workItem,
                            KogitoWorkItemManager mgr) {
                        try {
                            Document document = DocumentBuilderFactory
                                    .newInstance()
                                    .newDocumentBuilder()
                                    .parse(new ByteArrayInputStream(
                                            "<user hello='hello world' />"
                                                    .getBytes()));
                            Map<String, Object> params = new HashMap<>();
                            params.put("output", document.getFirstChild());
                            mgr.completeWorkItem(workItem.getStringId(), params);
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }

                    }

                });
        KogitoProcessInstance processInstance = kruntime
                .startProcess("process");

    }

    @Test
    public void testDefaultProcessVariableValue() throws Exception {

        kruntime = createKogitoProcessRuntime("BPMN2-CorrelationKey.bpmn2");

        Map<String, Object> parameters = new HashMap<String, Object>();

        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime.startProcess("org.jbpm.test.functional.CorrelationKey",
                parameters);

        assertEquals("defaultProc", processInstance.getVariable("procVar"));
        assertEquals(1, processInstance.getVariable("intVar"));

    }

}
