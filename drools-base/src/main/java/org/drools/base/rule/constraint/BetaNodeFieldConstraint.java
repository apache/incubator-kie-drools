package org.drools.base.rule.constraint;

import org.drools.base.rule.ContextEntry;
import org.drools.base.reteoo.BaseTuple;
import org.kie.api.runtime.rule.FactHandle;

public interface BetaNodeFieldConstraint
    extends
    Constraint {

    boolean isAllowedCachedLeft(ContextEntry context,
                                FactHandle handle);

    boolean isAllowedCachedRight(BaseTuple tuple,
                                 ContextEntry context);

    ContextEntry createContextEntry();

    /**
     * Clone this constraints only if it is already used by a different node, otherwise returns this
     */
    BetaNodeFieldConstraint cloneIfInUse();
}
