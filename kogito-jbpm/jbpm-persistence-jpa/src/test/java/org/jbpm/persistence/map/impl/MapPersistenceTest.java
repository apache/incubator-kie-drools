package org.jbpm.persistence.map.impl;

import org.drools.common.AbstractRuleBase;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.process.instance.WorkItemHandler;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.junit.Assert;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;

public abstract class MapPersistenceTest {

    @Test
    public void startProcessInPersistentEnvironment() {
        String processId = "minimalProcess";

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase())
                .addProcess( ProcessCreatorForHelp.newShortestProcess( processId ) );
        
        StatefulKnowledgeSession crmPersistentSession = createSession(kbase);

        crmPersistentSession.startProcess(processId);

        crmPersistentSession.dispose();
    }

    @Test
    public void createProcessStartItDisposeAndLoadItAgain() {
        String processId = "minimalProcess";
        String workName = "MyWork";

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase())
                .addProcess( ProcessCreatorForHelp.newProcessWithOneWork( processId,
                                                                          workName ) );

        StatefulKnowledgeSession ksession = createSession(kbase);
        int ksessionId = ksession.getId();

        DummyWorkItemHandler handler = new DummyWorkItemHandler();
        ksession.getWorkItemManager()
            .registerWorkItemHandler(workName, handler);

        long process1Id = ksession.startProcess(processId).getId();

        ksession = disposeAndReloadSession(ksession, ksessionId, kbase);
        ksession.getWorkItemManager().registerWorkItemHandler(workName, handler);

        long workItemId = handler.getLatestWorkItem().getId();

        ksession.getWorkItemManager().completeWorkItem(workItemId, null);

        Assert.assertNotNull(ksession);

        Assert.assertNull( ksession.getProcessInstance( process1Id ) );

    }
    
    @Test
    public void signalEventTest() {
        String processId = "signalProcessTest";
        String eventType = "myEvent";
        RuleFlowProcess process = ProcessCreatorForHelp.newSimpleEventProcess( processId,
                                                         eventType );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess( process );
        
        StatefulKnowledgeSession crmPersistentSession = createSession(kbase);

        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) crmPersistentSession.startProcess( processId );
        long processInstanceId = processInstance.getId();
        Assert.assertEquals( ProcessInstance.STATE_ACTIVE,
                             processInstance.getState() );

        crmPersistentSession = createSession(kbase);

        crmPersistentSession.signalEvent( eventType,
                              null );
        processInstance = (RuleFlowProcessInstance) crmPersistentSession.getProcessInstance( processInstanceId );

        Assert.assertNull( processInstance );
    }
    
    @Test
    public void executeMultipleProcessTest() {
        String processId = "minimalProcess";
        String workName = "MyWork";

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase())
                .addProcess( ProcessCreatorForHelp.newProcessWithOneWork( processId,
                                                                          workName ) );

        StatefulKnowledgeSession ksession1 = createSession(kbase);
        int ksession1Id = ksession1.getId();
        StatefulKnowledgeSession ksession2 = createSession(kbase);
        int ksession2Id = ksession2.getId();

        DummyWorkItemHandler handler1 = new DummyWorkItemHandler();
        ksession1.getWorkItemManager()
                .registerWorkItemHandler( workName,
                                          handler1 );

        DummyWorkItemHandler handler2 = new DummyWorkItemHandler();
        ksession2.getWorkItemManager()
                .registerWorkItemHandler( workName,
                                          handler2 );

        long process1Id = ksession1.startProcess( processId ).getId();
        long workItem1Id = handler1.getLatestWorkItem().getId();
        ksession1 = disposeAndReloadSession( ksession1, ksession1Id,
                                             kbase);
        ksession1.getWorkItemManager().completeWorkItem( workItem1Id,
                                                         null );
        Assert.assertNull( ksession1.getProcessInstance( process1Id ) );

        ksession2 = disposeAndReloadSession(ksession2, ksession2Id, kbase);
        Assert.assertNotNull(ksession2);

        ksession2.getWorkItemManager()
                .registerWorkItemHandler( workName,
                                          handler2 );

        long process2Id = ksession2.startProcess( processId ).getId();
        long workItem2Id = handler2.getLatestWorkItem().getId();
        ksession2.getWorkItemManager().completeWorkItem( workItem2Id,
                                                         null );
        Assert.assertNull( ksession2.getProcessInstance( process2Id ) );
    }
    
    @Test
    public void multipleKSessionDifferentIdTest() {
        KnowledgeBase kbase1 = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();

        StatefulKnowledgeSession ksession1 = createSession(kbase1);
        StatefulKnowledgeSession ksession2 = createSession(kbase2);

        Assert.assertNotSame(ksession1.getId(), ksession2.getId());

    }

    @Test
    public void multipleSessionsWithSameProcessAndDifferentIdTest() {
        String processId = "signalProcessTest";
        String eventType = "myEvent";

        RuleFlowProcess process1 = ProcessCreatorForHelp.newSimpleEventProcess( processId,
                eventType );
        RuleFlowProcess process2 = ProcessCreatorForHelp.newSimpleEventProcess( processId,
                                                                               eventType );

        KnowledgeBase kbase1 = KnowledgeBaseFactory.newKnowledgeBase();
        KnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();

        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase1).getRuleBase()).addProcess( process1 );
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase2).getRuleBase()).addProcess( process2 );

        StatefulKnowledgeSession ksession1 = createSession(kbase1);
        StatefulKnowledgeSession ksession2 = createSession(kbase2);

        Assert.assertNotSame(ksession1.getId(), ksession2.getId());

        Long processInstance1Id = ksession1.startProcess(processId).getId();

        Long processInstance2Id = ksession2.startProcess(processId).getId();

        Assert.assertNotSame(processInstance1Id, processInstance2Id);

    }
    
    @Test
    public void multipleSessionsWithSameProcessAndSameWorkItemAndDifferentIdTest() {
        String processId = "minimalProcess";
        String workName = "MyWork";

        KnowledgeBase kbase1 = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase1).getRuleBase())
                .addProcess( ProcessCreatorForHelp.newProcessWithOneWork( processId,
                                                                          workName ) );

        KnowledgeBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase2).getRuleBase())
                .addProcess( ProcessCreatorForHelp.newProcessWithOneWork( processId,
                                                                          workName ) );

        StatefulKnowledgeSession ksession1 = createSession(kbase1);
        StatefulKnowledgeSession ksession2 = createSession(kbase2);

        DummyWorkItemHandler handler1 = new DummyWorkItemHandler();
        DummyWorkItemHandler handler2 = new DummyWorkItemHandler();

        ksession1.getWorkItemManager().registerWorkItemHandler(workName, handler1);
        ksession2.getWorkItemManager().registerWorkItemHandler(workName, handler2);

        ksession1.startProcess(processId);
        ksession2.startProcess(processId);

        long workItem1Id = handler1.getLatestWorkItem().getId();
        long workItem2Id = handler2.getLatestWorkItem().getId();
        Assert.assertNotSame(workItem1Id, workItem2Id);
    }
    
    @Test
    public void crashProcessBeforePersisting() {
        String processId = "myProcess";
        String workName = "someWork";

        int knowledgeSessionsCountBeforeTest = getKnowledgeSessionsCount(); 
        int processInstancesBeforeTest = getProcessInstancesCount();
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase())
                .addProcess( ProcessCreatorForHelp.newProcessWithOneWork( processId,
                                                                          workName ) );

        StatefulKnowledgeSession crmPersistentSession = createSession( kbase );
        ChrashingWorkItemHandler handler = new ChrashingWorkItemHandler();
        crmPersistentSession.getWorkItemManager()
                .registerWorkItemHandler( workName,
                                          handler );

        try {
            crmPersistentSession.startProcess( processId );
            Assert.fail();
        } catch ( RuntimeException re ) {
        }
        
        Assert.assertEquals( knowledgeSessionsCountBeforeTest + 1, getKnowledgeSessionsCount() );
        Assert.assertEquals( processInstancesBeforeTest, getProcessInstancesCount() );
    }
    
    @Test
    public void processWithSubProcessThatCrashTest() {
        String processId = "minimalProcess";
        String subProcessId = "subProcess";
        String workName = "MyWork";

        int knowledgeSessionsCountBeforeTest = getKnowledgeSessionsCount(); 
        int processInstancesBeforeTest = getProcessInstancesCount();
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase())
                .addProcess( ProcessCreatorForHelp.newProcessWithOneWork(subProcessId, workName));

        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase())
        .addProcess( ProcessCreatorForHelp.newProcessWithOneSubProcess(processId, subProcessId));

        StatefulKnowledgeSession ksession = createSession(kbase);

        ChrashingWorkItemHandler handler = new ChrashingWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler(workName, handler);

        try {
            ksession.startProcess(processId);
            Assert.fail();
        } catch ( RuntimeException re ) {
            Assert.assertEquals( knowledgeSessionsCountBeforeTest + 1, getKnowledgeSessionsCount() );
            Assert.assertEquals( processInstancesBeforeTest, getProcessInstancesCount() );
        }
    }


    protected abstract StatefulKnowledgeSession createSession(KnowledgeBase kbase);
    
    protected abstract StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession crmPersistentSession,
                                                                        int ksessionId,
                                                                        KnowledgeBase kbase);

    protected abstract int getProcessInstancesCount();

    protected abstract int getKnowledgeSessionsCount();
   
    private static class DummyWorkItemHandler
        implements
        WorkItemHandler {

        private WorkItem latestWorkItem;

        public void executeWorkItem(WorkItem workItem,
                                    WorkItemManager manager) {
            this.setLatestWorkItem( workItem );
        }

        public void abortWorkItem(WorkItem workItem,
                                  WorkItemManager manager) {
        }

        public void setLatestWorkItem(WorkItem latestWorkItem) {
            this.latestWorkItem = latestWorkItem;
        }

        public WorkItem getLatestWorkItem() {
            return latestWorkItem;
        }
    }

    private static class ChrashingWorkItemHandler
        implements
        WorkItemHandler {

        public void executeWorkItem(WorkItem workItem,
                                    WorkItemManager manager) {
            throw new RuntimeException( "I die" );
        }

        public void abortWorkItem(WorkItem workItem,
                                  WorkItemManager manager) {
        }
    }
}
