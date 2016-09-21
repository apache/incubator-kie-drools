/*
Copyright 2013 Red Hat, Inc. and/or its affiliates.

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

import java.io.StringReader;
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
import org.jbpm.test.util.CountDownProcessEventListener;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
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

    @Before
    public void prepare() {
        clearHistory();
    }

    @After
    public void dispose() {
        if (ksession != null) {
            ksession.dispose();
            ksession = null;
        }
    }

    @Test
    public void testConditionalStart() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-ConditionalStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        Person person = new Person();
        person.setName("jack");
        ksession.insert(person);

        person = new Person();
        person.setName("john");
        ksession.insert(person);


    }

    @Test(timeout=10000)
    public void testTimerStartCycleLegacy() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartProcess", 5);
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

        assertEquals(0, list.size());
        // then wait 5 times 5oo ms as that is period configured on the process
        countDownListener.waitTillCompleted();
        ksession.dispose();
        assertEquals(5, getNumberOfProcessInstances("Minimal"));

    }

    @Test(timeout=10000)
    public void testTimerStart() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartProcess", 5);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        assertEquals(0, list.size());
        countDownListener.waitTillCompleted();
        assertEquals(5, getNumberOfProcessInstances("Minimal"));

    }

    @Test(timeout=10000)
    public void testTimerStartDateISO() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartProcess", 1);
        byte[] content = IoUtils.readBytesFromInputStream(this.getClass().getResourceAsStream("/BPMN2-TimerStartDate.bpmn2"));
        String processContent = new String(content, "UTF-8");

        DateTime now = new DateTime(System.currentTimeMillis());
        now = now.plus(2000);

        processContent = processContent.replaceFirst("#\\{date\\}", now.toString());
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
        assertEquals(0, list.size());
        countDownListener.waitTillCompleted();
        assertEquals(1, list.size());

    }

    @Test(timeout=10000)
    public void testTimerStartCycleISO() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartProcess", 6);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        assertEquals(0, list.size());
        countDownListener.waitTillCompleted();
        assertEquals(6, getNumberOfProcessInstances("Minimal"));

    }

    @Test(timeout=10000)
    public void testTimerStartDuration() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartProcess", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        assertEquals(0, list.size());

        countDownListener.waitTillCompleted();

        assertEquals(1, getNumberOfProcessInstances("Minimal"));

    }

    @Test(timeout=10000)
    public void testTimerStartCron() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartProcess", 5);
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
        assertEquals(5, getNumberOfProcessInstances("Minimal"));

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
        assertEquals(1, getNumberOfProcessInstances("Minimal"));
        assertEquals(1, getNumberOfProcessInstances("SignalIntermediateEvent"));
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

        assertEquals(1, getNumberOfProcessInstances("Minimal"));

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

        assertEquals(1, getNumberOfProcessInstances("Minimal"));
        // now remove the process from kbase to make sure runtime based listeners are removed from signal manager
        kbase.removeProcess("Minimal");
        
        try {
            ksession.signalEvent("MySignal", "NewValue");
        } catch (IllegalArgumentException e) {
            assertEquals("Unknown process ID: Minimal", e.getMessage());
        }
        // must be still one as the process was removed
        assertEquals(1, getNumberOfProcessInstances("Minimal"));

    }

    @Test
    public void testMessageStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MessageStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.signalEvent("Message-HelloMessage", "NewValue");
        assertEquals(1, getNumberOfProcessInstances("Minimal"));
    }

    @Test
    public void testMultipleStartEventsRegularStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcess.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("MultipleStartEvents");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);

    }

    @Test(timeout=10000)
    public void testMultipleStartEventsStartOnTimer() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartTimer", 5);
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcess.bpmn2");
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
        assertEquals(0, list.size());
        // Timer in the process takes 500ms, so after 2.5 seconds, there should be 5 process IDs in the list.
        countDownListener.waitTillCompleted();
        assertEquals(5, getNumberOfProcessInstances("MultipleStartEvents"));

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

        assertEquals(1, list.size());
        WorkItem workItem = workItemHandler.getWorkItem();
        long processInstanceId = ((WorkItemImpl) workItem)
                .getProcessInstanceId();

        ProcessInstance processInstance = ksession
                .getProcessInstance(processInstanceId);
        ksession = restoreSession(ksession, true);

        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
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

        assertEquals(1, list.size());
        WorkItem workItem = workItemHandler.getWorkItem();
        long processInstanceId = ((WorkItemImpl) workItem)
                .getProcessInstanceId();

        ProcessInstance processInstance = ksession
                .getProcessInstance(processInstanceId);
        ksession = restoreSession(ksession, true);

        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstanceId, "Start", "Script 1", "User task", "End");
    }

    @Test(timeout=10000)
    public void testMultipleEventBasedStartEventsTimerDifferentPaths() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartTimer", 2);
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

        assertEquals(0, list.size());
        // Timer in the process takes 1000ms, so after 2 seconds, there should be 2 process IDs in the list.
        countDownListener.waitTillCompleted();

        assertEquals(2, list.size());
        List<WorkItem> workItems = workItemHandler.getWorkItems();

        for (WorkItem workItem : workItems) {
            long processInstanceId = ((WorkItemImpl) workItem).getProcessInstanceId();

            ProcessInstance processInstance = ksession
                    .getProcessInstance(processInstanceId);
            ksession = restoreSession(ksession, true);

            assertNotNull(workItem);
            assertEquals("john", workItem.getParameter("ActorId"));
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

        assertEquals(1, list.size());
        WorkItem workItem = workItemHandler.getWorkItem();
        long processInstanceId = ((WorkItemImpl) workItem)
                .getProcessInstanceId();

        ProcessInstance processInstance = ksession
                .getProcessInstance(processInstanceId);
        ksession = restoreSession(ksession, true);

        assertNotNull(workItem);
        assertEquals("john", workItem.getParameter("ActorId"));
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstanceId, "StartSignal", "Script 3", "User task", "End");
    }

    @Test(timeout=10000)
    public void testMultipleEventBasedStartEventsStartOnTimer()
            throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartTimer", 5);
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
        assertEquals(0, list.size());
        // Timer in the process takes 500ms, so after 2.5 seconds, there should be 5 process IDs in the list.
        countDownListener.waitTillCompleted();
        assertEquals(5, getNumberOfProcessInstances("MultipleStartEvents"));

    }

    @Test(timeout=10000)
    public void testTimerCycle() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("start", 5);
        KieBase kbase = createKnowledgeBase("timer/BPMN2-StartTimerCycle.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        StartCountingListener listener = new StartCountingListener();
        ksession.addEventListener(listener);

        countDownListener.waitTillCompleted();
        assertEquals(5, listener.getCount("start.cycle"));

    }


    @Test(timeout=10000)
    public void testSignalStartWithTransformation() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("StartProcess", 1);
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
        assertEquals(1, getNumberOfProcessInstances("Minimal"));
        assertNotNull(list);
        assertEquals(1, list.size());
        String var = getProcessVarValue(list.get(0), "x");
        assertEquals("NEWVALUE", var);
    }

    /**
     * This is how I would expect the start event to work (same as the recurring event)
     */
    @Test(timeout=10000)
    public void testTimerDelay() throws Exception {
        CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("start", 1);
        KieBase kbase = createKnowledgeBase("timer/BPMN2-StartTimerDuration.bpmn2");

        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        StartCountingListener listener = new StartCountingListener();
        ksession.addEventListener(listener);

        countDownListener.waitTillCompleted();

        assertEquals(1, listener.getCount("start.delaying"));
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

        assertEquals(1, getNumberOfProcessInstances("org.jbpm.example.SignalObjectProcess"));
        assertEquals(1, list.size());
        assertProcessVarValue(list.get(0), "report", "NotAvailableGoodsReport{type:test}");

    }

    @Test
    public void testInvalidDateTimerStart() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-StartTimerDateInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("Could not parse date 'abcdef'"));
        }
    }

    @Test
    public void testInvalidDurationTimerStart() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-StartTimerDurationInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Could not parse delay 'abcdef'"));
        }
    }

    @Test
    public void testInvalidCycleTimerStart() throws Exception {
        try {
            createKnowledgeBase("timer/BPMN2-StartTimerCycleInvalid.bpmn2");
            fail("Should fail as timer expression is not valid");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Could not parse delay 'abcdef'"));
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
