package org.drools.core.util.debug;

import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.core.util.ObjectHashMap;
import org.drools.reteoo.RightInputAdapterNode;

public class RightInputAdapterNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final RightInputAdapterNodeVisitor INSTANCE = new RightInputAdapterNodeVisitor();
    
    protected RightInputAdapterNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        RightInputAdapterNode an = (RightInputAdapterNode) node;
        DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
        final ObjectHashMap memory = (ObjectHashMap) info.getSession().getNodeMemory( an );
        
        ni.setMemoryEnabled( true );
        ni.setTupleMemorySize( memory.size() );
        ni.setCreatedFactHandles( memory.size() );
    }

}
