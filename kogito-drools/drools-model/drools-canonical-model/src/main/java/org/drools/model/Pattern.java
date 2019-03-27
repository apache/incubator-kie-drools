package org.drools.model;

import java.util.Collection;

public interface Pattern<T> extends Condition {

    Variable<T> getPatternVariable();

    default DomainClassMetadata getPatternClassMetadata() {
        Variable<T> var = getPatternVariable();
        return var instanceof Declaration ? (( Declaration<T> ) var).getMetadata() : null;
    }

    Variable[] getInputVariables();

    Constraint getConstraint();

    Collection<Binding> getBindings();

    String[] getWatchedProps();
}
