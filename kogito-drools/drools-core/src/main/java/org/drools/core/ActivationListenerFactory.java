package org.drools.core;

import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.GroupElement;

public interface ActivationListenerFactory {
    TerminalNode createActivationListener(int id,
                                          LeftTupleSource source,
                                          RuleImpl rule,
                                          GroupElement subrule,
                                          int subruleIndex,                                          
                                          BuildContext context,
                                          Object... args);
}
