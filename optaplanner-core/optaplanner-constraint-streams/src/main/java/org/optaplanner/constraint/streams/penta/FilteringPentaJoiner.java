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

package org.optaplanner.constraint.streams.penta;

import java.util.Objects;

import org.optaplanner.core.api.function.PentaPredicate;
import org.optaplanner.core.api.score.stream.penta.PentaJoiner;

public final class FilteringPentaJoiner<A, B, C, D, E> implements PentaJoiner<A, B, C, D, E> {

    private final PentaPredicate<A, B, C, D, E> filter;

    public FilteringPentaJoiner(PentaPredicate<A, B, C, D, E> filter) {
        this.filter = filter;
    }

    @Override
    public FilteringPentaJoiner<A, B, C, D, E> and(PentaJoiner<A, B, C, D, E> otherJoiner) {
        FilteringPentaJoiner<A, B, C, D, E> castJoiner = (FilteringPentaJoiner<A, B, C, D, E>) otherJoiner;
        return new FilteringPentaJoiner<>(filter.and(castJoiner.getFilter()));
    }

    public PentaPredicate<A, B, C, D, E> getFilter() {
        return filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FilteringPentaJoiner)) {
            return false;
        }
        FilteringPentaJoiner<?, ?, ?, ?, ?> other = (FilteringPentaJoiner<?, ?, ?, ?, ?>) o;
        return Objects.equals(filter, other.filter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filter);
    }
}
