package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;

public class EvalConditionNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final EvalConditionNodeVisitor INSTANCE = new EvalConditionNodeVisitor();
    
    protected EvalConditionNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        ni.setMemoryEnabled( false );
    }

}
