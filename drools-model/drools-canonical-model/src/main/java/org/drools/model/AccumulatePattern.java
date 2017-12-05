package org.drools.model;

import java.util.Optional;

import org.drools.model.functions.accumulate.UserDefinedAccumulateFunction;
import org.drools.model.patterns.CompositePatterns;

public interface AccumulatePattern<T> extends Pattern<T> {

    AccumulateFunction<T, ?, ?>[] getFunctions();

    UserDefinedAccumulateFunction[] getUserDefinedAccumulateFunctions();

    Optional<CompositePatterns> getCompositePatterns();

    Pattern getPattern();
}
