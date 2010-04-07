package org.drools.core.util.debug;

import java.util.LinkedList;
import java.util.Stack;

import org.drools.common.NetworkNode;
import org.drools.reteoo.QueryTerminalNode;
import org.drools.rule.Rule;

public class QueryTerminalNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final QueryTerminalNodeVisitor INSTANCE = new QueryTerminalNodeVisitor();
    
    protected QueryTerminalNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Stack<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        QueryTerminalNode rtn = (QueryTerminalNode) node;
        Rule rule = rtn.getRule();
        // first thing, associate all nodes belonging to this rule
        for( NetworkNode snode : nodeStack ) {
            info.assign( snode, rule );
        }

        // Query Terminal Nodes no longer have memory
        // TODO delete this
//        final LinkedList<?> memory = (LinkedList<?>) info.getSession().getNodeMemory( rtn );
//        final DefaultNodeInfo ni = (DefaultNodeInfo) info.getNodeInfo( node );
//        ni.setMemoryEnabled( true );
//        ni.setTupleMemorySize( memory.size() );
    }

}
