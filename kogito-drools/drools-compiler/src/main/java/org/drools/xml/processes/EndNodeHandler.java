package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.EndNode;

public class EndNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new EndNode();
    }

    public Class generateNodeFor() {
        return EndNode.class;
    }

}
