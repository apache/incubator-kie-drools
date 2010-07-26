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

package org.drools.process;

import junit.framework.TestCase;

import org.drools.RuleBaseFactory;
import org.drools.common.AbstractRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.process.core.Work;
import org.drools.process.core.impl.WorkImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.WorkingMemory;
import org.drools.spi.Action;
import org.drools.spi.KnowledgeHelper;
import org.drools.spi.ProcessContext;
import org.drools.workflow.core.DroolsAction;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.impl.DroolsConsequenceAction;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.SubProcessNode;
import org.drools.workflow.core.node.WorkItemNode;

public class SubProcessTest extends TestCase {

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
        
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.addProcess(process);
        
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
            public void execute(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory, ProcessContext context) throws Exception {
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
        
        ruleBase.addProcess(process);
        
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        workingMemory.startProcess("org.drools.process.process");
        assertTrue(executed);
        assertEquals(0, workingMemory.getProcessInstances().size());
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
        
        AbstractRuleBase ruleBase = (AbstractRuleBase) RuleBaseFactory.newRuleBase();
        ruleBase.addProcess(process);
        
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
        
        ruleBase.addProcess(process);
        
        InternalWorkingMemory workingMemory = new ReteooWorkingMemory(1, ruleBase);
        workingMemory.getWorkItemManager().registerWorkItemHandler("MyWork", new WorkItemHandler() {
			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
				System.out.println("Executing work item");
				SubProcessTest.this.workItem = workItem;
			}
			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			}
        });
        workingMemory.startProcess("org.drools.process.process");
        assertNotNull(workItem);
        assertEquals(2, workingMemory.getProcessInstances().size());
        
        workingMemory.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(0, workingMemory.getProcessInstances().size());
    }
    
}
