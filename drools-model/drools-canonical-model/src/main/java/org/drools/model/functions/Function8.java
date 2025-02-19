package org.drools.model.functions;

import java.io.Serializable;

public interface Function8<A, B, C, D, E, F, G, H, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h);

    class Impl<A, B, C, D, E, F, G, H, R> extends IntrospectableLambda implements Function8<A, B, C, D, E, F, G, H, R> {

        private final Function8<A, B, C, D, E, F, G, H, R> function;

        public Impl(Function8<A, B, C, D, E, F, G, H, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e, F f, G g, H h) {
            return function.apply(a, b, c, d, e, f, g, h);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}