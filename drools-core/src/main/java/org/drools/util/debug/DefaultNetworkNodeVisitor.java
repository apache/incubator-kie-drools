package org.drools.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;

public class DefaultNetworkNodeVisitor extends AbstractNetworkNodeVisitor {

    public static final DefaultNetworkNodeVisitor INSTANCE = new DefaultNetworkNodeVisitor();
    
    protected DefaultNetworkNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        // do nothing for now
    }
    
    
}
