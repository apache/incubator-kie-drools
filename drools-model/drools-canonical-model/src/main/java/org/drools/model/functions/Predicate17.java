package org.drools.model.functions;

import java.io.Serializable;

public interface Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> extends Serializable {

    boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o, P p, Q q) throws Exception;

    default Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> negate() {
        return (a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q) -> !test(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q);
    }

    default PredicateInformation predicateInformation() { return PredicateInformation.EMPTY_PREDICATE_INFORMATION; }

    class Impl<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> extends IntrospectableLambda implements Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> {

        private final Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> predicate;

        public Impl(Predicate17<A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q> predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean test(A a, B b, C c, D d, E e, F f, G g, H h, I i, J j, K k, L l, M m, N n, O o, P p, Q q) throws Exception {
            return predicate.test(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q);
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
