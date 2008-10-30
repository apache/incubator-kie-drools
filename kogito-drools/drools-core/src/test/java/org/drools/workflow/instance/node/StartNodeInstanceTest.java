package org.drools.workflow.instance.node;


import java.util.List;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.knowledge.definitions.process.Node;
import org.drools.process.instance.InternalProcessInstance;
import org.drools.process.instance.NodeInstance;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.workflow.core.impl.ConnectionImpl;
import org.drools.workflow.core.node.StartNode;

public class StartNodeInstanceTest extends TestCase {
    
    public void testStartNode() {
        
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf );
        StatefulSession session = ruleBase.newStatefulSession();        
        
        MockNode mockNode = new MockNode();
        MockNodeInstanceFactory mockNodeFactory = new MockNodeInstanceFactory( new MockNodeInstance( mockNode ) );
        conf.getProcessNodeInstanceFactoryRegistry().register( mockNode.getClass(), mockNodeFactory );
        
        RuleFlowProcess process = new RuleFlowProcess(); 
        
        StartNode startNode = new StartNode();  
        startNode.setId( 1 );
        startNode.setName( "start node" );                
                            
        mockNode.setId( 2 );
        new ConnectionImpl(
    		startNode, org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE,
    		mockNode, org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        
        process.addNode( startNode );
        process.addNode( mockNode );
                
        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();   
        processInstance.setProcess( process );
        processInstance.setWorkingMemory( (InternalWorkingMemory) session );              
        
        assertEquals(  InternalProcessInstance.STATE_PENDING, processInstance.getState() );
        processInstance.start();        
        assertEquals(  InternalProcessInstance.STATE_ACTIVE, processInstance.getState() );
        
        MockNodeInstance mockNodeInstance = mockNodeFactory.getMockNodeInstance();
        List<NodeInstance> triggeredBy =
        	mockNodeInstance.getTriggers().get(org.drools.workflow.core.Node.CONNECTION_DEFAULT_TYPE);
        assertNotNull(triggeredBy);
        assertEquals(1, triggeredBy.size());
        assertSame(startNode.getId(), triggeredBy.get(0).getNodeId());
    }
}
