package org.drools.model;

import java.util.Optional;

import org.drools.model.patterns.CompositePatterns;

public interface AccumulatePattern<T> extends Pattern<T> {

    AccumulateFunction<T, ?, ?>[] getFunctions();

    Optional<CompositePatterns> getCompositePatterns();
}
