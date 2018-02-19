package org.drools.model.view;

import org.drools.model.Binding;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.model.impl.ModelComponent;

public class BindViewItem2<T> implements ViewItem<T>,
                                         Binding,
                                         ModelComponent {

    private final Variable<T> boundVariable;
    private final Function2 bindingFunction;
    private final Variable inputVariable1;
    private final Variable inputVariable2;
    private final String reactOn;
    private final String[] watchedProps;

    public BindViewItem2(Variable<T> boundVariable, Function2 bindingFunction, Variable inputVariable1, Variable inputVariable2, String reactOn, String[] watchedProps) {
        this.boundVariable = boundVariable;
        this.bindingFunction = bindingFunction;
        this.inputVariable1 = inputVariable1;
        this.inputVariable2 = inputVariable2;
        this.reactOn = reactOn;
        this.watchedProps = watchedProps;
    }

    @Override
    public Variable<T> getFirstVariable() {
        return boundVariable;
    }

    @Override
    public Variable<?>[] getVariables() {
        return new Variable[]{boundVariable};
    }

    @Override
    public Variable<T> getBoundVariable() {
        return boundVariable;
    }

    @Override
    public Function1 getBindingFunction() {
        return null;
    }

    @Override
    public Variable getInputVariable() {
        return inputVariable1;
    }

    @Override
    public Variable[] getInputVariables() {
        return new Variable[]{inputVariable1, inputVariable2};
    }

    @Override
    public String getReactOn() {
        return reactOn;
    }

    public String[] getWatchedProps() {
        return watchedProps;
    }

    @Override
    public Object eval(Object... args) {
        return bindingFunction.apply(args[0], args[1]);
    }

    @Override
    public boolean isEqualTo(ModelComponent o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BindViewItem2)) {
            return false;
        }

        BindViewItem2<?> that = (BindViewItem2<?>) o;

        if (!ModelComponent.areEqualInModel(boundVariable, that.boundVariable)) {
            return false;
        }
        if (!ModelComponent.areEqualInModel(inputVariable1, that.inputVariable1)) {
            return false;
        }
        if (!ModelComponent.areEqualInModel(inputVariable2, that.inputVariable2)) {
            return false;
        }
        if (!bindingFunction.equals(that.bindingFunction)) {
            return false;
        }
        return reactOn == null ? that.reactOn == null : reactOn.equals(that.reactOn);
    }
}
