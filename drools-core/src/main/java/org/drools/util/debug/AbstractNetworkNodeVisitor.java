package org.drools.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;

public abstract class AbstractNetworkNodeVisitor
    implements
    NetworkNodeVisitor {
    
    protected AbstractNetworkNodeVisitor() {
    }

    public void visit(NetworkNode node,
                      Stack<NetworkNode> nodeStack,
                      StatefulKnowledgeSessionInfo info) {
        info.info( this.getClass().getSimpleName() + " - Visiting "+node );
        DefaultNodeInfo dni = new DefaultNodeInfo( node );
        info.addNodeInfo( node,
                          dni );
        doVisit( node,
                 nodeStack,
                 info );
    }

    protected abstract void doVisit(NetworkNode node,
                                    Stack<NetworkNode> nodeStack,
                                    StatefulKnowledgeSessionInfo info);

}
