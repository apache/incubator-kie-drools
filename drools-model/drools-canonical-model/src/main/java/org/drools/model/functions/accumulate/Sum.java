package org.drools.model.functions.accumulate;

import java.io.Serializable;
import java.util.Optional;

import org.drools.model.Variable;
import org.drools.model.impl.ModelComponent;

public class Sum<N extends Number> extends AbstractAccumulateFunction<N, Sum.Context<N>, N> implements ModelComponent {

    public Sum(Variable<N> source) {
        super(source);
    }

    @Override
    public Context<N> init() {
        return new Context<N>();
    }

    @Override
    public void action(Context<N> acc, N obj) {
        acc.add(obj);
    }

    @Override
    public void reverse(Context<N> acc, N obj) {
        acc.subtract(obj);
    }

    @Override
    public N result(Context<N> acc) {
        return acc.result();
    }

    @Override
    public Variable<N> getSource() {
        return source;
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( !(o instanceof Sum) ) return false;

        Sum<?> that = ( Sum<?> ) o;

        return (!ModelComponent.areEqualInModel( getVariable(), that.getVariable() ) );
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