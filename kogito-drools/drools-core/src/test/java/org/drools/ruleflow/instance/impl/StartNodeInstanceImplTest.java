package org.drools.ruleflow.instance.impl;


import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.ruleflow.common.instance.ProcessInstance;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.impl.ConnectionImpl;
import org.drools.ruleflow.core.impl.NodeImpl;
import org.drools.ruleflow.core.impl.RuleFlowProcessImpl;
import org.drools.ruleflow.core.impl.StartNodeImpl;

import junit.framework.TestCase;

public class StartNodeInstanceImplTest extends TestCase {
    public void testStartNode() {
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        StatefulSession session = ruleBase.newStatefulSession();        
        
        NodeImpl mockNode = new NodeImpl()  { };        
        
        RuleFlowProcessImpl process = new RuleFlowProcessImpl(); 
        
        Node startNode = new  StartNodeImpl();  
        startNode.setId( 1 );
        startNode.setName( "start node" );        
                            
        mockNode.setId( 2 );
        new ConnectionImpl(startNode, mockNode, Connection.TYPE_NORMAL);
        
        process.addNode( startNode );
        process.addNode( mockNode );
                
        RuleFlowProcessInstanceImpl processInstance = new RuleFlowProcessInstanceImpl();   
        processInstance.setProcess( process );
        processInstance.setWorkingMemory( (InternalWorkingMemory) session );
        
        
        MockNodeInstanceFactory factory = new MockNodeInstanceFactory( new MockNodeInstance( mockNode ) );
        processInstance.registerNodeInstanceFactory( mockNode.getClass(), factory );                
        
        assertEquals(  ProcessInstance.STATE_PENDING, processInstance.getState() );
        processInstance.start();        
        assertEquals(  ProcessInstance.STATE_ACTIVE, processInstance.getState() );
        
        MockNodeInstance nodeInstance = factory.getMockNodeInstance();
        assertSame( startNode, ((RuleFlowNodeInstanceImpl) nodeInstance.getList().get( 0  )).getNode() );                       
    }
}
