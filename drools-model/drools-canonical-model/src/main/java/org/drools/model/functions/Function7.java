package org.drools.model.functions;

import java.io.Serializable;

public interface Function7<A, B, C, D, E, F, G, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e, F f, G g);

    class Impl<A, B, C, D, E, F, G, R> extends IntrospectableLambda implements Function7<A, B, C, D, E, F, G, R> {

        private final Function7<A, B, C, D, E, F, G, R> function;

        public Impl(Function7<A, B, C, D, E, F, G, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e, F f, G g) {
            return function.apply(a, b, c, d, e, f, g);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}