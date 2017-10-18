package org.drools.model.view;

import org.drools.model.Binding;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;

public class BindViewItem<T> implements ViewItem<T>, Binding {

    private final Variable<T> boundVariable;
    private final Function1 bindingFunction;
    private final Variable inputVariable;
    private final String reactOn;

    public BindViewItem( Variable<T> boundVariable, Function1 bindingFunction, Variable inputVariable, String reactOn ) {
        this.bindingFunction = bindingFunction;
        this.boundVariable = boundVariable;
        this.inputVariable = inputVariable;
        this.reactOn = reactOn;
    }

    @Override
    public Variable<T> getFirstVariable() {
        return boundVariable;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { boundVariable };
    }

    @Override
    public Variable<T> getBoundVariable() {
        return boundVariable;
    }

    @Override
    public Function1 getBindingFunction() {
        return bindingFunction;
    }

    @Override
    public Variable getInputVariable() {
        return inputVariable;
    }

    @Override
    public String getReactOn() {
        return reactOn;
    }
}
