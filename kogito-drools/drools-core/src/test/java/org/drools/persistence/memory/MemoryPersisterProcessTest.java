package org.drools.persistence.memory;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.persistence.Transaction;
import org.drools.persistence.session.StatefulSessionSnapshotter;
import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.rule.Package;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ProcessContext;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.WorkItemNode;

public class MemoryPersisterProcessTest extends TestCase {

	private WorkItem workItem;
	
    public void testSave() throws Exception {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.drools.test" );
        pkg.addProcess( getProcess() );
        ruleBase.addPackage( pkg );

        StatefulSession session = ruleBase.newStatefulSession();
        session.getWorkItemManager().registerWorkItemHandler("MyWork", new WorkItemHandler() {
			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
				MemoryPersisterProcessTest.this.workItem = workItem;
			}
			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			}
        });
        ProcessInstance processInstance = session.startProcess("org.drools.test.TestProcess");
        assertNotNull(workItem);

        MemoryPersister pm = new MemoryPersister( new StatefulSessionSnapshotter( session ) );
        pm.save();

        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        pm.load();
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    public void testTransactionWithRollback() throws Exception {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        Package pkg = new Package( "org.drools.test" );
        pkg.addProcess( getProcess() );
        ruleBase.addPackage( pkg );

        StatefulSession session = ruleBase.newStatefulSession();
        session.getWorkItemManager().registerWorkItemHandler("MyWork", new WorkItemHandler() {
			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
				MemoryPersisterProcessTest.this.workItem = workItem;
			}
			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			}
        });
        ProcessInstance processInstance = session.startProcess("org.drools.test.TestProcess");

        MemoryPersister pm = new MemoryPersister( new StatefulSessionSnapshotter( session ) );
        Transaction t = pm.getTransaction();
        t.start();

        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        t.rollback();
        
        processInstance = session.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    private RuleFlowProcess getProcess() {
    	RuleFlowProcess process = new RuleFlowProcess();
    	process.setId("org.drools.test.TestProcess");
    	process.setName("TestProcess");
    	process.setPackageName("org.drools.test");
    	StartNode start = new StartNode();
    	start.setId(1);
    	start.setName("Start");
    	process.addNode(start);
    	ActionNode actionNode = new ActionNode();
    	actionNode.setId(2);
    	actionNode.setName("Action");
    	DroolsConsequenceAction action = new DroolsConsequenceAction();
    	action.setMetaData("Action", new Action() {
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
            	System.out.println("Executed action");
            }
        });
    	actionNode.setAction(action);
    	process.addNode(actionNode);
    	new ConnectionImpl(start, Node.CONNECTION_DEFAULT_TYPE, actionNode, Node.CONNECTION_DEFAULT_TYPE);
    	WorkItemNode workItemNode = new WorkItemNode();
    	workItemNode.setId(3);
    	workItemNode.setName("WorkItem");
    	Work work = new WorkImpl();
    	work.setName("MyWork");
    	workItemNode.setWork(work);
    	process.addNode(workItemNode);
    	new ConnectionImpl(actionNode, Node.CONNECTION_DEFAULT_TYPE, workItemNode, Node.CONNECTION_DEFAULT_TYPE);
    	EndNode end = new EndNode();
    	end.setId(4);
    	end.setName("End");
    	process.addNode(end);
    	new ConnectionImpl(workItemNode, Node.CONNECTION_DEFAULT_TYPE, end, Node.CONNECTION_DEFAULT_TYPE);
        return process;
    }

    public boolean assertEquals(byte[] bytes1,
                                byte[] bytes2) {
        if ( bytes1.length != bytes2.length ) {
            return false;
        }

        for ( int i = 0; i < bytes1.length; i++ ) {
            if ( bytes1[i] != bytes2[i] ) {
                return false;
            }
        }

        return true;
    }

}
