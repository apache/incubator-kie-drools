package org.drools.model.functions;

import java.io.Serializable;

public interface Function1<T, R> extends Serializable {
    R apply(T t);

    class Impl<T,R> implements Function1<T, R> {

        private final Function1<T,R> function;

        public Impl(Function1<T, R> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) {
            return function.apply(t);
        }

        @Override
        public String toString() {
            return LambdaIntrospector.getLambdaFingerprint(function);
        }
    }

}
