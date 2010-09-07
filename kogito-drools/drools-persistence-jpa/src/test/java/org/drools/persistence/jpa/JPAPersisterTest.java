package org.drools.persistence.jpa;

import javax.persistence.EntityManagerFactory;

import junit.framework.TestCase;

import org.drools.runtime.process.WorkItem;

public class JPAPersisterTest extends TestCase {
	
	private EntityManagerFactory emf;
	private WorkItem workItem;
	
//	protected void setUp() {
//		emf = Persistence.createEntityManagerFactory("org.drools.persistence.jpa");
//	}
//	
//	protected void tearDown() {
//        emf.close();
//    }
	
	public void testDummy() {
	    
	}
	
//	public void testPersistence() {
//        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
//        Package pkg = new Package("org.drools.test");
//        pkg.addProcess(getProcess());
//        ruleBase.addPackage(pkg);
//
//        StatefulSession session = ruleBase.newStatefulSession();
//		JPAPersister<StatefulSession> persister = new JPAPersister<StatefulSession>(
//			emf, new JPASessionMashallingHelper(session));
//		
//		session.getWorkItemManager().registerWorkItemHandler("MyWork", new WorkItemHandler() {
//			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
//				JPAPersisterTest.this.workItem = workItem;
//			}
//			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
//			}
//        });
//		
//		
//        ProcessInstance processInstance = session.startProcess("org.drools.test.TestProcess");
//        assertNotNull(workItem);
//        persister.save();
//        
//        persister.load();
//        processInstance = session.getProcessInstance(processInstance.getId());
//        assertNotNull(processInstance);
//        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
//        
//        session.getWorkItemManager().completeWorkItem(workItem.getId(), null);
//        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
//    }
//
//    private RuleFlowProcess getProcess() {
//    	RuleFlowProcess process = new RuleFlowProcess();
//    	process.setId("org.drools.test.TestProcess");
//    	process.setName("TestProcess");
//    	process.setPackageName("org.drools.test");
//    	StartNode start = new StartNode();
//    	start.setId(1);
//    	start.setName("Start");
//    	process.addNode(start);
//    	ActionNode actionNode = new ActionNode();
//    	actionNode.setId(2);
//    	actionNode.setName("Action");
//    	DroolsConsequenceAction action = new DroolsConsequenceAction();
//    	action.setMetaData("Action", new Action() {
//            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
//            	System.out.println("Executed action");
//            }
//        });
//    	actionNode.setAction(action);
//    	process.addNode(actionNode);
//    	new ConnectionImpl(start, Node.CONNECTION_DEFAULT_TYPE, actionNode, Node.CONNECTION_DEFAULT_TYPE);
//    	WorkItemNode workItemNode = new WorkItemNode();
//    	workItemNode.setId(3);
//    	workItemNode.setName("WorkItem");
//    	Work work = new WorkImpl();
//    	work.setName("MyWork");
//    	workItemNode.setWork(work);
//    	process.addNode(workItemNode);
//    	new ConnectionImpl(actionNode, Node.CONNECTION_DEFAULT_TYPE, workItemNode, Node.CONNECTION_DEFAULT_TYPE);
//    	EndNode end = new EndNode();
//    	end.setId(4);
//    	end.setName("End");
//    	process.addNode(end);
//    	new ConnectionImpl(workItemNode, Node.CONNECTION_DEFAULT_TYPE, end, Node.CONNECTION_DEFAULT_TYPE);
//        return process;
//    }
    
}
