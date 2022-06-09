package org.optaplanner.constraint.streams.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.score.stream.JoinerType.CONTAINING;
import static org.optaplanner.core.impl.score.stream.JoinerType.DISJOINT;
import static org.optaplanner.core.impl.score.stream.JoinerType.EQUAL;
import static org.optaplanner.core.impl.score.stream.JoinerType.GREATER_THAN;
import static org.optaplanner.core.impl.score.stream.JoinerType.GREATER_THAN_OR_EQUAL;
import static org.optaplanner.core.impl.score.stream.JoinerType.INTERSECTING;
import static org.optaplanner.core.impl.score.stream.JoinerType.LESS_THAN;
import static org.optaplanner.core.impl.score.stream.JoinerType.LESS_THAN_OR_EQUAL;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class JoinerTypeTest {

    @Test
    void equal() {
        assertThat(EQUAL.matches(1, 1)).isTrue();
        assertThat(EQUAL.matches(1, 2)).isFalse();
        assertThat(EQUAL.matches(1, null)).isFalse();
        assertThat(EQUAL.matches(null, 1)).isFalse();
    }

    @Test
    void lessThan() {
        assertThat(LESS_THAN.matches(1, 1)).isFalse();
        assertThat(LESS_THAN.matches(1, 2)).isTrue();
        assertThat(LESS_THAN.matches(2, 1)).isFalse();
    }

    @Test
    void lessThanOrEquals() {
        assertThat(LESS_THAN_OR_EQUAL.matches(1, 1)).isTrue();
        assertThat(LESS_THAN_OR_EQUAL.matches(1, 2)).isTrue();
        assertThat(LESS_THAN_OR_EQUAL.matches(2, 1)).isFalse();
    }

    @Test
    void greaterThan() {
        assertThat(GREATER_THAN.matches(1, 1)).isFalse();
        assertThat(GREATER_THAN.matches(2, 1)).isTrue();
        assertThat(GREATER_THAN.matches(1, 2)).isFalse();
    }

    @Test
    void greaterThanOrEquals() {
        assertThat(GREATER_THAN_OR_EQUAL.matches(1, 1)).isTrue();
        assertThat(GREATER_THAN_OR_EQUAL.matches(2, 1)).isTrue();
        assertThat(GREATER_THAN_OR_EQUAL.matches(1, 2)).isFalse();
    }

    @Test
    void containing() {
        Collection<Integer> collection = Arrays.asList(1);
        assertThat(CONTAINING.matches(collection, 1)).isTrue();
        assertThat(CONTAINING.matches(collection, 2)).isFalse();
    }

    @Test
    void intersecting() {
        Collection<Integer> left = Arrays.asList(1, 2, 3);
        Collection<Integer> right = Arrays.asList(3, 4, 5);
        assertThat(INTERSECTING.matches(left, right)).isTrue();
        assertThat(INTERSECTING.matches(right, left)).isTrue();
        assertThat(INTERSECTING.matches(left, Collections.emptySet())).isFalse();
    }

    @Test
    void disjoint() {
        Collection<Integer> first = Arrays.asList(1, 2, 3);
        Collection<Integer> second = Arrays.asList(3, 4, 5);
        assertThat(DISJOINT.matches(first, second)).isFalse();
        assertThat(DISJOINT.matches(second, first)).isFalse();
        Collection<Integer> third = Arrays.asList(4, 5);
        assertThat(DISJOINT.matches(first, third)).isTrue();
        assertThat(DISJOINT.matches(third, first)).isTrue();
        // empty sets are disjoint
        assertThat(DISJOINT.matches(Collections.emptyList(), Collections.emptySet())).isTrue();
        assertThat(DISJOINT.matches(first, Collections.emptySet())).isTrue();
    }
}
