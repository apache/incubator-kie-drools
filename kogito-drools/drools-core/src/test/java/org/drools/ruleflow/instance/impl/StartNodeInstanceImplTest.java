package org.drools.ruleflow.instance.impl;


import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.ruleflow.common.instance.ProcessInstance;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.impl.ConnectionImpl;
import org.drools.ruleflow.core.impl.EndNodeImpl;
import org.drools.ruleflow.core.impl.NodeImpl;
import org.drools.ruleflow.core.impl.RuleFlowProcessImpl;
import org.drools.ruleflow.core.impl.StartNodeImpl;

import junit.framework.TestCase;

public class StartNodeInstanceImplTest extends TestCase {
    public void testStartNode() {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf );
        StatefulSession session = ruleBase.newStatefulSession();        
        
        MockNode mockNode = new MockNode();
        MockNodeInstanceFactory mockNodeFactory = new MockNodeInstanceFactory( new MockNodeInstance( mockNode ) );
        conf.getProcessNodeInstanceFactoryRegistry().register(  mockNode.getClass(), mockNodeFactory );
        
        MockEndNode mockEndNode = new MockEndNode();
        MockEndNodeInstanceFactory mockEndNodeFactory = new MockEndNodeInstanceFactory( new MockEndNodeInstance( mockEndNode ) );        
        conf.getProcessNodeInstanceFactoryRegistry().register(  mockEndNode.getClass(), mockEndNodeFactory );      
        
        RuleFlowProcessImpl process = new RuleFlowProcessImpl(); 
        
        Node startNode = new  StartNodeImpl();  
        startNode.setId( 1 );
        startNode.setName( "start node" );                
                            
        mockNode.setId( 2 );
        mockEndNode.setId( 3 );
        new ConnectionImpl(startNode, mockNode, Connection.TYPE_NORMAL);
        new ConnectionImpl(mockNode, mockEndNode, Connection.TYPE_NORMAL);
        
        process.addNode( startNode );
        process.addNode( mockNode );
        process.addNode( mockEndNode );
                
        RuleFlowProcessInstanceImpl processInstance = new RuleFlowProcessInstanceImpl();   
        processInstance.setProcess( process );
        processInstance.setWorkingMemory( (InternalWorkingMemory) session );              
        
        assertEquals(  ProcessInstance.STATE_PENDING, processInstance.getState() );
        processInstance.start();        
        assertEquals(  ProcessInstance.STATE_ACTIVE, processInstance.getState() );
        
        MockNodeInstance mockNodeInstance = mockNodeFactory.getMockNodeInstance();
        assertSame( startNode, ((RuleFlowNodeInstanceImpl) mockNodeInstance.getList().get( 0  )).getNode() );
        
        MockEndNodeInstance mockEndNodeInstance = mockEndNodeFactory.getMockEndNodeInstance();
        assertSame( mockNode, ((RuleFlowNodeInstanceImpl) mockEndNodeInstance.getList().get( 0  )).getNode() );        
    }
}
