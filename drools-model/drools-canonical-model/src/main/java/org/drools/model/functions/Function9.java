package org.drools.model.functions;

import java.io.Serializable;

public interface Function9<A, B, C, D, E, F, G, H, I, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i);

    class Impl<A, B, C, D, E, F, G, H, I, R> extends IntrospectableLambda implements Function9<A, B, C, D, E, F, G, H, I, R> {

        private final Function9<A, B, C, D, E, F, G, H, I, R> function;

        public Impl(Function9<A, B, C, D, E, F, G, H, I, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i) {
            return function.apply(a, b, c, d, e, f, g, h, i);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}