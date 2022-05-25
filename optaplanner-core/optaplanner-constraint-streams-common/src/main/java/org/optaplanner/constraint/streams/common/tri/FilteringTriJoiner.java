/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.common.tri;

import java.util.Objects;

import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;

public final class FilteringTriJoiner<A, B, C> implements TriJoiner<A, B, C> {

    private final TriPredicate<A, B, C> filter;

    public FilteringTriJoiner(TriPredicate<A, B, C> filter) {
        this.filter = filter;
    }

    @Override
    public FilteringTriJoiner<A, B, C> and(TriJoiner<A, B, C> otherJoiner) {
        FilteringTriJoiner<A, B, C> castJoiner = (FilteringTriJoiner<A, B, C>) otherJoiner;
        return new FilteringTriJoiner<>(filter.and(castJoiner.getFilter()));
    }

    public TriPredicate<A, B, C> getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilteringTriJoiner)) {
            return false;
        }
        FilteringTriJoiner<?, ?, ?> other = (FilteringTriJoiner<?, ?, ?>) o;
        return Objects.equals(filter, other.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter);
    }
}
