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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.instance.context.variable.VariableScopeInstance;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.process.instance.impl.humantask.HumanTaskWorkItemImpl;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.jbpm.workflow.instance.impl.NodeInstanceImpl;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.CompositeContextNodeInstance;
import org.jbpm.workflow.instance.node.ForEachNodeInstance;
import org.jbpm.workflow.instance.node.ForEachNodeInstance.ForEachJoinNodeInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.internal.command.RegistryContext;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class FlowTest extends JbpmBpmn2TestCase {

    @BeforeAll
    public static void setup() throws Exception {
        VariableScope.setVariableStrictOption(true);
    }

    @AfterEach
    public void clearProperties() {
        System.clearProperty("jbpm.enable.multi.con");
    }

    @Test
    public void testExclusiveSplitWithNoConditions() throws Exception {
        try {
            createKogitoProcessRuntime("BPMN2-ExclusiveGatewayWithNoConditionsDefined.bpmn2");
            fail("Should fail as XOR gateway does not have conditions defined");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("does not have a constraint for Connection"));
        }

    }

    @Test
    public void testExclusiveSplit() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplit.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "First");
        params.put("y", "Second");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testExclusiveSplitXPathAdvanced() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitXPath-advanced.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
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
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testExclusiveSplitXPathAdvanced2() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitXPath-advanced-vars-not-signaled.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
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
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testExclusiveSplitXPathAdvancedWithVars() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitXPath-advanced-with-vars.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
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
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testExclusiveSplitPriority() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitPriority.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "First");
        params.put("y", "Second");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testExclusiveSplitDefault() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitDefault.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Email",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("x", "NotFirst");
        params.put("y", "Second");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testExclusiveXORGateway() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-gatewayTest.bpmn2");
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(
                        "<instanceMetadata><user approved=\"false\" /></instanceMetadata>"
                                .getBytes()));
        Map<String, Object> params = new HashMap<>();
        params.put("instanceMetadata", document);
        params.put(
                "startMessage",
                DocumentBuilderFactory
                        .newInstance()
                        .newDocumentBuilder()
                        .parse(new ByteArrayInputStream(
                                "<task subject='foobar2'/>".getBytes()))
                        .getFirstChild());
        KogitoProcessInstance processInstance = kruntime.startProcess("process",
                params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testInclusiveSplit() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplit.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("x", 15);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testInclusiveSplitDefaultConnection() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveGatewayWithDefault.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("test", "c");
        KogitoProcessInstance processInstance = kruntime.startProcess("InclusiveGatewayWithDefault", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testInclusiveSplitAndJoin() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoin.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 15);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(2, activeWorkItems.size());

        for (KogitoWorkItem wi : activeWorkItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveSplitAndJoinLoop() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoinLoop.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 21);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(3, activeWorkItems.size());

        for (KogitoWorkItem wi : activeWorkItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveSplitAndJoinLoop2() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoinLoop2.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 21);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(3, activeWorkItems.size());

        for (KogitoWorkItem wi : activeWorkItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveSplitAndJoinNested() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoinNested.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 15);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(2, activeWorkItems.size());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        for (KogitoWorkItem wi : activeWorkItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }

        activeWorkItems = workItemHandler.getWorkItems();
        assertEquals(2, activeWorkItems.size());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        for (KogitoWorkItem wi : activeWorkItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveSplitAndJoinEmbedded() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoinEmbedded.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 15);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(2, activeWorkItems.size());

        for (KogitoWorkItem wi : activeWorkItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveSplitAndJoinWithParallel() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoinWithParallel.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 25);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(4, activeWorkItems.size());

        for (KogitoWorkItem wi : activeWorkItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveSplitAndJoinWithEnd() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoinWithEnd.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 25);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(3, activeWorkItems.size());

        for (int i = 0; i < 2; i++) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(
                    activeWorkItems.get(i).getStringId(), null);
        }
        assertProcessInstanceActive(processInstance);

        kruntime.getKogitoWorkItemManager().completeWorkItem(
                activeWorkItems.get(2).getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    @Timeout(10000)
    public void testInclusiveSplitAndJoinWithTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoinWithTimer.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 15);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(1, activeWorkItems.size());
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                activeWorkItems.get(0).getStringId(), null);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        activeWorkItems = workItemHandler.getWorkItems();
        assertEquals(2, activeWorkItems.size());
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                activeWorkItems.get(0).getStringId(), null);
        assertProcessInstanceActive(processInstance);

        kruntime.getKogitoWorkItemManager().completeWorkItem(
                activeWorkItems.get(1).getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveSplitAndJoinExtraPath() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitAndJoinExtraPath.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("x", 25);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);

        kruntime.signalEvent("signal", null);

        List<KogitoWorkItem> activeWorkItems = workItemHandler.getWorkItems();

        assertEquals(4, activeWorkItems.size());

        for (int i = 0; i < 3; i++) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(
                    activeWorkItems.get(i).getStringId(), null);
        }
        assertProcessInstanceActive(processInstance);

        kruntime.getKogitoWorkItemManager().completeWorkItem(
                activeWorkItems.get(3).getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveSplitDefault() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveSplitDefault.bpmn2");
        Map<String, Object> params = new HashMap<>();
        params.put("x", -5);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "com.sample.test", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testInclusiveParallelExclusiveSplitNoLoop() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveNestedInParallelNestedInExclusive.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI2", new SystemOutWorkItemHandler() {

            @Override
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                Integer x = (Integer) workItem.getParameter("input1");
                x++;
                Map<String, Object> results = new HashMap<>();
                results.put("output1", x);
                manager.completeWorkItem(workItem.getStringId(), results);
            }

        });
        final Map<String, Integer> nodeInstanceExecutionCounter = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info(event.getNodeInstance().getNodeName());
                Integer value = nodeInstanceExecutionCounter.get(event.getNodeInstance().getNodeName());
                if (value == null) {
                    value = 0;
                }

                value++;
                nodeInstanceExecutionCounter.put(event.getNodeInstance().getNodeName(), value);
            }

        });
        Map<String, Object> params = new HashMap<>();
        params.put("x", 0);
        KogitoProcessInstance processInstance = kruntime.startProcess("Process_1", params);
        assertProcessInstanceCompleted(processInstance);

        assertEquals(12, nodeInstanceExecutionCounter.size());
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Start"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("XORGateway-converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("ANDGateway-diverging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("ORGateway-diverging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("testWI3"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("testWI2"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("ORGateway-converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Script"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("XORGateway-diverging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("ANDGateway-converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("testWI6"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("End"));
    }

    @Test
    public void testInclusiveParallelExclusiveSplitLoop() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveNestedInParallelNestedInExclusive.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI", new SystemOutWorkItemHandler());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI2", new SystemOutWorkItemHandler() {

            @Override
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                Integer x = (Integer) workItem.getParameter("input1");
                x++;
                Map<String, Object> results = new HashMap<>();
                results.put("output1", x);
                manager.completeWorkItem(workItem.getStringId(), results);
            }

        });
        final Map<String, Integer> nodeInstanceExecutionCounter = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                Integer value = nodeInstanceExecutionCounter.get(event.getNodeInstance().getNodeName());
                if (value == null) {
                    value = 0;
                }

                value++;
                nodeInstanceExecutionCounter.put(event.getNodeInstance().getNodeName(), value);
            }

        });
        Map<String, Object> params = new HashMap<>();
        params.put("x", -1);
        KogitoProcessInstance processInstance = kruntime.startProcess("Process_1", params);
        assertProcessInstanceCompleted(processInstance);

        assertEquals(12, nodeInstanceExecutionCounter.size());
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Start"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("XORGateway-converging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("ANDGateway-diverging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("ORGateway-diverging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("testWI3"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("testWI2"));
        assertEquals(4, (int) nodeInstanceExecutionCounter.get("ORGateway-converging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("Script"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("XORGateway-diverging"));
        assertEquals(4, (int) nodeInstanceExecutionCounter.get("ANDGateway-converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("testWI6"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("End"));
    }

    @Test
    public void testInclusiveParallelExclusiveSplitNoLoopAsync() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveNestedInParallelNestedInExclusive.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI", handler);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI2", new SystemOutWorkItemHandler() {

            @Override
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                Integer x = (Integer) workItem.getParameter("input1");
                x++;
                Map<String, Object> results = new HashMap<>();
                results.put("output1", x);
                manager.completeWorkItem(workItem.getStringId(), results);
            }

        });
        final Map<String, Integer> nodeInstanceExecutionCounter = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                Integer value = nodeInstanceExecutionCounter.get(event.getNodeInstance().getNodeName());
                if (value == null) {
                    value = new Integer(0);
                }

                value++;
                nodeInstanceExecutionCounter.put(event.getNodeInstance().getNodeName(), value);
            }

        });
        Map<String, Object> params = new HashMap<>();
        params.put("x", 0);
        KogitoProcessInstance processInstance = kruntime.startProcess("Process_1", params);
        assertProcessInstanceActive(processInstance);

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(2, workItems.size());
        // complete work items within OR gateway
        for (KogitoWorkItem KogitoWorkItem : workItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(KogitoWorkItem.getStringId(), null);
        }
        assertProcessInstanceActive(processInstance);

        workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(1, workItems.size());
        // complete last KogitoWorkItem after AND gateway
        for (KogitoWorkItem KogitoWorkItem : workItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(KogitoWorkItem.getStringId(), null);
        }
        assertProcessInstanceCompleted(processInstance);

        assertEquals(12, nodeInstanceExecutionCounter.size());
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Start"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("XORGateway-converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("ANDGateway-diverging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("ORGateway-diverging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("testWI3"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("testWI2"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("ORGateway-converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Script"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("XORGateway-diverging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("ANDGateway-converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("testWI6"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("End"));
    }

    @Test
    public void testInclusiveParallelExclusiveSplitLoopAsync() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveNestedInParallelNestedInExclusive.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI", handler);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI2", new SystemOutWorkItemHandler() {

            @Override
            public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                Integer x = (Integer) workItem.getParameter("input1");
                x++;
                Map<String, Object> results = new HashMap<>();
                results.put("output1", x);
                manager.completeWorkItem(workItem.getStringId(), results);
            }

        });
        final Map<String, Integer> nodeInstanceExecutionCounter = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                Integer value = nodeInstanceExecutionCounter.get(event.getNodeInstance().getNodeName());
                if (value == null) {
                    value = 0;
                }

                value++;
                nodeInstanceExecutionCounter.put(event.getNodeInstance().getNodeName(), value);
            }

        });
        Map<String, Object> params = new HashMap<>();
        params.put("x", -1);
        KogitoProcessInstance processInstance = kruntime.startProcess("Process_1", params);
        assertProcessInstanceActive(processInstance);

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(2, workItems.size());
        // complete work items within OR gateway
        for (KogitoWorkItem KogitoWorkItem : workItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(KogitoWorkItem.getStringId(), null);
        }
        assertProcessInstanceActive(processInstance);

        workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(2, workItems.size());
        // complete work items within OR gateway
        for (KogitoWorkItem KogitoWorkItem : workItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(KogitoWorkItem.getStringId(), null);
        }
        assertProcessInstanceActive(processInstance);

        workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(1, workItems.size());
        // complete last KogitoWorkItem after AND gateway
        for (KogitoWorkItem KogitoWorkItem : workItems) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(KogitoWorkItem.getStringId(), null);
        }
        assertProcessInstanceCompleted(processInstance);

        assertEquals(12, nodeInstanceExecutionCounter.size());
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Start"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("XORGateway-converging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("ANDGateway-diverging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("ORGateway-diverging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("testWI3"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("testWI2"));
        assertEquals(4, (int) nodeInstanceExecutionCounter.get("ORGateway-converging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("Script"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("XORGateway-diverging"));
        assertEquals(4, (int) nodeInstanceExecutionCounter.get("ANDGateway-converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("testWI6"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("End"));
    }

    @Test
    public void testInclusiveSplitNested() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveGatewayNested.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        TestWorkItemHandler handler2 = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI", handler);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI2", handler2);
        Map<String, Object> params = new HashMap<>();
        KogitoProcessInstance processInstance = kruntime.startProcess("Process_1", params);

        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler2.getWorkItem().getStringId(), null);
        assertProcessInstanceActive(processInstance);

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(2, workItems.size());

        for (KogitoWorkItem wi : workItems) {
            assertProcessInstanceActive(processInstance);
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testInclusiveSplitWithLoopInside() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveGatewayWithLoopInside.bpmn2");

        final Map<String, Integer> nodeInstanceExecutionCounter = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("{} {}", event.getNodeInstance().getNodeName(), ((NodeInstanceImpl) event.getNodeInstance()).getLevel());
                Integer value = nodeInstanceExecutionCounter.get(event.getNodeInstance().getNodeName());
                if (value == null) {
                    value = new Integer(0);
                }

                value++;
                nodeInstanceExecutionCounter.put(event.getNodeInstance().getNodeName(), value);
            }

        });
        TestWorkItemHandler handler = new TestWorkItemHandler();
        TestWorkItemHandler handler2 = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI", handler);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI2", handler2);
        Map<String, Object> params = new HashMap<>();
        params.put("x", -1);
        KogitoProcessInstance processInstance = kruntime.startProcess("Process_1", params);

        assertProcessInstanceActive(processInstance);
        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(2, workItems.size());

        for (KogitoWorkItem wi : workItems) {
            assertProcessInstanceActive(processInstance);
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }

        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler2.getWorkItem().getStringId(), null);
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler2.getWorkItem().getStringId(), null);
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(10, nodeInstanceExecutionCounter.size());
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Start"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("OR diverging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("tareaWorkflow3"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("tareaWorkflow2"));
        assertEquals(3, (int) nodeInstanceExecutionCounter.get("OR converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("tareaWorkflow6"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("Script"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("XOR diverging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("XOR converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("End"));
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testInclusiveSplitWithLoopInsideSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveGatewayWithLoopInsideSubprocess.bpmn2");

        final Map<String, Integer> nodeInstanceExecutionCounter = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("{} {}", event.getNodeInstance().getNodeName(), ((NodeInstanceImpl) event.getNodeInstance()).getLevel());
                Integer value = nodeInstanceExecutionCounter.get(event.getNodeInstance().getNodeName());
                if (value == null) {
                    value = 0;
                }

                value++;
                nodeInstanceExecutionCounter.put(event.getNodeInstance().getNodeName(), value);
            }

        });
        TestWorkItemHandler handler = new TestWorkItemHandler();
        TestWorkItemHandler handler2 = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI", handler);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("testWI2", handler2);
        Map<String, Object> params = new HashMap<>();
        params.put("x", -1);
        KogitoProcessInstance processInstance = kruntime.startProcess("Process_1", params);

        assertProcessInstanceActive(processInstance);
        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(2, workItems.size());

        for (KogitoWorkItem wi : workItems) {
            assertProcessInstanceActive(processInstance);
            kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
        }

        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler2.getWorkItem().getStringId(), null);
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler2.getWorkItem().getStringId(), null);
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(13, nodeInstanceExecutionCounter.size());
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Start"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Sub Process 1"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("sb-start"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("sb-end"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("OR diverging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("tareaWorkflow3"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("tareaWorkflow2"));
        assertEquals(3, (int) nodeInstanceExecutionCounter.get("OR converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("tareaWorkflow6"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("Script"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("XOR diverging"));
        assertEquals(2, (int) nodeInstanceExecutionCounter.get("XOR converging"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("End"));
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcessWithORGateway()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsProcessWithORgateway.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        Map<String, Object> params = new HashMap<>();
        List<Integer> myList = new ArrayList<>();
        myList.add(12);
        myList.add(15);
        params.put("list", myList);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MultiInstanceLoopCharacteristicsProcess", params);

        List<KogitoWorkItem> workItems = workItemHandler.getWorkItems();
        assertEquals(4, workItems.size());

        Collection<NodeInstance> nodeInstances = ((WorkflowProcessInstanceImpl) processInstance)
                .getNodeInstances();
        assertEquals(1, nodeInstances.size());
        NodeInstance nodeInstance = nodeInstances.iterator().next();
        assertTrue(nodeInstance instanceof ForEachNodeInstance);

        Collection<NodeInstance> nodeInstancesChild = ((ForEachNodeInstance) nodeInstance)
                .getNodeInstances();
        assertEquals(2, nodeInstancesChild.size());

        for (NodeInstance child : nodeInstancesChild) {
            assertTrue(child instanceof CompositeContextNodeInstance);
            assertEquals(2, ((CompositeContextNodeInstance) child)
                    .getNodeInstances().size());
        }

        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItems.get(0).getStringId(), null);
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItems.get(1).getStringId(), null);

        processInstance = kruntime.getProcessInstance(processInstance.getStringId());
        nodeInstances = ((WorkflowProcessInstanceImpl) processInstance)
                .getNodeInstances();
        assertEquals(1, nodeInstances.size());
        nodeInstance = nodeInstances.iterator().next();
        assertTrue(nodeInstance instanceof ForEachNodeInstance);

        nodeInstancesChild = ((ForEachNodeInstance) nodeInstance)
                .getNodeInstances();
        assertEquals(2, nodeInstancesChild.size());

        Iterator<NodeInstance> childIterator = nodeInstancesChild
                .iterator();

        assertTrue(childIterator.next() instanceof CompositeContextNodeInstance);
        assertTrue(childIterator.next() instanceof ForEachJoinNodeInstance);

        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItems.get(2).getStringId(), null);
        kruntime.getKogitoWorkItemManager().completeWorkItem(
                workItems.get(3).getStringId(), null);

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testInclusiveJoinWithLoopAndHumanTasks() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-InclusiveGatewayWithHumanTasksProcess.bpmn2");

        final Map<String, Integer> nodeInstanceExecutionCounter = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("{} {}", event.getNodeInstance().getNodeName(), ((NodeInstanceImpl) event.getNodeInstance()).getLevel());
                Integer value = nodeInstanceExecutionCounter.get(event.getNodeInstance().getNodeName());
                if (value == null) {
                    value = 0;
                }

                value++;
                nodeInstanceExecutionCounter.put(event.getNodeInstance().getNodeName(), value);
            }

        });
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<>();
        params.put("firstXor", true);
        params.put("secondXor", true);
        params.put("thirdXor", true);
        KogitoProcessInstance processInstance = kruntime.startProcess("InclusiveWithAdvancedLoop", params);
        // simulate completion of first task
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceActive(processInstance);
        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(2, workItems.size());

        KogitoWorkItem remainingWork = null;
        for (KogitoWorkItem wi : workItems) {
            assertProcessInstanceActive(processInstance);
            // complete second task that will trigger converging OR gateway
            if (wi.getParameter("NodeName").equals("HT Form2")) {
                kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);
            } else {
                remainingWork = wi;
            }
        }

        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(remainingWork.getStringId(), null);
        assertProcessInstanceActive(processInstance);
        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(13, nodeInstanceExecutionCounter.size());
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Start"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("HT Form1"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("and1"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("HT Form2"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("xor1"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("xor2"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("HT Form3"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Koniec"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("xor 3"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("HT Form4"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("xor4"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("Koniec2"));
        assertEquals(1, (int) nodeInstanceExecutionCounter.get("or1"));
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsProcess.bpmn2");
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MultiInstanceLoopCharacteristicsProcess", params);
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testMultiInstanceLoopNumberTest() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoop-Numbering.bpmn2");
        Map<String, Object> params = new HashMap<>();

        final Map<String, String> nodeIdNodeNameMap = new HashMap<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                NodeInstance nodeInstance = event.getNodeInstance();
                String uniqId = ((NodeInstanceImpl) nodeInstance).getUniqueId();
                String nodeName = nodeInstance.getNode().getName();

                String prevNodeName = nodeIdNodeNameMap.put(uniqId, nodeName);
                if (prevNodeName != null) {
                    assertEquals(uniqId + " is used for more than one node instance: ", prevNodeName, nodeName);
                }
            }

        });

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("Test.MultipleInstancesBug", params);

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        logger.debug("COMPLETING TASKS.");
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItems.remove(0).getStringId(), null);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItems.remove(0).getStringId(), null);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcess2() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceProcessWithOutputOnTask.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        List<String> myOutList = null;
        myList.add("John");
        myList.add("Mary");
        params.put("miinput", myList);

        KogitoProcessInstance processInstance = kruntime.startProcess("miprocess", params);
        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertNotNull(workItems);
        assertEquals(2, workItems.size());

        myOutList = (List<String>) kruntime.getKieSession().execute(new GetProcessVariableCommand(processInstance.getStringId(), "mioutput"));
        assertNull(myOutList);

        Map<String, Object> results = new HashMap<>();
        results.put("reply", "Hello John");
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItems.get(0).getStringId(), results);
        myOutList = (List<String>) kruntime.getKieSession().execute(new GetProcessVariableCommand(processInstance.getStringId(), "mioutput"));
        assertNull(myOutList);

        results = new HashMap<>();
        results.put("reply", "Hello Mary");
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItems.get(1).getStringId(), results);

        myOutList = (List<String>) kruntime.getKieSession().execute(new GetProcessVariableCommand(processInstance.getStringId(), "mioutput"));
        assertNotNull(myOutList);
        assertEquals(2, myOutList.size());
        assertTrue(myOutList.contains("Hello John"));
        assertTrue(myOutList.contains("Hello Mary"));

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcessWithOutput()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsProcessWithOutput.bpmn2");
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        List<String> myListOut = new ArrayList<>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        params.put("listOut", myListOut);
        assertEquals(0, myListOut.size());
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MultiInstanceLoopCharacteristicsProcessWithOutput", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(2, myListOut.size());

    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcessWithOutputCompletionCondition()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsProcessWithOutputCmpCond.bpmn2");
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        List<String> myListOut = new ArrayList<>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        params.put("listOut", myListOut);
        assertEquals(0, myListOut.size());
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MultiInstanceLoopCharacteristicsProcessWithOutput", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(1, myListOut.size());

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testMultiInstanceLoopCharacteristicsProcessWithOutputAndScripts()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsProcessWithOutputAndScripts.bpmn2");
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        List<String> myListOut = new ArrayList<>();
        List<String> scriptList = new ArrayList<>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        params.put("listOut", myListOut);
        params.put("scriptList", scriptList);
        assertEquals(0, myListOut.size());
        KogitoProcessInstance processInstance = kruntime.startProcess("MultiInstanceLoopCharacteristicsProcessWithOutput", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(2, myListOut.size());
        assertEquals(2, scriptList.size());

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testMultiInstanceLoopCharacteristicsTaskWithOutput()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsTaskWithOutput.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        List<String> myListOut = new ArrayList<>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        params.put("listOut", myListOut);
        assertEquals(0, myListOut.size());
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MultiInstanceLoopCharacteristicsTask", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(2, myListOut.size());

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testMultiInstanceLoopCharacteristicsTaskWithOutputCompletionCondition()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsTaskWithOutputCmpCond.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        List<String> myListOut = new ArrayList<>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        params.put("listOut", myListOut);
        assertEquals(0, myListOut.size());
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MultiInstanceLoopCharacteristicsTask", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(1, myListOut.size());

    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testMultiInstanceLoopCharacteristicsTaskWithOutputCompletionCondition2()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsTaskWithOutputCmpCond2.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        List<String> myListOut = new ArrayList<>();
        myList.add("approved");
        myList.add("rejected");
        myList.add("approved");
        myList.add("approved");
        myList.add("rejected");
        params.put("list", myList);
        params.put("listOut", myListOut);
        assertEquals(0, myListOut.size());
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MultiInstanceLoopCharacteristicsTask", params);
        assertProcessInstanceCompleted(processInstance);
        // only two approved outcomes are required to complete multiinstance and since there was reject in between we should have
        // three elements in the list
        assertEquals(3, myListOut.size());

    }

    @Test
    public void testMultiInstanceLoopCharacteristicsTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultiInstanceLoopCharacteristicsTask.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        List<String> myList = new ArrayList<>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "MultiInstanceLoopCharacteristicsTask", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testMultipleInOutgoingSequenceFlows() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);
        System.setProperty("jbpm.enable.multi.con", "true");
        kruntime = createKogitoProcessRuntime("BPMN2-MultipleInOutgoingSequenceFlows.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
            }
        });

        assertEquals(0, list.size());

        countDownListener.waitTillCompleted();

        assertEquals(1, list.size());
        System.clearProperty("jbpm.enable.multi.con");

    }

    @Test
    public void testMultipleIncomingFlowToEndNode() throws Exception {
        System.setProperty("jbpm.enable.multi.con", "true");

        kruntime = createKogitoProcessRuntime("BPMN2-MultipleFlowEndNode.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess("MultipleFlowEndNode");
        assertProcessInstanceCompleted(processInstance);
        System.clearProperty("jbpm.enable.multi.con");
    }

    @Test
    public void testMultipleEnabledOnSingleConditionalSequenceFlow() throws Exception {
        System.setProperty("jbpm.enable.multi.con", "true");
        kruntime = createKogitoProcessRuntime("BPMN2-MultiConnEnabled.bpmn2");

        final List<Long> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {
            public void afterNodeTriggered(org.kie.api.event.process.ProcessNodeTriggeredEvent event) {
                if ("Task2".equals(event.getNodeInstance().getNodeName())) {
                    list.add(event.getNodeInstance().getNodeId());
                }
            }
        });

        assertEquals(0, list.size());
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-MultiConnEnabled");
        assertProcessInstanceActive(processInstance);
        kruntime.signalEvent("signal", null, processInstance.getStringId());
        assertProcessInstanceCompleted(processInstance);

        assertEquals(1, list.size());
        System.clearProperty("jbpm.enable.multi.con");

    }

    @Test
    public void testMultipleInOutgoingSequenceFlowsDisable() throws Exception {
        try {
            createKogitoProcessRuntime("BPMN2-MultipleInOutgoingSequenceFlows.bpmn2");
            fail("Should fail as multiple outgoing and incoming connections are disabled by default");
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("This type of node [ScriptTask_1, Script Task] cannot have more than one outgoing connection!");
        }
    }

    @Test
    public void testConditionalFlow() throws Exception {
        System.setProperty("jbpm.enable.multi.con", "true");
        String processId = "designer.conditional-flow";

        kruntime = createKogitoProcessRuntime("BPMN2-ConditionalFlowWithoutGateway.bpmn2");

        KogitoProcessInstance wpi = kruntime.startProcess(processId);

        assertProcessInstanceFinished(wpi, kruntime);
        assertNodeTriggered(wpi.getStringId(), "start", "script", "end1");
        System.clearProperty("jbpm.enable.multi.con");

    }

    @Test
    public void testLane() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-Lane.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoWorkItem KogitoWorkItem = workItemHandler.getWorkItem();
        assertNotNull(KogitoWorkItem);
        assertEquals("john", KogitoWorkItem.getParameter("ActorId"));
        Map<String, Object> results = new HashMap<>();
        ((HumanTaskWorkItemImpl) KogitoWorkItem).setActualOwner("mary");
        kruntime.getKogitoWorkItemManager().completeWorkItem(KogitoWorkItem.getStringId(),
                results);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoWorkItem = workItemHandler.getWorkItem();
        assertNotNull(KogitoWorkItem);
        assertEquals("mary", KogitoWorkItem.getParameter("SwimlaneActorId"));
        kruntime.getKogitoWorkItemManager().completeWorkItem(KogitoWorkItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testExclusiveSplitDefaultNoCondition() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ExclusiveSplitDefaultNoCondition.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.test");
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testMultipleGatewaysProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MultipleGatewaysProcess.bpmn2");
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {
            KogitoProcessInstance pi;

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                if (event.getNodeInstance().getNodeName().equals("CreateAgent")) {
                    pi.signalEvent("Signal_1", null);
                }
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("Before Node triggered event received for node: {}", event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                pi = (KogitoProcessInstance) event.getProcessInstance();

            }
        });
        Map<String, Object> params = new HashMap<>();
        params.put("action", "CreateAgent");
        KogitoProcessInstance processInstance = kruntime.startProcess("multiplegateways", params);

        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testTimerAndGateway() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);
        kruntime = createKogitoProcessRuntime("timer/BPMN2-ParallelSplitWithTimerProcess.bpmn2");

        kruntime.getProcessEventManager().addEventListener(countDownListener);

        TestWorkItemHandler handler1 = new TestWorkItemHandler();
        TestWorkItemHandler handler2 = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("task1", handler1);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("task2", handler2);

        KogitoProcessInstance instance = kruntime.createProcessInstance("timer-process", new HashMap<>());
        kruntime.startProcessInstance(instance.getStringId());

        KogitoWorkItem workItem1 = handler1.getWorkItem();
        assertNotNull(workItem1);
        assertNull(handler1.getWorkItem());
        //first safe state: task1 completed
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem1.getStringId(), null);
        kruntime.getProcessEventManager().addEventListener(countDownListener);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("task1", handler1);
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("task2", handler2);
        //second safe state: timer completed, waiting on task2
        countDownListener.waitTillCompleted();

        KogitoWorkItem workItem2 = handler2.getWorkItem();
        //Both sides of the join are completed. But on the process instance, there are two
        //JoinInstance for the same Join, and since it is an AND join, it never reaches task2
        //It fails after the next assertion
        assertNotNull(workItem2);
        assertNull(handler2.getWorkItem());

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem2.getStringId(), null);
        assertProcessInstanceCompleted(instance);
    }

    private static class GetProcessVariableCommand implements ExecutableCommand<Object> {

        private String processInstanceId;
        private String variableName;

        public GetProcessVariableCommand(String processInstanceId, String variableName) {
            this.processInstanceId = processInstanceId;
            this.variableName = variableName;
        }

        public Object execute(Context context) {
            KogitoProcessRuntime kruntime = KogitoProcessRuntime.asKogitoProcessRuntime(((RegistryContext) context).lookup(KieSession.class));

            org.jbpm.process.instance.ProcessInstance processInstance =
                    (org.jbpm.process.instance.ProcessInstance) kruntime.getProcessInstance(processInstanceId);

            VariableScopeInstance variableScope =
                    (VariableScopeInstance) processInstance.getContextInstance(VariableScope.VARIABLE_SCOPE);

            Object variable = variableScope.getVariable(variableName);

            return variable;
        }

    }
}
