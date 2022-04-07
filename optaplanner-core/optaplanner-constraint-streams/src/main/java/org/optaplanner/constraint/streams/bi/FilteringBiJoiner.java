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

package org.optaplanner.constraint.streams.bi;

import java.util.Objects;
import java.util.function.BiPredicate;

import org.optaplanner.core.api.score.stream.bi.BiJoiner;

public final class FilteringBiJoiner<A, B> implements BiJoiner<A, B> {

    private final BiPredicate<A, B> filter;

    public FilteringBiJoiner(BiPredicate<A, B> filter) {
        this.filter = filter;
    }

    @Override
    public FilteringBiJoiner<A, B> and(BiJoiner<A, B> otherJoiner) {
        FilteringBiJoiner<A, B> castJoiner = (FilteringBiJoiner<A, B>) otherJoiner;
        return new FilteringBiJoiner<>(filter.and(castJoiner.getFilter()));
    }

    public BiPredicate<A, B> getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilteringBiJoiner)) {
            return false;
        }
        FilteringBiJoiner<?, ?> other = (FilteringBiJoiner<?, ?>) o;
        return Objects.equals(filter, other.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter);
    }
}
