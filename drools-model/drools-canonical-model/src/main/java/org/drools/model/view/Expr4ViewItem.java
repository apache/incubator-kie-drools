package org.drools.model.view;

import org.drools.model.Index;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function3;

public interface Expr4ViewItem<A, B, C, D> extends ExprNViewItem<A> {

    <V> Expr4ViewItemImpl<A, B, C, D> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function3<B, C, D, ?> rightOperandExtractor );

    <V> Expr4ViewItemImpl<A, B, C, D> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function3<B, C, D, ?> rightOperandExtractor, Class<?> rightReturnType );

}
