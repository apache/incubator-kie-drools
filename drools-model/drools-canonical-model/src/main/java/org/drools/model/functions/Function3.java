package org.drools.model.functions;

import java.io.Serializable;

public interface Function3<A, B, C, R> extends Serializable {

    R apply(A a, B b, C c);

    class Impl<A, B, C, R> extends IntrospectableLambda implements Function3<A, B, C, R> {

        private final Function3<A, B, C, R> function;

        public Impl(Function3<A, B, C, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c) {
            return function.apply(a, b, c);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}
