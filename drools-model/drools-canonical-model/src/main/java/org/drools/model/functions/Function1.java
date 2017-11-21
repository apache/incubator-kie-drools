package org.drools.model.functions;

import java.io.Serializable;

public interface Function1<T, R> extends Serializable {
    R apply(T t);

    class Impl<T,R> implements Function1<T, R> {

        private final Function1<T,R> function;
        private String lambdaFingerprint;

        public Impl(Function1<T, R> function) {
            this.function = function;
        }

        @Override
        public R apply(T t) {
            return function.apply(t);
        }

        @Override
        public String toString() {
            if(lambdaFingerprint == null) {
                lambdaFingerprint = LambdaIntrospector.getLambdaFingerprint(function);
            }
            return lambdaFingerprint;
        }
    }

}
