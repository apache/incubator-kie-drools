package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;

import java.util.Collection;

public class EvalConditionNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final EvalConditionNodeVisitor INSTANCE = new EvalConditionNodeVisitor();
    
    protected EvalConditionNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        DefaultNodeInfo ni = info.getNodeInfo(node);
    }

}
