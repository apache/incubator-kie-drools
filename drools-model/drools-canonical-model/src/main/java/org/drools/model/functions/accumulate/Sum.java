   package org.drools.model.functions.accumulate;

import org.drools.model.Variable;
import org.drools.model.functions.Function1;

import java.io.Serializable;
import java.util.Optional;

public class Sum<T, N extends Number> extends AbstractAccumulateFunction<T, Sum.Context<N>, N> {

    private final Function1<T, N> mapper;
    private final Optional<Variable<T>> optSource;

    public Sum(Optional<Variable<T>> source, Function1<T, N> mapper) {
        this.mapper = mapper;
        this.optSource = source;
    }

    @Override
    public Context<N> init() {
        return new Context<N>();
    }

    @Override
    public void action(Context<N> acc, T obj) {
        acc.add(mapper.apply(obj));
    }

    @Override
    public void reverse(Context<N> acc, T obj) {
        acc.subtract(mapper.apply(obj));
    }

    @Override
    public N result(Context<N> acc) {
        return acc.result();
    }

    @Override
    public Optional<Variable<T>> getOptSource() {
        return optSource;
    }

    public static class Context<N extends Number> implements Serializable {
        private Double total;
        private Class<N> clazz;

        public Context() {
            this(null, null);
        }

        public Context(Double total, Class<N> clazz) {
            this.total = total;
            this.clazz = clazz;
        }

        private void add(N value) {
            if (value != null) {
                if (total == null) {
                    total = value.doubleValue();
                    clazz = (Class<N>)value.getClass();
                } else {
                    total = total + value.doubleValue();
                }
            }
        }

        private void subtract(N value) {
            if (value != null) {
                total = total - value.doubleValue();
            }
        }

        private N result() {
            if (clazz == Integer.class || clazz == int.class) {
                return (N) new Integer(total.intValue());
            }
            if (clazz == Long.class || clazz == long.class) {
                return (N) new Long(total.longValue());
            }
            return (N) total;
        }
    }
}