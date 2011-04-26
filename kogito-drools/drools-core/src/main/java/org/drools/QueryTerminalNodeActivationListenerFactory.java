package org.drools;

import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.QueryTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;

public class QueryTerminalNodeActivationListenerFactory implements ActivationListenerFactory  {
    public static final QueryTerminalNodeActivationListenerFactory INSTANCE = new QueryTerminalNodeActivationListenerFactory();

    public TerminalNode createActivationListener(int id,
                                                 LeftTupleSource source,
                                                 Rule rule,
                                                 GroupElement subrule,
                                                 BuildContext context,
                                                 Object... args) {
        return new QueryTerminalNode( id, source, rule, subrule, context );
    }

}
