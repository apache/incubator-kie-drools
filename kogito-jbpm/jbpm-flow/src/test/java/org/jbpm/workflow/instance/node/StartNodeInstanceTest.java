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

package org.jbpm.workflow.instance.node;


import java.util.List;

import junit.framework.TestCase;

import org.drools.common.InternalKnowledgeRuntime;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.NodeInstance;

public class StartNodeInstanceTest extends TestCase {
    
    public void testStartNode() {
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();        
        
        MockNode mockNode = new MockNode();
        MockNodeInstanceFactory mockNodeFactory = new MockNodeInstanceFactory( new MockNodeInstance( mockNode ) );
        NodeInstanceFactoryRegistry.INSTANCE.register( mockNode.getClass(), mockNodeFactory );
        
        RuleFlowProcess process = new RuleFlowProcess(); 
        
        StartNode startNode = new StartNode();  
        startNode.setId( 1 );
        startNode.setName( "start node" );                
                            
        mockNode.setId( 2 );
        new ConnectionImpl(
    		startNode, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
    		mockNode, org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        
        process.addNode( startNode );
        process.addNode( mockNode );
                
        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();   
        processInstance.setProcess( process );
        processInstance.setKnowledgeRuntime( (InternalKnowledgeRuntime) ksession );              
        
        assertEquals(  ProcessInstance.STATE_PENDING, processInstance.getState() );
        processInstance.start();        
        assertEquals(  ProcessInstance.STATE_ACTIVE, processInstance.getState() );
        
        MockNodeInstance mockNodeInstance = mockNodeFactory.getMockNodeInstance();
        List<NodeInstance> triggeredBy =
        	mockNodeInstance.getTriggers().get(org.jbpm.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        assertNotNull(triggeredBy);
        assertEquals(1, triggeredBy.size());
        assertSame(startNode.getId(), triggeredBy.get(0).getNodeId());
    }
}
