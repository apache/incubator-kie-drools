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

import java.io.StringReader;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.core.util.IoUtils;
import org.jbpm.bpmn2.objects.NotAvailableGoodsReport;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieRepository;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class StartEventTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };

    private static final Logger logger = LoggerFactory.getLogger(StartEventTest.class);

    private KieSession ksession;

    public StartEventTest(boolean persistence) {
        super(persistence);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    @After
    public void dispose() {
        if (ksession != null) {
            abortProcessInstances(ksession);
            clearHistory();
            ksession.dispose();
            ksession = null;
        }
    }

    @Test
    public void testConditionalStart() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-ConditionalStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<Long> startedInstances = new ArrayList<>();
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterProcessStarted(ProcessStartedEvent event) {
                startedInstances.add(event.getProcessInstance().getId());
            }
            
        });
        Person person = new Person();
        person.setName("jack");
        ksession.insert(person);
        assertThat(startedInstances).hasSize(0);

        person = new Person();
        person.setName("john");
        ksession.insert(person);
        assertThat(startedInstances).hasSize(1);
        
        assertNodeTriggered(startedInstances.get(0), "StartProcess", "Hello", "EndProcess");
    }

    @Test(timeout=10000)
    public void testTimerStartCycleLegacy() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartCycleLegacy.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        logger.debug("About to start ###### " + new Date());

        assertThat(list.size()).isEqualTo(0);
        // then wait 5 times 5oo ms as that is period configured on the process
        countDownListener.waitTillCompleted();
        ksession.dispose();
        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(2);

    }

    @Test(timeout=10000)
    public void testTimerStart() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 5);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        assertThat(list.size()).isEqualTo(0);
        countDownListener.waitTillCompleted();
        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(5);

    }

    @Test(timeout=10000)
    public void testTimerStartDateISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 1);
        byte[] content = IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/BPMN2-TimerStartDate.bpmn2"));
        String processContent = new String(content, "UTF-8");

        OffsetDateTime plusTwoSeconds = OffsetDateTime.now().plusSeconds(2);

        processContent = processContent.replaceFirst("#\\{date\\}", plusTwoSeconds.toString());
        Resource resource = ResourceFactory.newReaderResource(new StringReader(processContent));
        resource.setSourcePath("/BPMN2-TimerStartDate.bpmn2");
        resource.setTargetPath("/BPMN2-TimerStartDate.bpmn2");
        KieBase kbase = createKnowledgeBaseFromResources(resource);

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        assertThat(list.size()).isEqualTo(0);
        countDownListener.waitTillCompleted();
        assertThat(list.size()).isEqualTo(1);

    }

    @Test(timeout=10000)
    public void testTimerStartCycleISO() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 6);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        assertThat(list.size()).isEqualTo(0);
        countDownListener.waitTillCompleted();
        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(6);

    }

    @Test(timeout=10000)
    public void testTimerStartDuration() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        assertThat(list.size()).isEqualTo(0);

        countDownListener.waitTillCompleted();

        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(1);

    }

    @Test(timeout=15000)
    public void testTimerStartCron() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 5);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartCron.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        // Timer in the process takes 1s, so after 5 seconds, there should be 5 process IDs in the list.
        countDownListener.waitTillCompleted();
        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(5);

    }

    @Test
    public void testSignalToStartProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalStart.bpmn2",
                "BPMN2-IntermediateThrowEventSignal.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                handler);
        final List<String> startedProcesses = new ArrayList<String>();
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void beforeProcessStarted(ProcessStartedEvent event) {
                startedProcesses.add(event.getProcessInstance().getProcessId());
            }
        });

        ProcessInstance processInstance = ksession
                .startProcess("SignalIntermediateEvent");
        assertProcessInstanceFinished(processInstance, ksession);
        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(1);
        assertThat(getNumberOfProcessInstances("SignalIntermediateEvent")).isEqualTo(1);
    }

    @Test
    public void testSignalStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        ksession.signalEvent("MySignal", "NewValue");

        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(1);

    }

    @Test
    public void testSignalStartDynamic() throws Exception {

        KieBase kbase = createKnowledgeBase("BPMN2-SignalStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        // create KieContainer after session was created to make sure no runtime data
        // will be used during serialization (deep clone)
        KieServices ks = KieServices.Factory.get();
        KieRepository kr = ks.getRepository();
        KieContainer kContainer = ks.newKieContainer(kr.getDefaultReleaseId());
        kContainer.getKieBase();

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                logger.info("{}", event.getProcessInstance().getId());
                list.add(event.getProcessInstance().getId());
            }
        });
        ksession.signalEvent("MySignal", "NewValue");

        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(1);
        // now remove the process from kbase to make sure runtime based listeners are removed from signal manager
        kbase.removeProcess("Minimal");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> { ksession.signalEvent("MySignal", "NewValue"); })
                    .withMessageContaining("Unknown process ID: Minimal");
        // must be still one as the process was removed
        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(1);

    }

    @Test
    public void testMessageStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MessageStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.signalEvent("Message-HelloMessage", "NewValue");
        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(1);
    }

    @Test
    public void testMultipleStartEventsRegularStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcessLongInterval.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("MultipleStartEvents");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testMultipleStartEventsStartOnTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartTimer", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        try {
            ksession.addEventListener(countDownListener);
            TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
            ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                    workItemHandler);
            final List<Long> list = new ArrayList<Long>();
            ksession.addEventListener(new DefaultProcessEventListener() {
                public void beforeProcessStarted(ProcessStartedEvent event) {
                    list.add(event.getProcessInstance().getId());
                }
            });
            assertThat(list.size()).isEqualTo(0);
            // Timer in the process takes 500ms, so after 1 second, there should be 2 process IDs in the list.
            countDownListener.waitTillCompleted();
            assertThat(getNumberOfProcessInstances("MultipleStartEvents")).isEqualTo(2);
        } finally {
            abortProcessInstances(ksession);
        }
    }

    @Test
    public void testMultipleEventBasedStartEventsSignalStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleEventBasedStartEventProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        ksession.signalEvent("startSignal", null);

        assertThat(list.size()).isEqualTo(1);
        WorkItem workItem = workItemHandler.getWorkItem();
        long processInstanceId = ((WorkItemImpl) workItem)
                .getProcessInstanceId();

        ProcessInstance processInstance = ksession
                .getProcessInstance(processInstanceId);
        ksession = restoreSession(ksession, true);

        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test
    public void testMultipleEventBasedStartEventsDifferentPaths() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcessDifferentPaths.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        ksession.startProcess("muliplestartevents", null);

        assertThat(list.size()).isEqualTo(1);
        WorkItem workItem = workItemHandler.getWorkItem();
        long processInstanceId = ((WorkItemImpl) workItem)
                .getProcessInstanceId();

        ProcessInstance processInstance = ksession
                .getProcessInstance(processInstanceId);
        ksession = restoreSession(ksession, true);

        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstanceId, "Start", "Script 1", "User task", "End");
    }

    @Test(timeout=10000)
    public void testMultipleEventBasedStartEventsTimerDifferentPaths() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartTimer", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcessDifferentPaths.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        assertThat(list.size()).isEqualTo(0);
        // Timer in the process takes 1000ms, so after 2 seconds, there should be 2 process IDs in the list.
        countDownListener.waitTillCompleted();

        assertThat(list.size()).isEqualTo(2);
        List<WorkItem> workItems = workItemHandler.getWorkItems();

        for (WorkItem workItem : workItems) {
            long processInstanceId = ((WorkItemImpl) workItem).getProcessInstanceId();

            ProcessInstance processInstance = ksession
                    .getProcessInstance(processInstanceId);

            assertThat(workItem).isNotNull();
            assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
            ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
            assertProcessInstanceFinished(processInstance, ksession);
            assertNodeTriggered(processInstanceId, "StartTimer", "Script 2", "User task", "End");
        }
    }

    @Test
    public void testMultipleEventBasedStartEventsSignalDifferentPaths() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcessDifferentPaths.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        ksession.signalEvent("startSignal", null);

        assertThat(list.size()).isEqualTo(1);
        WorkItem workItem = workItemHandler.getWorkItem();
        long processInstanceId = ((WorkItemImpl) workItem)
                .getProcessInstanceId();

        ProcessInstance processInstance = ksession
                .getProcessInstance(processInstanceId);
        ksession = restoreSession(ksession, true);

        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstanceId, "StartSignal", "Script 3", "User task", "End");
    }

    @Test(timeout=10000)
    public void testMultipleEventBasedStartEventsStartOnTimer()
            throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartTimer", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleEventBasedStartEventProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        assertThat(list.size()).isEqualTo(0);
        // Timer in the process takes 500ms, so after 1 second, there should be 2 process IDs in the list.
        countDownListener.waitTillCompleted();
        assertThat(getNumberOfProcessInstances("MultipleStartEvents")).isEqualTo(2);

    }

    @Test(timeout=10000)
    public void testTimerCycle() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("start", 5);
        KieBase kbase = createKnowledgeBase("timer/BPMN2-StartTimerCycle.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        StartCountingListener listener = new StartCountingListener();
        ksession.addEventListener(listener);

        countDownListener.waitTillCompleted();
        assertThat(listener.getCount("start.cycle")).isEqualTo(5);
    }

    @Test(timeout=10000)
    public void testSignalStartWithTransformation() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 1);
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-SignalStartWithTransformation.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<ProcessInstance> list = new ArrayList<ProcessInstance>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance());
            }
        });
        ksession.signalEvent("MySignal", "NewValue");
        countDownListener.waitTillCompleted();
        assertThat(getNumberOfProcessInstances("Minimal")).isEqualTo(1);
        assertThat(list).isNotNull();
        assertThat(list.size()).isEqualTo(1);
        String var = getProcessVarValue(list.get(0), "x");
        assertThat(var).isEqualTo("NEWVALUE");
    }

    /**
     * This is how I would expect the start event to work (same as the recurring event)
     */
    @Test(timeout=10000)
    public void testTimerDelay() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("start", 1);
        KieBase kbase = createKnowledgeBase("timer/BPMN2-StartTimerDuration.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        StartCountingListener listener = new StartCountingListener();
        ksession.addEventListener(listener);
        countDownListener.waitTillCompleted();
        assertThat(listener.getCount("start.delaying")).isEqualTo(1);
    }

    @Test
    public void testSignalStartWithCustomEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SingalStartWithCustomEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<ProcessInstance> list = new ArrayList<ProcessInstance>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance());
            }
        });
        NotAvailableGoodsReport report = new NotAvailableGoodsReport("test");
        ksession.signalEvent("SignalNotAvailableGoods", report);
        assertThat(getNumberOfProcessInstances("org.jbpm.example.SignalObjectProcess")).isEqualTo(1);
        assertThat(list.size()).isEqualTo(1);
        assertProcessVarValue(list.get(0), "report", "NotAvailableGoodsReport{type:test}");

    }

    /**
     * Should fail as timer expression is not valid
     *
     * @throws Exception
     */
    @Test
    public void testInvalidDateTimerStart() throws Exception {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> { createKnowledgeBase("timer/BPMN2-StartTimerDateInvalid.bpmn2"); })
                .withMessageContaining("Could not parse date 'abcdef'");
    }

    /**
     * Should fail as timer expression is not valid
     *
     * @throws Exception
     */
    @Test
    public void testInvalidDurationTimerStart() throws Exception {
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> { createKnowledgeBase("timer/BPMN2-StartTimerDurationInvalid.bpmn2"); })
                .withMessageContaining("Could not parse delay 'abcdef'");
    }

    /**
     * Should fail as timer expression is not valid
     *
     * @throws Exception
     */
    @Test
    public void testInvalidCycleTimerStart() throws Exception {
        assertThatExceptionOfType(Exception.class).isThrownBy(() -> { createKnowledgeBase("timer/BPMN2-StartTimerCycleInvalid.bpmn2"); })
                .withMessageContaining("Could not parse delay 'abcdef'");
    }
    
    @Test
    public void testStartithMultipleOutgoingFlows() throws Exception {
        System.setProperty("jbpm.enable.multi.con", "true");
        try {
            KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-StartEventWithMultipleOutgoingFlows.bpmn2");
            ksession = createKnowledgeSession(kbase);
            
            ProcessInstance pi = ksession.startProcess("starteventwithmutlipleflows");
            assertProcessInstanceCompleted(pi);
            
            assertNodeTriggered(pi.getId(), "Script 1", "Script 2");
        } finally {
            System.clearProperty("jbpm.enable.multi.con");
        }
    }


    private static class StartCountingListener extends DefaultProcessEventListener {
        private Map<String, Integer> map = new HashMap<String, Integer>();

        public void beforeProcessStarted(ProcessStartedEvent event) {
            String processId = event.getProcessInstance().getProcessId();
            Integer count = map.get(processId);

            if (count == null) {
                map.put(processId, 1);
            } else {
                map.put(processId, count + 1);
            }
        }

        public int getCount(String processId) {
            Integer count = map.get(processId);
            return (count == null) ? 0 : count;
        }
    }

}
