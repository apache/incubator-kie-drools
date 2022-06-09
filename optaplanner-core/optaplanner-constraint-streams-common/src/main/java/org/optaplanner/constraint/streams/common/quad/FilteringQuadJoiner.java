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
