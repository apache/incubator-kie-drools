package org.drools.model.functions;

import java.io.Serializable;

public interface Function1<T, R> extends Serializable {
    R apply(T t);

    static <T> Function1<T, T> identity() {
        return a -> a;
    }

    default <V> Function1<T, V> andThen(Function1<R, V> f2) {
        return (T t) -> f2.apply(apply(t));
    }

    class Impl<T,R> extends IntrospectableLambda implements Function1<T, R> {

        private final Function1<T,R> function;

        public Impl(Function1<T, R> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) {
            return function.apply(t);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}
