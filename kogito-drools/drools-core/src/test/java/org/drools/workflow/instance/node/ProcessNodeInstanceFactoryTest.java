package org.drools.workflow.instance.node;

import java.util.Properties;

import junit.framework.TestCase;

import org.drools.RuleBaseConfiguration;
import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.ActionNode;
import org.drools.workflow.instance.impl.NodeInstanceFactoryRegistry;
import org.drools.workflow.instance.impl.factory.CreateNewNodeFactory;

public class ProcessNodeInstanceFactoryTest extends TestCase {
    
    public void testDefaultEntries() throws Exception {
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
        NodeInstanceFactoryRegistry registry = conf.getProcessNodeInstanceFactoryRegistry();
        Node node = new ActionNode();
        assertEquals( CreateNewNodeFactory.class, registry.getProcessNodeInstanceFactory( node ).getClass() );       
    }
    
    public void testDiscoveredEntry() {
        Properties properties = new Properties(); 
        properties.put( "processNodeInstanceFactoryRegistry", "mockProcessNodeInstanceFactory.conf" );        
        RuleBaseConfiguration conf = new RuleBaseConfiguration( properties );
        NodeInstanceFactoryRegistry registry = conf.getProcessNodeInstanceFactoryRegistry();
        assertEquals( MockNodeInstanceFactory.class, registry.getProcessNodeInstanceFactory( new MockNode() ).getClass() );
    }
}
