package org.drools.model;

import org.drools.model.functions.accumulate.AccumulateFunction;

public interface AccumulatePattern<T> extends Pattern<T> {

    AccumulateFunction[] getAccumulateFunctions();

    boolean isCompositePatterns();

    Pattern getPattern();

    Condition getCondition();
}
