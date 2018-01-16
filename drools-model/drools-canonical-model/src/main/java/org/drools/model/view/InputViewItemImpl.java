package org.drools.model.view;

import org.drools.model.Variable;

public class InputViewItemImpl<T> implements InputViewItem<T> {
    private final Variable<T> var;

    public InputViewItemImpl( Variable<T> var ) {
        this.var = var;
    }

    @Override
    public Variable getFirstVariable() {
        return var;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { var };
    }
}
