package org.drools.model;

import org.drools.model.functions.Function1;

public interface Index<A, V> {
    enum IndexType {
        ALPHA, BETA;
    }

    enum ConstraintType {
        EQUAL,
        NOT_EQUAL,
        GREATER_THAN,
        GREATER_OR_EQUAL,
        LESS_THAN,
        LESS_OR_EQUAL,
        RANGE,
        FORALL_SELF_JOIN,
        UNKNOWN;

        public ConstraintType negate() {
            switch (this) {
                case FORALL_SELF_JOIN:
                case EQUAL:
                    return NOT_EQUAL;
                case NOT_EQUAL:
                    return EQUAL;
                case GREATER_THAN:
                    return LESS_OR_EQUAL;
                case GREATER_OR_EQUAL:
                    return LESS_THAN;
                case LESS_OR_EQUAL:
                    return GREATER_THAN;
                case LESS_THAN:
                    return GREATER_OR_EQUAL;
            }
            return UNKNOWN;
        }
    }

    IndexType getIndexType();

    Class<V> getIndexedClass();

    ConstraintType getConstraintType();

    int getIndexId();

    Function1<A, V> getLeftOperandExtractor();

    Index<A, V> negate();
}
