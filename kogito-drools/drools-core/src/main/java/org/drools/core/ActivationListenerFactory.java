package org.drools.core;

import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Rule;

public interface ActivationListenerFactory {
    TerminalNode createActivationListener(int id,
                                          LeftTupleSource source,
                                          Rule rule,
                                          GroupElement subrule,
                                          int subruleIndex,                                          
                                          BuildContext context,
                                          Object... args);
}
