package org.drools.model.functions.accumulate;

import java.io.Serializable;
import java.util.Optional;

import org.drools.model.AccumulateFunction;
import org.drools.model.Variable;

public abstract class AbstractAccumulateFunction<T, A extends Serializable, R> implements AccumulateFunction<T, A, R> {
    private Variable<R> var;
    protected final Optional<Variable<T>> optSource;

    public AbstractAccumulateFunction(Optional<Variable<T>> optSource) {
        this.optSource = optSource;
    }

    @Override
    public Variable<R> getVariable() {
        return var;
    }

    public AbstractAccumulateFunction<T, A, R> as(Variable<R> var) {
        this.var = var;
        return this;
    }
}
