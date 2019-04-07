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

package org.jbpm.process;

import org.jbpm.process.core.Work;
import org.jbpm.process.core.impl.WorkImpl;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.test.TestProcessEventListener;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.jbpm.workflow.core.node.EndNode;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.core.node.SubProcessNode;
import org.jbpm.workflow.core.node.WorkItemNode;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class SubProcessTest extends AbstractBaseTest  {
    
    public void addLogger() { 
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
	private boolean executed = false;
	private WorkItem workItem;
	
	@Before
	public void setUp() {
		executed = false;
		workItem = null;
	}
  
	String [] syncEventorder = { 
	        "bps",
	        "bnt-0", "bnl-0",
	        "bnt-1",
	        "bps",
	        "bnt-0", "bnl-0",
	        "bnt-1", "bnl-1",
	        "bnt-2", "bnl-2",
	        "bpc", "apc",
	        "anl-2", "ant-2",
	        "anl-1", "ant-1",
	        "anl-0", "ant-0",
	        "aps",
	        "bnl-1",
	        "bnt-2", "bnl-2",
	        "bpc",
	        "apc",
	        "anl-2", "ant-2",
	        "anl-1", "ant-1",
	        "anl-0", "ant-0",
	        "aps"
	};
	
	@Test
    public void testSynchronousSubProcess() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.process");
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
        subProcessNode.setProcessId("org.drools.core.process.subprocess");
        process.addNode(subProcessNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            subProcessNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            subProcessNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        RuleFlowProcess subprocess = new RuleFlowProcess();
        subprocess.setId("org.drools.core.process.subprocess");
        subprocess.setName("SubProcess");
        
        startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        subprocess.addNode(startNode);
        endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        subprocess.addNode(endNode);
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Action");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
            public void execute(ProcessContext context) throws Exception {
                logger.info("Executed action");
            	executed = true;
            }
        });
        actionNode.setAction(action);
        actionNode.setId(3);
        subprocess.addNode(actionNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            actionNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
    		actionNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        KieSession ksession = createKieSession(process, subprocess); 
        TestProcessEventListener procEventListener = new TestProcessEventListener();
        ksession.addEventListener(procEventListener); 
        
        ksession.startProcess("org.drools.core.process.process");
        assertTrue(executed);
        assertEquals(0, ksession.getProcessInstances().size());
        
        verifyEventHistory(syncEventorder, procEventListener.getEventHistory());
    }

	String [] asyncEventOrder = { 
	        "bnl-1",
	        "bnt-2", "bnl-2",
	        "bpc", "apc",
	        "bnl-1",
	        "bnt-2", "bnl-2",
	        "bpc", "apc",
	        "anl-2", "ant-2",
	        "anl-1",
	        "anl-2", "ant-2",
	        "anl-1",
	};
	
	@Test
    public void testAsynchronousSubProcess() {
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.process");
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
        subProcessNode.setProcessId("org.drools.core.process.subprocess");
        process.addNode(subProcessNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            subProcessNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
            subProcessNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        RuleFlowProcess subProcess = new RuleFlowProcess();
        subProcess.setId("org.drools.core.process.subprocess");
        subProcess.setName("SubProcess");
        
        startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        subProcess.addNode(startNode);
        endNode = new EndNode();
        endNode.setName("EndNode");
        endNode.setId(2);
        subProcess.addNode(endNode);
        WorkItemNode workItemNode = new WorkItemNode();
        workItemNode.setName("WorkItem");
        Work work = new WorkImpl();
        work.setName("MyWork");
        workItemNode.setWork(work);
        workItemNode.setId(4);
        subProcess.addNode(workItemNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            workItemNode, Node.CONNECTION_DEFAULT_TYPE
        );
        new ConnectionImpl(
    		workItemNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        
        KieSession ksession = createKieSession(process, subProcess);
        
        ksession.getWorkItemManager().registerWorkItemHandler("MyWork", new WorkItemHandler() {
			public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
			    logger.info("Executing work item");
				SubProcessTest.this.workItem = workItem;
			}
			public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
			}
        });
        ksession.startProcess("org.drools.core.process.process");
        TestProcessEventListener procEventListener = new TestProcessEventListener();
        ksession.addEventListener(procEventListener); 
        
        assertNotNull(workItem);
        assertEquals(2, ksession.getProcessInstances().size());
        
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertEquals(0, ksession.getProcessInstances().size());
        
        verifyEventHistory(asyncEventOrder, procEventListener.getEventHistory());
    }
    
	@Test
    public void testNonExistentSubProcess() {
	    String nonExistentSubProcessName = "nonexistent.process";
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.core.process.process");
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

        KieSession ksession = createKieSession(process);
        
        try{
            ksession.startProcess("org.drools.core.process.process");
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
