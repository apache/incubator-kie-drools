package org.drools.model.index;

import org.drools.model.BetaIndexN;
import org.drools.model.functions.Function1;

public abstract class AbstractBetaIndex<A, V> extends AbstractIndex<A, V> implements BetaIndexN<A, V> {

    private final Class<?> rightReturnType;

    public AbstractBetaIndex(Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Class<?> rightReturnType) {
        super(indexedClass, constraintType, indexId, leftOperandExtractor);
        this.rightReturnType = rightReturnType;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.BETA;
    }

    @Override
    public Class<?> getRightReturnType() {
        return rightReturnType;
    }

}
