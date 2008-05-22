package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EndNode;
import org.drools.workflow.core.node.StartNode;

public class EndNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new EndNode();
    }

    public Class generateNodeFor() {
        return EndNode.class;
    }

	public void writeNode(Node node, StringBuffer xmlDump, boolean includeMeta) {
		EndNode endNode = (EndNode) node;
		writeNode("end", endNode, xmlDump, includeMeta);
        endNode(xmlDump);
	}

}
