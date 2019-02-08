package org.drools.model.view;

import org.drools.model.Variable;

public class InputViewItemImpl<T> implements InputViewItem<T> {
    private final Variable<T> var;
    private String[] watchedProps;

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

    @Override
    public InputViewItemImpl<T> watch(String... props) {
        this.watchedProps = props;
        return this;
    }

    public String[] getWatchedProps() {
        return watchedProps;
    }
}
