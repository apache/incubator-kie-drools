package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate13<A, B, C, D, E, F, G, H, I, J, K, L, M> extends Serializable {

    boolean test( A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m ) throws Exception;

    default Predicate13<A, B, C, D, E, F, G, H, I, J, K, L, M> negate() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m) -> !test( a, b, c, d, e, f, g, h, i, j, k, l, m );
    }

    class Impl<A, B, C, D, E, F, G, H, I, J, K, L, M> extends IntrospectableLambda implements Predicate13<A, B, C, D, E, F, G, H, I, J, K, L, M> {

        private final Predicate13<A, B, C, D, E, F, G, H, I, J, K, L, M> predicate;

        public Impl(Predicate13<A, B, C, D, E, F, G, H, I, J, K, L, M> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i, j, k, l, m);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
