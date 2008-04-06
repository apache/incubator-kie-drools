package org.drools.workflow.instance.impl.factory;

import org.drools.workflow.core.Node;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceFactory;
import org.drools.workflow.instance.impl.NodeInstanceImpl;

public class ReuseNodeFactory implements NodeInstanceFactory {
    
    public final Class<? extends NodeInstance> cls;
    
    public ReuseNodeFactory(Class<? extends NodeInstance> cls){
        this.cls = cls;
    }

	public NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer) {    	
        NodeInstance result = nodeInstanceContainer.getFirstNodeInstance( node.getId() );
        if (result != null) {
            return result;
        }
        try {
            NodeInstanceImpl nodeInstance = (NodeInstanceImpl) cls.newInstance();
            nodeInstance.setNodeId(node.getId());
            nodeInstance.setNodeInstanceContainer(nodeInstanceContainer);
            nodeInstance.setProcessInstance(processInstance);
            return nodeInstance;
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate node '"
                + this.cls.getName() + "': " + e.getMessage());
        }
	}

}
