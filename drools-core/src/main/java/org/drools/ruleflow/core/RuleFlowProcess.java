package org.drools.ruleflow.core;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.NodeContainer;
import org.drools.workflow.core.impl.NodeContainerImpl;
import org.drools.workflow.core.impl.WorkflowProcessImpl;
import org.drools.workflow.core.node.StartNode;

public class RuleFlowProcess extends WorkflowProcessImpl {

    public static final String RULEFLOW_TYPE = "RuleFlow";

    private static final long serialVersionUID = 400L;
    
    public RuleFlowProcess() {
        setType(RULEFLOW_TYPE);
    }

    protected NodeContainer createNodeContainer() {
        return new WorkflowProcessNodeContainer();
    }
    
    public StartNode getStart() {
        Node[] nodes = getNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof StartNode) {
                return (StartNode) nodes[i];
            }
        }
        return null;
    }

    private class WorkflowProcessNodeContainer extends NodeContainerImpl {
        
        private static final long serialVersionUID = 400L;

        protected void validateAddNode(Node node) {
            super.validateAddNode(node);
            if ((node instanceof StartNode) && (getStart() != null)) {
                throw new IllegalArgumentException(
                    "A RuleFlowProcess cannot have more than one start node!");
            }
        }
        
    }

}
