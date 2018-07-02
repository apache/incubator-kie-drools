package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate10<A, B, C, D, E, F, G, H, I, J> extends Serializable {

    boolean test( A a, B b, C c, D d, E e, F f, G g, H h, I i, J j ) throws Exception;

    class Impl<A, B, C, D, E, F, G, H, I, J> extends IntrospectableLambda implements Predicate10<A, B, C, D, E, F, G, H, I, J> {

        private final Predicate10<A, B, C, D, E, F, G, H, I, J> predicate;

        public Impl(Predicate10<A, B, C, D, E, F, G, H, I, J> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i, j);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
