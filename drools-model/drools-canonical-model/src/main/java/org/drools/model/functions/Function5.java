package org.drools.model.functions;

import java.io.Serializable;

public interface Function5<A, B, C, D, E, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e);

    class Impl<A, B, C, D, E, R> extends IntrospectableLambda implements Function5<A, B, C, D, E, R> {

        private final Function5<A, B, C, D, E, R> function;

        public Impl(Function5<A, B, C, D, E, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e) {
            return function.apply(a, b, c, d, e);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}
