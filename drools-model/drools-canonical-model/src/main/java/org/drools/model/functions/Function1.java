package org.drools.model.functions;

import java.io.Serializable;

public interface Function1<T, R> extends Serializable {
    R apply(T t);

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
        protected Object getLambda() {
            return function;
        }
    }
}
