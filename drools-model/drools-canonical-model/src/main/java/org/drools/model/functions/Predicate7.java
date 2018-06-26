package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate7<A, B, C, D, E, F, G> extends Serializable {

    boolean test( A a, B b, C c, D d, E e, F f, G g ) throws Exception;

    class Impl<A, B, C, D, E, F, G> extends IntrospectableLambda implements Predicate7<A, B, C, D, E, F, G> {

        private final Predicate7<A, B, C, D, E, F, G> predicate;

        public Impl(Predicate7<A, B, C, D, E, F, G> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g) throws Exception {
            return predicate.test(a, b, c, d, e, f, g);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }
    }
}
