package org.drools.model.index;

import org.drools.model.BetaIndex;
import org.drools.model.functions.Function1;

public class BetaIndexImpl<A, B, V> extends AbstractBetaIndex<A, V> implements BetaIndex<A, B, V> {

    private final Function1<B, ?> rightOperandExtractor;

    public BetaIndexImpl( Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function1<B, ?> rightOperandExtractor) {
        this(indexedClass, constraintType, indexId, leftOperandExtractor, rightOperandExtractor, null);
    }

    public BetaIndexImpl( Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function1<B, ?> rightOperandExtractor, Class<?> rightReturnType) {
        super(indexedClass, constraintType, indexId, leftOperandExtractor, rightReturnType);
        this.rightOperandExtractor = rightOperandExtractor;
    }

    @Override
    public Function1<B, ?> getRightOperandExtractor() {
        return rightOperandExtractor;
    }

    @Override
    public String toString() {
        return "BetaIndex #" + getIndexId() + " (" + getConstraintType() + ", " +
                "left: lambda " + System.identityHashCode(getLeftOperandExtractor()) + ", " +
                "right: lambda " + System.identityHashCode(rightOperandExtractor) + ")";
    }
}
