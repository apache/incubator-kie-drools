package org.drools.workflow.instance.impl;

import org.drools.definition.process.Node;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.workflow.instance.WorkflowProcessInstance;

public interface NodeInstanceFactory {
    
	NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer);
	
}
