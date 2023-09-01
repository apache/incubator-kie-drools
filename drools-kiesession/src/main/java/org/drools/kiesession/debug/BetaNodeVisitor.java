package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;

import java.util.Collection;

public class BetaNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final BetaNodeVisitor INSTANCE = new BetaNodeVisitor();
    
    protected BetaNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        BetaNode bn = (BetaNode) node;
        DefaultNodeInfo ni = info.getNodeInfo(node);
        final BetaMemory memory = (BetaMemory) info.getSession().getNodeMemory( bn );
        
        if( bn.isObjectMemoryEnabled() ) {
            ni.setFactMemorySize( memory.getRightTupleMemory().size() );
        }
        if( bn.isLeftTupleMemoryEnabled() ) {
            ni.setTupleMemorySize( memory.getLeftTupleMemory().size() );
        }

    }

}
