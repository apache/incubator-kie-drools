package org.drools.model.index;

import org.drools.model.BetaIndex;
import org.drools.model.functions.Function1;

public class BetaIndexImpl<A, B, V> extends AbstractIndex<A, V> implements BetaIndex<A, B, V> {

    private final Function1<B, V> rightOperandExtractor;

    public BetaIndexImpl(Class<?> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function1<B, V> rightOperandExtractor) {
        super( indexedClass, constraintType, indexId, leftOperandExtractor );
        this.rightOperandExtractor = rightOperandExtractor;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.BETA;
    }

    @Override
    public Function1<B, V> getRightOperandExtractor() {
        return rightOperandExtractor;
    }
}
