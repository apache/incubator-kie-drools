package org.drools.persistence.memory;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.persistence.Persister;
import org.drools.persistence.session.MemoryPersisterManager;
import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.process.instance.InternalProcessInstance;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemHandler;
import org.drools.process.instance.WorkItemManager;
import org.drools.rule.Package;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.WorkingMemory;
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

public class MemoryPersistenceManagerTest extends TestCase {
	
	private Map<Long, WorkItem> workItems = new HashMap<Long, WorkItem>();

	public void testProcessPersistence() {
		MemoryPersisterManager manager = new MemoryPersisterManager();
		
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		Package pkg = new Package("org.drools.test");
		pkg.addProcess(getProcess());
		ruleBase.addPackage(pkg);
		StatefulSession session = ruleBase.newStatefulSession();
		
		Persister<StatefulSession> persister = manager.getSessionPersister(session);
		session.getWorkItemManager().registerWorkItemHandler("MyWork", new WorkItemHandler() {
			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
				MemoryPersistenceManagerTest.this.workItems.put(workItem.getProcessInstanceId(), workItem);
			}
			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			}
        });
        InternalProcessInstance processInstance = ( InternalProcessInstance ) session.startProcess("org.drools.test.TestProcess");
        long processInstanceId = processInstance.getId();
		persister.setUniqueId(processInstanceId + "");
		persister.save();
		
		persister = manager.getSessionPersister(processInstanceId + "", ruleBase);
		session = persister.getObject();
		processInstance = ( InternalProcessInstance ) session.getProcessInstance(processInstanceId); 
        assertEquals(InternalProcessInstance.STATE_ACTIVE, processInstance.getState());
		
		session.getWorkItemManager().completeWorkItem(
			workItems.get(processInstanceId).getId(), null);
        assertEquals(InternalProcessInstance.STATE_COMPLETED, processInstance.getState());
        persister.save();
        
        persister = manager.getSessionPersister(processInstanceId + "", ruleBase);
		session = persister.getObject();
		processInstance = ( InternalProcessInstance ) session.getProcessInstance(processInstanceId); 
        assertNull(processInstance);
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
			public void execute(KnowledgeHelper knowledgeHelper,
					WorkingMemory workingMemory, ProcessContext context)
					throws Exception {
				System.out.println("Executed action");
			}
		});
		actionNode.setAction(action);
		process.addNode(actionNode);
		new ConnectionImpl(
			start, Node.CONNECTION_DEFAULT_TYPE,
			actionNode, Node.CONNECTION_DEFAULT_TYPE);
		WorkItemNode workItemNode = new WorkItemNode();
		workItemNode.setId(3);
		workItemNode.setName("WorkItem");
		Work work = new WorkImpl();
		work.setName("MyWork");
		workItemNode.setWork(work);
		process.addNode(workItemNode);
		new ConnectionImpl(
			actionNode, Node.CONNECTION_DEFAULT_TYPE,
			workItemNode, Node.CONNECTION_DEFAULT_TYPE);
		EndNode end = new EndNode();
		end.setId(4);
		end.setName("End");
		process.addNode(end);
		new ConnectionImpl(
			workItemNode, Node.CONNECTION_DEFAULT_TYPE, 
			end, Node.CONNECTION_DEFAULT_TYPE);
		return process;
	}

}
