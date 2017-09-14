package org.drools.model.view;

import org.drools.model.Variable;

public interface ViewItem<T> extends ViewItemBuilder<T> {

    default Variable<T> getFirstVariable() {
        return (Variable<T>) getVariables()[0];
    }

    Variable<?>[] getVariables();

    @Override
    default ViewItem<T> get() { return this; }
}
