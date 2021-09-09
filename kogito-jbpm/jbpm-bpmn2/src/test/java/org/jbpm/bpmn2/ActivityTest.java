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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngineManager;

import org.assertj.core.api.Assumptions;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.objects.Account;
import org.jbpm.bpmn2.objects.Address;
import org.jbpm.bpmn2.objects.HelloService;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.bpmn2.test.RequirePersistence;
import org.jbpm.process.builder.ActionBuilder;
import org.jbpm.process.builder.AssignmentBuilder;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.builder.ProcessClassBuilder;
import org.jbpm.process.builder.ReturnValueEvaluatorBuilder;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.process.core.impl.DataTransformerRegistry;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventListener;
import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;
import org.jbpm.process.instance.impl.AssignmentAction;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.util.NodeLeftCountDownProcessEventListener;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.Assignment;
import org.jbpm.workflow.core.node.DataAssociation;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.definition.process.Node;
import org.kie.api.definition.process.NodeContainer;
import org.kie.api.definition.process.Process;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.process.DataTransformer;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstanceContainer;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;
import org.kie.kogito.internal.process.runtime.KogitoWorkItemManager;
import org.kie.kogito.internal.process.runtime.KogitoWorkflowProcessInstance;
import org.kie.kogito.process.workitems.InternalKogitoWorkItem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActivityTest extends JbpmBpmn2TestCase {

    @Test
    public void testMinimalProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcess.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testMinimalProcessImplicit() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcessImplicit.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testMinimalProcessWithGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcessWithGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testMinimalProcessWithDIGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcessWithDIGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testMinimalProcessMetaData() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-MinimalProcessMetaData.bpmn2");

        final List<String> list1 = new ArrayList<>();
        final List<String> list2 = new ArrayList<>();
        final List<String> list3 = new ArrayList<>();
        final List<String> list4 = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.debug("before node");
                Map<String, Object> metaData = event.getNodeInstance().getNode().getMetaData();
                for (Map.Entry<String, Object> entry : metaData.entrySet()) {
                    logger.debug(entry.getKey() + " " + entry.getValue());
                }
                String customTag = (String) metaData.get("customTag");
                if (customTag != null) {
                    list1.add(customTag);
                }
                String customTag2 = (String) metaData.get("customTag2");
                if (customTag2 != null) {
                    list2.add(customTag2);
                }
            }

            @Override
            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                logger.debug("after variable");
                VariableScope variableScope = (VariableScope) ((org.jbpm.process.core.impl.ProcessImpl) event.getProcessInstance().getProcess())
                        .resolveContext(VariableScope.VARIABLE_SCOPE, event.getVariableId());
                if (variableScope == null) {
                    return;
                }
                Map<String, Object> metaData = variableScope.findVariable(event.getVariableId()).getMetaData();
                for (Map.Entry<String, Object> entry : metaData.entrySet()) {
                    logger.debug(entry.getKey() + " " + entry.getValue());
                }
                String customTag = (String) metaData.get("customTagVar");
                if (customTag != null) {
                    list3.add(customTag);
                }
            }

            @Override
            public void afterProcessStarted(ProcessStartedEvent event) {
                logger.debug("after process");
                Map<String, Object> metaData = event.getProcessInstance().getProcess().getMetaData();
                for (Map.Entry<String, Object> entry : metaData.entrySet()) {
                    logger.debug(entry.getKey() + " " + entry.getValue());
                }
                String customTag = (String) metaData.get("customTagProcess");
                if (customTag != null) {
                    list4.add(customTag);
                }
            }
        });
        Map<String, Object> params = new HashMap<>();
        params.put("x", "krisv");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals(3, list1.size());
        assertEquals(2, list2.size());
        assertEquals(1, list3.size());
        assertEquals(1, list4.size());
    }

    @Test
    public void testCompositeProcessWithDIGraphical() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CompositeProcessWithDIGraphical.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Composite");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testScriptTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ScriptTask.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("ScriptTask");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067 and JavaScript not supported in ScriptTask")
    public void testScriptTaskJS() throws Exception {
        Assumptions.assumeThat(
                new ScriptEngineManager().getEngineByName("JavaScript")
                        .getClass().getSimpleName())
                .describedAs("GraalJS is not supported.")
                .isNotEqualTo("GraalJSScriptEngine");

        kruntime = createKogitoProcessRuntime("BPMN2-ScriptTaskJS.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<>();
        params.put("name", "krisv");
        Person person = new Person();
        person.setName("krisv");
        params.put("person", person);

        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime.startProcess("ScriptTask", params);
        assertEquals("Entry", processInstance.getVariable("x"));
        assertNull(processInstance.getVariable("y"));

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertEquals("Exit", getProcessVarValue(processInstance, "y"));
        assertEquals("tester", processInstance.getVariable("surname"));
    }

    @Test
    public void testScriptTaskWithIO() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ScriptTaskWithIO.bpmn2");

        Process scriptProcess = kruntime.getKieBase().getProcess("ScriptTask");
        assertThat(scriptProcess).isNotNull();
        Node[] nodes = ((NodeContainer) scriptProcess).getNodes();
        assertThat(nodes).hasSize(3);
        assertThat(nodes).filteredOn(n -> n instanceof ActionNode).allMatch(n -> ((ActionNode) n).getInAssociations().size() == 1 && ((ActionNode) n).getOutAssociations().size() == 1);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "John");
        KogitoProcessInstance processInstance = kruntime.startProcess("ScriptTask", params);

        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testRuleTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-RuleTask.bpmn2",
                "BPMN2-RuleTask.drl");

        List<String> list = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("RuleTask");
        kruntime.getKieSession().setGlobal("list", list);
        assertEquals(1, list.size());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testRuleTask2() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-RuleTask2.bpmn2", "BPMN2-RuleTask2.drl");

        List<String> list = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", list);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "SomeString");
        KogitoProcessInstance processInstance = kruntime.startProcess("RuleTask",
                params);
        assertTrue(list.isEmpty());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testRuleTaskSetVariableWithReconnect() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-RuleTask2.bpmn2", "BPMN2-RuleTaskSetVariableReconnect.drl");

        List<String> list = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", list);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "SomeString");

        KogitoProcessInstance processInstance = kruntime.startProcess("RuleTask",
                params);
        assertEquals(1, list.size());

        assertProcessVarValue(processInstance, "x", "AnotherString");
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    @RequirePersistence(false)
    public void testRuleTaskWithFacts() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-RuleTaskWithFact.bpmn2", "BPMN2-RuleTask3.drl");

        kruntime.getKieSession().addEventListener(new AgendaEventListener() {
            public void matchCreated(MatchCreatedEvent event) {
            }

            public void matchCancelled(MatchCancelledEvent event) {
            }

            public void beforeRuleFlowGroupDeactivated(
                    org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
            }

            public void beforeRuleFlowGroupActivated(
                    org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
            }

            public void beforeMatchFired(BeforeMatchFiredEvent event) {
            }

            public void agendaGroupPushed(
                    org.kie.api.event.rule.AgendaGroupPushedEvent event) {
            }

            public void agendaGroupPopped(
                    org.kie.api.event.rule.AgendaGroupPoppedEvent event) {
            }

            public void afterRuleFlowGroupDeactivated(
                    org.kie.api.event.rule.RuleFlowGroupDeactivatedEvent event) {
            }

            public void afterRuleFlowGroupActivated(
                    org.kie.api.event.rule.RuleFlowGroupActivatedEvent event) {
                kruntime.getKieSession().fireAllRules();
            }

            public void afterMatchFired(AfterMatchFiredEvent event) {
            }

        });

        Map<String, Object> params = new HashMap<>();
        params.put("x", "SomeString");
        KogitoProcessInstance processInstance = kruntime.startProcess("RuleTask",
                params);
        assertProcessInstanceFinished(processInstance, kruntime);

        params = new HashMap<>();

        processInstance = kruntime.startProcess("RuleTask", params);

        assertEquals(KogitoProcessInstance.STATE_ERROR, processInstance.getState());

        params = new HashMap<>();
        params.put("x", "SomeString");
        processInstance = kruntime.startProcess("RuleTask", params);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testRuleTaskAcrossSessions() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-RuleTask.bpmn2", "BPMN2-RuleTask.drl");
        KogitoProcessRuntime kruntime2 = createKogitoProcessRuntime("BPMN2-RuleTask.bpmn2", "BPMN2-RuleTask.drl");

        List<String> list1 = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", list1);
        List<String> list2 = new ArrayList<>();
        kruntime2.getKieSession().setGlobal("list", list2);
        KogitoProcessInstance processInstance1 = kruntime.startProcess("RuleTask");
        KogitoProcessInstance processInstance2 = kruntime2.startProcess("RuleTask");
        assertProcessInstanceFinished(processInstance1, kruntime);
        assertProcessInstanceFinished(processInstance2, kruntime2);
        kruntime2.getKieSession().dispose(); // kruntime's session is disposed in the @AfterEach method
    }

    @Test
    public void testUserTaskWithDataStoreScenario() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithDataStore.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        kruntime.startProcess("UserProcess");
        // we can't test further as user tasks are asynchronous.
    }

    @Test
    public void testUserTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTask.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testUserTaskVerifyParameters() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTask.bpmn2");

        kruntime.getKieSession().getEnvironment().set("deploymentId", "test-deployment-id");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        final String pId = processInstance.getStringId();

        kruntime.getKieSession().execute((ExecutableCommand<Void>) context -> {

            KogitoProcessInstance processInstance1 = kruntime.getProcessInstance(pId);
            assertNotNull(processInstance1);
            NodeInstance nodeInstance = ((KogitoNodeInstanceContainer) processInstance1)
                    .getNodeInstance(((InternalKogitoWorkItem) workItem).getNodeInstanceStringId());

            assertNotNull(nodeInstance);
            assertTrue(nodeInstance instanceof WorkItemNodeInstance);
            String deploymentId = ((WorkItemNodeInstance) nodeInstance).getWorkItem().getDeploymentId();
            String nodeInstanceId = ((WorkItemNodeInstance) nodeInstance).getWorkItem().getNodeInstanceStringId();
            long nodeId = ((WorkItemNodeInstance) nodeInstance).getWorkItem().getNodeId();

            assertEquals(((InternalKogitoWorkItem) workItem).getDeploymentId(), deploymentId);
            assertEquals(((InternalKogitoWorkItem) workItem).getNodeId(), nodeId);
            assertEquals(((InternalKogitoWorkItem) workItem).getNodeInstanceStringId(), nodeInstanceId);

            return null;
        });

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testCallActivityWithContantsAssignment() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/SingleTaskWithVarDef.bpmn2", "subprocess/InputMappingUsingValue.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("CustomTask", handler);
        Map<String, Object> params = new HashMap<>();
        KogitoProcessInstance processInstance = kruntime.startProcess("defaultPackage.InputMappingUsingValue", params);

        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);

        Object value = workItem.getParameter("TaskName");
        assertNotNull(value);
        assertEquals("test string", value);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testSubProcessWithEntryExitScripts() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/BPMN2-SubProcessWithEntryExitScripts.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        KogitoProcessInstance processInstance = kruntime.startProcess("com.sample.bpmn.hello");

        assertNodeTriggered(processInstance.getStringId(), "Task1");
        Object var1 = getProcessVarValue(processInstance, "var1");
        assertNotNull(var1);
        assertEquals("10", var1.toString());

        assertNodeTriggered(processInstance.getStringId(), "Task2");
        Object var2 = getProcessVarValue(processInstance, "var2");
        assertNotNull(var2);
        assertEquals("20", var2.toString());

        assertNodeTriggered(processInstance.getStringId(), "Task3");
        Object var3 = getProcessVarValue(processInstance, "var3");
        assertNotNull(var3);
        assertEquals("30", var3.toString());

        assertNodeTriggered(processInstance.getStringId(), "SubProcess");
        Object var4 = getProcessVarValue(processInstance, "var4");
        assertNotNull(var4);
        assertEquals("40", var4.toString());

        Object var5 = getProcessVarValue(processInstance, "var5");
        assertNotNull(var5);
        assertEquals("50", var5.toString());

        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = handler.getWorkItem();
        assertNotNull(workItem);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testCallActivity() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CallActivity.bpmn2", "BPMN2-CallActivitySubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "ParentProcess", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals("new value",
                ((KogitoWorkflowProcessInstance) processInstance).getVariable("y"));
    }

    @Test
    public void testCallActivityMI() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CallActivityMI.bpmn2", "BPMN2-CallActivitySubProcess.bpmn2");

        final List<String> subprocessStarted = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                if (event.getProcessInstance().getProcessId().equals("SubProcess")) {
                    subprocessStarted.add(((KogitoProcessInstance) event.getProcessInstance()).getStringId());
                }
            }

        });

        List<String> list = new ArrayList<>();
        list.add("first");
        list.add("second");
        List<String> listOut = new ArrayList<>();

        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");
        params.put("list", list);
        params.put("listOut", listOut);

        KogitoProcessInstance processInstance = kruntime.startProcess("ParentProcess", params);
        assertProcessInstanceCompleted(processInstance);

        assertEquals(2, subprocessStarted.size());
        listOut = (List) ((KogitoWorkflowProcessInstance) processInstance).getVariable("listOut");
        assertNotNull(listOut);
        assertEquals(2, listOut.size());

        assertEquals("new value", listOut.get(0));
        assertEquals("new value", listOut.get(1));
    }

    @Test
    public void testCallActivity2() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CallActivity2.bpmn2", "BPMN2-CallActivitySubProcess.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "ParentProcess", params);
        assertProcessInstanceActive(processInstance);
        assertEquals("new value",
                ((KogitoWorkflowProcessInstance) processInstance).getVariable("y"));

        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("krisv", workItem.getParameter("ActorId"));
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testCallActivityByName() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CallActivityByName.bpmn2",
                "BPMN2-CallActivitySubProcess.bpmn2",
                "BPMN2-CallActivitySubProcessV2.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "ParentProcess", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals("new value V2",
                ((KogitoWorkflowProcessInstance) processInstance).getVariable("y"));
    }

    @Test
    public void testSubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SubProcess.bpmn2");

        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void afterProcessStarted(ProcessStartedEvent event) {
                logger.debug(event.toString());
            }

            @Override
            public void beforeVariableChanged(ProcessVariableChangedEvent event) {
                logger.debug(event.toString());
            }

            @Override
            public void afterVariableChanged(ProcessVariableChangedEvent event) {
                logger.debug(event.toString());
            }
        });
        KogitoProcessInstance processInstance = kruntime.startProcess("SubProcess");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testInvalidSubProcess() throws Exception {
        try {
            kruntime = createKogitoProcessRuntime("BPMN2-SubProcessInvalid.bpmn2");
            fail("Process should be invalid, there should be build errors");
        } catch (RuntimeException e) {
            // there should be build errors
        }
    }

    @Test
    public void testSubProcessWrongStartEvent() throws Exception {
        try {
            kruntime = createKogitoProcessRuntime("BPMN2-SubProcessWrongStartEvent.bpmn2");
            fail("Process should be invalid, there should be build errors");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Embedded subprocess can only have none start event.");
        }
    }

    @Test
    public void testSubProcessWrongStartEventTimer() throws Exception {
        try {
            kruntime = createKogitoProcessRuntime("SubprocessWithTimer.bpmn2");
            fail("Process should be invalid, there should be build errors");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("Embedded subprocess can only have none start event.");
        }
    }

    @Test
    public void testMultiinstanceSubProcessWrongStartEvent() throws Exception {
        try {
            kruntime = createKogitoProcessRuntime("MultipleSubprocessWithSignalStartEvent.bpmn2");
            fail("Process should be invalid, there should be build errors");
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("MultiInstance subprocess can only have none start event.");
        }
    }

    @Test
    public void testSubProcessWithTerminateEndEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SubProcessWithTerminateEndEvent.bpmn2");
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                list.add(event.getNodeInstance().getNodeName());
            }
        });
        KogitoProcessInstance processInstance = kruntime.startProcess("SubProcessTerminate");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(7, list.size());
    }

    @Test
    public void testSubProcessWithTerminateEndEventProcessScope()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SubProcessWithTerminateEndEventProcessScope.bpmn2");
        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                list.add(event.getNodeInstance().getNodeName());
            }
        });
        KogitoProcessInstance processInstance = kruntime.startProcess("SubProcessTerminate");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(5, list.size());
    }

    @Test
    public void testAdHocProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-AdHocProcess.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess("AdHocProcess");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        logger.debug("Triggering node");
        kruntime.signalEvent("Task1", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        kruntime.signalEvent("User1", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        kruntime.getKieSession().insert(new Person());
        kruntime.signalEvent("Task3", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testAdHocProcessDynamicTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-AdHocProcess.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess("AdHocProcess");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        logger.debug("Triggering node");
        kruntime.signalEvent("Task1", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("OtherTask",
                workItemHandler2);
        DynamicUtils.addDynamicWorkItem(processInstance, kruntime.getKieSession(), "OtherTask",
                new HashMap<>());
        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler2.getWorkItem();
        assertNotNull(workItem);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        kruntime.signalEvent("User1", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        kruntime.getKieSession().insert(new Person());
        kruntime.signalEvent("Task3", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testAdHocProcessDynamicSubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-AdHocProcess.bpmn2", "BPMN2-MinimalProcess.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess("AdHocProcess");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        logger.debug("Triggering node");
        kruntime.signalEvent("Task1", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("OtherTask",
                workItemHandler2);
        DynamicUtils.addDynamicSubProcess(processInstance, kruntime.getKieSession(), "Minimal",
                new HashMap<>());
        kruntime.signalEvent("User1", null, processInstance.getStringId());
        assertProcessInstanceActive(processInstance);
        kruntime.getKieSession().insert(new Person());
        kruntime.signalEvent("Task3", null, processInstance.getStringId());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testServiceTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceProcess.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task",
                new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "john");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("ServiceProcess", params);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertEquals("Hello john!", processInstance.getVariable("s"));
    }

    @Test
    public void testServiceTaskWithAccessToWorkItemInfo() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceProcess.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task",
                new ServiceTaskHandler() {

                    @Override
                    public void executeWorkItem(org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                        assertThat(workItem.getProcessInstance()).isNotNull();
                        assertThat(workItem.getNodeInstance()).isNotNull();
                        super.executeWorkItem(workItem, manager);
                    }

                });
        Map<String, Object> params = new HashMap<>();
        params.put("s", "john");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime.startProcess("ServiceProcess", params);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertEquals("Hello john!", processInstance.getVariable("s"));
    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testServiceTaskWithTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceProcessWithTransformation.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task",
                new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "JoHn");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime.startProcess("ServiceProcess", params);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertEquals("hello john!", processInstance.getVariable("s"));
    }

    @Test
    public void testServiceTaskWithMvelTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceProcessWithMvelTransformation.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task",
                new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "JoHn");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("ServiceProcess", params);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertEquals("hello john!", processInstance.getVariable("s"));
    }

    @Test
    public void testServiceTaskWithCustomTransformation() throws Exception {
        DataTransformerRegistry.get().register("http://custom/transformer", new DataTransformer() {

            @Override
            public Object transform(Object expression, Map<String, Object> parameters) {
                // support only single object
                String value = parameters.values().iterator().next().toString();
                Object result = null;
                if ("caplitalizeFirst".equals(expression)) {
                    String first = value.substring(0, 1);
                    String main = value.substring(1, value.length());

                    result = first.toUpperCase() + main;
                } else if ("caplitalizeLast".equals(expression)) {
                    String last = value.substring(value.length() - 1);
                    String main = value.substring(0, value.length() - 1);

                    result = main + last.toUpperCase();
                } else {
                    throw new IllegalArgumentException("Unknown expression " + expression);
                }
                return result;
            }

            @Override
            public Object compile(String expression, Map<String, Object> parameters) {
                // compilation not supported
                return expression;
            }
        });
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceProcessWithCustomTransformation.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task",
                new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "john doe");

        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("ServiceProcess", params);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertEquals("John doE", processInstance.getVariable("s"));
    }

    @Test
    public void testServiceTaskNoInterfaceName() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceTask-web-service.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task",
                new SystemOutWorkItemHandler() {

                    @Override
                    public void executeWorkItem(KogitoWorkItem workItem, KogitoWorkItemManager manager) {
                        assertEquals("SimpleService", workItem.getParameter("Interface"));
                        assertEquals("hello", workItem.getParameter("Operation"));
                        assertEquals("java.lang.String", workItem.getParameter("ParameterType"));
                        assertEquals("##WebService", workItem.getParameter("implementation"));
                        assertEquals("hello", workItem.getParameter("operationImplementationRef"));
                        assertEquals("SimpleService", workItem.getParameter("interfaceImplementationRef"));
                        super.executeWorkItem(workItem, manager);
                    }

                });
        Map<String, Object> params = new HashMap<>();
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("org.jboss.qa.jbpm.CallWS", params);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testSendTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-SendTask.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "john");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("SendTask", params);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testReceiveTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ReceiveTask.bpmn2");
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(kruntime);

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("ReceiveTask");
        assertProcessInstanceActive(processInstance);
        receiveTaskHandler.setKnowledgeRuntime(kruntime);
        receiveTaskHandler.messageReceived("HelloMessage", "Hello john!");
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    @RequirePersistence(false)
    public void testBusinessRuleTask() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTask.bpmn2", "BPMN2-BusinessRuleTask.drl");
        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask");
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    @RequirePersistence(true)
    public void testBusinessRuleTaskWithPersistence() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTask.bpmn2", "BPMN2-BusinessRuleTask.drl");
        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BPMN2-BusinessRuleTask");

        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());

        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testBusinessRuleTaskDynamic() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTaskDynamic.bpmn2",
                "BPMN2-BusinessRuleTask.drl");

        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());

        Map<String, Object> params = new HashMap<>();
        params.put("dynamicrule", "MyRuleFlow");
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testBusinessRuleTaskWithDataInputsWithPersistence()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTaskWithDataInputs.bpmn2",
                "BPMN2-BusinessRuleTaskWithDataInput.drl");

        Map<String, Object> params = new HashMap<>();
        params.put("person", new Person());
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testBusinessRuleTaskWithDataInputs2WithPersistence()
            throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTaskWithDataInput.bpmn2",
                "BPMN2-BusinessRuleTaskWithDataInput.drl");

        Map<String, Object> params = new HashMap<>();
        params.put("person", new Person());
        KogitoProcessInstance processInstance = kruntime.startProcess(
                "BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testBusinessRuleTaskWithContionalEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ConditionalEventRuleTask.bpmn2",
                "BPMN2-ConditionalEventRuleTask.drl");
        List<String> list = new ArrayList<>();
        kruntime.getKieSession().setGlobal("list", list);
        KogitoProcessInstance processInstance = kruntime.startProcess("TestFlow");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        Person person = new Person();
        person.setName("john");
        kruntime.getKieSession().insert(person);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertEquals(1, list.size());
    }

    @Test
    public void testScriptTaskWithVariableByName() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("myVar", "test");
        kruntime = createKogitoProcessRuntime("BPMN2-ProcessWithVariableName.bpmn2");

        KogitoProcessInstance processInstance = kruntime.startProcess(
                "BPMN2-ProcessWithVariableName", params);
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testCallActivityWithBoundaryEvent() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Boundary event", 1);
        kruntime = createKogitoProcessRuntime(
                "BPMN2-CallActivityWithBoundaryEvent.bpmn2",
                "BPMN2-CallActivitySubProcessWithBoundaryEvent.bpmn2");
        kruntime.getProcessEventManager().addEventListener(countDownListener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("ParentProcess", params);

        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, kruntime);
        // assertEquals("new timer value",
        // ((WorkflowProcessInstance) processInstance).getVariable("y"));
        // first check the parent process executed nodes
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "CallActivity", "Boundary event", "Script Task", "end");
        // then check child process executed nodes - is there better way to get child process id than simply increment?
        assertNodeTriggered(processInstance.getStringId() + 1, "StartProcess2",
                "User Task");
    }

    @Test
    public void testCallActivityWithSubProcessWaitState() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "BPMN2-CallActivity.bpmn2",
                "BPMN2-CallActivitySubProcessWithBoundaryEvent.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        Map<String, Object> params = new HashMap<>();
        KogitoProcessInstance processInstance = kruntime.startProcess("ParentProcess", params);
        assertProcessInstanceActive(processInstance.getStringId(), kruntime);

        org.kie.kogito.internal.process.runtime.KogitoWorkItem wi = workItemHandler.getWorkItem();
        assertNotNull(wi);

        kruntime.getKogitoWorkItemManager().completeWorkItem(wi.getStringId(), null);

        assertProcessInstanceFinished(processInstance, kruntime);
        // first check the parent process executed nodes
        assertNodeTriggered(processInstance.getStringId(), "StartProcess", "CallActivity", "EndProcess");
        // then check child process executed nodes - is there better way to get child process id than simply increment?
        assertNodeTriggered(processInstance.getStringId() + 1, "StartProcess2", "User Task", "EndProcess");
    }

    @Test
    public void testUserTaskWithBooleanOutput() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithBooleanOutput.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("com.sample.boolean");
        assertProcessInstanceActive(processInstance);
        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        HashMap<String, Object> output = new HashMap<>();
        output.put("isCheckedCheckbox", "true");
        kruntime.getKogitoWorkItemManager()
                .completeWorkItem(workItem.getStringId(), output);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testUserTaskWithSimData() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithSimulationMetaData.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testCallActivityWithBoundaryErrorEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CallActivityProcessBoundaryError.bpmn2",
                "BPMN2-CallActivitySubProcessBoundaryError.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("task1",
                new SystemOutWorkItemHandler());
        KogitoProcessInstance processInstance = kruntime.startProcess("ParentProcess");

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "Call Activity 1", "Boundary event", "Task Parent", "End2");
        // then check child process executed nodes - is there better way to get child process id than simply increment?
        assertNodeTriggered(processInstance.getStringId() + 1, "StartProcess", "Task 1", "End");
    }

    @Test
    public void testCallActivityWithBoundaryErrorEventWithWaitState() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "BPMN2-CallActivityProcessBoundaryError.bpmn2",
                "BPMN2-CallActivitySubProcessBoundaryError.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("task1", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("ParentProcess");

        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "StartProcess",
                "Call Activity 1", "Boundary event", "Task Parent", "End2");
        // then check child process executed nodes - is there better way to get child process id than simply increment?
        assertNodeTriggered(processInstance.getStringId() + 1, "StartProcess", "Task 1", "End");
    }

    @Test
    @Timeout(10)
    public void testInvalidServiceTask() {
        assertThrows(RuntimeException.class, () -> createKogitoProcessRuntime("BPMN2-InvalidServiceProcess.bpmn2"));
    }

    @Test // JBPM-3951
    public void testServiceTaskInterface() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceTask.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();

        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("EAID_DP000000_23D3_4e7e_80FE_6D8C0AF83CAA", params);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @SuppressWarnings("unchecked")
    @Test
    @Disabled("Transfomer has been disabled")
    public void testBusinessRuleTaskWithTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-RuleTaskWithTransformation.bpmn2",
                "BPMN2-RuleTaskWithTransformation.drl");

        List<String> data = new ArrayList<>();

        kruntime.getKieSession().setGlobal("data", data);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "JoHn");
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-RuleTaskWithTransformation", params);

        assertProcessInstanceFinished(processInstance, kruntime);

        data = (List<String>) kruntime.getKieSession().getGlobal("data");
        assertNotNull(data);
        assertEquals(1, data.size());
        assertEquals("JOHN", data.get(0));

        String nameVar = getProcessVarValue(processInstance, "name");
        assertNotNull(nameVar);
        assertEquals("john", nameVar);

    }

    @Test
    @Disabled("Transfomer has been disabled")
    public void testCallActivityWithTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CallActivityWithTransformation.bpmn2", "BPMN2-CallActivitySubProcess.bpmn2");

        final List<KogitoProcessInstance> instances = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add((KogitoProcessInstance) event.getProcessInstance());
            }

        });

        Map<String, Object> params = new HashMap<>();
        params.put("x", "oldValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("ParentProcess", params);
        assertProcessInstanceCompleted(processInstance);

        assertEquals(2, instances.size());
        // assert variables of parent process, first in start (input transformation, then on end output transformation)
        assertEquals("oldValue", ((KogitoWorkflowProcessInstance) instances.get(0)).getVariable("x"));
        assertEquals("NEW VALUE", ((KogitoWorkflowProcessInstance) instances.get(0)).getVariable("y"));
        // assert variables of subprocess, first in start (input transformation, then on end output transformation)
        assertEquals("OLDVALUE", ((KogitoWorkflowProcessInstance) instances.get(1)).getVariable("subX"));
        assertEquals("new value", ((KogitoWorkflowProcessInstance) instances.get(1)).getVariable("subY"));
    }

    @Test
    public void testServiceTaskWithMvelCollectionTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceProcessWithMvelCollectionTransformation.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task",
                new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("s", "john,poul,mary");
        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("ServiceProcess", params);
        assertProcessInstanceFinished(processInstance, kruntime);
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) processInstance.getVariable("list");
        assertEquals(3, result.size());
    }

    @Test
    public void testServiceTaskWithMvelJaxbTransformation() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ServiceProcessWithMvelJaxbTransformation.bpmn2");

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Service Task",
                new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<>();
        Person person = new Person();
        person.setId(123);
        person.setName("john");
        params.put("s", person);

        HelloService.VALIDATE_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><id>123</id><name>john</name></person>";

        KogitoWorkflowProcessInstance processInstance = (KogitoWorkflowProcessInstance) kruntime
                .startProcess("ServiceProcess", params);
        assertProcessInstanceFinished(processInstance, kruntime);

    }

    @Test
    public void testErrorBetweenProcessesProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/ErrorsBetweenProcess-Process.bpmn2",
                "subprocess/ErrorsBetweenProcess-SubProcess.bpmn2");

        Map<String, Object> variables = new HashMap<>();

        variables.put("tipoEvento", "error");
        variables.put("pasoVariable", 3);
        KogitoProcessInstance processInstance = kruntime.startProcess("Principal", variables);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertProcessInstanceAborted(processInstance.getStringId() + 1, kruntime);

        assertProcessVarValue(processInstance, "event", "error desde Subproceso");
    }

    @Test
    public void testProcessCustomDescriptionMetaData() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ProcessCustomDescriptionMetaData.bpmn2");

        Map<String, Object> params = new HashMap<>();

        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal", params);
        assertProcessInstanceCompleted(processInstance);

        String description = processInstance.getDescription();
        assertNotNull(description);
        assertEquals("my process with description", description);
    }

    @Test
    public void testProcessVariableCustomDescriptionMetaData() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ProcessVariableCustomDescriptionMetaData.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("x", "variable name for process");
        KogitoProcessInstance processInstance = kruntime.startProcess("Minimal", params);
        assertProcessInstanceCompleted(processInstance);

        String description = processInstance.getDescription();
        assertNotNull(description);
        assertEquals("variable name for process", description);
    }

    @Test
    public void testInvalidSubProcessNoOutgoingSF() throws Exception {
        try {
            createKogitoProcessRuntime("subprocess/BPMN2-InvalidEmdeddedSubProcess.bpmn2");
            fail("Process should be invalid, there should be build errors");
        } catch (RuntimeException e) {
            // there should be build errors
        }
    }

    @Test
    public void testAdHocSubProcessEmptyCompleteExpression() throws Exception {
        try {
            createKogitoProcessRuntime("BPMN2-AdHocSubProcessEmptyCompleteExpression.bpmn2");
            fail("Process should be invalid, there should be build errors");
        } catch (RuntimeException e) {
            // there should be build errors
        }
    }

    @Test
    public void testSubProcessWithTypeVariable() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/BPMN2-SubProcessWithTypeVariable.bpmn2");

        final List<String> list = new ArrayList<>();
        kruntime.getProcessEventManager().addEventListener(new DefaultKogitoProcessEventListener() {

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                if (event.getNodeInstance().getNodeName().equals("Read Map")) {
                    list.add(event.getNodeInstance().getNodeName());
                }
            }
        });
        KogitoProcessInstance processInstance = kruntime.startProcess("sub_variable.sub_variables");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(2, list.size());
    }

    @Test
    public void testUserTaskParametrizedInput() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithParametrizedInput.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask");
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("Executing task of process instance " + processInstance.getStringId() + " as work item with Hello",
                workItem.getParameter("Description").toString().trim());
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testMultipleBusinessRuleTaskWithDataInputsWithPersistence()
            throws Exception {
        kruntime = createKogitoProcessRuntime(
                "BPMN2-MultipleRuleTasksWithDataInput.bpmn2",
                "BPMN2-MultipleRuleTasks.drl");

        kruntime.getKieSession().addEventListener(new TriggerRulesEventListener(kruntime));

        List<String> listPerson = new ArrayList<>();
        List<String> listAddress = new ArrayList<>();

        kruntime.getKieSession().setGlobal("listPerson", listPerson);
        kruntime.getKieSession().setGlobal("listAddress", listAddress);

        Person person = new Person();
        person.setName("john");

        Address address = new Address();
        address.setStreet("5th avenue");

        Map<String, Object> params = new HashMap<>();
        params.put("person", person);
        params.put("address", address);
        KogitoProcessInstance processInstance = kruntime.startProcess("multiple-rule-tasks", params);

        assertEquals(1, listPerson.size());
        assertEquals(1, listAddress.size());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testSubProcessInAdHocProcess() throws Exception {
        // JBPM-5374
        kruntime = createKogitoProcessRuntime(
                "BPMN2-SubProcessInAdHocProcess.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

        Map<String, Object> parameters = new HashMap<>();
        KogitoProcessInstance processInstance = kruntime.startProcess("SubProcessInAdHocProcess", parameters);
        assertProcessInstanceActive(processInstance);

        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testCallActivityWithDataAssignment() throws Exception {
        kruntime = createKogitoProcessRuntime("subprocess/AssignmentProcess.bpmn2", "subprocess/AssignmentSubProcess.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("name", "oldValue");
        KogitoProcessInstance processInstance = kruntime.startProcess("assignmentProcess", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals("Hello Genworth welcome to jBPMS!", ((KogitoWorkflowProcessInstance) processInstance).getVariable("message"));
    }

    @Test
    public void testDMNBusinessRuleTask() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "dmn/BPMN2-BusinessRuleTaskDMN.bpmn2", "dmn/0020-vacation-days.dmn");

        // first run 16, 1 and expected days is 27
        Map<String, Object> params = new HashMap<>();
        params.put("age", 16);
        params.put("yearsOfService", 1);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        BigDecimal vacationDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("vacationDays");
        assertEquals(BigDecimal.valueOf(27), vacationDays);

        // second run 44, 20 and expected days is 24
        params = new HashMap<>();
        params.put("age", 44);
        params.put("yearsOfService", 20);
        processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        vacationDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("vacationDays");
        assertEquals(BigDecimal.valueOf(24), vacationDays);

        // second run 50, 30 and expected days is 30
        params = new HashMap<>();
        params.put("age", 50);
        params.put("yearsOfService", 30);
        processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        vacationDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("vacationDays");
        assertEquals(BigDecimal.valueOf(30), vacationDays);
    }

    @Disabled
    @Test
    public void testDMNBusinessRuleTaskByDecisionName() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "dmn/BPMN2-BusinessRuleTaskDMNByDecisionName.bpmn2", "dmn/0020-vacation-days.dmn");

        // first run 16, 1 and expected days is 5
        Map<String, Object> params = new HashMap<>();
        params.put("age", 16);
        params.put("yearsOfService", 1);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        BigDecimal vacationDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("vacationDays");
        assertEquals(BigDecimal.valueOf(5), vacationDays);
    }

    @Disabled
    @Test
    public void testDMNBusinessRuleTaskMultipleDecisionsOutput() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "dmn/BPMN2-BusinessRuleTaskDMNMultipleDecisionsOutput.bpmn2", "dmn/0020-vacation-days.dmn");

        // first run 16, 1 and expected days is 5
        Map<String, Object> params = new HashMap<>();
        params.put("age", 16);
        params.put("yearsOfService", 1);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        BigDecimal vacationDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("vacationDays");
        assertEquals(BigDecimal.valueOf(27), vacationDays);
        BigDecimal extraDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("extraDays");
        assertEquals(BigDecimal.valueOf(5), extraDays);
    }

    @Disabled
    @Test
    public void testDMNBusinessRuleTaskInvalidExecution() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "dmn/BPMN2-BusinessRuleTaskDMNByDecisionName.bpmn2", "dmn/0020-vacation-days.dmn");
        Map<String, Object> params = new HashMap<>();
        params.put("age", 16);

        try {
            kruntime.startProcess("BPMN2-BusinessRuleTask", params);
        } catch (Exception e) {
            assertTrue(e instanceof WorkflowRuntimeException);
            assertTrue(e.getCause() instanceof RuntimeException);
            assertTrue(e.getCause().getMessage().contains("DMN result errors"));
        }
    }

    @Disabled
    @Test
    public void testDMNBusinessRuleTaskModelById() throws Exception {
        kruntime = createKogitoProcessRuntime(
                "dmn/BPMN2-BusinessRuleTaskDMNModelById.bpmn2", "dmn/0020-vacation-days.dmn");

        // first run 16, 1 and expected days is 27
        Map<String, Object> params = new HashMap<>();
        params.put("age", 16);
        params.put("yearsOfService", 1);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        BigDecimal vacationDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("vacationDays");
        assertEquals(BigDecimal.valueOf(27), vacationDays);

        // second run 44, 20 and expected days is 24
        params = new HashMap<>();
        params.put("age", 44);
        params.put("yearsOfService", 20);
        processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        vacationDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("vacationDays");
        assertEquals(BigDecimal.valueOf(24), vacationDays);

        // second run 50, 30 and expected days is 30
        params = new HashMap<>();
        params.put("age", 50);
        params.put("yearsOfService", 30);
        processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", params);

        assertProcessInstanceFinished(processInstance, kruntime);
        vacationDays = (BigDecimal) ((KogitoWorkflowProcessInstance) processInstance).getVariable("vacationDays");
        assertEquals(BigDecimal.valueOf(30), vacationDays);
    }

    @Test
    public void testBusinessRuleTaskFireLimit() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTaskLoop.bpmn2",
                "BPMN2-BusinessRuleTaskInfiniteLoop.drl");

        kruntime.getKieSession().insert(new Person());
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask");

        assertEquals(KogitoProcessInstance.STATE_ERROR, processInstance.getState());
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage()).contains("Fire rule limit reached 10000");
    }

    @Test
    public void testBusinessRuleTaskFireLimitAsParameter() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTaskWithDataInputLoop.bpmn2",
                "BPMN2-BusinessRuleTaskInfiniteLoop.drl");

        kruntime.getKieSession().insert(new Person());

        Map<String, Object> parameters = Collections.singletonMap("limit", 5);

        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask", parameters);
        assertEquals(KogitoProcessInstance.STATE_ERROR, processInstance.getState());
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage()).contains("Fire rule limit reached 5");
    }

    @Test
    @Disabled("On Exit not supported, see https://issues.redhat.com/browse/KOGITO-2067")
    public void testScriptTaskFEEL() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-ScriptTaskFEEL.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "krisv");
        Person person = new Person();
        person.setName("krisv");
        params.put("person", person);

        KogitoProcessInstance processInstance = kruntime.startProcess("ScriptTask", params);
        assertEquals("Entry", ((org.jbpm.workflow.instance.WorkflowProcessInstance) processInstance).getVariable("x"));
        assertNull(((org.jbpm.workflow.instance.WorkflowProcessInstance) processInstance).getVariable("y"));

        kruntime.getKogitoWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertEquals("Exit", getProcessVarValue(processInstance, "y"));
        assertEquals("tester", ((org.jbpm.workflow.instance.WorkflowProcessInstance) processInstance).getVariable("surname"));

        assertNodeTriggered(processInstance.getStringId(), "Script1");
    }

    @Test
    public void testGatewayFEEL() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-GatewayFEEL.bpmn2");

        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("VA", Boolean.TRUE);
        params1.put("VB", Boolean.FALSE);
        org.jbpm.workflow.instance.WorkflowProcessInstance procInstance1 = (org.jbpm.workflow.instance.WorkflowProcessInstance) kruntime.startProcess("BPMN2-GatewayFEEL", params1);
        assertEquals("ok", procInstance1.getVariable("Task1"));
        assertEquals("ok", procInstance1.getVariable("Task2"));
        assertNull(procInstance1.getVariable("Task3"));
        assertNodeTriggered(procInstance1.getStringId(), "Task2", "VA and not(VB)");

        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("VA", Boolean.FALSE);
        params2.put("VB", Boolean.TRUE);
        org.jbpm.workflow.instance.WorkflowProcessInstance procInstance2 = (org.jbpm.workflow.instance.WorkflowProcessInstance) kruntime.startProcess("BPMN2-GatewayFEEL", params2);
        assertEquals("ok", procInstance2.getVariable("Task1"));
        assertNull(procInstance2.getVariable("Task2"));
        assertEquals("ok", procInstance2.getVariable("Task3"));
        assertNodeTriggered(procInstance2.getStringId(), "Task3", "VB or not(VA)");
    }

    @Test
    public void testGatewayFEELWrong() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-GatewayFEEL-wrong.bpmn2");

        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("VA", Boolean.TRUE);
        params1.put("VB", Boolean.FALSE);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-GatewayFEEL", params1);
        // changed to comply with Kogito-style assertions:
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage()).contains("offending symbol: 'Not'");
    }

    @Test
    public void testBusinessRuleTaskException() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTask.bpmn2",
                "BPMN2-BusinessRuleTaskWithException.drl");

        kruntime.getKieSession().insert(new Person());
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-BusinessRuleTask");

        assertEquals(KogitoProcessInstance.STATE_ERROR, processInstance.getState());
        assertThat(((WorkflowProcessInstanceImpl) processInstance).getErrorMessage()).contains("On purpose");
    }

    @Test
    public void testXORWithSameTargetProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("build/XORSameTarget.bpmn2");

        Map<String, Object> params = new HashMap<>();
        params.put("choice", 1);
        KogitoProcessInstance processInstance = kruntime.startProcess("XORTest.XOR2", params);
        assertProcessInstanceCompleted(processInstance);

        params = new HashMap<>();
        params.put("choice", 2);
        processInstance = kruntime.startProcess("XORTest.XOR2", params);
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testUserTaskWithExpressionsForIO() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-UserTaskWithIOexpression.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("person", new Person("john"));

        KogitoProcessInstance processInstance = kruntime.startProcess("UserTask", parameters);
        assertEquals(KogitoProcessInstance.STATE_ACTIVE, processInstance.getState());
        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        assertEquals("john", workItem.getParameter("personName"));

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), Collections.singletonMap("personAge", 50));
        Person person = (Person) processInstance.getVariables().get("person");
        assertEquals(50, person.getAge());
        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    public void testCallActivitykWithExpressionsForIO() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-CallActivityWithIOexpression.bpmn2", "BPMN2-CallActivitySubProcess.bpmn2");

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("person", new Person("john"));
        KogitoProcessInstance processInstance = kruntime.startProcess("ParentProcess", params);
        assertProcessInstanceActive(processInstance);

        Person person = (Person) processInstance.getVariables().get("person");
        assertEquals("new value", person.getName());

        org.kie.kogito.internal.process.runtime.KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("krisv", workItem.getParameter("ActorId"));
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItem.getStringId(), null);

        assertProcessInstanceFinished(processInstance, kruntime);
    }

    @Test
    @RequirePersistence(false)
    public void testBusinessRuleTaskWithExpressionsForIO() throws Exception {
        kruntime = createKogitoProcessRuntime("BPMN2-BusinessRuleTaskWithDataInputIOExpression.bpmn2",
                "BPMN2-BusinessRuleTaskWithDataInput.drl");
        kruntime.getProcessEventManager().addEventListener(new RuleAwareProcessEventListener());

        Map<String, Object> params = new HashMap<>();
        params.put("person", new Person(null));
        params.put("account", new Account());
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BPMN2-BusinessRuleTask", params);
        assertProcessInstanceFinished(processInstance, kruntime);
        Person person = (Person) processInstance.getVariables().get("person");
        assertEquals("john", person.getName());

        Account account = (Account) processInstance.getVariables().get("account");
        assertNotNull(account.getPerson());
    }

    @Test
    public void testUserTaskWithAssignment() throws Exception {
        ProcessDialectRegistry.setDialect("custom", new ProcessDialect() {

            @Override
            public ReturnValueEvaluatorBuilder getReturnValueEvaluatorBuilder() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public ProcessClassBuilder getProcessClassBuilder() {
                return null;
            }

            @Override
            public AssignmentBuilder getAssignmentBuilder() {
                return (context, assignment, sourceExpr, targetExpr, contextResolver, isInput) -> assignment.setMetaData("Action", (AssignmentAction) (workItem, context1) -> {
                    assertEquals("from_expression", assignment.getFrom());
                    assertEquals("to_expression", assignment.getTo());
                });
            }

            @Override
            public ActionBuilder getActionBuilder() {
                return null;
            }

            @Override
            public void addProcess(ProcessBuildContext context) {

            }
        });
        kruntime = createKogitoProcessRuntime("BPMN2-DataOutputAssignmentCustomExpressionLang.bpmn2");

        Process scriptProcess = kruntime.getKieBase().getProcess("process");
        assertThat(scriptProcess).isNotNull();
        Node[] nodes = ((NodeContainer) scriptProcess).getNodes();
        assertThat(nodes).hasSize(3);
        assertThat(nodes).filteredOn(n -> n instanceof WorkItemNode).allMatch(this::matchExpectedAssociationSetup);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);

        Map<String, Object> params = new HashMap<>();
        params.put("name", "John");
        KogitoProcessInstance processInstance = kruntime.startProcess("process", params);

        kruntime.abortProcessInstance(processInstance.getStringId());

        assertProcessInstanceAborted(processInstance);
    }

    protected boolean matchExpectedAssociationSetup(Node node) {
        List<DataAssociation> inputs = ((WorkItemNode) node).getInAssociations();
        List<DataAssociation> outputs = ((WorkItemNode) node).getOutAssociations();

        assertThat(inputs).hasSize(1);
        assertThat(outputs).hasSize(1);

        DataAssociation association = inputs.get(0);
        assertThat(association.getAssignments()).hasSize(1);
        assertThat(association.getSources()).hasSize(2);

        Assignment assignment = association.getAssignments().get(0);
        assertThat(assignment.getDialect()).isEqualTo("custom");
        assertThat(assignment.getFrom()).isEqualTo("from_expression");
        assertThat(assignment.getTo()).isEqualTo("to_expression");

        association = outputs.get(0);
        assertThat(association.getAssignments()).hasSize(1);
        assertThat(association.getSources()).hasSize(2);

        assignment = association.getAssignments().get(0);
        assertThat(assignment.getDialect()).isEqualTo("custom");
        assertThat(assignment.getFrom()).isEqualTo("from_expression");
        assertThat(assignment.getTo()).isEqualTo("to_expression");

        return true;
    }
}
