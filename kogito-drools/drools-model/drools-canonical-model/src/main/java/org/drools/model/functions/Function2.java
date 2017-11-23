package org.drools.model.functions;

public interface Function2<A, B, R> {
    R apply(A a, B b);

    class Impl<A,B,R> implements Function2<A,B,R> {

        private final Function2<A,B,R> function;
        private String lambdaFingerprint;

        public Impl(Function2<A,B,R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b) {
            return function.apply(a, b);
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
