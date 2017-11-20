package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate1<A> extends Serializable {
    boolean test(A a);

    class Impl<A> extends IntrospectableLambda implements Predicate1<A> {

        private final Predicate1<A> predicate;

        public Impl(Predicate1<A> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a) {
            return predicate.test(a);
        }

        @Override
        protected Object getLambda() {
            return predicate;
        }
    }
}
