package org.drools.model.view;

import org.drools.model.Index;
import org.drools.model.functions.Function1;

public interface Expr1ViewItem<T> extends ExprNViewItem<T> {
    <U> Expr1ViewItem<T> indexedBy( Class<U> indexedClass, Index.ConstraintType constraintType, int indexId, Function1<T, U> leftOperandExtractor, U rightValue );
}
