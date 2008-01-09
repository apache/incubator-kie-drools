package org.drools.workflow.instance.impl.factory;

import org.drools.workflow.core.Node;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;
import org.drools.workflow.instance.impl.NodeInstanceFactory;

public class CreateNewNodeFactory implements NodeInstanceFactory {
    
    public final Class<? extends NodeInstance> cls;
    
    public CreateNewNodeFactory(Class<? extends NodeInstance> cls){
        this.cls = cls;
    }
    
    public NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer ) {     
        try {
            return this.cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Unable to instantiate node: '"
                + this.cls.getName() + "':" + e.getMessage());
        }
	}

}
