package org.drools.core.base.accumulators;

import java.io.Serializable;

import org.kie.api.runtime.rule.AccumulateFunction;

public abstract class AbstractAccumulateFunction<C extends Serializable> implements AccumulateFunction<C> {

    @Override
    public boolean equals( Object o ) {
        return this == o || o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
        return getClass().getName().hashCode();
    }
}
