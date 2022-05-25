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

package org.optaplanner.constraint.streams.common.quad;

import java.util.Objects;

import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;

public final class FilteringQuadJoiner<A, B, C, D> implements QuadJoiner<A, B, C, D> {

    private final QuadPredicate<A, B, C, D> filter;

    public FilteringQuadJoiner(QuadPredicate<A, B, C, D> filter) {
        this.filter = filter;
    }

    @Override
    public FilteringQuadJoiner<A, B, C, D> and(QuadJoiner<A, B, C, D> otherJoiner) {
        FilteringQuadJoiner<A, B, C, D> castJoiner = (FilteringQuadJoiner<A, B, C, D>) otherJoiner;
        return new FilteringQuadJoiner<>(filter.and(castJoiner.getFilter()));
    }

    public QuadPredicate<A, B, C, D> getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilteringQuadJoiner)) {
            return false;
        }
        FilteringQuadJoiner<?, ?, ?, ?> other = (FilteringQuadJoiner<?, ?, ?, ?>) o;
        return Objects.equals(filter, other.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFilter());
    }
}
