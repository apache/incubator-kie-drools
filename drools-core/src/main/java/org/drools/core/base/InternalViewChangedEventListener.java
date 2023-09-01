package org.drools.core.base;

import java.util.List;

import org.drools.core.common.ReteEvaluator;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTuple;

public interface InternalViewChangedEventListener {
    void rowAdded(RuleImpl rule, LeftTuple tuple, ReteEvaluator reteEvaluator);

    void rowRemoved(RuleImpl rule, LeftTuple tuple, ReteEvaluator reteEvaluator);

    void rowUpdated(RuleImpl rule, LeftTuple leftTuple, ReteEvaluator reteEvaluator);
    
    List<? extends Object> getResults();
}
