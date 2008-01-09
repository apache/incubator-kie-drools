/**
 * 
 */
package org.drools.workflow.instance.node;

import org.drools.workflow.core.Node;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceFactory;

public class MockNodeInstanceFactory implements NodeInstanceFactory {
    
    private MockNodeInstance instance;
    
    public MockNodeInstanceFactory(MockNodeInstance instance) {
        this.instance = instance;
    }
    
    public MockNodeInstance getMockNodeInstance() {
        return this.instance;
    }

    public NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer) {
        return instance;
    }        
}