package org.drools.model.view;

import org.drools.model.Condition;

public interface ExprViewItem<T> extends ViewItem<T> {
    Condition.Type getType();

    String getExprId();

    ExprViewItem<T> reactOn(String... props);
    ExprViewItem<T> watch(String... props);
}
