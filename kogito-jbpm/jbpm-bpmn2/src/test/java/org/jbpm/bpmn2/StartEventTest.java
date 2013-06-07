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
import java.util.List;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class StartEventTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };

    private Logger logger = LoggerFactory.getLogger(StartEventTest.class);

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
        ksession.fireAllRules();
        person = new Person();
        person.setName("john");
        ksession.insert(person);
        ksession.fireAllRules();

    }

    @Test
    public void testTimerStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(500);
        }
        assertEquals(5, getNumberOfProcessInstances("Minimal"));

    }

    @Test
    public void testTimerStartCycleISO() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartISO.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        assertEquals(0, list.size());
        for (int i = 0; i < 6; i++) {
            Thread.sleep(1000);
        }
        assertEquals(6, getNumberOfProcessInstances("Minimal"));

    }

    @Test
    public void testTimerStartDuration() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartDuration.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Thread.sleep(250);
        assertEquals(0, list.size());

        Thread.sleep(3000);

        assertEquals(1, getNumberOfProcessInstances("Minimal"));

    }

    @Test
    public void testTimerStartCron() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartCron.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
        }
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
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        ksession.signalEvent("MySignal", "NewValue");
        Thread.sleep(500);
        assertEquals(1, getNumberOfProcessInstances("Minimal"));

    }

    @Test
    public void testSignalStartDynamic() throws Exception {
        KieBase kbase = createKnowledgeBase();
        ksession = createKnowledgeSession(kbase);
        KieBase kbase2 = createKnowledgeBase("BPMN2-SignalStart.bpmn2");
        kbase.getKiePackages().addAll(kbase2.getKiePackages());
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                System.out.println(event.getProcessInstance().getId());
                list.add(event.getProcessInstance().getId());
            }
        });
        ksession.signalEvent("MySignal", "NewValue");
        Thread.sleep(500);
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

    @Test
    public void testMultipleStartEventsStartOnTimer() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MultipleStartEventProcess.bpmn2");
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
        assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(500);
        }
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
    public void testMultipleEventBasedStartEventsStartOnTimer()
            throws Exception {
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
        assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
            Thread.sleep(500);
        }
        assertEquals(5, getNumberOfProcessInstances("MultipleStartEvents"));

    }

}
