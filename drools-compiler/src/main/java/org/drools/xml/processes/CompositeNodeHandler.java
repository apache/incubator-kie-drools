package org.drools.xml.processes;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.CompositeNode;

public class CompositeNodeHandler extends AbstractNodeHandler {

    protected Node createNode() {
        return new CompositeNode();
    }

    public Class generateNodeFor() {
        return CompositeNode.class;
    }

}
