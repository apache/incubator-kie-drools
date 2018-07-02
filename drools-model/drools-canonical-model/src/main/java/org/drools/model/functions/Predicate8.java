package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate8<A, B, C, D, E, F, G, H> extends Serializable {

    boolean test( A a, B b, C c, D d, E e, F f, G g, H h ) throws Exception;

    class Impl<A, B, C, D, E, F, G, H> extends IntrospectableLambda implements Predicate8<A, B, C, D, E, F, G, H> {

        private final Predicate8<A, B, C, D, E, F, G, H> predicate;

        public Impl(Predicate8<A, B, C, D, E, F, G, H> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
