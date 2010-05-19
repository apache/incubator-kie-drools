package org.drools.base;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.rule.Rule;
import org.drools.spi.PropagationContext;

public interface InternalViewChangedEventListener {
    public void rowAdded(Rule rule, 
                         LeftTuple tuple,
                         PropagationContext context,
                         InternalWorkingMemory workingMemory);

    public void rowRemoved(Rule rule, LeftTuple tuple,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory);

    public void rowUpdated(Rule rule,
                           LeftTuple leftTuple,
                           PropagationContext context,
                           InternalWorkingMemory workingMemory);
}
