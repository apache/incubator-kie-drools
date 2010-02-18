package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.reteoo.CollectNode;
import org.drools.reteoo.CollectNode.CollectMemory;

public class CollectNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final CollectNodeVisitor INSTANCE = new CollectNodeVisitor();
    
    protected CollectNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        CollectNode an = (CollectNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        final CollectMemory memory = (CollectMemory) info.getSession().getNodeMemory( an );
        
        ni.setMemoryEnabled( true );
        
        if( an.isObjectMemoryEnabled() ) {
            ni.setFactMemorySize( memory.betaMemory.getRightTupleMemory().size() );
        }
        if( an.isLeftTupleMemoryEnabled() ) {
            ni.setTupleMemorySize( memory.betaMemory.getLeftTupleMemory().size() );
            ni.setCreatedFactHandles( memory.betaMemory.getCreatedHandles().size() );
        }

    }

}
