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

package org.jbpm.workflow.instance.node;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.impl.ConnectionImpl;
import org.jbpm.workflow.core.node.StartNode;
import org.jbpm.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.NodeInstance;
import org.slf4j.LoggerFactory;

public class StartNodeInstanceTest extends AbstractBaseTest {
    
    public void addLogger() { 
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @Test
    public void testStartNode() {
        
        KieBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();        
        
        MockNode mockNode = new MockNode();
        MockNodeInstanceFactory mockNodeFactory = new MockNodeInstanceFactory( new MockNodeInstance( mockNode ) );
        NodeInstanceFactoryRegistry.getInstance(ksession.getEnvironment()).register( mockNode.getClass(), mockNodeFactory );
        
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
