package org.optaplanner.constraint.streams.common.bi;

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
