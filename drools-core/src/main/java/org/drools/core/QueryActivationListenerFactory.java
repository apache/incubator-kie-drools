package org.drools.core;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.GroupElement;

public class QueryActivationListenerFactory implements ActivationListenerFactory  {
    public static final QueryActivationListenerFactory INSTANCE = new QueryActivationListenerFactory();

    public TerminalNode createActivationListener(int id,
                                                 LeftTupleSource source,
                                                 RuleImpl rule,
                                                 GroupElement subrule,
                                                 int subruleIndex,
                                                 BuildContext context,
                                                 Object... args) {
        return CoreComponentFactory.get().getNodeFactoryService().buildQueryTerminalNode( id, source, rule, subrule, subruleIndex, context );
    }

}
