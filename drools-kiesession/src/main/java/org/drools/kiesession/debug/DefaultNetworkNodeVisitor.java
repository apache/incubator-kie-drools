package org.drools.kiesession.debug;

import java.util.Collection;

import org.drools.base.common.NetworkNode;

public class DefaultNetworkNodeVisitor extends AbstractNetworkNodeVisitor {

    public static final DefaultNetworkNodeVisitor INSTANCE = new DefaultNetworkNodeVisitor();
    
    protected DefaultNetworkNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        // do nothing for now
    }
    
    
}
