package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.core.util.ObjectHashSet;
import org.drools.reteoo.ObjectTypeNode;

public class ObjectTypeNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final ObjectTypeNodeVisitor INSTANCE = new ObjectTypeNodeVisitor();
    
    protected ObjectTypeNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        ObjectTypeNode otn = (ObjectTypeNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        ni.setMemoryEnabled( otn.isObjectMemoryEnabled() );
        
        if( otn.isObjectMemoryEnabled() ) {
            final ObjectHashSet memory = (ObjectHashSet) info.getSession().getNodeMemory( otn );
            ni.setFactMemorySize( memory.size() );
        }

    }

}
