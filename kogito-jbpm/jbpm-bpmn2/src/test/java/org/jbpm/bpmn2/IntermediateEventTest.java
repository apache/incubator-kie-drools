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

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.drools.core.command.SingleSessionCommandService;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.RegistryContext;
import org.drools.core.command.runtime.process.SetProcessInstanceVariablesCommand;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.process.instance.WorkItemHandler;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.bpmn2.test.RequirePersistence;
import org.jbpm.persistence.api.ProcessPersistenceContext;
import org.jbpm.persistence.api.ProcessPersistenceContextManager;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.jbpm.process.instance.command.UpdateTimerCommand;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.process.instance.timer.TimerInstance;
import org.jbpm.process.instance.timer.TimerManager;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertNull;


@RunWith(Parameterized.class)
public class IntermediateEventTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] {
                { false, false },
                { true, false },
                { true, true }
        };
        return Arrays.asList(data);
    };

    private Logger logger = LoggerFactory
            .getLogger(IntermediateEventTest.class);

    private KieSession ksession;

    public IntermediateEventTest(boolean persistence, boolean locking) {
        super(persistence, locking);
    }

    private ProcessEventListener LOGGING_EVENT_LISTENER = new DefaultProcessEventListener() {

        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            logger.info("After node left {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            logger.info("After node triggered {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void beforeNodeLeft(ProcessNodeLeftEvent event) {
            logger.info("Before node left {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
            logger.info("Before node triggered {}", event.getNodeInstance().getNodeName());
        }

    };

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

    /*
     * helper methods
     */

    private TimerManager getTimerManager(KieSession ksession) {
        KieSession internal = ksession;
        if (ksession instanceof CommandBasedStatefulKnowledgeSession) {
            internal = ( (SingleSessionCommandService) ( (CommandBasedStatefulKnowledgeSession) ksession ).getRunner() ).getKieSession();;
        }

        return ((InternalProcessRuntime)((StatefulKnowledgeSessionImpl)internal).getProcessRuntime()).getTimerManager();
    }

    /*
     * TESTS!
     */

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
    public void testSignalBoundaryNonEffectiveEvent() throws Exception {
        final String signal = "signalTest";
        final MutableBoolean eventAfterNodeLeftTriggered = new MutableBoolean(false);
        KieBase kbase = createKnowledgeBase(
                "BPMN2-BoundaryEventWithNonEffectiveSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        ksession.addEventListener(new DefaultProcessEventListener() {
            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                // BoundaryEventNodeInstance
                if(signal.equals(event.getNodeInstance().getNodeName())) {
                	eventAfterNodeLeftTriggered.setTrue();
                }
            }
        });
        ProcessInstance processInstance = ksession
                .startProcess("BoundaryEventWithNonEffectiveSignal");

        ksession.signalEvent(signal, signal);

        // outer human work
        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);

        // inner human task
        ksession.getWorkItemManager().completeWorkItem(
                handler.getWorkItem().getId(), null);

        assertProcessInstanceFinished(processInstance, ksession);
        assertThat(eventAfterNodeLeftTriggered.isFalse()).isTrue();
    }

    @Test
    public void testSignalBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ksession.addEventListener(LOGGING_EVENT_LISTENER);
        ProcessInstance processInstance = ksession
                .startProcess("BoundarySignalOnTask");
        ksession.signalEvent("MySignal", "value");
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testSignalBoundaryEventOnTaskWithSignalName() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundarySignalWithNameEventOnTaskbpmn2.bpmn");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ksession.addEventListener(LOGGING_EVENT_LISTENER);
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
        ksession.addEventListener(LOGGING_EVENT_LISTENER);
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
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);

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

    @Test(timeout=10000)
    public void testEventBasedSplit2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit2.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.addEventListener(countDownListener);
        // Yes
        ProcessInstance processInstance = ksession
                .startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());

        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());
        // Timer
        processInstance = ksession.startProcess("com.sample.test");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();

        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1",
                new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2",
                new SystemOutWorkItemHandler());

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
    public void testEventBasedSplitWithSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExclusiveEventBasedGatewayInSubprocess.bpmn2");
        ksession = createKnowledgeSession(kbase);

        // Stop
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.testEBGInSubprocess");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);

        ksession.signalEvent("StopSignal", "", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);


        // Continue and Stop
        processInstance = ksession.startProcess("com.sample.bpmn.testEBGInSubprocess");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);

        ksession.signalEvent("ContinueSignal", "", processInstance.getId());

        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);

        ksession.signalEvent("StopSignal", "", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
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
                "Sub End",
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
        assertThat(workItem).isNotNull();

        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), completedNodes );
        assertThat(executednodes.size()).isEqualTo(4);

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
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        ksession.signalEvent("MySignal", null);
        assertProcessInstanceActive(processInstance);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        assertThat(workItemTopProcess).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(
                workItemTopProcess.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "User Task 2", "end-sub");
        assertThat(executednodes.size()).isEqualTo(4);

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
        assertThat(executednodes.size()).isEqualTo(1);

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
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes.size()).isEqualTo(4);

    }

    @Test(timeout=10000)
    public void testEventSubprocessTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Script Task 1", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessTimer.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessTimer");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");

    }

    @Test(timeout=10000)
    @RequirePersistence
    public void testEventSubprocessTimerCycle() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Script Task 1", 4);

        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessTimerCycle.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessTimer");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "start-sub", "Script Task 1", "end-sub");

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


        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertThat(executednodes.size()).isEqualTo(1);

    }

    @Test(timeout=10000)
    public void testEventSubprocessMessageWithLocalVars() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("subprocess/BPMN2-EventSubProcessWithLocalVariables.bpmn2");
        final Set<String> variablevalues = new HashSet<String>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
            	@SuppressWarnings("unchecked")
				Map<String, String> variable = (Map<String, String>)event.getNodeInstance().getVariable("richiesta");
            	if (variable != null) {
            		variablevalues.addAll(variable.keySet());
            	}
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        ksession.addEventListener(countDownListener);
        ProcessInstance processInstance = ksession.startProcess("EventSPWithVars");
        assertProcessInstanceActive(processInstance);

        Map<String, String> data = new HashMap<String, String>();
        ksession.signalEvent("Message-MAIL", data, processInstance.getId());
        countDownListener.waitTillCompleted();

        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertThat(processInstance).isNull();
        assertThat(variablevalues.size()).isEqualTo(2);
        assertThat(variablevalues.contains("SCRIPT1")).isTrue();
        assertThat(variablevalues.contains("SCRIPT2")).isTrue();
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
    public void testMessageIntermediateThrowVerifyWorkItemData() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

        WorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem instanceof org.drools.core.process.instance.WorkItem).isTrue();

        long nodeInstanceId = ((org.drools.core.process.instance.WorkItem) workItem).getNodeInstanceId();
        long nodeId = ((org.drools.core.process.instance.WorkItem) workItem).getNodeId();

        assertThat(nodeId).isNotNull();
        assertThat(nodeId > 0).isTrue();
        assertThat(nodeInstanceId).isNotNull();
        assertThat(nodeInstanceId > 0).isTrue();
    }

    @Test
    public void testMessageIntermediateThrowVerifyWorkItemDataDeploymentId() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

        WorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem instanceof org.drools.core.process.instance.WorkItem).isTrue();

        long nodeInstanceId = ((org.drools.core.process.instance.WorkItem) workItem).getNodeInstanceId();
        long nodeId = ((org.drools.core.process.instance.WorkItem) workItem).getNodeId();
        String deploymentId = ((org.drools.core.process.instance.WorkItem) workItem).getDeploymentId();

        assertThat(nodeId).isNotNull();
        assertThat(nodeId > 0).isTrue();
        assertThat(nodeInstanceId).isNotNull();
        assertThat(nodeInstanceId > 0).isTrue();
        assertThat(deploymentId).isNull();

        // now set deployment id as part of ksession's env
        ksession.getEnvironment().set("deploymentId", "testDeploymentId");

        ksession.startProcess("MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

        workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem instanceof org.drools.core.process.instance.WorkItem).isTrue();

        nodeInstanceId = ((org.drools.core.process.instance.WorkItem) workItem).getNodeInstanceId();
        nodeId = ((org.drools.core.process.instance.WorkItem) workItem).getNodeId();
        deploymentId = ((org.drools.core.process.instance.WorkItem) workItem).getDeploymentId();

        assertThat(nodeId).isNotNull();
        assertThat(nodeId > 0).isTrue();
        assertThat(nodeInstanceId).isNotNull();
        assertThat(nodeInstanceId > 0).isTrue();
        assertThat(deploymentId).isNotNull();
        assertThat(deploymentId).isEqualTo("testDeploymentId");
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

    @Test(timeout=10000)
    public void testTimerBoundaryEventDuration() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventDurationISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDurationISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventDateISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);

        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-TimerBoundaryEventDateISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        HashMap<String, Object> params = new HashMap<String, Object>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        params.put("date", plusTwoSeconds.toString());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent", params);
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventCycle1() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle1.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventCycle2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 3);

        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycle2.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();

        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());

    }

    @Test(timeout=10000)
    @RequirePersistence(false)
    public void testTimerBoundaryEventCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycleISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());
    }

    @Test(timeout=10000)
    @RequirePersistence
    public void testTimerBoundaryEventCycleISOWithPersistence() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 2);
        // load up the knowledge base
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventCycleISO.bpmn2");

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        ksession.addEventListener(countDownListener);

        long sessionId = ksession.getIdentifier();
        Environment env = ksession.getEnvironment();
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        logger.info("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        ksession.addEventListener(countDownListener);

        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventInterrupting() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        logger.debug("Firing timer");

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventInterruptingOnTask() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterruptingOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",  new TestWorkItemHandler());
        ksession.addEventListener(countDownListener);

        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        logger.debug("Firing timer");

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testTimerBoundaryEventInterruptingOnTaskCancelTimer() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterruptingOnTaskCancelTimer.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertProcessInstanceActive(processInstance);
        Collection<TimerInstance> timers = getTimerManager(ksession).getTimers();
        assertThat(timers.size()).isEqualTo(1);

        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        timers = getTimerManager(ksession).getTimers();
        assertThat(timers.size()).isEqualTo(1);
        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);

        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        timers = getTimerManager(ksession).getTimers();
        assertThat(timers).isNullOrEmpty();
        WorkItem workItem = handler.getWorkItem();
        if (workItem != null) {
            ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        }

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testIntermediateCatchEventSignal() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
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
                new SystemOutWorkItemHandler());
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
    public void testIntermediateCatchEventMessageWithRef() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventMessageWithRef.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("Message-HelloMessage", "SomeValue",
                processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimerDuration() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);

        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimerDateISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventTimerDateISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        HashMap<String, Object> params = new HashMap<String, Object>();
        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);
        params.put("date", plusTwoSeconds.toString());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent", params);
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimerDurationISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDurationISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1.5 second for timer to trigger
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimerCycle1() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle1.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimerCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 5);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());

    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimerCycle2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycle2.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);

        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        ksession.abortProcessInstance(processInstance.getId());

    }

    @Test
    public void testIntermediateCatchEventCondition() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
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

        // First process should be completed
        assertThat(ksession.getProcessInstance(pi1id)).isNull();
        // Second process should NOT be completed
        assertThat(ksession.getProcessInstance(pi2id)).isNotNull();

    }

    @Test(timeout=10000)
    @RequirePersistence(false)
    public void testIntermediateCatchEventTimerCycleWithError()
            throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 0);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent", params);
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        processInstance = ksession.getProcessInstance(processInstance.getId());
        Integer xValue = (Integer) ((WorkflowProcessInstance) processInstance).getVariable("x");
        assertThat(xValue).isEqualTo(3);

        ksession.abortProcessInstance(processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    @RequirePersistence
    public void testIntermediateCatchEventTimerCycleWithErrorWithPersistence() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);


        final long piId = processInstance.getId();
        ksession.execute(new ExecutableCommand<Void>() {

            public Void execute(Context context) {
                StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((RegistryContext) context).lookup( KieSession.class );
                WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.getProcessInstance(piId);
                processInstance.setVariable("x", 0);
                return null;
            }
        });

        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        Integer xValue = ksession.execute(new ExecutableCommand<Integer>() {

            public Integer execute(Context context) {
                StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) ((RegistryContext) context).lookup( KieSession.class );
                WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.getProcessInstance(piId);
                return (Integer) processInstance.getVariable("x");

            }
        });
        assertThat(xValue).isEqualTo(2);
        ksession.abortProcessInstance(processInstance.getId());
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


        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "Hello",
                "StartSubProcess", "Task", "BoundaryEvent", "Goodbye",
                "EndProcess");

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

    @Test
    public void testIntermediateCatchEventSameSignalOnTwoKsessions() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");

        KieBase kbase2 = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal2.bpmn2");
        KieSession ksession2 = createKnowledgeSession(kbase2);
        ksession2.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ProcessInstance processInstance2 = ksession2.startProcess("IntermediateCatchEvent2");

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);
        ksession = restoreSession(ksession, true);
        ksession2 = restoreSession(ksession2, true);
        // now signal process instance
        ksession.signalEvent("MyMessage", "SomeValue");
        assertProcessInstanceFinished(processInstance, ksession);
        assertProcessInstanceActive(processInstance2);

        // now signal the other one
        ksession2.signalEvent("MyMessage", "SomeValue");
        assertProcessInstanceFinished(processInstance2, ksession2);
        ksession2.dispose();
    }

    @Test
    @RequirePersistence
    public void testEventTypesLifeCycle() throws Exception {
        // JBPM-4246
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchSignalBetweenUserTasks.bpmn2");
        EntityManagerFactory separateEmf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        Environment env = createEnvironment(separateEmf);
        ksession = createKnowledgeSession(kbase, null, env);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.startProcess("BPMN2-IntermediateCatchSignalBetweenUserTasks");

        int signalListSize = ksession.execute(new ExecutableCommand<Integer>() {
            public Integer execute(Context context) {
                SingleSessionCommandService commandService = (SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
                InternalKnowledgeRuntime kruntime = (InternalKnowledgeRuntime) commandService.getKieSession();
                ProcessPersistenceContextManager contextManager = (ProcessPersistenceContextManager) kruntime
                        .getEnvironment().get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER);
                ProcessPersistenceContext pcontext = contextManager.getProcessPersistenceContext();

                List<Long> processInstancesToSignalList = pcontext.getProcessInstancesWaitingForEvent("MySignal");
                return processInstancesToSignalList.size();
            }
        });

        // Process instance is not waiting for signal
        assertThat(signalListSize).isEqualTo(0);

        ksession.getWorkItemManager().completeWorkItem(1, null);

        signalListSize = ksession.execute(new ExecutableCommand<Integer>() {
            public Integer execute(Context context) {
                SingleSessionCommandService commandService = (SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
                InternalKnowledgeRuntime kruntime = (InternalKnowledgeRuntime) commandService.getKieSession();
                ProcessPersistenceContextManager contextManager = (ProcessPersistenceContextManager) kruntime
                        .getEnvironment().get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER);
                ProcessPersistenceContext pcontext = contextManager.getProcessPersistenceContext();

                List<Long> processInstancesToSignalList = pcontext.getProcessInstancesWaitingForEvent("MySignal");
                return processInstancesToSignalList.size();
            }
        });

        // Process instance is waiting for signal now
        assertThat(signalListSize).isEqualTo(1);

        ksession.signalEvent("MySignal", null);

        signalListSize = ksession.execute(new ExecutableCommand<Integer>() {
            public Integer execute(Context context) {
                SingleSessionCommandService commandService = (SingleSessionCommandService) ((CommandBasedStatefulKnowledgeSession) ksession).getRunner();
                InternalKnowledgeRuntime kruntime = (InternalKnowledgeRuntime) commandService.getKieSession();
                ProcessPersistenceContextManager contextManager = (ProcessPersistenceContextManager) kruntime
                        .getEnvironment().get(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER);
                ProcessPersistenceContext pcontext = contextManager.getProcessPersistenceContext();

                List<Long> processInstancesToSignalList = pcontext.getProcessInstancesWaitingForEvent("MySignal");
                return processInstancesToSignalList.size();
            }
        });

        // Process instance is not waiting for signal
        assertThat(signalListSize).isEqualTo(0);

        ksession.getWorkItemManager().completeWorkItem(2, null);

        ksession.dispose();
        ksession = null;
        separateEmf.close();
    }

    @Test
    public void testIntermediateCatchEventNoIncommingConnection() throws Exception {
        try {
	    	KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventNoIncommingConnection.bpmn2");
	        ksession = createKnowledgeSession(kbase);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).isNotNull();
            assertThat(e.getMessage().contains("has no incoming connection")).isTrue();
        }

    }

    @Test
    public void testSignalBoundaryEventOnMultiInstanceSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "subprocess/BPMN2-MultiInstanceSubprocessWithBoundarySignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> approvers = new ArrayList<String>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        ProcessInstance processInstance = ksession.startProcess("boundary-catch-error-event", params);
        assertProcessInstanceActive(processInstance);

        List<WorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(2);

        ksession.signalEvent("Outside", null, processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

        ksession.dispose();
    }

    @Test
    public void testSignalBoundaryEventNoInteruptOnMultiInstanceSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "subprocess/BPMN2-MultiInstanceSubprocessWithBoundarySignalNoInterupting.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> approvers = new ArrayList<String>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        ProcessInstance processInstance = ksession.startProcess("boundary-catch-error-event", params);
        assertProcessInstanceActive(processInstance);

        List<WorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(2);

        ksession.signalEvent("Outside", null, processInstance.getId());

        assertProcessInstanceActive(processInstance.getId(), ksession);

        for (WorkItem wi : workItems) {
        	ksession.getWorkItemManager().completeWorkItem(wi.getId(), null);
        }
        assertProcessInstanceFinished(processInstance, ksession);

        ksession.dispose();
    }

    @Test
    public void testErrorBoundaryEventOnMultiInstanceSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBase(
                "subprocess/BPMN2-MultiInstanceSubprocessWithBoundaryError.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> params = new HashMap<String, Object>();
        List<String> approvers = new ArrayList<String>();
        approvers.add("john");
        approvers.add("john");

        params.put("approvers", approvers);

        ProcessInstance processInstance = ksession.startProcess("boundary-catch-error-event", params);
        assertProcessInstanceActive(processInstance);

        List<WorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(2);

        ksession.signalEvent("Inside", null, processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);

        ksession.dispose();
    }

    @Test
    public void testIntermediateCatchEventSignalAndBoundarySignalEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryEventWithSignals.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("boundaryeventtest");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        // now signal process instance
        ksession.signalEvent("moveon", "", processInstance.getId());
        assertProcessInstanceActive(processInstance);

        WorkItem wi = handler.getWorkItem();
        assertThat(wi).isNotNull();

        // signal boundary event on user task
        ksession.signalEvent("moveon", "", processInstance.getId());

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test
    public void testSignalIntermediateThrowEventWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn",
                "BPMN2-IntermediateThrowEventSignalWithTransformation.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "john");
        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");

        ProcessInstance processInstance2 = ksession.startProcess("SignalIntermediateEvent", params);
        assertProcessInstanceFinished(processInstance2, ksession);

        assertProcessInstanceFinished(processInstance, ksession);

        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isEqualTo("JOHN");
    }

    @Test
    public void testSignalBoundaryEventWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper(
                "BPMN2-BoundarySignalEventOnTaskWithTransformation.bpmn",
                "BPMN2-IntermediateThrowEventSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "john");
        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");

        ProcessInstance processInstance2 = ksession.startProcess("SignalIntermediateEvent", params);
        assertProcessInstanceFinished(processInstance2, ksession);

        assertProcessInstanceFinished(processInstance, ksession);

        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isEqualTo("JOHN");
    }

    @Test
    public void testMessageIntermediateThrowWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateThrowEventMessageWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final StringBuffer messageContent = new StringBuffer();
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler(){

					@Override
					public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
						// collect message content for verification
						messageContent.append(workItem.getParameter("Message"));
						super.executeWorkItem(workItem, manager);
					}

        });
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess(
                "MessageIntermediateEvent", params);
        assertProcessInstanceCompleted(processInstance);

        assertThat(messageContent.toString()).isEqualTo("MYVALUE");

    }

    @Test
    public void testIntermediateCatchEventSignalWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventSignalWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("MyMessage", "SomeValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "UserTask", "EndProcess", "event");
        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull();
        assertThat(var).isEqualTo("SOMEVALUE");
    }

    @Test
    public void testIntermediateCatchEventMessageWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventMessageWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("Message-HelloMessage", "SomeValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull();
        assertThat(var).isEqualTo("SOMEVALUE");
    }

    @Test
    public void testEventSubprocessSignalWithTransformation() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-EventSubprocessSignalWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);

        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessSignal");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);

        ksession.signalEvent("MySignal", "john", processInstance.getId());

        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "Sub Process 1", "start-sub", "end-sub");

        String var = getProcessVarValue(processInstance, "x");
        assertThat(var).isNotNull();
        assertThat(var).isEqualTo("JOHN");

    }

    @Test
    public void testMultipleMessageSignalSubprocess() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-MultipleMessageSignalSubprocess.bpmn2");
        ksession = createKnowledgeSession(kbase);

        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.Multiple_MessageSignal_Subprocess");
		logger.debug("Parent Process ID: " + processInstance.getId());

		ksession.signalEvent("Message-Message 1","Test",processInstance.getId());
		assertProcessInstanceActive(processInstance.getId(), ksession);

		ksession.signalEvent("Message-Message 1","Test",processInstance.getId());
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testIntermediateCatchEventSignalWithRef() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateCatchEventSignalWithRef.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("Signal1", "SomeValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test(timeout=10000)
    public void testTimerMultipleInstances() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);
        KieBase kbase = createKnowledgeBase("BPMN2-MultiInstanceLoopBoundaryTimer.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("boundaryTimerMultipleInstances");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();

        List<WorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(3);

        for (WorkItem wi : workItems) {
            ksession.getWorkItemManager().completeWorkItem(wi.getId(), null);
        }

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimerCycleCron() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 3);
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleCron.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);

        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        ksession.abortProcessInstance(processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimerDurationValueFromGlobal() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-GlobalTimerInterrupted.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.setGlobal("time", "2s");

        ProcessInstance processInstance = ksession.startProcess("interruptedTimer");

        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventCronCycle() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Send Update Timer", 3);
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryTimerCycleCron.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("boundaryTimerCycleCron");
        assertProcessInstanceActive(processInstance);

        List<WorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(1);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(3);

        ksession.abortProcessInstance(processInstance.getId());

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test(timeout=10000)
    public void testIntermediateTimerParallelGateway() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Timer1", 1);
        NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("Timer2", 1);
        NodeLeftCountDownProcessEventListener countDownListener3 = new NodeLeftCountDownProcessEventListener("Timer3", 1);
        KieBase kbase = createKnowledgeBase("timer/BPMN2-IntermediateTimerParallelGateway.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.addEventListener(countDownListener2);
        ksession.addEventListener(countDownListener3);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("Evaluation.timer-parallel");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        countDownListener2.waitTillCompleted();
        countDownListener3.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getId(), ksession);

    }

    @Test(timeout=10000)
    public void testIntermediateTimerEventMI() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("After timer", 3);
        KieBase kbase = createKnowledgeBase("timer/BPMN2-IntermediateTimerEventMI.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance processInstance = ksession.startProcess("defaultprocessid");
        assertProcessInstanceActive(processInstance);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance.getId(), ksession);

        ksession.abortProcessInstance(processInstance.getId());

        assertProcessInstanceAborted(processInstance.getId(), ksession);
    }

    @Test
    public void testThrowIntermediateSignalWithScope() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2IntermediateThrowEventScope.bpmn2");
        ksession = createKnowledgeSession(kbase);

        TestWorkItemHandler handler = new TestWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();

        ProcessInstance processInstance = ksession.startProcess("intermediate-event-scope", params);
        ProcessInstance processInstance2 = ksession.startProcess("intermediate-event-scope", params);

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);

        assertNodeActive(processInstance.getId(), ksession, "Complete work", "Wait");
        assertNodeActive(processInstance2.getId(), ksession, "Complete work", "Wait");

        List<WorkItem> items = handler.getWorkItems();

        WorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("_output", "sending event");

        ksession.getWorkItemManager().completeWorkItem(wi.getId(), result);

        assertProcessInstanceCompleted(processInstance);
        assertProcessInstanceActive(processInstance2);
        assertNodeActive(processInstance2.getId(), ksession, "Complete work", "Wait");

        wi = items.get(1);
        ksession.getWorkItemManager().completeWorkItem(wi.getId(), result);
        assertProcessInstanceCompleted(processInstance2);

    }

    @Test
    public void testThrowEndSignalWithScope() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2EndThrowEventScope.bpmn2");
        ksession = createKnowledgeSession(kbase);

        TestWorkItemHandler handler = new TestWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        Map<String, Object> params = new HashMap<String, Object>();

        ProcessInstance processInstance = ksession.startProcess("end-event-scope", params);
        ProcessInstance processInstance2 = ksession.startProcess("end-event-scope", params);

        assertProcessInstanceActive(processInstance);
        assertProcessInstanceActive(processInstance2);

        assertNodeActive(processInstance.getId(), ksession, "Complete work", "Wait");
        assertNodeActive(processInstance2.getId(), ksession, "Complete work", "Wait");

        List<WorkItem> items = handler.getWorkItems();

        WorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("_output", "sending event");

        ksession.getWorkItemManager().completeWorkItem(wi.getId(), result);

        assertProcessInstanceCompleted(processInstance);
        assertProcessInstanceActive(processInstance2);
        assertNodeActive(processInstance2.getId(), ksession, "Complete work", "Wait");

        wi = items.get(1);
        ksession.getWorkItemManager().completeWorkItem(wi.getId(), result);
        assertProcessInstanceCompleted(processInstance2);

    }



    @Test
    public void testThrowIntermediateSignalWithExternalScope() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventExternalScope.bpmn2");
        ksession = createKnowledgeSession(kbase);

        TestWorkItemHandler handler = new TestWorkItemHandler();
        WorkItemHandler externalHandler = new WorkItemHandler() {

            @Override
            public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
                String signal = (String) workItem.getParameter("Signal");
                ksession.signalEvent(signal, null);

                manager.completeWorkItem(workItem.getId(), null);

            }

            @Override
            public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            }
        };

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.getWorkItemManager().registerWorkItemHandler("External Send Task", externalHandler);
        Map<String, Object> params = new HashMap<String, Object>();

        ProcessInstance processInstance = ksession.startProcess("intermediate-event-scope", params);

        assertProcessInstanceActive(processInstance);

        assertNodeActive(processInstance.getId(), ksession, "Complete work", "Wait");

        List<WorkItem> items = handler.getWorkItems();
        assertThat(items.size()).isEqualTo(1);
        WorkItem wi = items.get(0);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("_output", "sending event");

        ksession.getWorkItemManager().completeWorkItem(wi.getId(), result);

        assertProcessInstanceCompleted(processInstance);

    }

    @Test
    public void testIntermediateCatchEventSignalWithVariable() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignalWithVariable.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());

        String signalVar = "myVarSignal";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("signalName", signalVar);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent", parameters);
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent(signalVar, "SomeValue", processInstance.getId());
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "UserTask", "EndProcess", "event");

    }

    @Test
    public void testSignalIntermediateThrowWithVariable() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventSignalWithVariable.bpmn2", "BPMN2-IntermediateCatchEventSignalWithVariable.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        // create catch process instance
        String signalVar = "myVarSignal";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("signalName", signalVar);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent", parameters);
        assertProcessInstanceActive(processInstance);

        ksession = restoreSession(ksession, true);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        params.put("signalName", signalVar);
        ProcessInstance processInstanceThrow = ksession.startProcess("SignalIntermediateEvent", params);
        assertThat(processInstanceThrow.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        // catch process instance should now be completed
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testInvalidDateTimerBoundary() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-TimerBoundaryEventDateInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (RuntimeException e) {
            assertThat(e.getMessage().contains("Could not parse date 'abcdef'")).isTrue();
        }
    }

    @Test
    public void testInvalidDurationTimerBoundary() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-TimerBoundaryEventDurationInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (Exception e) {
            assertThat(e.getMessage().contains("Could not parse delay 'abcdef'")).isTrue();
        }
    }

    @Test
    public void testInvalidCycleTimerBoundary() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-TimerBoundaryEventCycleInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (Exception e) {
            assertThat(e.getMessage().contains("Could not parse delay 'abcdef'")).isTrue();
        }
    }

    @Test
    public void testIntermediateCatchEventConditionSetVariableAfter() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventConditionSetVariableAfter.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(new RuleAwareProcessEventLister());

        Collection<? extends Object> processInstances = ksession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {
                if (object instanceof ProcessInstance) {
                    return true;
                }
                return false;
            }
        });
        assertThat(processInstances).isNotNull();
        assertThat(processInstances.size()).isEqualTo(1);

        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
        assertProcessInstanceFinished(processInstance, ksession);

        processInstances = ksession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {
                if (object instanceof ProcessInstance) {
                    return true;
                }
                return false;
            }
        });
        assertThat(processInstances).isNotNull();
        assertThat(processInstances.size()).isEqualTo(0);
    }

    @Test
    public void testIntermediateCatchEventConditionRemovePIAfter() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(new RuleAwareProcessEventLister());

        Collection<? extends Object> processInstances = ksession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {
                if (object instanceof ProcessInstance) {
                    return true;
                }
                return false;
            }
        });
        assertThat(processInstances).isNotNull();
        assertThat(processInstances.size()).isEqualTo(1);

        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
        assertProcessInstanceFinished(processInstance, ksession);

        processInstances = ksession.getObjects(new ObjectFilter() {

            @Override
            public boolean accept(Object object) {
                if (object instanceof ProcessInstance) {
                    return true;
                }
                return false;
            }
        });
        assertThat(processInstances).isNotNull();
        assertThat(processInstances.size()).isEqualTo(0);
    }

    @Test(timeout=10000)
    @RequirePersistence
    public void testIntermediateCatchEventTimerDurationWithError()
            throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);

        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDurationWithError.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ksession.addEventListener(countDownListener);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 0);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent", params);

        long waitTime = 2;
        assertProcessInstanceActive(processInstance);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted(waitTime * 1000);
        assertProcessInstanceActive(processInstance);

        processInstance = ksession.getProcessInstance(processInstance.getId());

        // reschedule it to allow to move on
        ksession.setGlobal("TestOK", Boolean.TRUE);

        ksession.execute(new UpdateTimerCommand(processInstance.getId(), "timer", waitTime + 1));
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventCronCycleVariable() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Send Update Timer", 3);
        KieBase kbase = createKnowledgeBase("BPMN2-BoundaryTimerCycleCronVariable.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cronStr", "0/1 * * * * ?");

        ProcessInstance processInstance = ksession.startProcess("boundaryTimerCycleCron", parameters);
        assertProcessInstanceActive(processInstance);

        List<WorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(1);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);
        workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(3);

        ksession.abortProcessInstance(processInstance.getId());

        assertProcessInstanceFinished(processInstance, ksession);
    }

    @Test(timeout=10000)
    public void testMultipleTimerBoundaryEventCronCycleVariable() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Send Update Timer", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleBoundaryTimerCycleCronVariable.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler handler = new TestWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cronStr", "0/1 * * * * ?");

        ProcessInstance processInstance = ksession.startProcess("boundaryTimerCycleCron", parameters);
        assertProcessInstanceActive(processInstance);

        List<WorkItem> workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(1);

        countDownListener.waitTillCompleted();
        assertProcessInstanceActive(processInstance);

        workItems = handler.getWorkItems();
        assertThat(workItems).isNotNull();
        assertThat(workItems.size()).isEqualTo(2);

        ksession.abortProcessInstance(processInstance.getId());

        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test(timeout=10000)
    public void testEventBasedSplitWithCronTimerAndSignal() throws Exception {
        System.setProperty("jbpm.enable.multi.con", "true");
        try {
            NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Request photos of order in use", 1);
            NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("Request an online review", 1);
            NodeLeftCountDownProcessEventListener countDownListener3 = new NodeLeftCountDownProcessEventListener("Send a thank you card", 1);
            NodeLeftCountDownProcessEventListener countDownListener4 = new NodeLeftCountDownProcessEventListener("Request an online review", 1);
            KieBase kbase = createKnowledgeBase("timer/BPMN2-CronTimerWithEventBasedGateway.bpmn2");
            ksession = createKnowledgeSession(kbase);
            
            TestWorkItemHandler handler = new TestWorkItemHandler();
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);       
            ksession.addEventListener(countDownListener);
            ksession.addEventListener(countDownListener2);
            ksession.addEventListener(countDownListener3);
            ksession.addEventListener(countDownListener4);
            
            ProcessInstance processInstance = ksession.startProcess("timerWithEventBasedGateway");
            assertProcessInstanceActive(processInstance.getId(), ksession);
            
            countDownListener.waitTillCompleted();
            logger.debug("First timer triggered");
            countDownListener2.waitTillCompleted();
            logger.debug("Second timer triggered");
            countDownListener3.waitTillCompleted();
            logger.debug("Third timer triggered");
            countDownListener4.waitTillCompleted();
            logger.debug("Fourth timer triggered");
            
            List<WorkItem> wi = handler.getWorkItems();
            assertThat(wi).isNotNull();
            assertThat(wi.size()).isEqualTo(3);
    
            ksession.abortProcessInstance(processInstance.getId());
        } finally {
            // clear property only as the only relevant value is when it's set to true
            System.clearProperty("jbpm.enable.multi.con");
        }
    }
    
    @Test
    public void testEventSubprocessWithEmbeddedSignals() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessErrorSignalEmbedded.bpmn2");
        ksession = createKnowledgeSession(kbase);
               
        ProcessInstance processInstance = ksession.startProcess("project2.myerrorprocess");
        
        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
        ksession.signalEvent("signal1", null, processInstance.getId());        
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        for (NodeInstance nodeInstance: ((WorkflowProcessInstance) processInstance).getNodeInstances()) {
            System.out.println("Active node instance " + nodeInstance);
        }
        
        ksession.signalEvent("signal2", null, processInstance.getId());
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        ksession.signalEvent("signal3", null, processInstance.getId());

        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testEventSubprocessWithExpression() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventSubprocessSignalExpression.bpmn2");
        ksession = createKnowledgeSession(kbase);
               
        Map<String, Object> params = new HashMap<>();
        params.put("x", "signalling");
        ProcessInstance processInstance = ksession.startProcess("BPMN2-EventSubprocessSignalExpression", params);
        
        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
        ksession.signalEvent("signalling", null, processInstance.getId());        
  
        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testConditionalProcessFactInsertedBefore() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventConditionPI.bpmn2", "BPMN2-IntermediateCatchEventSignal.bpmn2");        
        ksession = createKnowledgeSession(kbase);
        
        Person person0 = new Person("john");
        ksession.insert(person0);
        
        Map<String, Object> params0 = new HashMap<String, Object>();
        params0.put("name", "john");
        ProcessInstance pi0 = ksession.startProcess("IntermediateCatchEvent", params0);
        ksession.insert(pi0);

        Person person = new Person("Jack");
        ksession.insert(person);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Poul");
        ProcessInstance pi = ksession.startProcess("IntermediateCatchEventPI", params);
        ksession.insert(pi);
        pi = ksession.getProcessInstance(pi.getId());
        assertThat(pi).isNotNull();
        
        Person person2 = new Person("Poul");
        ksession.insert(person2);
        
        pi = ksession.getProcessInstance(pi.getId());
        assertThat(pi).isNull();
        
    }

    @Test
    public void testBoundarySignalEventOnSubprocessWithVariableResolution() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SubprocessWithSignalEndEventAndSignalBoundaryEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(LOGGING_EVENT_LISTENER);

        Map<String, Object> params = new HashMap<>();
        params.put("document-ref", "signalling");
        params.put("message", "hello");
        ProcessInstance processInstance = ksession.startProcess("SubprocessWithSignalEndEventAndSignalBoundaryEvent", params);

        assertNodeTriggered(processInstance.getId(), "sysout from boundary", "end2");
        assertNotNodeTriggered(processInstance.getId(),"end1");

        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    public void testSignalEndWithData() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-IntermediateThrowEventSignalWithData.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                                                              new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = ksession.startProcess("testThrowingSignalEvent", params);

        assertProcessInstanceActive(processInstance);
        
        ksession.signalEvent("mysignal", null, processInstance.getId());
        
        assertProcessInstanceCompleted(processInstance);

    }
    
    @Test
    public void testDynamicCatchEventSignal() throws Exception {
        KieBase kbase = createKnowledgeBase("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        final List<Long> instances = new ArrayList<>();
        
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add(event.getProcessInstance().getId());
            }
            
        });
        
        ProcessInstance processInstance = ksession.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
        assertThat(instances).hasSize(4);
        
        // remove the parent process instance
        instances.remove(processInstance.getId());
        
        for (Long id : instances) {
            ProcessInstance child = ksession.getProcessInstance(id);
            assertProcessInstanceActive(child);
        }
        
        // now complete user task to signal all child instances to stop
        WorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        assertProcessInstanceFinished(processInstance, ksession);
        
        for (Long id : instances) {            
            assertNull("Child process instance has not been finished.", ksession.getProcessInstance(id));
        }
    }
    
    @Test
    public void testDynamicCatchEventSignalWithVariableUpdated() throws Exception {        
        KieBase kbase = createKnowledgeBase("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        final List<Long> instances = new ArrayList<>();
        
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add(event.getProcessInstance().getId());
            }
            
        });
        
        ProcessInstance processInstance = ksession.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
        assertThat(instances).hasSize(4);
        
        // remove the parent process instance
        instances.remove(processInstance.getId());
        
        for (Long id : instances) {
            ProcessInstance child = ksession.getProcessInstance(id);
            assertProcessInstanceActive(child);
        }
        
        // change one child process instance variable (fatherId) to something else then original fatherId
        Long changeProcessInstanceId = instances.remove(0);
        Map<String, Object> updatedVariables = new HashMap<>();
        updatedVariables.put("fatherId", 999L);
        ksession.execute(new SetProcessInstanceVariablesCommand(changeProcessInstanceId, updatedVariables));
        
        // now complete user task to signal all child instances to stop
        WorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        assertProcessInstanceFinished(processInstance, ksession);
        
        for (Long id : instances) {            
            assertNull("Child process instance has not been finished.", ksession.getProcessInstance(id));
        }
        
        ProcessInstance updatedChild = ksession.getProcessInstance(changeProcessInstanceId);
        assertProcessInstanceActive(updatedChild);
        
        ksession.signalEvent("stopChild:999", null, changeProcessInstanceId);
        assertProcessInstanceFinished(updatedChild, ksession);
    }
    
    @RequirePersistence
    @Test
    public void testDynamicCatchEventSignalWithVariableUpdatedBroadcastSignal() throws Exception {        
        KieBase kbase = createKnowledgeBase("subprocess/dynamic-signal-parent.bpmn2", "subprocess/dynamic-signal-child.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        final List<Long> instances = new ArrayList<>();
        
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                instances.add(event.getProcessInstance().getId());
            }
            
        });
        
        ProcessInstance processInstance = ksession.startProcess("src.father");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        
        assertThat(instances).hasSize(4);
        
        // remove the parent process instance
        instances.remove(processInstance.getId());
        
        for (Long id : instances) {
            ProcessInstance child = ksession.getProcessInstance(id);
            assertProcessInstanceActive(child);
        }
        
        // change one child process instance variable (fatherId) to something else then original fatherId
        Long changeProcessInstanceId = instances.remove(0);
        Map<String, Object> updatedVariables = new HashMap<>();
        updatedVariables.put("fatherId", 999L);
        ksession.execute(new SetProcessInstanceVariablesCommand(changeProcessInstanceId, updatedVariables));
        
        // now complete user task to signal all child instances to stop
        WorkItem workItem = handler.getWorkItem();
        assertThat(workItem).isNotNull();
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        assertProcessInstanceFinished(processInstance, ksession);
        
        for (Long id : instances) {            
            assertNull("Child process instance has not been finished.", ksession.getProcessInstance(id));
        }
        
        ProcessInstance updatedChild = ksession.getProcessInstance(changeProcessInstanceId);
        assertProcessInstanceActive(updatedChild);
        
        ksession.signalEvent("stopChild:999", null);
        assertProcessInstanceFinished(updatedChild, ksession);
    }
}
