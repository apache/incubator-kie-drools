package org.drools.model.index;

import org.drools.model.BetaIndex2;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;

public class BetaIndex2Impl<A, B, C, V> extends AbstractBetaIndex<A, V> implements BetaIndex2<A, B, C, V> {

    private final Function2<B, C, ?> rightOperandExtractor;

    public BetaIndex2Impl( Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function2<B, C, ?> rightOperandExtractor, Class<?> rightReturnType) {
        super(indexedClass, constraintType, indexId, leftOperandExtractor, rightReturnType);
        this.rightOperandExtractor = rightOperandExtractor;
    }

    @Override
    public Function2<B, C, ?> getRightOperandExtractor() {
        return rightOperandExtractor;
    }

    @Override
    public String toString() {
        return "BetaIndex #" + getIndexId() + " (" + getConstraintType() + ", " +
                "left: lambda " + System.identityHashCode(getLeftOperandExtractor()) + ", " +
                "right: lambda " + System.identityHashCode(rightOperandExtractor) + ")";
    }
}
