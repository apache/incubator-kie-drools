package org.optaplanner.core.impl.heuristic.selector.list;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SubListTest {

    @Test
    void equals() {
        SubList expected = new SubList("A", 3, 9);
        assertThat(new SubList("B", 3, 9)).isNotEqualTo(expected);
        assertThat(new SubList("A", 9, 3)).isNotEqualTo(expected);
        assertThat(new SubList("A", 9, 9)).isNotEqualTo(expected);
        assertThat(new SubList("A", 3, 3)).isNotEqualTo(expected);
        assertThat(new SubList("A", 3, 9)).isEqualTo(expected);
    }

    @Test
    void testToString() {
        assertThat(new SubList("A", 2, 5)).hasToString("A[2..7]");
    }
}
