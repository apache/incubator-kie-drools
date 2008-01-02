package org.drools.ruleflow.instance.impl;

import java.util.Properties;

import junit.framework.TestCase;

import org.drools.RuleBaseConfiguration;
import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.core.impl.ActionNodeImpl;
import org.drools.ruleflow.instance.impl.factories.CreateNewNodeFactory;

public class ProcessNodeInstanceFactoryTest extends TestCase {
    
    public void testDefaultEntries() throws Exception {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        
        ProcessNodeInstanceFactoryRegistry registry = conf.getProcessNodeInstanceFactoryRegistry();
        Node node = new ActionNodeImpl();
        assertEquals( CreateNewNodeFactory.class, registry.getRuleFlowNodeFactory( node ).getClass() );       
    }
    
    public void testDiscoveredEntry() {
        Properties properties = new Properties(); 
        properties.put( "processNodeInstanceFactoryRegistry", "mockProcessNodeInstanceFactory.conf" );        
        
        RuleBaseConfiguration conf = new RuleBaseConfiguration( properties );
        
        ProcessNodeInstanceFactoryRegistry registry = conf.getProcessNodeInstanceFactoryRegistry();
        
        assertEquals( MockNodeInstanceFactory.class, registry.getRuleFlowNodeFactory( new MockNode() ).getClass() );
    }
}
