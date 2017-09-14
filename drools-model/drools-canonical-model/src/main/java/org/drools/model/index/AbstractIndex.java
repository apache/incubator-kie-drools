package org.drools.model.index;

import org.drools.model.Index;
import org.drools.model.functions.Function1;

public abstract class AbstractIndex<A, V> implements Index<A, V> {

    private final Class<?> indexedClass;
    private final ConstraintType constraintType;
    private final Function1<A, V> leftOperandExtractor;

    protected AbstractIndex( Class<?> indexedClass, ConstraintType constraintType, Function1<A, V> leftOperandExtractor ) {
        this.indexedClass = indexedClass;
        this.constraintType = constraintType;
        this.leftOperandExtractor = leftOperandExtractor;
    }

    @Override
    public Class<?> getIndexedClass() {
        return indexedClass;
    }

    @Override
    public ConstraintType getConstraintType() {
        return constraintType;
    }

    @Override
    public Function1<A, V> getLeftOperandExtractor() {
        return leftOperandExtractor;
    }
}
