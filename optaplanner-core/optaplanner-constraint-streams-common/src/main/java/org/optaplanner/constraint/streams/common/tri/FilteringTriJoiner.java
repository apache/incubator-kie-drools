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
