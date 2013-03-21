package org.jbpm.bpmn2.persistence;

import java.util.ArrayList;
import java.util.List;

import org.drools.core.WorkingMemory;
import org.drools.core.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.event.ActivationCancelledEvent;
import org.drools.core.event.ActivationCreatedEvent;
import org.drools.core.event.AfterActivationFiredEvent;
import org.drools.core.event.AgendaEventListener;
import org.drools.core.event.AgendaGroupPoppedEvent;
import org.drools.core.event.AgendaGroupPushedEvent;
import org.drools.core.event.BeforeActivationFiredEvent;
import org.drools.core.event.RuleFlowGroupActivatedEvent;
import org.drools.core.event.RuleFlowGroupDeactivatedEvent;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.bpmn2.JbpmTestCase;
import org.jbpm.process.audit.AuditLoggerFactory;
import org.jbpm.process.audit.AuditLoggerFactory.Type;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.runtime.Environment;

public class TimerCycleOnBinaryPackageTest extends JbpmTestCase {

    private StatefulKnowledgeSession ksession;

    public TimerCycleOnBinaryPackageTest() {
        super(true);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
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

        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);
        int sessionId = ksession.getId();
        Environment env = ksession.getEnvironment();

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        System.out.println("dispose");
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

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(3, list2.size());
    }

    @Test
    public void testStartTimerCycleFromClassPath() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-StartTimerCycle.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);
        int sessionId = ksession.getId();
        Environment env = ksession.getEnvironment();

        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        System.out.println("dispose");
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

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(3, list2.size());
    }

    @Test
    public void testStartTimerCycleFromDiscDRL() throws Exception {
        KieBase kbase = createKnowledgeBaseFromDisc("rules-timer.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);
        int sessionId = ksession.getId();
        Environment env = ksession.getEnvironment();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        System.out.println("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);

        final List<String> list2 = new ArrayList<String>();
        ksession.setGlobal("list", list2);

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(3, list2.size());
    }

    @Test
    public void testStartTimerCycleFromClasspathDRL() throws Exception {
        KieBase kbase = createKnowledgeBase("rules-timer.drl");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);
        int sessionId = ksession.getId();
        Environment env = ksession.getEnvironment();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        System.out.println("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId,
                kbase, null, env);
        AuditLoggerFactory.newInstance(Type.JPA, ksession, null);

        final List<String> list2 = new ArrayList<String>();
        ksession.setGlobal("list", list2);

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getKieSession()).session
                .addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(3, list2.size());
    }

    private static class TriggerRulesEventListener implements
            AgendaEventListener {

        private StatefulKnowledgeSession ksession;

        public TriggerRulesEventListener(StatefulKnowledgeSession ksession) {

            this.ksession = ksession;
        }

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
                RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
        }

        public void afterRuleFlowGroupActivated(
                RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
            workingMemory.fireAllRules();
        }

        public void beforeRuleFlowGroupDeactivated(
                RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
        }

        public void afterRuleFlowGroupDeactivated(
                RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
        }

    }
}
