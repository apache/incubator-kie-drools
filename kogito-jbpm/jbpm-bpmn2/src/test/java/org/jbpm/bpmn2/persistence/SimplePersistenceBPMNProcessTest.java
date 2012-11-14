package org.jbpm.bpmn2.persistence;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.drools.WorkingMemory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderConfiguration;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.command.Context;
import org.drools.command.impl.CommandBasedStatefulKnowledgeSession;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.compiler.PackageBuilderConfiguration;
import org.kie.definition.process.Process;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;
import org.kie.event.process.DefaultProcessEventListener;
import org.kie.event.process.ProcessNodeLeftEvent;
import org.kie.event.process.ProcessNodeTriggeredEvent;
import org.kie.event.rule.DebugAgendaEventListener;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.kie.io.ResourceFactory;
import org.kie.persistence.jpa.JPAKnowledgeService;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.SimpleBPMNProcessTest;
import org.jbpm.bpmn2.JbpmBpmn2TestCase.TestWorkItemHandler;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.instance.impl.RuleAwareProcessEventLister;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.ruleflow.core.RuleFlowProcess;

public class SimplePersistenceBPMNProcessTest extends JbpmBpmn2TestCase {

    public SimplePersistenceBPMNProcessTest() {
        super(true);
    }
    
    public void testBusinessRuleTask() throws Exception {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        resources.put("BPMN2-BusinessRuleTask.bpmn2", ResourceType.BPMN2);
        resources.put("BPMN2-BusinessRuleTask.drl", ResourceType.DRL);
        KnowledgeBase kbase = createKnowledgeBase(resources);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ProcessInstance processInstance = ksession.startProcess("BPMN2-BusinessRuleTask");

        restoreSession(ksession, true);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);

    }
    
    public void testScriptTaskWithHistoryLog() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-ScriptTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        
        List<NodeInstanceLog> logs = JPAProcessInstanceDbLog.findNodeInstances(processInstance.getId());
        assertNotNull(logs);
        assertEquals(6, logs.size());
        
        for (NodeInstanceLog log : logs) {
            assertNotNull(log.getDate());
        }
        
        ProcessInstanceLog pilog = JPAProcessInstanceDbLog.findProcessInstance(processInstance.getId());
        assertNotNull(pilog);
        assertNotNull(pilog.getEnd());
        
        List<ProcessInstanceLog> pilogs = JPAProcessInstanceDbLog.findActiveProcessInstances(processInstance.getProcessId());
        assertNotNull(pilogs);
        assertEquals(0, pilogs.size());
        
    }
    
    public void testIntermediateCatchEventTimerCycleWithError() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerCycleWithError.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance.getId(), ksession);

        final long piId = processInstance.getId();
        ksession.execute(new GenericCommand<Void>() {

            public Void execute(Context context) {
                StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
                WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.getProcessInstance(piId);
                processInstance.setVariable("x", 0);
                return null;
            }
        });
        
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance.getId(), ksession);
        Thread.sleep(1000);
        assertProcessInstanceActive(processInstance.getId(), ksession);
        
        Integer xValue = ksession.execute(new GenericCommand<Integer>() {

            public Integer execute(Context context) {
                StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
                WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.getProcessInstance(piId);
                return (Integer) processInstance.getVariable("x");
                
            }
        });
        assertEquals(new Integer(2), xValue);
        ksession.abortProcessInstance(processInstance.getId());
        assertProcessInstanceAborted(processInstance.getId(), ksession);
    }
    
    public void testCallActivityWithTimer() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(ResourceFactory
                .newClassPathResource("BPMN2-ParentProcess.bpmn2"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory
                .newClassPathResource("BPMN2-SubProcessWithTimer.bpmn2"),  ResourceType.BPMN2);
   
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        Environment env = EnvironmentFactory.newEnvironment();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
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
        
        System.out.println("dispose");
        ksession.dispose();
        Thread.sleep(3000);
        
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
        Thread.sleep(3000);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);

    }
    
    public void testProcesWithHumanTaskWithTimer() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(ResourceFactory
                .newClassPathResource("BPMN2-SubProcessWithTimer.bpmn2"),  ResourceType.BPMN2);
   
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        Environment env = EnvironmentFactory.newEnvironment();
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
        env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);

        StatefulKnowledgeSession ksession = JPAKnowledgeService.newStatefulKnowledgeSession(kbase, null, env);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance processInstance = ksession.startProcess(
                "subproc", params);
        
        ksession.getWorkItemManager().completeWorkItem(
                workItemHandler.getWorkItem().getId(), null);
        
        int sessionId = ksession.getId();

        ksession.dispose();        
        Thread.sleep(3000);
        
        ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionId, kbase, null, env);
        Thread.sleep(3000);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);

    }
    
    public void testBusinessRuleTaskWithDataInputs() throws Exception {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        resources.put("BPMN2-BusinessRuleTaskWithDataInputs.bpmn2", ResourceType.BPMN2);
        resources.put("BPMN2-BusinessRuleTaskWithDataInput.drl", ResourceType.DRL);
        KnowledgeBase kbase = createKnowledgeBase(resources);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", new Person());
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-BusinessRuleTask", params);
        
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    public void testBusinessRuleTaskDynamic() throws Exception {
        Map<String, ResourceType> resources = new HashMap<String, ResourceType>();
        resources.put("BPMN2-BusinessRuleTaskDynamic.bpmn2", ResourceType.BPMN2);
        resources.put("BPMN2-BusinessRuleTask.drl", ResourceType.DRL);
        KnowledgeBase kbase = createKnowledgeBase(resources);
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(new RuleAwareProcessEventLister());
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dynamicrule", "MyRuleFlow");
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-BusinessRuleTask", params);
        
        int fired = ksession.fireAllRules();
        assertEquals(1, fired);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    public void testCompensateEndEventProcess() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-CompensateEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("CompensateEndEvent");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "Task", "CompensateEvent", "CompensateEvent2", "Compensate", "EndEvent");
    }
    
    public void testSignalBoundaryEventOnTask() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-BoundarySignalEventOnTaskbpmn2.bpmn");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new TestWorkItemHandler());
        ksession.addEventListener(new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("After node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("After node triggered " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeLeft(ProcessNodeLeftEvent event) {
                System.out.println("Before node left " + event.getNodeInstance().getNodeName());
            }

            @Override
            public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
                System.out.println("Before node triggered " + event.getNodeInstance().getNodeName());
            }
           
        });
        ProcessInstance processInstance = ksession.startProcess("BoundarySignalOnTask");
        ksession.signalEvent("MySignal", "hello");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "User Task", "Boundary event", "Signal received", "End2");
    }
    
    public void testIntermediateCatchEventSignal() throws Exception {
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession
                .startProcess("IntermediateCatchEvent");
        assertTrue(processInstance.getState() == ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("MyMessage", "SomeValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "StartProcess", "UserTask", "EndProcess", "event");
    }
    
    public void testRuleTaskWithFacts() throws Exception {
        KnowledgeBuilderConfiguration conf = KnowledgeBuilderFactory
                .newKnowledgeBuilderConfiguration();
        ((PackageBuilderConfiguration) conf).initSemanticModules();
        ((PackageBuilderConfiguration) conf)
                .addSemanticModule(new BPMNSemanticModule());
        ((PackageBuilderConfiguration) conf)
                .addSemanticModule(new BPMNDISemanticModule());
        // ProcessDialectRegistry.setDialect("XPath", new XPathDialect());
        XmlProcessReader processReader = new XmlProcessReader(
                ((PackageBuilderConfiguration) conf).getSemanticModules(),
                getClass().getClassLoader());
        List<Process> processes = processReader
                .read(SimpleBPMNProcessTest.class
                        .getResourceAsStream("/BPMN2-RuleTaskWithFact.bpmn2"));
        assertNotNull(processes);
        assertEquals(1, processes.size());
        RuleFlowProcess p = (RuleFlowProcess) processes.get(0);
        assertNotNull(p);
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder(conf);
        // logger.debug(XmlBPMNProcessDumper.INSTANCE.dump(p));
        kbuilder.add(ResourceFactory.newReaderResource(new StringReader(
                XmlBPMNProcessDumper.INSTANCE.dump(p))), ResourceType.BPMN2);
        kbuilder.add(
                ResourceFactory.newClassPathResource("BPMN2-RuleTask3.drl"),
                ResourceType.DRL);
        if (!kbuilder.getErrors().isEmpty()) {
            throw new IllegalArgumentException(
                    "Errors while parsing knowledge base");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        
        final org.drools.event.AgendaEventListener agendaEventListener = new org.drools.event.AgendaEventListener() {
            public void activationCreated(ActivationCreatedEvent event, WorkingMemory workingMemory){
                ksession.fireAllRules();
            }
            public void activationCancelled(ActivationCancelledEvent event, WorkingMemory workingMemory){
            }
            public void beforeActivationFired(BeforeActivationFiredEvent event, WorkingMemory workingMemory) {
            }
            public void afterActivationFired(AfterActivationFiredEvent event, WorkingMemory workingMemory) {
            }
            public void agendaGroupPopped(AgendaGroupPoppedEvent event, WorkingMemory workingMemory) {
            }

            public void agendaGroupPushed(AgendaGroupPushedEvent event, WorkingMemory workingMemory) {
            }
            public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
            }
            public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent event, WorkingMemory workingMemory) {
                workingMemory.fireAllRules();
            }
            public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
            }
            public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent event, WorkingMemory workingMemory) {
            }
        };
        ((StatefulKnowledgeSessionImpl)  ((KnowledgeCommandContext) ((CommandBasedStatefulKnowledgeSession) ksession)
                .getCommandService().getContext()).getStatefulKnowledgesession() )
                .session.addEventListener(agendaEventListener);
        ksession.addEventListener(new DebugAgendaEventListener());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "SomeString");
        ProcessInstance processInstance = ksession.startProcess("RuleTask", params);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);

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
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

}
