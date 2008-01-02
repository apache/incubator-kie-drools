package org.drools.ruleflow.instance.impl;


import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.common.InternalWorkingMemory;
import org.drools.ruleflow.common.core.impl.ProcessImpl;
import org.drools.ruleflow.common.instance.ProcessInstance;
import org.drools.ruleflow.core.Connection;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.impl.ConnectionImpl;
import org.drools.ruleflow.core.impl.EndNodeImpl;
import org.drools.ruleflow.core.impl.NodeImpl;
import org.drools.ruleflow.core.impl.RuleFlowProcessImpl;
import org.drools.ruleflow.core.impl.StartNodeImpl;

import junit.framework.TestCase;

public class EndNodeInstanceImplTest extends TestCase {
    public void testStartNode() {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        
        RuleBase ruleBase = RuleBaseFactory.newRuleBase( conf );
        StatefulSession session = ruleBase.newStatefulSession();        
        
        NodeImpl mockNode = new NodeImpl()  { };        
        MockNodeInstanceFactory factory = new MockNodeInstanceFactory( new MockNodeInstance( mockNode ) );
        conf.getProcessNodeInstanceFactoryRegistry().register(  mockNode.getClass(), factory );
        
        RuleFlowProcessImpl process = new RuleFlowProcessImpl(); 
        
        Node endNode = new  EndNodeImpl();  
        endNode.setId( 1 );
        endNode.setName( "end node" );        
                            
        mockNode.setId( 2 );
        new ConnectionImpl(mockNode, endNode, Connection.TYPE_NORMAL);
        
        process.addNode( mockNode );
        process.addNode( endNode );
                
        RuleFlowProcessInstanceImpl processInstance = new RuleFlowProcessInstanceImpl();   
        processInstance.setState( ProcessInstance.STATE_ACTIVE );
        processInstance.setProcess( process );
        processInstance.setWorkingMemory( (InternalWorkingMemory) session );
        
        

        
        MockNodeInstance mockNodeInstance = ( MockNodeInstance ) processInstance.getNodeInstance( mockNode );
        
        mockNodeInstance.trigger( null );
        assertEquals( ProcessInstance.STATE_COMPLETED, processInstance.getState() );                               
    }
}
