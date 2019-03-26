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

package org.jbpm.persistence.map.impl;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.process.instance.WorkItemHandler;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public abstract class MapPersistenceTest extends AbstractBaseTest {

    @Test
    public void startProcessInPersistentEnvironment() {
        String processId = "minimalProcess";

        KieBase kbase = createKieBase(ProcessCreatorForHelp.newShortestProcess( processId ) );
        
        StatefulKnowledgeSession crmPersistentSession = createSession(kbase);

        crmPersistentSession.startProcess(processId);

        crmPersistentSession.dispose();
    }

    @Test
    public void createProcessStartItDisposeAndLoadItAgain() {
        String processId = "minimalProcess";
        String workName = "MyWork";

        KieBase kbase = createKieBase(ProcessCreatorForHelp.newProcessWithOneWork( processId, workName ));

        StatefulKnowledgeSession ksession = createSession(kbase);
        long ksessionId = ksession.getIdentifier();

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

        KieBase kbase = createKieBase(process);
        
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

        KieBase kbase = createKieBase(ProcessCreatorForHelp.newProcessWithOneWork( processId, workName ) );

        StatefulKnowledgeSession ksession1 = createSession(kbase);
        long ksession1Id = ksession1.getIdentifier();
        StatefulKnowledgeSession ksession2 = createSession(kbase);
        long ksession2Id = ksession2.getIdentifier();

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
        KieBase kbase1 = KnowledgeBaseFactory.newKnowledgeBase();
        KieBase kbase2 = KnowledgeBaseFactory.newKnowledgeBase();

        StatefulKnowledgeSession ksession1 = createSession(kbase1);
        StatefulKnowledgeSession ksession2 = createSession(kbase2);

        Assert.assertNotSame(ksession1.getIdentifier(), ksession2.getIdentifier());

    }

    @Test
    public void multipleSessionsWithSameProcessAndDifferentIdTest() {
        String processId = "signalProcessTest";
        String eventType = "myEvent";

        RuleFlowProcess process1 = ProcessCreatorForHelp.newSimpleEventProcess( processId,
                eventType );
        RuleFlowProcess process2 = ProcessCreatorForHelp.newSimpleEventProcess( processId,
                                                                               eventType );

        KieBase kbase1 = createKieBase(process1);
        KieBase kbase2 = createKieBase(process2);

        StatefulKnowledgeSession ksession1 = createSession(kbase1);
        StatefulKnowledgeSession ksession2 = createSession(kbase2);

        Assert.assertNotSame(ksession1.getIdentifier(), ksession2.getIdentifier());

        Long processInstance1Id = ksession1.startProcess(processId).getId();

        Long processInstance2Id = ksession2.startProcess(processId).getId();

        Assert.assertNotSame(processInstance1Id, processInstance2Id);

    }
    
    @Test
    public void multipleSessionsWithSameProcessAndSameWorkItemAndDifferentIdTest() {
        String processId = "minimalProcess";
        String workName = "MyWork";

        KieBase kbase1 = createKieBase(ProcessCreatorForHelp.newProcessWithOneWork( processId, workName ) );
        KieBase kbase2 = createKieBase(ProcessCreatorForHelp.newProcessWithOneWork( processId, workName ) );

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
        
        KieBase kbase = createKieBase(ProcessCreatorForHelp.newProcessWithOneWork( processId, workName ) );

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
        
        KieBase kbase = createKieBase(
                ProcessCreatorForHelp.newProcessWithOneWork(subProcessId, workName),
                ProcessCreatorForHelp.newProcessWithOneSubProcess(processId, subProcessId));

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
    
    @Test
    public void processWithNotNullStartDateTest() {
        String processId = "signalProcessTest";
        String eventType = "myEvent";
        RuleFlowProcess process = ProcessCreatorForHelp.newSimpleEventProcess( processId,
                                                                               eventType );

        KieBase kbase = createKieBase(process);
        StatefulKnowledgeSession crmPersistentSession = createSession(kbase);
        
        RuleFlowProcessInstance processInstance = (RuleFlowProcessInstance) crmPersistentSession.startProcess( processId );   
        InternalKnowledgeRuntime kruntime = processInstance.getKnowledgeRuntime();
        Assert.assertEquals( ProcessInstance.STATE_ACTIVE,
                             processInstance.getState() );

        ProcessInstanceInfo processInstanceInfo = new ProcessInstanceInfo(processInstance);
        processInstance = (RuleFlowProcessInstance) processInstanceInfo.getProcessInstance(kruntime, crmPersistentSession.getEnvironment());

        Assert.assertNotNull(processInstance.getStartDate());
        Assert.assertEquals(processInstance.getStartDate(), processInstanceInfo.getStartDate());
    }
    
    protected abstract StatefulKnowledgeSession createSession(KieBase kbase);
    
    protected abstract StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession crmPersistentSession,
                                                                        long ksessionId,
                                                                        KieBase kbase);

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
