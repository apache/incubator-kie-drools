package org.drools.model.functions;

import java.io.Serializable;

public interface Function11<A, B, C, D, E, F, G, H, I, J, K, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k);

    class Impl<A, B, C, D, E, F, G, H, I, J, K, R> extends IntrospectableLambda implements Function11<A, B, C, D, E, F, G, H, I, J, K, R> {

        private final Function11<A, B, C, D, E, F, G, H, I, J, K, R> function;

        public Impl(Function11<A, B, C, D, E, F, G, H, I, J, K, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k) {
            return function.apply(a, b, c, d, e, f, g, h, i, j, k);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}