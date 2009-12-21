package org.drools.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.reteoo.EvalConditionNode;
import org.drools.reteoo.EvalConditionNode.EvalMemory;

public class EvalConditionNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final EvalConditionNodeVisitor INSTANCE = new EvalConditionNodeVisitor();
    
    protected EvalConditionNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        EvalConditionNode ecn = (EvalConditionNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        final EvalMemory memory = (EvalMemory) info.getSession().getNodeMemory( ecn );
        
        ni.setMemoryEnabled( ecn.isLeftTupleMemoryEnabled() );
        
        if( ecn.isLeftTupleMemoryEnabled() ) {
            ni.setTupleMemorySize( memory.getLeftTupleMemory().size() );
        }

    }

}
