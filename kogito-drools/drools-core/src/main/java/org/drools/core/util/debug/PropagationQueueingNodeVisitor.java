package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.reteoo.PropagationQueuingNode;
import org.drools.reteoo.PropagationQueuingNode.PropagationQueueingNodeMemory;

public class PropagationQueueingNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final PropagationQueueingNodeVisitor INSTANCE = new PropagationQueueingNodeVisitor();
    
    protected PropagationQueueingNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        PropagationQueuingNode pqn = (PropagationQueuingNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        PropagationQueueingNodeMemory memory = (PropagationQueueingNodeMemory) info.getSession().getNodeMemory( pqn ); 
        ni.setMemoryEnabled( true );
        ni.setActionQueueSize( memory.getSize() );
    }

}
