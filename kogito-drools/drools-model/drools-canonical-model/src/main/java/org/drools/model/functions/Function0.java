package org.drools.model.functions;

public interface Function0<R> {
    R apply();

    public static final class Null implements Function0 {

        public static final Null INSTANCE = new Null();

        private Null() { }

        @Override
        public Object apply() {
            return null;
        }
    }

    class Impl<R> implements Function0<R> {

        private final Function0<R> function;
        private String lambdaFingerprint;

        public Impl(Function0<R> function) {
            this.function = function;
        }

        @Override
        public R apply() {
            return function.apply();
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
