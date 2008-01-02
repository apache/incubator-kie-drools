/**
 * 
 */
package org.drools.ruleflow.instance.impl;

import org.drools.ruleflow.core.Node;
import org.drools.ruleflow.instance.RuleFlowNodeInstance;

public class MockEndNodeInstanceFactory implements ProcessNodeInstanceFactory {
    private MockEndNodeInstance instance;
    
    public MockEndNodeInstanceFactory(MockEndNodeInstance instance) {
        this.instance = instance;
    }
    
    public MockEndNodeInstance getMockEndNodeInstance() {
        return this.instance;
    }

    public RuleFlowNodeInstance getNodeInstance(Node node,
                                                RuleFlowProcessInstanceImpl processInstance) {
        this.instance.setNodeId( node.getId() );
        processInstance.addNodeInstance( this.instance );            
        return instance;
    }        
}