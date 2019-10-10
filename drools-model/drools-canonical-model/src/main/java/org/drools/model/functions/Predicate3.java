package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate3<A, B, C> extends Serializable {

    boolean test(A a, B b, C c) throws Exception;

    default Predicate3<A, B, C> negate() {
        return (a, b, c) -> !test( a, b, c );
    }

    class Impl<A, B, C> extends IntrospectableLambda implements Predicate3<A, B, C> {

        private final Predicate3<A, B, C> predicate;

        public Impl(Predicate3<A, B, C> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c) throws Exception {
            return predicate.test(a, b, c);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
