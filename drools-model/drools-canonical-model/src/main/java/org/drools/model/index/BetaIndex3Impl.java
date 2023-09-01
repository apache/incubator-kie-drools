package org.drools.model.index;

import org.drools.model.BetaIndex3;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function3;

public class BetaIndex3Impl<A, B, C, D, V> extends AbstractBetaIndex<A, V> implements BetaIndex3<A, B, C, D, V> {

    private final Function3<B, C, D, ?> rightOperandExtractor;

    public BetaIndex3Impl( Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function3<B, C, D, ?> rightOperandExtractor, Class<?> rightReturnType) {
        super(indexedClass, constraintType, indexId, leftOperandExtractor, rightReturnType);
        this.rightOperandExtractor = rightOperandExtractor;
    }

    @Override
    public Function3<B, C, D, ?> getRightOperandExtractor() {
        return rightOperandExtractor;
    }

    @Override
    public String toString() {
        return "BetaIndex #" + getIndexId() + " (" + getConstraintType() + ", " +
                "left: lambda " + System.identityHashCode(getLeftOperandExtractor()) + ", " +
                "right: lambda " + System.identityHashCode(rightOperandExtractor) + ")";
    }
}
