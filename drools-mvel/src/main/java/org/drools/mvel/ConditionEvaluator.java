package org.drools.mvel;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.kie.api.runtime.rule.FactHandle;

public interface ConditionEvaluator {
    boolean evaluate(FactHandle handle, ValueResolver valueResolver, BaseTuple tuple);
}
