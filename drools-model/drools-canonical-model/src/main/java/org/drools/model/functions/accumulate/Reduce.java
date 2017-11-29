package org.drools.model.functions.accumulate;

import java.io.Serializable;
import java.util.Optional;

import org.drools.model.Variable;
import org.drools.model.functions.Function2;
import org.drools.model.impl.ModelComponent;

public class Reduce<T, R extends Serializable> extends AbstractAccumulateFunction<T, Reduce.Context<R>, R> implements ModelComponent {

    private final R zero;
    private final Function2<R, T, R> reducingFunction;

    public Reduce(Optional<Variable<T>> source, R zero, Function2<R, T, R> reducingFunction, Optional<String> paramName) {
        super(source, paramName);
        this.zero = zero;
        this.reducingFunction = reducingFunction;
    }

    @Override
    public Optional<Variable<T>> getOptSource() {
        return Optional.empty();
    }

    @Override
    public Context init() {
        return new Context(zero);
    }

    @Override
    public void action(Context<R> acc, T obj) {
        acc.value = reducingFunction.apply(acc.value, obj);
    }

    @Override
    public void reverse(Context<R> acc, T obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public R result(Context<R> acc) {
        return acc.value;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof Reduce) ) return false;

        Reduce<?, ?> that = ( Reduce<?, ?> ) o;

        if ( !ModelComponent.areEqualInModel( getVariable(), that.getVariable() ) ) return false;
        return zero.equals( that.zero ) && reducingFunction.equals( that.reducingFunction );
    }

    public static class Context<A extends Serializable> implements Serializable {
        private A value;

        private Context(A value) {
            this.value = value;
        }
    }
}
