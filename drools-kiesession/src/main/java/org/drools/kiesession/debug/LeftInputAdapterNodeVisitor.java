package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;
import org.drools.core.reteoo.LeftInputAdapterNode;

import java.util.Collection;

public class LeftInputAdapterNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final LeftInputAdapterNodeVisitor INSTANCE = new LeftInputAdapterNodeVisitor();
    
    protected LeftInputAdapterNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        LeftInputAdapterNode an = (LeftInputAdapterNode) node;
        DefaultNodeInfo ni = info.getNodeInfo(node);
    }

}
