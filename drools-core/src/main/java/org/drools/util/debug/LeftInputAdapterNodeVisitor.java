package org.drools.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.reteoo.LeftInputAdapterNode;
import org.drools.util.RightTupleList;

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
        final RightTupleList memory = (RightTupleList) info.getSession().getNodeMemory( an );
        
        ni.setMemoryEnabled( true );
        ni.setFactMemorySize( memory.size() );
    }

}
