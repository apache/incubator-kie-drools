package org.drools.model.view;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;

public class BindViewItem<T> implements ViewItem<T> {

    private final Function1 invokedFunction;
    private final Variable<T> variable;
    private final Variable inputVariable;

    public BindViewItem( Variable<T> boundVariable, Function1 function, Variable inputVariable) {
        this.invokedFunction = function;
        this.variable = boundVariable;
        this.inputVariable = inputVariable;
    }

    @Override
    public Variable<T> getFirstVariable() {
        return variable;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { variable };
    }

    public Function1 getInvokedFunction() {
        return invokedFunction;
    }

    public Variable getInputVariable() {
        return inputVariable;
    }
}
