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

import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.jbpm.bpmn2.objects.ExceptionService;
import org.jbpm.bpmn2.objects.MyError;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.Context;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class IntermediateEventTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };

    private Logger logger = LoggerFactory
            .getLogger(IntermediateEventTest.class);

    private KieSession ksession;
    
    public IntermediateEventTest(boolean persistence) {
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
    public void testSignalBoundaryEvent() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn",
                "BPMN2-IntermediateThrowEventSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ProcessInstance processInstance = ksession
                .startProcess("BoundarySignalOnTask");

        ProcessInstance processInstance2 = ksession
                .startProcess("SignalIntermediateEvent");
        assertProcessInstanceFinished(processInstance2, ksession);

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testSignalBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                logger.info("After node left {}", event.getNodeInstance().getNodeName());
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("After node triggered {}"
                        , event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                logger.info("Before node left {}"
                        , event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("Before node triggered {}"
                        , event.getNodeInstance().getNodeName());
            }

        });
        ProcessInstance processInstance = ksession
                .startProcess("BoundarySignalOnTask");
        ksession.signalEvent("MySignal", "value");
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSignalBoundaryEventOnTaskComplete() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                logger.info("After node left {}"
                        , event.getNodeInstance().getNodeName());
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("After node triggered {}"
                        , event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                logger.info("Before node left {}"
                        , event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("Before node triggered {}"
                        , event.getNodeInstance().getNodeName());
            }

        });
        ProcessInstance processInstance = ksession
                .startProcess("BoundarySignalOnTask");
        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);
        ksession.signalEvent("MySignal", "value");
        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSignalBoundaryEventInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("SignalBoundaryEvent");
        assertProcessInstanceActive(processInstance);

        ksession = restoreSession(ksession, true);
        ksession.signalEvent("MyMessage", null);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSignalIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventSignal.bpmn2");
        ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess(
                "SignalIntermediateEvent", params);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                processInstance.getState());

    }

    @Test
    public void testSignalBetweenProcesses() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "BPMN2-IntermediateCatchSignalSingle.bpmn2",
                "BPMN2-IntermediateThrowEventSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-IntermediateCatchSignalSingle");
        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);

        ProcessInstance processInstance2 = ksession
                .startProcess("SignalIntermediateEvent");
        assertProcessInstanceFinished(processInstance2, ksession);

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testEventBasedSplit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        // No
        processInstance = ksession.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.signalEvent("No", "NoValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEventBasedSplitBefore() throws Exception {
        // signaling before the split is reached should have no effect
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceActive(processInstance);
        // No
        processInstance = ksession.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new DoNothingWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        ksession.signalEvent("No", "NoValue", processInstance.getId());
        assertProcessInstanceActive(processInstance);

    }

    @Test
    public void testEventBasedSplitAfter() throws Exception {
        // signaling the other alternative after one has been selected should
        // have no effect
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new DoNothingWorkItemHandler());
        // No
        ksession.signalEvent("No", "NoValue", processInstance.getId());

    }

    @Test
    public void testEventBasedSplit2() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit2.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
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
        assertProcessInstanceActive(processInstance);
        Thread.sleep(800);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEventBasedSplit3() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit3.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        Person jack = new Person();
        jack.setName("Jack");
        // Yes
        ProcessInstance processInstance = ksession
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        // Condition
        processInstance = ksession.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.insert(jack);
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEventBasedSplit4() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit4.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.signalEvent("Message-YesMessage", "YesValue",
                processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // No
        processInstance = ksession.startProcess("com.sample.test");
        ksession.signalEvent("Message-NoMessage", "NoValue",
                processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEventBasedSplit5() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit5.bpmn2");
        ksession = createKnowledgeSession(kbase);
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
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task",
                receiveTaskHandler);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
        assertProcessInstanceFinished(processInstance, ksession);
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
        assertProcessInstanceFinished(processInstance, ksession);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");

    }

    @Test
    public void testEventSubprocessSignal() throws Exception {
        String [] nodes = { 
                "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "sub-script", "end-sub"
        };
        runTestEventSubprocessSignal("BPMN2-EventSubprocessSignal.bpmn2", nodes);
    }
    
    @Test
    public void testEventSubprocessSignalNested() throws Exception {
        String [] nodes = { 
                "Start",
                "Sub Process",
                "Sub Start",
                "Sub Sub Process",
                "Sub Sub Start",
                "Sub Sub User Task",
                "Sub Sub Sub Process",
                "start-sub",
                "sub-script",
                "end-sub",
                "Sub Sub End",
                "Sub End ",
                "End"
        };
        runTestEventSubprocessSignal("BPMN2-EventSubprocessSignal-Nested.bpmn2", nodes);
    }
    
    public void runTestEventSubprocessSignal(String processFile, String [] completedNodes) throws Exception { 
        KieBase kbase = createKnowledgeBase(processFile);
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("sub-script")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

        ksession.signalEvent("MySignal", null, processInstance.getId());
        assertProcessInstanceActive(processInstance);
        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), completedNodes );
        assertEquals(4, executednodes.size());

    }

    @Test
    public void testEventSubprocessSignalWithStateNode() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessSignalWithStateNode.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("User Task 2")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        WorkItem workItemTopProcess = workItemHandler.getWorkItem();

        ksession.signalEvent("MySignal", null, processInstance.getId());
        assertProcessInstanceActive(processInstance);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        assertNotNull(workItemTopProcess);
        ksession.getWorkItemManager().completeWorkItem(
                workItemTopProcess.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "User Task 2", "end-sub");
        assertEquals(4, executednodes.size());

    }

    @Test
    public void testEventSubprocessSignalInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessSignalInterrupting.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

        ksession.signalEvent("MySignal", null, processInstance.getId());

        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());

    }

    @Test
    public void testEventSubprocessMessage() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessMessage.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessMessage");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

        ksession.signalEvent("Message-HelloMessage", null,
                processInstance.getId());
        ksession.signalEvent("Message-HelloMessage", null);
        ksession.signalEvent("Message-HelloMessage", null);
        ksession.signalEvent("Message-HelloMessage", null);
        ksession.getProcessInstance(processInstance.getId());
        ksession.getProcessInstance(processInstance.getId());
        ksession.getProcessInstance(processInstance.getId());
        ksession.getProcessInstance(processInstance.getId());
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(4, executednodes.size());

    }

    @Test
    public void testEventSubprocessEscalation() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessEscalation.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessEscalation");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());

    }

    @Test
    public void testEventSubprocessError() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessError.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessError");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());

    }
    
    @Test
    public void testEventSubprocessErrorThrowOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessError.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler(){

            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                throw new MyError();
                
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                manager.abortWorkItem(workItem.getId());
            }
  
            
        });
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessError");
 
        assertProcessInstanceFinished(processInstance, ksession);
        assertProcessInstanceAborted(processInstance);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());

    }

   

    @Test
    public void testEventSubprocessTimer() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessTimer.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessTimer");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);

        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());

    }

    @Test
    @RequirePersistence
    public void testEventSubprocessTimerCycle() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessTimerCycle.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessTimer");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(2000);

        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "start-sub", "Script Task 1", "end-sub");
        assertEquals(4, executednodes.size());

    }

    @Test
    public void testEventSubprocessConditional() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessConditional.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessConditional");
        assertProcessInstanceActive(processInstance);

        Person person = new Person();
        person.setName("john");
        ksession.insert(person);
        ksession.fireAllRules();

        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());

    }

    @Test
    public void testMessageIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess(
                "MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testMessageBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryMessageEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());

        ProcessInstance processInstance = ksession
                .startProcess("BoundaryMessageOnTask");
        ksession.signalEvent("Message-HelloMessage", "message data");
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testMessageBoundaryEventOnTaskComplete() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryMessageEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        ProcessInstance processInstance = ksession
                .startProcess("BoundaryMessageOnTask");
        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);
        ksession.signalEvent("Message-HelloMessage", "message data");
        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess",
                "User Task", "User Task2", "End1");

    }

    @Test
    public void testEscalationBoundaryEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EscalationBoundaryEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("EscalationBoundaryEvent");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testEscalationBoundaryEventInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EscalationBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("EscalationBoundaryEvent");
        assertProcessInstanceCompleted(processInstance);
        // TODO: testEscalationBoundaryEventInterrupting: check for cancellation of task

    }

    @Test
    public void testEscalationBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EscalationBoundaryEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                logger.info("After node left {}"
                        , event.getNodeInstance().getNodeName());
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("After node triggered {}"
                        , event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                logger.info("Before node left {}"
                        , event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                logger.info("Before node triggered {}"
                        , event.getNodeInstance().getNodeName());
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
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testEscalationIntermediateThrowEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventEscalation.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("EscalationIntermediateThrowEvent");
        assertProcessInstanceAborted(processInstance);

    }

    @Test
    public void testErrorBoundaryEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("ErrorBoundaryEvent");
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testErrorBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",handler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-ErrorBoundaryEventOnTask");

        List<WorkItem> workItems = handler.getWorkItems();
        assertEquals(2, workItems.size());

        WorkItem workItem = workItems.get(0);
        if (!"john".equalsIgnoreCase((String) workItem.getParameter("ActorId"))) {
            workItem = workItems.get(1);
        }

        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertProcessInstanceAborted(processInstance);
        assertNodeTriggered(processInstance.getId(), "start", "split", "User Task", "User task error attached", "error end event");
        assertNotNodeTriggered(processInstance.getId(), "Script Task", "error1", "error2");
    }
    
    @Test
    public void testCatchErrorBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new TestWorkItemHandler(){

            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                if (workItem.getParameter("ActorId").equals("mary")) {
                    throw new MyError();
                }
            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
                manager.abortWorkItem(workItem.getId());
            }
  
            
        });
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-ErrorBoundaryEventOnTask");

        assertProcessInstanceActive(processInstance);
        assertNodeTriggered(processInstance.getId(), "start", "split", "User Task", "User task error attached",
                "Script Task", "error1", "error2");
 
    }

    @Test
    public void testTimerBoundaryEventDuration() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testTimerBoundaryEventDurationISO() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDurationISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1500);
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testTimerBoundaryEventDateISO() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-TimerBoundaryEventDateISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        HashMap<String, Object> params = new HashMap<String, Object>();
        DateTime now = new DateTime(System.currentTimeMillis());
        now.plus(2000);
        params.put("date", now.toString());
        ProcessInstance processInstance = ksession.startProcess(
                "TimerBoundaryEvent", params);
        assertProcessInstanceActive(processInstance);
        Thread.sleep(2000);
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testTimerBoundaryEventCycle1() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle1.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testTimerBoundaryEventCycle2() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle2.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());
        Thread.sleep(1000);

    }

    @Test
    @RequirePersistence(false)
    public void testTimerBoundaryEventCycleISO() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycleISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());
        Thread.sleep(1000);
    }
    
    @Test
    @RequirePersistence
    public void testTimerBoundaryEventCycleISOWithPersistence() throws Exception {
        // load up the knowledge base
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycleISO.bpmn2");

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final List<Long> list = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName().equals("TimerEvent")) {
                    list.add(event.getProcessInstance().getId());
                }
            }

        };
        ksession.addEventListener(listener);
        int sessionId = ksession.getId();
        Environment env = ksession.getEnvironment();
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);

        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        logger.info("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        ksession.addEventListener(listener);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        Thread.sleep(2000);
        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());
        Thread.sleep(1000);
        assertEquals(2, list.size());
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testTimerBoundaryEventInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        logger.debug("Firing timer");
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testTimerBoundaryEventInterruptingOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterruptingOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        logger.debug("Firing timer");
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventSignal() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("MyMessage", "SomeValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testIntermediateCatchEventMessage() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventMessage.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("Message-HelloMessage", "SomeValue",
                processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventTimerDuration() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventTimerDateISO() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventTimerDateISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        HashMap<String, Object> params = new HashMap<String, Object>();
        DateTime now = new DateTime(System.currentTimeMillis());
        now.plus(2000);
        params.put("date", now.toString());
        ProcessInstance processInstance = ksession.startProcess(
                "IntermediateCatchEvent", params);
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        Thread.sleep(2000);
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventTimerDurationISO() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDurationISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1.5 second for timer to trigger
        Thread.sleep(1500);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventTimerCycle1() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle1.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventTimerCycleISO() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
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
        assertProcessInstanceActive(processInstance);

        Thread.sleep(500);
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
        }
        assertEquals(5, list.size());

    }

    @Test
    public void testIntermediateCatchEventTimerCycle2() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle2.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());
        Thread.sleep(1000);

    }

    @Test
    public void testIntermediateCatchEventCondition() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventConditionFilterByProcessInstance()
            throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventConditionFilterByProcessInstance.bpmn2");
        ksession = createKnowledgeSession(kbase);
        Map<String, Object> params1 = new HashMap<String, Object>();
        params1.put("personId", Long.valueOf(1L));
        Person person1 = new Person();
        person1.setId(1L);
        WorkflowProcessInstance pi1 = (WorkflowProcessInstance) ksession
                .createProcessInstance(
                        "IntermediateCatchEventConditionFilterByProcessInstance",
                        params1);
        long pi1id = pi1.getId();

        ksession.insert(pi1);
        FactHandle personHandle1 = ksession.insert(person1);

        ksession.startProcessInstance(pi1.getId());

        Map<String, Object> params2 = new HashMap<String, Object>();
        params2.put("personId", Long.valueOf(2L));
        Person person2 = new Person();
        person2.setId(2L);

        WorkflowProcessInstance pi2 = (WorkflowProcessInstance) ksession
                .createProcessInstance(
                        "IntermediateCatchEventConditionFilterByProcessInstance",
                        params2);
        long pi2id = pi2.getId();

        ksession.insert(pi2);
        FactHandle personHandle2 = ksession.insert(person2);

        ksession.startProcessInstance(pi2.getId());

        person1.setName("John");
        ksession.update(personHandle1, person1);
        ksession.fireAllRules();

        assertNull("First process should be completed",
                ksession.getProcessInstance(pi1id));
        assertNotNull("Second process should NOT be completed",
                ksession.getProcessInstance(pi2id));

    }

    @Test
    @RequirePersistence(false)
    public void testIntermediateCatchEventTimerCycleWithError()
            throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 0);
        ProcessInstance processInstance = ksession.startProcess(
                "IntermediateCatchEvent", params);
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        // ((WorkflowProcessInstance)ksession.getProcessInstance(processInstance.getId())).setVariable("x", 0);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);

        processInstance = ksession.getProcessInstance(processInstance.getId());
        Integer xValue = (Integer) ((WorkflowProcessInstance) processInstance)
                .getVariable("x");
        assertEquals(new Integer(3), xValue);

        ksession.abortProcessInstance(processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

    }
    
    @Test
    @RequirePersistence
    public void testIntermediateCatchEventTimerCycleWithErrorWithPersistence() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);

        final long piId = processInstance.getId();
        ksession.execute(new GenericCommand<Void>() {

            public Void execute(Context context) {
                StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((KnowledgeCommandContext) context).getKieSession();
                WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.getProcessInstance(piId);
                processInstance.setVariable("x", 0);
                return null;
            }
        });
        
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance);
        
        Integer xValue = ksession.execute(new GenericCommand<Integer>() {

            public Integer execute(Context context) {
                StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((KnowledgeCommandContext) context).getKieSession();
                WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.getProcessInstance(piId);
                return (Integer) processInstance.getVariable("x");
                
            }
        });
        assertEquals(new Integer(2), xValue);
        ksession.abortProcessInstance(processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    @Ignore(value="This test tested an incorrect implemenation of compensation")
    public void testCompensateIntermediateThrowEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventCompensate.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("CompensateIntermediateThrowEvent");
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testNoneIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventNone.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess(
                "NoneIntermediateEvent", null);
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testLinkIntermediateEvent() throws Exception {

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateLinkEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("linkEventProcessExample");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testLinkEventCompositeProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-LinkEventCompositeProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Composite");
        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testConditionalBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("BoundarySignalOnTask");

        Person person = new Person();
        person.setName("john");
        ksession.insert(person);
        ksession.fireAllRules();
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testConditionalBoundaryEventOnTaskComplete() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        ProcessInstance processInstance = ksession
                .startProcess("BoundarySignalOnTask");

        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);
        Person person = new Person();
        person.setName("john");
        // as the node that boundary event is attached to has been completed insert will not have any effect
        ksession.insert(person);
        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess",
                "User Task", "User Task2", "End1");

    }

    @Test
    public void testConditionalBoundaryEventOnTaskActiveOnStartup()
            throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryConditionalEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());

        ProcessInstance processInstance = ksession
                .startProcess("BoundarySignalOnTask");
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);
        ksession.fireAllRules();

        assertProcessInstanceCompleted(processInstance);
        assertNodeTriggered(processInstance.getId(), "StartProcess",
                "User Task", "Boundary event", "Condition met", "End2");

    }

    @Test
    public void testConditionalBoundaryEventInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ConditionalBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("ConditionalBoundaryEvent");
        assertProcessInstanceActive(processInstance);

        ksession = restoreSession(ksession, true);
        Person person = new Person();
        person.setName("john");
        ksession.insert(person);
        ksession.fireAllRules();

        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello",
                "StartSubProcess", "Task", "BoundaryEvent", "Goodbye",
                "EndProcess");

    }

    @Test
    public void testErrorSignallingExceptionServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionServiceProcess-ErrorSignalling.bpmn2");
        ksession = createKnowledgeSession(kbase);

        StandaloneBPMNProcessTest.runTestErrorSignallingExceptionServiceTask(ksession);
    }

    @Test
    public void testSignallingExceptionServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionServiceProcess-Signalling.bpmn2");
        ksession = createKnowledgeSession(kbase);

        StandaloneBPMNProcessTest.runTestSignallingExceptionServiceTask(ksession);
    }

    @Test
    public void testSignalBoundaryEventOnSubprocessTakingDifferentPaths() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "BPMN2-SignalBoundaryOnSubProcess.bpmn");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        ProcessInstance processInstance = ksession.startProcess("jbpm.testing.signal");
        assertProcessInstanceActive(processInstance);
        
        ksession.signalEvent("continue", null, processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        
        ksession.dispose();
        
        ksession = createKnowledgeSession(kbase);

        processInstance = ksession.startProcess("jbpm.testing.signal");
        assertProcessInstanceActive(processInstance);
        
        ksession.signalEvent("forward", null);
        assertProcessInstanceFinished(processInstance, ksession);
        
        ksession.dispose();
    }


    
}
