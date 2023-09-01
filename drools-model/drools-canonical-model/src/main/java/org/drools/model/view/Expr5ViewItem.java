package org.drools.model.view;

import org.drools.model.Index;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function4;

public interface Expr5ViewItem<A, B, C, D, E> extends ExprNViewItem<A> {

    <V> Expr5ViewItemImpl<A, B, C, D, E> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function4<B, C, D, E, ?> rightOperandExtractor );

    <V> Expr5ViewItemImpl<A, B, C, D, E> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function4<B, C, D, E, ?> rightOperandExtractor, Class<?> rightReturnType );

}
