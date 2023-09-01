package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate1<A> extends Serializable {
    boolean test(A a) throws Exception;

    default Predicate1<A> negate() {
        return a -> !test( a );
    }

    default PredicateInformation predicateInformation() { return PredicateInformation.EMPTY_PREDICATE_INFORMATION; }

    class Impl<A> extends IntrospectableLambda implements Predicate1<A> {

        private final Predicate1<A> predicate;

        public Impl(Predicate1<A> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a) throws Exception {
            return predicate.test(a);
        }

        @Override
        public Object getLambda() {
            return predicate;
        }

        @Override
        public PredicateInformation predicateInformation() {
            return predicate.predicateInformation();
        }
    }
}
