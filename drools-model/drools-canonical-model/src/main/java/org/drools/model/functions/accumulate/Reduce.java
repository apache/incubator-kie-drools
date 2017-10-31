package org.drools.model.functions.accumulate;

import org.drools.model.functions.Function2;

import java.io.Serializable;

public class Reduce<T, R extends Serializable> extends AbstractAccumulateFunction<T, Reduce.Context<R>, R> {

    private final R zero;
    private final Function2<R, T, R> reducingFunction;

    public Reduce(R zero, Function2<R, T, R> reducingFunction) {
        this.zero = zero;
        this.reducingFunction = reducingFunction;
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

    public static class Context<A extends Serializable> implements Serializable {
        private A value;

        private Context(A value) {
            this.value = value;
        }
    }
}
