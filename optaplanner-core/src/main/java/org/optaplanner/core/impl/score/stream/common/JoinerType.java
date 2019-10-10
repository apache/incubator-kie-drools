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
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.score.stream.common;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiPredicate;

public enum JoinerType {
    EQUAL(Objects::equals),
    LESS_THAN((a, b) -> ((Comparable) a).compareTo(b) < 0),
    LESS_THAN_OR_EQUAL((a, b) -> ((Comparable) a).compareTo(b) <= 0),
    GREATER_THAN((a, b) -> ((Comparable) a).compareTo(b) > 0),
    GREATER_THAN_OR_EQUAL((a, b) -> ((Comparable) a).compareTo(b) >= 0),
    CONTAINING((a, b) -> ((Collection) a).contains(b)),
    INTERSECTING((a, b) -> intersecting((Collection) a, (Collection) b)),
    DISJOINT((a, b) -> disjoint((Collection) a, (Collection) b));

    private final BiPredicate<Object, Object> matcher;

    JoinerType(BiPredicate<Object, Object> matcher) {
        this.matcher = matcher;
    }

    public JoinerType flip() {
        switch (this) {
            case LESS_THAN:
                return GREATER_THAN;
            case LESS_THAN_OR_EQUAL:
                return GREATER_THAN_OR_EQUAL;
            case GREATER_THAN:
                return LESS_THAN;
            case GREATER_THAN_OR_EQUAL:
                return LESS_THAN_OR_EQUAL;
            default:
                throw new IllegalStateException("The joinerType (" + this + ") cannot be flipped.");
        }
    }

    public boolean matches(Object left, Object right) {
        return matcher.test(left, right);
    }

    private static boolean disjoint(Collection leftCollection, Collection rightCollection) {
        return leftCollection.stream().noneMatch(rightCollection::contains) &&
                rightCollection.stream().noneMatch(leftCollection::contains);
    }

    private static boolean intersecting(Collection leftCollection, Collection rightCollection) {
        return leftCollection.stream().anyMatch(rightCollection::contains) ||
                rightCollection.stream().anyMatch(leftCollection::contains);
    }

}
