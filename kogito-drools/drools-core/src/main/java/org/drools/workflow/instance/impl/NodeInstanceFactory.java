package org.drools.workflow.instance.impl;

import org.drools.workflow.core.Node;
import org.drools.workflow.instance.NodeInstance;
import org.drools.workflow.instance.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;

public interface NodeInstanceFactory {
    
	NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer);
	
}
