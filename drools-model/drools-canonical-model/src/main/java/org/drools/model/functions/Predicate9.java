package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate9<A, B, C, D, E, F, G, H, I> extends Serializable {

    boolean test( A a, B b, C c, D d, E e, F f, G g, H h, I i ) throws Exception;

    default Predicate9<A, B, C, D, E, F, G, H, I> negate() {
        return (a, b, c, d, e, f, g, h, i) -> !test( a, b, c, d, e, f, g, h, i );
    }

    class Impl<A, B, C, D, E, F, G, H, I> extends IntrospectableLambda implements Predicate9<A, B, C, D, E, F, G, H, I> {

        private final Predicate9<A, B, C, D, E, F, G, H, I> predicate;

        public Impl(Predicate9<A, B, C, D, E, F, G, H, I> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
