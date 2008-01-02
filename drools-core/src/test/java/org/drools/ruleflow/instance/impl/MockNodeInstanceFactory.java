/**
 * 
 */
package org.drools.ruleflow.instance.impl;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;

public class MockNodeInstanceFactory implements ProcessNodeInstanceFactory {
    private MockNodeInstance instance;
    
    public MockNodeInstanceFactory(MockNodeInstance instance) {
        this.instance = instance;
    }
    
    public MockNodeInstance getMockNodeInstance() {
        return this.instance;
    }

    public RuleFlowNodeInstance getNodeInstance(Node node,
                                                RuleFlowProcessInstanceImpl processInstance) {
        this.instance.setNodeId( node.getId() );
        processInstance.addNodeInstance( this.instance );            
        return instance;
    }        
}