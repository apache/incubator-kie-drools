package org.jbpm.bpmn2.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessNodeLeftEvent;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.process.instance.impl.RuleAwareProcessEventLister;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;

public class SimplePersistedBPMNProcessTest extends JbpmBpmn2TestCase {

    public SimplePersistedBPMNProcessTest() {
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
}
