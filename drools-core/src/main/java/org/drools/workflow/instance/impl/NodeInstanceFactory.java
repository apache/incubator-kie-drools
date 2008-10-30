package org.drools.workflow.instance.impl;

import org.drools.knowledge.definitions.process.Node;
import org.drools.process.instance.NodeInstance;
import org.drools.process.instance.NodeInstanceContainer;
import org.drools.process.instance.WorkflowProcessInstance;

public interface NodeInstanceFactory {
    
	NodeInstance getNodeInstance(Node node, WorkflowProcessInstance processInstance, NodeInstanceContainer nodeInstanceContainer);
	
}
