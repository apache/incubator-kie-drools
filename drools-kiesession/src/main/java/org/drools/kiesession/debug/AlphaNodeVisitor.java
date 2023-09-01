package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;

import java.util.Collection;

public class AlphaNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final AlphaNodeVisitor INSTANCE = new AlphaNodeVisitor();
    
    protected AlphaNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        DefaultNodeInfo ni = info.getNodeInfo(node);
    }

}
