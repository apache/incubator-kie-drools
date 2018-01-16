package org.drools.model;

import java.util.Collection;

public interface Pattern<T> extends Condition {

    Variable<T> getPatternVariable();

    Variable[] getInputVariables();

    Constraint getConstraint();

    Collection<Binding> getBindings();

    String[] getWatchedProps();
}
