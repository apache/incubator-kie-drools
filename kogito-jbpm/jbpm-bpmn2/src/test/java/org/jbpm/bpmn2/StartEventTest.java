package org.jbpm.bpmn2;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartEventTest extends JbpmTestCase {

    private Logger logger = LoggerFactory.getLogger(StartEventTest.class);

    private StatefulKnowledgeSession ksession;

    @BeforeClass
    public static void setup() throws Exception {
        if (PERSISTENCE) {
            setUpDataSource();
        }
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
        KieBase kbase = createKnowledgeBase("BPMN2-ConditionalStart.bpmn2");
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

    /**
     * FIXME when it's run without persistence, list contains only 4 identifiers
     * 
     * @throws Exception
     */
    @Test
    @RequirePersistence
    public void testTimerStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Thread.sleep(250);
        assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
            ksession.fireAllRules();
            Thread.sleep(500);
        }
        assertEquals(5, list.size());

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
        Thread.sleep(250);
        assertEquals(0, list.size());
        for (int i = 0; i < 6; i++) {
            ksession.fireAllRules();
            Thread.sleep(1000);
        }
        assertEquals(6, list.size());

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
        ksession.fireAllRules();

        Thread.sleep(3000);

        assertEquals(1, list.size());

    }

    @Test
    @RequirePersistence(false)
    public void testTimerStartCron() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStartCron.bpmn2");
        ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        Thread.sleep(500);
        for (int i = 0; i < 5; i++) {
            ksession.fireAllRules();
            Thread.sleep(1000);
        }
        assertEquals(6, list.size());

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
        assertEquals(1, list.size());

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
                list.add(event.getProcessInstance().getId());
            }
        });
        ksession.signalEvent("MySignal", "NewValue");
        Thread.sleep(500);
        assertEquals(1, list.size());

    }

    @Test
    public void testSignalToStartProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalStart.bpmn2",
                "BPMN2-IntermediateThrowEventSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
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
        assertEquals(2, startedProcesses.size());
    }

    @Test
    public void testMessageStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MessageStart.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ksession.signalEvent("Message-HelloMessage", "NewValue");

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
        Thread.sleep(500);
        assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
            ksession.fireAllRules();
            Thread.sleep(500);
        }
        assertEquals(5, list.size());

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
        Thread.sleep(500);
        assertEquals(0, list.size());
        for (int i = 0; i < 5; i++) {
            ksession.fireAllRules();
            Thread.sleep(500);
        }
        assertEquals(5, list.size());

    }

}
