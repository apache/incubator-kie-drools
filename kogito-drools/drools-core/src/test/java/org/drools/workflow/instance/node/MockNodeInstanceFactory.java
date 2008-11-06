/**
 * 
 */
package org.drools.workflow.instance.node;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.WorkflowProcessInstance;
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
        instance.setProcessInstance(processInstance);
        instance.setNodeInstanceContainer(nodeInstanceContainer);
        return instance;
    }
      
}