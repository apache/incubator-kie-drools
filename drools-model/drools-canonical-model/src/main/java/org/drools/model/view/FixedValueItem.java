package org.drools.model.view;

import org.drools.model.Variable;

public class FixedValueItem implements ViewItem {
    private final String exprId;
    private final boolean value;

    public FixedValueItem( String exprId, boolean value ) {
        this.exprId = exprId;
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    @Override
    public Variable getFirstVariable() {
        return null;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[0];
    }
}
