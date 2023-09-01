package org.drools.model.functions;

import java.io.Serializable;

public interface Function6<A, B, C, D, E, F, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e, F f);

    class Impl<A, B, C, D, E, F, R> extends IntrospectableLambda implements Function6<A, B, C, D, E, F, R> {

        private final Function6<A, B, C, D, E, F, R> function;

        public Impl(Function6<A, B, C, D, E, F, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e, F f) {
            return function.apply(a, b, c, d, e, f);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}
