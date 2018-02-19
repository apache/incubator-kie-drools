package org.drools.model.view;

import org.drools.model.Binding;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.impl.ModelComponent;

public class BindViewItem1<T> implements ViewItem<T>, Binding, ModelComponent {

    private final Variable<T> boundVariable;
    private final Function1 bindingFunction;
    private final Variable inputVariable;
    private final String reactOn;
    private final String[] watchedProps;

    public BindViewItem1( Variable<T> boundVariable, Function1 bindingFunction, Variable inputVariable, String reactOn, String[] watchedProps ) {
        this.bindingFunction = bindingFunction;
        this.boundVariable = boundVariable;
        this.inputVariable = inputVariable;
        this.reactOn = reactOn;
        this.watchedProps = watchedProps;
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
    public Variable[] getInputVariables() {
        return new Variable[] { inputVariable} ;
    }

    @Override
    public String getReactOn() {
        return reactOn;
    }

    @Override
    public String[] getWatchedProps() {
        return watchedProps;
    }

    @Override
    public Object eval(Object... args) {
        return bindingFunction.apply(args[0]);
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof BindViewItem1) ) return false;

        BindViewItem1<?> that = (BindViewItem1<?>) o;

        if ( !ModelComponent.areEqualInModel( boundVariable, that.boundVariable )) return false;
        if ( !ModelComponent.areEqualInModel( inputVariable, that.inputVariable )) return false;
        if ( !bindingFunction.equals( that.bindingFunction )) return false;
        return reactOn == null ? that.reactOn == null : reactOn.equals( that.reactOn );
    }
}
