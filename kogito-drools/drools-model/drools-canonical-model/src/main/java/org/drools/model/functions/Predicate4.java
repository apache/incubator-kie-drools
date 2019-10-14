package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate4<A, B, C, D> extends Serializable {

    boolean test( A a, B b, C c, D d ) throws Exception;

    default Predicate4<A, B, C, D> negate() {
        return (a, b, c, d) -> !test( a, b, c, d );
    }

    class Impl<A, B, C, D> extends IntrospectableLambda implements Predicate4<A, B, C, D> {

        private final Predicate4<A, B, C, D> predicate;

        public Impl(Predicate4<A, B, C, D> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d) throws Exception {
            return predicate.test(a, b, c, d);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
