package org.drools.model.functions.accumulate;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;

import java.io.Serializable;
import java.util.Optional;

public class Average<T> extends AbstractAccumulateFunction<T, Average.Context, Double> {

    private final Function1<T, ? extends Number> mapper;

    public Average(Function1<T, ? extends Number> mapper) {
        this.mapper = mapper;
    }

    @Override
    public Optional<Variable<Double>> getOptSource() {
        return Optional.empty();
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
}
