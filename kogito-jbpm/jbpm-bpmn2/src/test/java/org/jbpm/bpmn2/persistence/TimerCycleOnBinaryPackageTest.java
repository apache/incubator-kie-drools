package org.jbpm.bpmn2.persistence;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.AuditLoggerFactory.Type;
import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.runtime.Environment;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerCycleOnBinaryPackageTest extends JbpmBpmn2TestCase {

    private static final Logger logger = LoggerFactory.getLogger(TimerCycleOnBinaryPackageTest.class);
    private StatefulKnowledgeSession ksession;

    public TimerCycleOnBinaryPackageTest() {
        super(true);
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
        }
    }

    @Test
    public void testStartTimerCycleFromDisc() throws Exception {
        KieBase kbase = createKnowledgeBaseFromDisc("BPMN2-StartTimerCycle.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        assertEquals(0, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
        long sessionId = ksession.getIdentifier();
        Environment env = ksession.getEnvironment();

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()
                .addEventListener(new TriggerRulesEventListener(ksession));

        

        Thread.sleep(5000);

        assertEquals(2, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
        logger.info("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);

        final List<Long> list2 = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list2.add(event.getProcessInstance().getId());
            }
        });

        ((KnowledgeCommandContext) 
                ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext())
        .getKieSession().addEventListener(new TriggerRulesEventListener(ksession));

        

        Thread.sleep(5000);
        ksession.dispose();
        assertEquals(4, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
    }

    @Test
    public void testStartTimerCycleFromClassPath() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-StartTimerCycle.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        assertEquals(0, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
        long sessionId = ksession.getIdentifier();
        Environment env = ksession.getEnvironment();

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        ((KnowledgeCommandContext) 
                ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext())
        .getKieSession().addEventListener(new TriggerRulesEventListener(ksession));

        Thread.sleep(5000);

        assertEquals(2, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
        logger.info("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);

        final List<Long> list2 = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list2.add(event.getProcessInstance().getId());
            }
        });

        ((KnowledgeCommandContext) 
                ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext())
        .getKieSession().addEventListener(new TriggerRulesEventListener(ksession));

        Thread.sleep(5000);
        ksession.dispose();
        assertEquals(4, getNumberOfProcessInstances("defaultPackage.TimerProcess"));
    }

    @Test @Ignore("beta4 phreak")
    public void testStartTimerCycleFromDiscDRL() throws Exception {
        KieBase kbase = createKnowledgeBaseFromDisc("rules-timer.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        long sessionId = ksession.getIdentifier();
        Environment env = ksession.getEnvironment();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ((KnowledgeCommandContext) 
                ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext())
        .getKieSession().addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        logger.info("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);

        final List<String> list2 = new ArrayList<String>();
        ksession.setGlobal("list", list2);

        ((KnowledgeCommandContext) 
                ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext())
        .getKieSession().addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(6000);

        assertEquals(3, list2.size());
    }

    @Test @Ignore("beta4 phreak")
    public void testStartTimerCycleFromClasspathDRL() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("rules-timer.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        long sessionId = ksession.getIdentifier();
        Environment env = ksession.getEnvironment();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ((KnowledgeCommandContext) 
                ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext())
        .getKieSession().addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        logger.info("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);

        final List<String> list2 = new ArrayList<String>();
        ksession.setGlobal("list", list2);

        ((KnowledgeCommandContext) 
                ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext())
        .getKieSession().addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(3, list2.size());
    }


}
