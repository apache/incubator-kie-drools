package org.drools.model.functions.accumulate;

import java.io.Serializable;
import java.util.Optional;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.impl.ModelComponent;

public class Average<T> extends AbstractAccumulateFunction<T, Average.Context, Double> implements ModelComponent {

    private final Function1<T, ? extends Number> mapper;

    public Average(Optional<Variable<T>> source, Function1<T, ? extends Number> mapper, Optional<String> paramName) {
        super(source, paramName);
        this.mapper = mapper;
    }

    @Override
    public Optional<Variable<T>> getOptSource() {
        return optSource;
    }

    @Override
    public Context init() {
        return new Context();
    }

    @Override
    public Double result(Context acc) {
        return acc.result();
    }

    @Override
    public void reverse(Context acc, T obj) {
        acc.subtract(mapper.apply(obj));
    }

    @Override
    public void action(Context acc, T obj) {
        acc.add(mapper.apply(obj));
    }

    public static class Context implements Serializable {
        private double total;
        private int count;

        private Context() {
            this(0.0, 0);
        }

        private Context(double total, int count) {
            this.total = total;
            this.count = count;
        }

        private void add(Number value) {
            total += value.doubleValue();
            count++;
        }

        private void subtract(Number value) {
            total -= value.doubleValue();
            count--;
        }

        private double result() {
            return count == 0 ? 0 : total / count;
        }
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof Average) ) return false;

        Average<?> that = ( Average<?> ) o;

        if ( !ModelComponent.areEqualInModel( getVariable(), that.getVariable() ) ) return false;
        return mapper.equals( that.mapper );
    }
}
