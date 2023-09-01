package org.drools.model.view;

import java.util.Arrays;

import org.drools.model.Binding;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function4;
import org.drools.model.impl.ModelComponent;

public class BindViewItem4<T> implements ViewItem<T>,
                                         Binding,
                                         ModelComponent {

    private final Variable<T> boundVariable;
    private final Function4 bindingFunction;
    private final Variable inputVariable1;
    private final Variable inputVariable2;
    private final Variable inputVariable3;
    private final Variable inputVariable4;
    private final String[] reactOn;
    private final String[] watchedProps;

    public BindViewItem4(Variable<T> boundVariable, Function4 bindingFunction, Variable inputVariable1, Variable inputVariable2, Variable inputVariable3, Variable inputVariable4, String[] reactOn, String[] watchedProps) {
        this.boundVariable = boundVariable;
        this.bindingFunction = bindingFunction;
        this.inputVariable1 = inputVariable1;
        this.inputVariable2 = inputVariable2;
        this.inputVariable3 = inputVariable3;
        this.inputVariable4 = inputVariable4;
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
        return new Variable[]{inputVariable1, inputVariable2, inputVariable3, inputVariable4};
    }

    @Override
    public String[] getReactOn() {
        return reactOn;
    }

    public String[] getWatchedProps() {
        return watchedProps;
    }

    @Override
    public Object eval(Object... args) {
        return bindingFunction.apply(args[0], args[1], args[2], args[3]);
    }

    @Override
    public boolean isEqualTo(ModelComponent o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BindViewItem4)) {
            return false;
        }

        BindViewItem4<?> that = (BindViewItem4<?>) o;

        if (!ModelComponent.areEqualInModel(boundVariable, that.boundVariable)) {
            return false;
        }
        if (!ModelComponent.areEqualInModel(inputVariable1, that.inputVariable1)) {
            return false;
        }
        if (!ModelComponent.areEqualInModel(inputVariable2, that.inputVariable2)) {
            return false;
        }
        if (!ModelComponent.areEqualInModel(inputVariable3, that.inputVariable3)) {
            return false;
        }
        if (!ModelComponent.areEqualInModel(inputVariable4, that.inputVariable4)) {
            return false;
        }
        if (!bindingFunction.equals(that.bindingFunction)) {
            return false;
        }
        return reactOn == null ? that.reactOn == null : Arrays.equals(reactOn, that.reactOn);
    }
}
