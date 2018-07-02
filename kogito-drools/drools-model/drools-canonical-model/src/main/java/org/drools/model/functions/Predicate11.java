package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate11<A, B, C, D, E, F, G, H, I, J, K> extends Serializable {

    boolean test( A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k ) throws Exception;

    class Impl<A, B, C, D, E, F, G, H, I, J, K> extends IntrospectableLambda implements Predicate11<A, B, C, D, E, F, G, H, I, J, K> {

        private final Predicate11<A, B, C, D, E, F, G, H, I, J, K> predicate;

        public Impl(Predicate11<A, B, C, D, E, F, G, H, I, J, K> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i, j, k);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
