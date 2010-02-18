package org.drools.compiler.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.DynamicNode;

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
