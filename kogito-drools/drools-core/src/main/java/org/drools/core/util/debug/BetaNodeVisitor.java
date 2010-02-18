package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;

public class BetaNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final BetaNodeVisitor INSTANCE = new BetaNodeVisitor();
    
    protected BetaNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        BetaNode bn = (BetaNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        final BetaMemory memory = (BetaMemory) info.getSession().getNodeMemory( bn );
        
        ni.setMemoryEnabled( true );
        
        if( bn.isObjectMemoryEnabled() ) {
            ni.setFactMemorySize( memory.getRightTupleMemory().size() );
        }
        if( bn.isLeftTupleMemoryEnabled() ) {
            ni.setTupleMemorySize( memory.getLeftTupleMemory().size() );
        }

    }

}
