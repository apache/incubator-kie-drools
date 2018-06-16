package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate6<A, B, C, D, E, F> extends Serializable {

    boolean test( A a, B b, C c, D d, E e, F f ) throws Exception;

    class Impl<A, B, C, D, E, F> extends IntrospectableLambda implements Predicate6<A, B, C, D, E, F> {

        private final Predicate6<A, B, C, D, E, F> predicate;

        public Impl(Predicate6<A, B, C, D, E, F> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f) throws Exception {
            return predicate.test(a, b, c, d, e, f);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
