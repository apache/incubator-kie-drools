package org.drools.model.view;

import org.drools.model.Index;
import org.drools.model.functions.Function1;

public interface Expr2ViewItem<T, U> extends ExprNViewItem<T> {

    <V> Expr2ViewItem<T, U> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, V> leftOperandExtractor, Function1<U, ?> rightOperandExtractor );

    <V> Expr2ViewItem<T, U> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, V> leftOperandExtractor, Function1<U, ?> rightOperandExtractor, Class<?> rightReturnType );

}
