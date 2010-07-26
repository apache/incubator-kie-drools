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

package org.drools.event.process;

/*
 * Copyright 2005 JBoss Inc
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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.definition.KnowledgePackage;
import org.drools.definitions.impl.KnowledgePackageImp;
import org.drools.rule.Package;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.runtime.StatefulKnowledgeSession;
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

public class ProcessEventSupportTest extends TestCase {

    public void testProcessEventListener() throws Exception {
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        // create a simple package with one process to test the events
        final Package pkg = new Package( "org.drools.test" );
        RuleFlowProcess process = new RuleFlowProcess();
        process.setId("org.drools.process.event");
        process.setName("Event Process");
        
        StartNode startNode = new StartNode();
        startNode.setName("Start");
        startNode.setId(1);
        process.addNode(startNode);
        
        ActionNode actionNode = new ActionNode();
        actionNode.setName("Print");
        DroolsAction action = new DroolsConsequenceAction("java", null);
        action.setMetaData("Action", new Action() {
			public void execute(KnowledgeHelper knowledgeHelper,
					org.drools.WorkingMemory workingMemory,
					ProcessContext context) throws Exception {
            	System.out.println("Executed action");
			}
        });
        actionNode.setAction(action);
        actionNode.setId(2);
        process.addNode(actionNode);
        new ConnectionImpl(
            startNode, Node.CONNECTION_DEFAULT_TYPE,
            actionNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        EndNode endNode = new EndNode();
        endNode.setName("End");
        endNode.setId(3);
        process.addNode(endNode);
        new ConnectionImpl(
            actionNode, Node.CONNECTION_DEFAULT_TYPE,
            endNode, Node.CONNECTION_DEFAULT_TYPE
        );
        
        pkg.addProcess(process);
        List<KnowledgePackage> pkgs = new ArrayList<KnowledgePackage>();
        pkgs.add( new KnowledgePackageImp( pkg ) );
        kbase.addKnowledgePackages( pkgs );
        
        StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();
        final List<ProcessEvent> processEventList = new ArrayList<ProcessEvent>();
        final ProcessEventListener processEventListener = new ProcessEventListener() {

        	public void afterNodeLeft(ProcessNodeLeftEvent event) {
				processEventList.add(event);
			}

			public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
				processEventList.add(event);
			}

			public void afterProcessCompleted(ProcessCompletedEvent event) {
				processEventList.add(event);
			}

			public void afterProcessStarted(ProcessStartedEvent event) {
				processEventList.add(event);
			}

			public void beforeNodeLeft(ProcessNodeLeftEvent event) {
				processEventList.add(event);
			}

			public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
				processEventList.add(event);
			}

			public void beforeProcessCompleted(ProcessCompletedEvent event) {
				processEventList.add(event);
			}

			public void beforeProcessStarted(ProcessStartedEvent event) {
				processEventList.add(event);
			}

        };
        session.addEventListener( processEventListener );

        // execute the process
        session.startProcess("org.drools.process.event");
        assertEquals( 16, processEventList.size() );
        assertEquals( "org.drools.process.event", ((ProcessStartedEvent) processEventList.get(0)).getProcessInstance().getProcessId());
        assertEquals( "Start", ((ProcessNodeTriggeredEvent) processEventList.get(1)).getNodeInstance().getNodeName());
        assertEquals( "Start", ((ProcessNodeLeftEvent) processEventList.get(2)).getNodeInstance().getNodeName());
        assertEquals( "Print", ((ProcessNodeTriggeredEvent) processEventList.get(3)).getNodeInstance().getNodeName());
        assertEquals( "Print", ((ProcessNodeLeftEvent) processEventList.get(4)).getNodeInstance().getNodeName());
        assertEquals( "End", ((ProcessNodeTriggeredEvent) processEventList.get(5)).getNodeInstance().getNodeName());
        assertEquals( "End", ((ProcessNodeLeftEvent) processEventList.get(6)).getNodeInstance().getNodeName());
        assertEquals( "org.drools.process.event", ((ProcessCompletedEvent) processEventList.get(7)).getProcessInstance().getProcessId());
        assertEquals( "org.drools.process.event", ((ProcessCompletedEvent) processEventList.get(8)).getProcessInstance().getProcessId());
        assertEquals( "End", ((ProcessNodeLeftEvent) processEventList.get(9)).getNodeInstance().getNodeName());
        assertEquals( "End", ((ProcessNodeTriggeredEvent) processEventList.get(10)).getNodeInstance().getNodeName());
        assertEquals( "Print", ((ProcessNodeLeftEvent) processEventList.get(11)).getNodeInstance().getNodeName());
        assertEquals( "Print", ((ProcessNodeTriggeredEvent) processEventList.get(12)).getNodeInstance().getNodeName());
        assertEquals( "Start", ((ProcessNodeLeftEvent) processEventList.get(13)).getNodeInstance().getNodeName());
        assertEquals( "Start", ((ProcessNodeTriggeredEvent) processEventList.get(14)).getNodeInstance().getNodeName());
        assertEquals( "org.drools.process.event", ((ProcessStartedEvent) processEventList.get(15)).getProcessInstance().getProcessId());
    }

}