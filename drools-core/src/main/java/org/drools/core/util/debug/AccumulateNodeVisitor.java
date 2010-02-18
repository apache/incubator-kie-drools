package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.reteoo.AccumulateNode;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;

public class AccumulateNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final AccumulateNodeVisitor INSTANCE = new AccumulateNodeVisitor();
    
    protected AccumulateNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        AccumulateNode an = (AccumulateNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        final AccumulateMemory memory = (AccumulateMemory) info.getSession().getNodeMemory( an );
        
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
