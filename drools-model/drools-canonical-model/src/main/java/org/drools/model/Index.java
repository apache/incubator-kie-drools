/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model;

import java.util.Objects;
import java.util.function.BiPredicate;

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
        EXISTS_PROTOTYPE_FIELD,
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

        public boolean canInverse() {
            switch (this) {
                case EQUAL:
                case NOT_EQUAL:
                case GREATER_THAN:
                case GREATER_OR_EQUAL:
                case LESS_THAN:
                case LESS_OR_EQUAL:
                    return true;
                default:
                    return false;
            }
        }

        public <T, V> BiPredicate<T, V> asPredicate() {
            switch (this) {
                case EQUAL:
                    return (t,v) -> Objects.equals(t, v);
                case NOT_EQUAL:
                    return (t,v) -> !Objects.equals(t, v);
                case GREATER_THAN:
                    return (t,v) -> t != null && ((Comparable) t).compareTo(v) > 0;
                case GREATER_OR_EQUAL:
                    return (t,v) -> t != null && ((Comparable) t).compareTo(v) >= 0;
                case LESS_THAN:
                    return (t,v) -> t != null && ((Comparable) t).compareTo(v) < 0;
                case LESS_OR_EQUAL:
                    return (t,v) -> t != null && ((Comparable) t).compareTo(v) <= 0;
                default:
                    throw new UnsupportedOperationException("Cannot convert " + this + " into a predicate");
            }
        }

        public ConstraintType inverse() {
            switch (this) {
                case GREATER_THAN:
                    return LESS_THAN;
                case GREATER_OR_EQUAL:
                    return LESS_OR_EQUAL;
                case LESS_THAN:
                    return GREATER_THAN;
                case LESS_OR_EQUAL:
                    return GREATER_OR_EQUAL;
                default:
                    return this;
            }
        }

        public boolean isComparison() {
            return isAscending() || isDescending();
        }

        public boolean isAscending() {
            return this == GREATER_THAN || this == GREATER_OR_EQUAL;
        }

        public boolean isDescending() {
            return this == LESS_THAN || this == LESS_OR_EQUAL;
        }
    }

    IndexType getIndexType();

    Class<V> getIndexedClass();

    ConstraintType getConstraintType();

    int getIndexId();

    Function1<A, V> getLeftOperandExtractor();

    Index<A, V> negate();
}
