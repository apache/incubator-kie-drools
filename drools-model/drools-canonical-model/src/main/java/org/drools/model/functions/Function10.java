package org.drools.model.functions;

import java.io.Serializable;

public interface Function10<A, B, C, D, E, F, G, H, I, J, R> extends Serializable {

    R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j);

    class Impl<A, B, C, D, E, F, G, H, I, J, R> extends IntrospectableLambda implements Function10<A, B, C, D, E, F, G, H, I, J, R> {

        private final Function10<A, B, C, D, E, F, G, H, I, J, R> function;

        public Impl(Function10<A, B, C, D, E, F, G, H, I, J, R> function) {
            this.function = function;
        }

        @Override
        public R apply(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) {
            return function.apply(a, b, c, d, e, f, g, h, i, j);
        }

        @Override
        public Object getLambda() {
            return function;
        }
    }
}