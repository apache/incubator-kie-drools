package org.drools.core.rule.constraint;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;

public interface ConditionEvaluator {
    boolean evaluate(InternalFactHandle handle, InternalWorkingMemory workingMemory, LeftTuple leftTuple);
}
