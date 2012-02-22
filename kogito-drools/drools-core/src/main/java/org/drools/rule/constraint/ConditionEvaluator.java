package org.drools.rule.constraint;

import org.drools.common.InternalWorkingMemory;
import org.drools.reteoo.LeftTuple;

public interface ConditionEvaluator {
    boolean evaluate(Object object, InternalWorkingMemory workingMemory, LeftTuple leftTuple);
}
