package org.drools;

import org.drools.reteoo.LeftTupleSource;
import org.drools.reteoo.ReteooComponentFactory;
import org.drools.reteoo.TerminalNode;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;

public class RuleActivationListenerFactory implements ActivationListenerFactory  {
    
    public static final RuleActivationListenerFactory INSTANCE = new RuleActivationListenerFactory();

    public TerminalNode createActivationListener(int id,
                                                 LeftTupleSource source,
                                                 Rule rule,
                                                 GroupElement subrule,
                                                 int subruleIndex,                                                 
                                                 BuildContext context,
                                                 Object... args) {
        return context.getComponentFactory().getNodeFactoryService().buildTerminalNode( id, source, rule, subrule, subruleIndex, context );
    }

}
