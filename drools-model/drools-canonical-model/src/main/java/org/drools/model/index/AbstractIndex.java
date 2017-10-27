package org.drools.model.index;

import org.drools.model.Index;
import org.drools.model.functions.Function1;

public abstract class AbstractIndex<A, V> implements Index<A, V> {

    private final Class<?> indexedClass;
    private final ConstraintType constraintType;
    private final int indexId;
    private final Function1<A, V> leftOperandExtractor;

    protected AbstractIndex( Class<?> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor ) {
        this.indexedClass = indexedClass;
        this.constraintType = constraintType;
        this.indexId = indexId;
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
    public int getIndexId() {
        return indexId;
    }

    @Override
    public Function1<A, V> getLeftOperandExtractor() {
        return leftOperandExtractor;
    }
}
