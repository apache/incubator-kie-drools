package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.core.util.RightTupleList;
import org.drools.reteoo.LeftInputAdapterNode;

public class LeftInputAdapterNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final LeftInputAdapterNodeVisitor INSTANCE = new LeftInputAdapterNodeVisitor();
    
    protected LeftInputAdapterNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        LeftInputAdapterNode an = (LeftInputAdapterNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        ni.setMemoryEnabled( false );
    }

}
