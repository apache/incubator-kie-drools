package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate5<A, B, C, D, E> extends Serializable {

    boolean test( A a, B b, C c, D d, E e) throws Exception;

    default Predicate5<A, B, C, D, E> negate() {
        return (a, b, c, d, e) -> !test( a, b, c, d, e );
    }

    class Impl<A, B, C, D, E> extends IntrospectableLambda implements Predicate5<A, B, C, D, E> {

        private final Predicate5<A, B, C, D, E> predicate;

        public Impl(Predicate5<A, B, C, D, E> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e) throws Exception {
            return predicate.test(a, b, c, d, e);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
