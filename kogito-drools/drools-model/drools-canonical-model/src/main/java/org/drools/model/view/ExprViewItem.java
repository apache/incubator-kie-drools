package org.drools.model.view;

import org.drools.model.Condition;
import org.drools.model.DomainClassMetadata;

public interface ExprViewItem<T> extends ViewItem<T> {
    Condition.Type getType();

    String getExprId();

    ExprViewItem<T> reactOn(String... props);
    ExprViewItem<T> reactOn( DomainClassMetadata metadata, String... props);
    ExprViewItem<T> watch(String... props);
}
