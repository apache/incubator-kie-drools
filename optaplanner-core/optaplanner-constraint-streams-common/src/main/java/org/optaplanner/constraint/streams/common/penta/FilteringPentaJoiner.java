package org.optaplanner.constraint.streams.common.penta;

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
