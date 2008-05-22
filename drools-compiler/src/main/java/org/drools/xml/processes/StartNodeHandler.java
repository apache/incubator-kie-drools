package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.StartNode;

public class StartNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode() {
        return new StartNode();
    }
    
    public Class generateNodeFor() {
        return StartNode.class;
    }

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		StartNode startNode = (StartNode) node;
		writeNode("start", startNode, xmlDump, includeMeta);
        endNode(xmlDump);
	}

}
