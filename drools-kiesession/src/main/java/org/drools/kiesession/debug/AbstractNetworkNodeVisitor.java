package org.drools.kiesession.debug;

import java.util.Collection;

import org.drools.base.common.NetworkNode;

public abstract class AbstractNetworkNodeVisitor
    implements
    NetworkNodeVisitor {
    
    protected AbstractNetworkNodeVisitor() {
    }

    public void visit(NetworkNode node,
                      Collection<NetworkNode> nodeList,
                      StatefulKnowledgeSessionInfo info) {
        info.info( this.getClass().getSimpleName() + " - Visiting "+node );
        DefaultNodeInfo dni = new DefaultNodeInfo( node );
        info.addNodeInfo( node,
                          dni );
        doVisit( node,
                 nodeList,
                 info );
    }

    protected abstract void doVisit(NetworkNode node,
                                    Collection<NetworkNode> nodeStack,
                                    StatefulKnowledgeSessionInfo info);

}
