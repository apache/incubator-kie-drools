package org.drools;

import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.TerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;

public class RuleTerminalNodeActivationListenerFactory implements ActivationListenerFactory  {
    
    public static final RuleTerminalNodeActivationListenerFactory INSTANCE = new RuleTerminalNodeActivationListenerFactory();

    public TerminalNode createActivationListener(int id,
                                                 LeftTupleSource source,
                                                 Rule rule,
                                                 GroupElement subrule,
                                                 BuildContext context,
                                                 Object... args) {
        return new RuleTerminalNode( id, source, rule, subrule, context );
    }

}
