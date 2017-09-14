package org.drools.model.functions.accumulate;

import org.drools.model.AccumulateFunction;
import org.drools.model.Variable;

import java.io.Serializable;

public abstract class AbstractAccumulateFunction<T, A extends Serializable, R> implements AccumulateFunction<T, A, R> {
    private Variable<R> var;

    @Override
    public Variable<R> getVariable() {
        return var;
    }

    public AbstractAccumulateFunction<T, A, R> as(Variable<R> var) {
        this.var = var;
        return this;
    }
}
