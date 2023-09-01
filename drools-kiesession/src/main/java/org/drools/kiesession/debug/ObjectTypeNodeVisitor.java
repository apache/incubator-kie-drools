package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;
import org.drools.core.reteoo.ObjectTypeNode;

import java.util.Collection;

public class ObjectTypeNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final ObjectTypeNodeVisitor INSTANCE = new ObjectTypeNodeVisitor();
    
    protected ObjectTypeNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        ObjectTypeNode otn = (ObjectTypeNode) node;
        DefaultNodeInfo ni = info.getNodeInfo(node);
    }

}
