package org.drools.model;

import java.util.Optional;

import org.drools.model.functions.accumulate.AccumulateFunction;
import org.drools.model.patterns.CompositePatterns;

public interface AccumulatePattern<T> extends Pattern<T> {

    AccumulateFunction[] getAccumulateFunctions();

    Optional<CompositePatterns> getCompositePatterns();

    Pattern getPattern();
}
