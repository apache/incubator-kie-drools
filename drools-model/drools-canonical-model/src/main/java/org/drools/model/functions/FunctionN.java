package org.drools.model.functions;

import java.io.Serializable;

public interface FunctionN<R> extends Serializable {
    R apply(Object... objs);

    default Function1 asFunction1() {
        return (Function1) ((Impl)this).f;
    }

    class Impl<A, R> extends IntrospectableLambda implements FunctionN<R> {

        private final Object f;
        private final FunctionN<R> function;

        public Impl(Object f, FunctionN<R> function) {
            super(getLambdaFingerprint( f ));
            this.f = f;
            this.function = function;
        }

        @Override
        public R apply(Object... objs) {
            return function.apply(objs);
        }

        @Override
        public Object getLambda() {
            throw new UnsupportedOperationException();
        }

        private static <A, R> String getLambdaFingerprint( Object f ) {
            return f instanceof HashedExpression ? (( HashedExpression ) f).getExpressionHash() : f.toString();
        }
    }
}
