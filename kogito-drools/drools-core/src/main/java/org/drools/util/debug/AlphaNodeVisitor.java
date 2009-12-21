package org.drools.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;

public class AlphaNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final AlphaNodeVisitor INSTANCE = new AlphaNodeVisitor();
    
    protected AlphaNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        ni.setMemoryEnabled( false );
    }

}
