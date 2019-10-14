package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate12<A, B, C, D, E, F, G, H, I, J, K, L> extends Serializable {

    boolean test( A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l ) throws Exception;

    default Predicate12<A, B, C, D, E, F, G, H, I, J, K, L> negate() {
        return (a, b, c, d, e, f, g, h, i, j, k, l) -> !test( a, b, c, d, e, f, g, h, i, j, k, l );
    }

    class Impl<A, B, C, D, E, F, G, H, I, J, K, L> extends IntrospectableLambda implements Predicate12<A, B, C, D, E, F, G, H, I, J, K, L> {

        private final Predicate12<A, B, C, D, E, F, G, H, I, J, K, L> predicate;

        public Impl(Predicate12<A, B, C, D, E, F, G, H, I, J, K, L> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i, j, k, l);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
