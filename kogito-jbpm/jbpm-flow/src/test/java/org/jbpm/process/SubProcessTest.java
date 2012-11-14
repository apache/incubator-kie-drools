/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.drools.common.AbstractRuleBase;
import org.drools.impl.InternalKnowledgeBase;
import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessContext;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.JbpmTestCase;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.WorkItemNode;

public class SubProcessTest extends JbpmTestCase {

	private boolean executed = false;
	private WorkItem workItem;
	
	public void setUp() {
		executed = false;
		workItem = null;
	}
    
    public void testSynchronousSubProcess() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.process");
        process.setName("Process");
        
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        SubProcessNode subProcessNode = new SubProcessNode();
        subProcessNode.setName("SubProcessNode");
        subProcessNode.setId(3);
        subProcessNode.setProcessId("org.drools.process.subprocess");
        process.addNode(subProcessNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            subProcessNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            subProcessNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess(process);
        
        process = new RuleFlowProcess();
        process.setId("org.drools.process.subprocess");
        process.setName("SubProcess");
        
        startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Action");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
            	System.out.println("Executed action");
            	executed = true;
            }
        });
        actionNode.setAction(action);
        process.addNode(actionNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            actionNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
    		actionNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess(process);
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.startProcess("org.drools.process.process");
        assertTrue(executed);
        assertEquals(0, ksession.getProcessInstances().size());
    }

    public void testAsynchronousSubProcess() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.process");
        process.setName("Process");
        
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        EndNode endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        SubProcessNode subProcessNode = new SubProcessNode();
        subProcessNode.setName("SubProcessNode");
        subProcessNode.setId(3);
        subProcessNode.setProcessId("org.drools.process.subprocess");
        process.addNode(subProcessNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            subProcessNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            subProcessNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess(process);
        
        process = new RuleFlowProcess();
        process.setId("org.drools.process.subprocess");
        process.setName("SubProcess");
        
        startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        process.addNode(endNode);
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName("WorkItem");
        Work work = new WorkImpl();
        work.setName("MyWork");
        workItemNode.setWork(work);
        process.addNode(workItemNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            workItemNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
    		workItemNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess(process);
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.getWorkItemManager().registerWorkItemHandler("MyWork", new WorkItemHandler() {
			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
				System.out.println("Executing work item");
				SubProcessTest.this.workItem = workItem;
			}
			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			}
        });
        ksession.startProcess("org.drools.process.process");
        assertNotNull(workItem);
        assertEquals(2, ksession.getProcessInstances().size());
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(0, ksession.getProcessInstances().size());
    }
    
    public void testNonExistentSubProcess() {
	    String nonExistentSubProcessName = "nonexistent.process";
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.process");
        process.setName("Process");
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        SubProcessNode subProcessNode = new SubProcessNode();
        subProcessNode.setName("SubProcessNode");
        subProcessNode.setId(2);
        subProcessNode.setProcessId(nonExistentSubProcessName);
        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);
        
        connect(startNode, subProcessNode);
        connect(subProcessNode, endNode);
        
        process.addNode( startNode );
        process.addNode( subProcessNode );
        process.addNode( endNode );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        ((AbstractRuleBase) ((InternalKnowledgeBase) kbase).getRuleBase()).addProcess(process);
        
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        try{
            ksession.startProcess("org.drools.process.process");
            fail("should throw exception");
        } catch (RuntimeException re){
            assertTrue(re.getMessage().contains( nonExistentSubProcessName ));
        }
    }
    
	private void connect(Node sourceNode, Node targetNode) {
		new ConnectionImpl(sourceNode, Node.CONNECTION_DEFAULT_TYPE,
				           targetNode, Node.CONNECTION_DEFAULT_TYPE);
	}

}
