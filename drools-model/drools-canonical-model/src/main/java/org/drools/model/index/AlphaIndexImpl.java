package org.drools.model.index;

import org.drools.model.AlphaIndex;
import org.drools.model.functions.Function1;

public class AlphaIndexImpl<A, V> extends AbstractIndex<A, V> implements AlphaIndex<A, V> {

    private final V rightValue;

    public AlphaIndexImpl(Class<?> indexedClass, ConstraintType constraintType, Function1<A, V> leftOperandExtractor, V rightValue) {
        super( indexedClass, constraintType, leftOperandExtractor );
        this.rightValue = rightValue;
    }

    @Override
    public V getRightValue() {
        return rightValue;
    }

    @Override
    public IndexType getIndexType() {
        return IndexType.ALPHA;
    }

}
