package org.drools.core.rule.constraint;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.LeftTuple;

public interface ConditionEvaluator {
    boolean evaluate(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple);
}
