package org.drools.model.view;

import org.drools.model.Variable;
import org.drools.model.functions.FunctionN;

public class SetViewItem<T> implements ViewItem<T> {

    private final FunctionN invokedFunction;
    private final boolean multivalue;
    private final Variable<T> variable;
    private final Variable[] inputVariables;

    public SetViewItem(FunctionN function, boolean multivalue, Variable<T> boundVariable, Variable... inputVariables) {
        this.invokedFunction = function;
        this.multivalue = multivalue;
        this.variable = boundVariable;
        this.inputVariables = inputVariables;
    }

    @Override
    public Variable<T> getFirstVariable() {
        return variable;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[] { variable };
    }

    public FunctionN getInvokedFunction() {
        return invokedFunction;
    }

    public Variable[] getInputVariables() {
        return inputVariables;
    }

    public boolean isMultivalue() {
        return multivalue;
    }
}
