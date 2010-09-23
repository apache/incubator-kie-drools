package org.jbpm.compiler.xml.processes;

import org.jbpm.workflow.core.Node;
import org.jbpm.workflow.core.node.DynamicNode;

public class DynamicNodeHandler extends CompositeNodeHandler {

    protected Node createNode() {
        return new DynamicNode();
    }

    public Class<?> generateNodeFor() {
        return DynamicNode.class;
    }
    
    protected String getNodeName() {
    	return "dynamic";
    }

}
