package org.drools.model;

import org.drools.model.functions.Function1;

public interface Index<A, V> {
    enum IndexType {
        ALPHA, BETA;
    }

    enum ConstraintType {
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        GREATER_OR_EQUAL,
        LESS_THAN,
        LESS_OR_EQUAL,
        RANGE
    }

    IndexType getIndexType();

    Class<?> getIndexedClass();

    ConstraintType getConstraintType();

    Function1<A, V> getLeftOperandExtractor();
}
