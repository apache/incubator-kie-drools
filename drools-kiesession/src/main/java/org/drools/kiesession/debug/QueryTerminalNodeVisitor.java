package org.drools.kiesession.debug;

import java.util.Collection;

import org.drools.base.common.NetworkNode;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.QueryTerminalNode;

public class QueryTerminalNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final QueryTerminalNodeVisitor INSTANCE = new QueryTerminalNodeVisitor();
    
    protected QueryTerminalNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        QueryTerminalNode rtn = (QueryTerminalNode) node;
        RuleImpl rule = rtn.getRule();
        // first thing, associate all nodes belonging to this rule
        for( NetworkNode snode : nodeStack ) {
            info.assign( snode, rule );
        }
    }

}
