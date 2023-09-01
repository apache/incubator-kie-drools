package org.drools.model.index;

import org.drools.model.BetaIndex4;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function4;

public class BetaIndex4Impl<A, B, C, D, E, V> extends AbstractBetaIndex<A, V> implements BetaIndex4<A, B, C, D, E, V> {

    private final Function4<B, C, D, E, ?> rightOperandExtractor;

    public BetaIndex4Impl( Class<V> indexedClass, ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function4<B, C, D, E, ?> rightOperandExtractor, Class<?> rightReturnType) {
        super(indexedClass, constraintType, indexId, leftOperandExtractor, rightReturnType);
        this.rightOperandExtractor = rightOperandExtractor;
    }

    @Override
    public Function4<B, C, D, E, ?> getRightOperandExtractor() {
        return rightOperandExtractor;
    }

    @Override
    public String toString() {
        return "BetaIndex #" + getIndexId() + " (" + getConstraintType() + ", " +
                "left: lambda " + System.identityHashCode(getLeftOperandExtractor()) + ", " +
                "right: lambda " + System.identityHashCode(rightOperandExtractor) + ")";
    }
}
