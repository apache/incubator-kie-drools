package org.drools.kiesession.debug;

import org.drools.base.common.NetworkNode;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNode;

import java.util.Collection;

public class RuleTerminalNodeVisitor extends AbstractNetworkNodeVisitor {
    
    public static final RuleTerminalNodeVisitor INSTANCE = new RuleTerminalNodeVisitor();
    
    protected RuleTerminalNodeVisitor() {
    }

    @Override
    protected void doVisit(NetworkNode node,
                           Collection<NetworkNode> nodeStack,
                           StatefulKnowledgeSessionInfo info) {
        RuleTerminalNode rtn = (RuleTerminalNode) node;
        RuleImpl rule = rtn.getRule();
        // first thing, associate all nodes belonging to this rule
        for( NetworkNode snode : nodeStack ) {
            info.assign( snode, rule );
        }
    }

}
