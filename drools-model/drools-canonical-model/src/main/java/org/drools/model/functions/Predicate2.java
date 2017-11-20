package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate2<A, B> extends Serializable {
    boolean test(A a, B b);

    class Impl<A, B> extends IntrospectableLambda implements Predicate2<A, B> {

        private final Predicate2<A, B> predicate;

        public Impl(Predicate2<A, B> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b) {
            return predicate.test(a, b);
        }

        @Override
        protected Object getLambda() {
            return predicate;
        }
    }
}
