/*
Copyright 2013 JBoss Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package org.jbpm.bpmn2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.WorkingMemory;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.event.ActivationCancelledEvent;
import org.drools.core.event.ActivationCreatedEvent;
import org.drools.core.event.AfterActivationFiredEvent;
import org.drools.core.event.AgendaGroupPoppedEvent;
import org.drools.core.event.AgendaGroupPushedEvent;
import org.drools.core.event.BeforeActivationFiredEvent;
import org.drools.core.event.RuleFlowGroupActivatedEvent;
import org.drools.core.event.RuleFlowGroupDeactivatedEvent;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.event.process.ProcessVariableChangedEvent;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.MatchCancelledEvent;
import org.kie.api.event.rule.MatchCreatedEvent;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class ActivityTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };

    private Logger logger = LoggerFactory.getLogger(ActivityTest.class);

    private KieSession ksession;
    private KieSession ksession2;

    public ActivityTest(boolean persistence) throws Exception {
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
        if (ksession2 != null) {
            ksession2.dispose();
            ksession2 = null;
        }
    }

    @Test
    public void testMinimalProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MinimalProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testMinimalProcessImplicit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MinimalProcessImplicit.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testMinimalProcessWithGraphical() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MinimalProcessWithGraphical.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testMinimalProcessWithDIGraphical() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MinimalProcessWithDIGraphical.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Minimal");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testCompositeProcessWithDIGraphical() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-CompositeProcessWithDIGraphical.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Composite");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testScriptTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ScriptTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    @RequirePersistence
    public void testScriptTaskWithHistoryLog() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ScriptTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertProcessInstanceCompleted(processInstance);

        List<NodeInstanceLog> logs = JPAProcessInstanceDbLog
                .findNodeInstances(processInstance.getId());
        assertNotNull(logs);
        assertEquals(6, logs.size());

        for (NodeInstanceLog log : logs) {
            assertNotNull(log.getDate());
        }

        ProcessInstanceLog pilog = JPAProcessInstanceDbLog
                .findProcessInstance(processInstance.getId());
        assertNotNull(pilog);
        assertNotNull(pilog.getEnd());

        List<ProcessInstanceLog> pilogs = JPAProcessInstanceDbLog
                .findActiveProcessInstances(processInstance.getProcessId());
        assertNotNull(pilogs);
        assertEquals(0, pilogs.size());

    }

    @Test
    public void testRuleTask() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-RuleTask.bpmn2",
                "BPMN2-RuleTask.drl");
        ksession = createKnowledgeSession(kbase);
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        ProcessInstance processInstance = ksession.startProcess("RuleTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        assertTrue(list.size() == 1);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testRuleTask2() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-RuleTask2.bpmn2",
                "BPMN2-RuleTask2.drl");
        ksession = createKnowledgeSession(kbase);
        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "SomeString");
        ProcessInstance processInstance = ksession.startProcess("RuleTask",
                params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.fireAllRules();
        assertTrue(list.size() == 0);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    @RequirePersistence(false)
    public void testRuleTaskWithFacts() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-RuleTaskWithFact.bpmn2",
                "BPMN2-RuleTask3.drl");
        ksession = createKnowledgeSession(kbase);

        ksession.addEventListener(new AgendaEventListener() {
            public void matchCreated(MatchCreatedEvent event) {
                ksession.fireAllRules();
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
                ksession.fireAllRules();
            }

            public void afterMatchFired(AfterMatchFiredEvent event) {
            }

        });

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "SomeString");
        ProcessInstance processInstance = ksession.startProcess("RuleTask",
                params);
        assertProcessInstanceFinished(processInstance, ksession);

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
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    @RequirePersistence
    public void testRuleTaskWithFactsWithPersistence() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-RuleTaskWithFact.bpmn2",
                "BPMN2-RuleTask3.drl");
        ksession = createKnowledgeSession(kbase);

        final org.drools.core.event.AgendaEventListener agendaEventListener = new org.drools.core.event.AgendaEventListener() {
            public void activationCreated(ActivationCreatedEvent event,
                    WorkingMemory workingMemory) {
                ksession.fireAllRules();
            }

            public void activationCancelled(ActivationCancelledEvent event,
                    WorkingMemory workingMemory) {
            }

            public void beforeActivationFired(BeforeActivationFiredEvent event,
                    WorkingMemory workingMemory) {
            }

            public void afterActivationFired(AfterActivationFiredEvent event,
                    WorkingMemory workingMemory) {
            }

            public void agendaGroupPopped(AgendaGroupPoppedEvent event,
                    WorkingMemory workingMemory) {
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event,
                    WorkingMemory workingMemory) {
            }

            public void beforeRuleFlowGroupActivated(
                    RuleFlowGroupActivatedEvent event,
                    WorkingMemory workingMemory) {
            }

            public void afterRuleFlowGroupActivated(
                    RuleFlowGroupActivatedEvent event,
                    WorkingMemory workingMemory) {
                workingMemory.fireAllRules();
            }

            public void beforeRuleFlowGroupDeactivated(
                    RuleFlowGroupDeactivatedEvent event,
                    WorkingMemory workingMemory) {
            }

            public void afterRuleFlowGroupDeactivated(
                    RuleFlowGroupDeactivatedEvent event,
                    WorkingMemory workingMemory) {
            }
        };
        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(agendaEventListener);
        ksession.addEventListener(new DebugAgendaEventListener());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "SomeString");
        ProcessInstance processInstance = ksession.startProcess("RuleTask",
                params);
        assertProcessInstanceFinished(processInstance, ksession);

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
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testRuleTaskAcrossSessions() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-RuleTask.bpmn2",
                "BPMN2-RuleTask.drl");
        ksession = createKnowledgeSession(kbase);
        ksession2 = createKnowledgeSession(kbase);
        List<String> list1 = new ArrayList<String>();
        ksession.setGlobal("list", list1);
        List<String> list2 = new ArrayList<String>();
        ksession2.setGlobal("list", list2);
        ProcessInstance processInstance1 = ksession.startProcess("RuleTask");
        ProcessInstance processInstance2 = ksession2.startProcess("RuleTask");
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance1, ksession);
        assertProcessInstanceActive(processInstance2);
        ksession2.fireAllRules();
        assertProcessInstanceFinished(processInstance2, ksession2);
    }

    @Test
    public void testUserTaskWithDataStoreScenario() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithDataStore.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ksession.startProcess("UserProcess");
        // we can't test further as user tasks are asynchronous.
    }

    @Test
    public void testUserTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
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
        assertProcessInstanceFinished(processInstance, ksession);
        ksession.dispose();
    }

    @Test
    @RequirePersistence
    public void testProcesWithHumanTaskWithTimer() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SubProcessWithTimer.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = ksession.startProcess("subproc",
                params);

        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), null);

        int sessionId = ksession.getId();
        Environment env = ksession.getEnvironment();

        ksession.dispose();
        Thread.sleep(3000);

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        Thread.sleep(3000);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testCallActivity() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-CallActivity.bpmn2",
                "BPMN2-CallActivitySubProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        ProcessInstance processInstance = ksession.startProcess(
                "ParentProcess", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals("new value",
                ((WorkflowProcessInstance) processInstance).getVariable("y"));
    }

	@Test
	public void testCallActivity2() throws Exception {
		KieBase kbase = createKnowledgeBase("BPMN2-CallActivity2.bpmn2",
				"BPMN2-CallActivitySubProcess.bpmn2");
		ksession = createKnowledgeSession(kbase);
		TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
				workItemHandler);
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("x", "oldValue");
		ProcessInstance processInstance = ksession.startProcess(
				"ParentProcess", params);
		assertProcessInstanceActive(processInstance);
		assertEquals("new value",
				((WorkflowProcessInstance) processInstance).getVariable("y"));

		ksession = restoreSession(ksession, true);
		WorkItem workItem = workItemHandler.getWorkItem();
		assertNotNull(workItem);
		assertEquals("krisv", workItem.getParameter("ActorId"));
		ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

		assertProcessInstanceFinished(processInstance, ksession);
	}

    @Test
    public void testCallActivityByName() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-CallActivityByName.bpmn2",
                "BPMN2-CallActivitySubProcess.bpmn2",
                "BPMN2-CallActivitySubProcessV2.bpmn2");
        ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        ProcessInstance processInstance = ksession.startProcess(
                "ParentProcess", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals("new value V2",
                ((WorkflowProcessInstance) processInstance).getVariable("y"));
    }

    @Test
    @RequirePersistence
    public void testCallActivityWithHistoryLog() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-CallActivity.bpmn2",
                "BPMN2-CallActivitySubProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        ProcessInstance processInstance = ksession.startProcess(
                "ParentProcess", params);
        assertProcessInstanceCompleted(processInstance);
        assertEquals("new value",
                ((WorkflowProcessInstance) processInstance).getVariable("y"));

        List<ProcessInstanceLog> subprocesses = JPAProcessInstanceDbLog
                .findSubProcessInstances(processInstance.getId());
        assertNotNull(subprocesses);
        assertEquals(1, subprocesses.size());
    }

    @Test
    @RequirePersistence
    public void testCallActivityWithTimer() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ParentProcess.bpmn2",
                "BPMN2-SubProcessWithTimer.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = ksession.startProcess(
                "ParentProcess", params);

        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), null);

        Map<String, Object> res = new HashMap<String, Object>();
        res.put("sleep", "2s");
        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), res);

        int sessionId = ksession.getId();
        Environment env = ksession.getEnvironment();

        System.out.println("dispose");
        ksession.dispose();
        Thread.sleep(3000);

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        Thread.sleep(3000);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSubProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SubProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
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
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testSubProcessWithTerminateEndEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SubProcessWithTerminateEndEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<String> list = new ArrayList<String>();
        ksession.addEventListener(new DefaultProcessEventListener() {

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                list.add(event.getNodeInstance().getNodeName());
            }
        });
        ProcessInstance processInstance = ksession
                .startProcess("SubProcessTerminate");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(7, list.size());
    }

    @Test
    public void testSubProcessWithTerminateEndEventProcessScope()
            throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SubProcessWithTerminateEndEventProcessScope.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<String> list = new ArrayList<String>();
        ksession.addEventListener(new DefaultProcessEventListener() {

            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                list.add(event.getNodeInstance().getNodeName());
            }
        });
        ProcessInstance processInstance = ksession
                .startProcess("SubProcessTerminate");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(5, list.size());
    }

    @Test
    public void testAdHocSubProcess() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-AdHocSubProcess.bpmn2",
                "BPMN2-AdHocSubProcess.drl");
        ksession = createKnowledgeSession(kbase);
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

    @Test
    public void testAdHocSubProcessAutoComplete() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-AdHocSubProcessAutoComplete.bpmn2",
                "BPMN2-AdHocSubProcess.drl");
        ksession = createKnowledgeSession(kbase);
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
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testAdHocSubProcessAutoCompleteDynamicTask() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-AdHocSubProcessAutoComplete.bpmn2",
                "BPMN2-AdHocSubProcess.drl");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("OtherTask",
                workItemHandler2);
        ProcessInstance processInstance = ksession
                .startProcess("AdHocSubProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        DynamicNodeInstance dynamicContext = (DynamicNodeInstance) ((WorkflowProcessInstance) processInstance)
                .getNodeInstances().iterator().next();
        DynamicUtils.addDynamicWorkItem(dynamicContext, ksession, "OtherTask",
                new HashMap<String, Object>());
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
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler2.getWorkItem();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        ksession.dispose();
    }

    @Test
    public void testAdHocSubProcessAutoCompleteDynamicSubProcess()
            throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-AdHocSubProcessAutoComplete.bpmn2",
                "BPMN2-AdHocSubProcess.drl", "BPMN2-MinimalProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
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
        DynamicNodeInstance dynamicContext = (DynamicNodeInstance) ((WorkflowProcessInstance) processInstance)
                .getNodeInstances().iterator().next();
        DynamicUtils.addDynamicSubProcess(dynamicContext, ksession, "Minimal",
                new HashMap<String, Object>());
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        // assertProcessInstanceActive(processInstance.getId(), ksession);
        // workItem = workItemHandler2.getWorkItem();
        // ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testAdHocSubProcessAutoCompleteDynamicSubProcess2()
            throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-AdHocSubProcessAutoComplete.bpmn2",
                "BPMN2-AdHocSubProcess.drl", "BPMN2-ServiceProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
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
        DynamicNodeInstance dynamicContext = (DynamicNodeInstance) ((WorkflowProcessInstance) processInstance)
                .getNodeInstances().iterator().next();
        DynamicUtils.addDynamicSubProcess(dynamicContext, ksession,
                "ServiceProcess", new HashMap<String, Object>());
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler2.getWorkItem();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testAdHocProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-AdHocProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("AdHocProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        logger.debug("Triggering node");
        ksession.signalEvent("Task1", null, processInstance.getId());
        assertProcessInstanceActive(processInstance);
        ksession.signalEvent("User1", null, processInstance.getId());
        assertProcessInstanceActive(processInstance);
        ksession.insert(new Person());
        ksession.signalEvent("Task3", null, processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testAdHocProcessDynamicTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-AdHocProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("AdHocProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        logger.debug("Triggering node");
        ksession.signalEvent("Task1", null, processInstance.getId());
        assertProcessInstanceActive(processInstance);
        TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("OtherTask",
                workItemHandler2);
        DynamicUtils.addDynamicWorkItem(processInstance, ksession, "OtherTask",
                new HashMap<String, Object>());
        WorkItem workItem = workItemHandler2.getWorkItem();
        assertNotNull(workItem);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        ksession.signalEvent("User1", null, processInstance.getId());
        assertProcessInstanceActive(processInstance);
        ksession.insert(new Person());
        ksession.signalEvent("Task3", null, processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testAdHocProcessDynamicSubProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-AdHocProcess.bpmn2",
                "BPMN2-MinimalProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("AdHocProcess");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        logger.debug("Triggering node");
        ksession.signalEvent("Task1", null, processInstance.getId());
        assertProcessInstanceActive(processInstance);
        TestWorkItemHandler workItemHandler2 = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("OtherTask",
                workItemHandler2);
        DynamicUtils.addDynamicSubProcess(processInstance, ksession, "Minimal",
                new HashMap<String, Object>());
        ksession = restoreSession(ksession, true);
        ksession.signalEvent("User1", null, processInstance.getId());
        assertProcessInstanceActive(processInstance);
        ksession.insert(new Person());
        ksession.signalEvent("Task3", null, processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ServiceProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
                .startProcess("ServiceProcess", params);
        assertProcessInstanceFinished(processInstance, ksession);
        assertEquals("Hello john!", processInstance.getVariable("s"));
    }
    
    @Test
    public void testServiceTaskNoInterfaceName() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ServiceTask-web-service.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                new SystemOutWorkItemHandler() {

                    @Override
                    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                        assertEquals("SimpleService", workItem.getParameter("Interface"));
                        assertEquals("hello", workItem.getParameter("Operation"));
                        assertEquals("java.lang.String", workItem.getParameter("ParameterType"));
                        assertEquals("##WebService", workItem.getParameter("implementation"));
                        assertEquals("hello", workItem.getParameter("operationImplementationRef"));
                        assertEquals("SimpleService", workItem.getParameter("interfaceImplementationRef"));
                        super.executeWorkItem(workItem, manager);
                    }
            
        });
        Map<String, Object> params = new HashMap<String, Object>();
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
                .startProcess("org.jboss.qa.jbpm.CallWS", params);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testSendTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SendTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
                .startProcess("SendTask", params);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testReceiveTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ReceiveTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession
                .startProcess("ReceiveTask");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        receiveTaskHandler.setKnowledgeRuntime(ksession);
        receiveTaskHandler.messageReceived("HelloMessage", "Hello john!");
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    @RequirePersistence(false)
    public void testBusinessRuleTask() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-BusinessRuleTask.bpmn2",
                "BPMN2-BusinessRuleTask.drl");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-BusinessRuleTask");

        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    @RequirePersistence(true)
    public void testBusinessRuleTaskWithPersistence() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-BusinessRuleTask.bpmn2",
                "BPMN2-BusinessRuleTask.drl");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-BusinessRuleTask");

        ksession = restoreSession(ksession, true);
        ksession.addEventListener(new RuleAwareProcessEventLister());

        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testBusinessRuleTaskDynamic() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-BusinessRuleTaskDynamic.bpmn2",
                "BPMN2-BusinessRuleTask.drl");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicrule", "MyRuleFlow");
        ProcessInstance processInstance = ksession.startProcess(
                "BPMN2-BusinessRuleTask", params);

        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testBusinessRuleTaskWithDataInputsWithPersistence()
            throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-BusinessRuleTaskWithDataInputs.bpmn2",
                "BPMN2-BusinessRuleTaskWithDataInput.drl");
        ksession = createKnowledgeSession(kbase);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", new Person());
        ProcessInstance processInstance = ksession.startProcess(
                "BPMN2-BusinessRuleTask", params);

        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testNullVariableInScriptTaskProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-NullVariableInScriptTaskProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("nullVariableInScriptAfterTimer");

        assertProcessInstanceActive(processInstance);

        long sleep = 1000;
        logger.debug("Sleeping " + sleep / 1000 + " seconds.");
        Thread.sleep(sleep);

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testScriptTaskWithVariableByName() throws Exception {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("myVar", "test");
        KieBase kbase = createKnowledgeBase("BPMN2-ProcessWithVariableName.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess(
                "BPMN2-ProcessWithVariableName", params);
        assertProcessInstanceCompleted(processInstance);
    }

    @Test
    public void testCallActivityWithBoundaryEvent() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "BPMN2-CallActivityWithBoundaryEvent.bpmn2",
                "BPMN2-CallActivitySubProcessWithBoundaryEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        ProcessInstance processInstance = ksession.startProcess(
                "ParentProcess", params);

        Thread.sleep(3000);

        assertProcessInstanceFinished(processInstance, ksession);
        // assertEquals("new timer value",
        // ((WorkflowProcessInstance) processInstance).getVariable("y"));
        // first check the parent process executed nodes
        assertNodeTriggered(processInstance.getId(), "StartProcess",
                "CallActivity", "Boundary event", "Script Task", "end");
        // then check child process executed nodes - is there better way to get child process id than simply increment?
        assertNodeTriggered(processInstance.getId() + 1, "StartProcess2",
                "User Task");
    }

    @Test
    public void testUserTaskWithBooleanOutput() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithBooleanOutput.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("com.sample.boolean");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        HashMap<String, Object> output = new HashMap<String, Object>();
        output.put("isCheckedCheckbox", "true");
        ksession.getWorkItemManager()
                .completeWorkItem(workItem.getId(), output);
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testUserTaskWithSimData() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTaskWithSimulationMetaData.bpmn2");
        ksession = createKnowledgeSession(kbase);
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
        assertProcessInstanceFinished(processInstance, ksession);
    }

}
