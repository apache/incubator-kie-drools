package org.drools.model.functions;

import java.io.Serializable;

public interface Function0<R> extends Serializable {
    R apply();

    public static final class Null implements Function0 {

        public static final Null INSTANCE = new Null();

        private Null() { }

        @Override
        public Object apply() {
            return null;
        }
    }

    class Impl<R> extends IntrospectableLambda implements Function0<R> {

        private final Function0<R> function;

        public Impl(Function0<R> function) {
            this.function = function;
        }

        @Override
        public R apply() {
            return function.apply();
        }

        @Override
        protected Object getLambda() {
            return function;
        }
    }
}
