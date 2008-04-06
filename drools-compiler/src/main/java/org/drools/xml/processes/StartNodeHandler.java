package org.drools.xml.processes;

import java.util.HashSet;

import org.drools.workflow.core.Node;
import org.drools.workflow.core.node.StartNode;

public class StartNodeHandler extends AbstractNodeHandler {
    
    protected Node createNode() {
        return new StartNode();
    }
    
    protected void initValidPeers() {
        this.validPeers = new HashSet();
        this.validPeers.add(null);
    }

    public Class generateNodeFor() {
        return StartNode.class;
    }

}
