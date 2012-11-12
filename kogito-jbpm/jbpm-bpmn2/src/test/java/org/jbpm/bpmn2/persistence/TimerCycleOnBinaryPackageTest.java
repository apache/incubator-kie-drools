package org.jbpm.bpmn2.persistence;

import static org.jbpm.persistence.util.PersistenceUtil.JBPM_PERSISTENCE_UNIT_NAME;
import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.drools.WorkingMemory;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.core.util.DroolsStreamUtils;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.DefaultProcessEventListener;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.definition.KnowledgePackage;
import org.kie.event.process.ProcessStartedEvent;
import org.kie.io.ResourceFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;

public class TimerCycleOnBinaryPackageTest {
    
    private HashMap<String, Object> context;
    private Environment env;
    
    @Before
    public void setUp() {
        context = setupWithPoolingDataSource(JBPM_PERSISTENCE_UNIT_NAME);

        // load up the knowledge base
        env = EnvironmentFactory.newEnvironment();
        EntityManagerFactory emf = (EntityManagerFactory) context.get(EnvironmentName.ENTITY_MANAGER_FACTORY);
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
    }
    
    @After
    public void tearDown() {
        env = null;
        cleanUp(context);
    }
    

    @Test
    public void testStartTimerCycleFromDisc() throws Exception {
        KnowledgeBase kbase = readKnowledgeBaseFromDisc();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);

        new JPAWorkingMemoryDbLogger(ksession);
        int sessionId = ksession.getId();
        
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        
        ((StatefulKnowledgeSessionImpl)  ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession() )
            .session.addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();
        
        Thread.sleep(5000);
        
        assertEquals(2, list.size());
        System.out.println("dispose");
        ksession.dispose();
        
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
        new JPAWorkingMemoryDbLogger(ksession);
        
        final List<Long> list2 = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list2.add(event.getProcessInstance().getId());
            }
        });
        
        ((StatefulKnowledgeSessionImpl)  ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession() )
            .session.addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();
        
        Thread.sleep(5000);
        
        assertEquals(3, list2.size());
        ksession.dispose();
    }
    
    @Test
    public void testStartTimerCycleFromClassPath() throws Exception {
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);

        new JPAWorkingMemoryDbLogger(ksession);
        int sessionId = ksession.getId();
        
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        
        ((StatefulKnowledgeSessionImpl)  ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession() )
            .session.addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();
        
        Thread.sleep(5000);
        
        assertEquals(2, list.size());
        System.out.println("dispose");
        ksession.dispose();
        
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
        new JPAWorkingMemoryDbLogger(ksession);
        
        final List<Long> list2 = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list2.add(event.getProcessInstance().getId());
            }
        });
        
        ((StatefulKnowledgeSessionImpl)  ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession() )
            .session.addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();
        
        Thread.sleep(5000);
        
        assertEquals(3, list2.size());
        ksession.dispose();
    }
    
    @Test
    public void testStartTimerCycleFromDiscDRL() throws Exception {
        KnowledgeBase kbase = readKnowledgeBaseFromDiscDRL();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);

        new JPAWorkingMemoryDbLogger(ksession);
        int sessionId = ksession.getId();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession()).session.addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        System.out.println("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
        new JPAWorkingMemoryDbLogger(ksession);

        final List<String> list2 = new ArrayList<String>();
        ksession.setGlobal("list", list2);

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession()).session.addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(3, list2.size());
        ksession.dispose();
    }
    
     @Test
    public void testStartTimerCycleFromClasspathDRL() throws Exception {
        // load up the knowledge base
        KnowledgeBase kbase = readKnowledgeBaseDRL();
        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);

        new JPAWorkingMemoryDbLogger(ksession);
        int sessionId = ksession.getId();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession()).session.addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(2, list.size());
        System.out.println("dispose");
        ksession.dispose();

        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
        new JPAWorkingMemoryDbLogger(ksession);

        final List<String> list2 = new ArrayList<String>();
        ksession.setGlobal("list", list2);

        ((StatefulKnowledgeSessionImpl) ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession()).session.addEventListener(new TriggerRulesEventListener(ksession));

        ksession.fireAllRules();

        Thread.sleep(5000);

        assertEquals(3, list2.size());
        ksession.dispose();
    }

    private KnowledgeBase readKnowledgeBaseFromDisc() throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-StartTimerCycle.bpmn2"), ResourceType.BPMN2);
        File packageFile = null;
        // build and store compiled package
        for (KnowledgePackage pkg : kbuilder.getKnowledgePackages() ) {
            packageFile = new File(System.getProperty("java.io.tmpdir") + File.separator + pkg.getName()+".pkg");
            writePackage(pkg, packageFile);
            
            // store first package only
            break;
        }
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newFileResource(packageFile), ResourceType.PKG);

        return kbuilder.newKnowledgeBase();
    }
    
    private KnowledgeBase readKnowledgeBase() throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-StartTimerCycle.bpmn2"), ResourceType.BPMN2);

        return kbuilder.newKnowledgeBase();
    }
    
    private KnowledgeBase readKnowledgeBaseFromDiscDRL() throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("rules-timer.drl"), ResourceType.DRL);
        File packageFile = null;
        // build and store compiled package
        for (KnowledgePackage pkg : kbuilder.getKnowledgePackages()) {
            packageFile = new File(System.getProperty("java.io.tmpdir") + File.separator + pkg.getName() + ".pkg");
            writePackage(pkg, packageFile);

            // store first package only
            break;
        }
        kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newFileResource(packageFile), ResourceType.PKG);

        return kbuilder.newKnowledgeBase();
    }

    private KnowledgeBase readKnowledgeBaseDRL() throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("rules-timer.drl"), ResourceType.DRL);

        return kbuilder.newKnowledgeBase();
    }
    
    private void writePackage(KnowledgePackage kpackage, File p1file)
            throws IOException, FileNotFoundException {
        FileOutputStream out = new FileOutputStream(p1file);
        try {
            DroolsStreamUtils.streamOut(out, kpackage);
        } finally {
            out.close();
        }
    }

    
    private static class TriggerRulesEventListener implements AgendaEventListener {
        
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

        public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
                WorkingMemory workingMemory) {
        }

        public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event,
                WorkingMemory workingMemory) {
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