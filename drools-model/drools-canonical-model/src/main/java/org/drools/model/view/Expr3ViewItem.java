package org.drools.model.view;

import org.drools.model.Index;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;

public interface Expr3ViewItem<A, B, C> extends ExprNViewItem<A> {

    <V> Expr3ViewItemImpl<A, B, C> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function2<B, C, ?> rightOperandExtractor );

    <V> Expr3ViewItemImpl<A, B, C> indexedBy( Class<V> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<A, V> leftOperandExtractor, Function2<B, C, ?> rightOperandExtractor, Class<?> rightReturnType );

}
