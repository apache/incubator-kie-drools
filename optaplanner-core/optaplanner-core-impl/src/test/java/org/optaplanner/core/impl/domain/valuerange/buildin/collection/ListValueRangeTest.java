package org.optaplanner.core.impl.domain.valuerange.buildin.collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testutil.TestRandom;

class ListValueRangeTest {

    @Test
    void getSize() {
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).getSize()).isEqualTo(4L);
        assertThat(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).getSize()).isEqualTo(5L);
        assertThat(new ListValueRange<>(Arrays.asList(-15, 25, 0)).getSize()).isEqualTo(3L);
        assertThat(new ListValueRange<>(Arrays.asList("b", "z", "a")).getSize()).isEqualTo(3L);
        assertThat(new ListValueRange<>(Collections.emptyList()).getSize()).isEqualTo(0L);
    }

    @Test
    void get() {
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).get(2L).intValue()).isEqualTo(5);
        assertThat(new ListValueRange<>(Arrays.asList(100, -120)).get(1L).intValue()).isEqualTo(-120);
        assertThat(new ListValueRange<>(Arrays.asList("b", "z", "a", "c", "g", "d")).get(3L)).isEqualTo("c");
    }

    @Test
    void contains() {
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(5)).isTrue();
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(4)).isFalse();
        assertThat(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).contains(null)).isFalse();
        assertThat(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).contains(7)).isTrue();
        assertThat(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).contains(9)).isFalse();
        assertThat(new ListValueRange<>(Arrays.asList(-15, 25, 0)).contains(-15)).isTrue();
        assertThat(new ListValueRange<>(Arrays.asList(-15, 25, 0)).contains(-14)).isFalse();
        assertThat(new ListValueRange<>(Arrays.asList("b", "z", "a")).contains("a")).isTrue();
        assertThat(new ListValueRange<>(Arrays.asList("b", "z", "a")).contains("n")).isFalse();
    }

    @Test
    void createOriginalIterator() {
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).createOriginalIterator(), 0, 2, 5, 10);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).createOriginalIterator(), 100, 120,
                5, 7, 8);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList(-15, 25, 0)).createOriginalIterator(), -15, 25, 0);
        assertAllElementsOfIterator(new ListValueRange<>(Arrays.asList("b", "z", "a")).createOriginalIterator(), "b", "z", "a");
        assertAllElementsOfIterator(new ListValueRange<>(Collections.emptyList()).createOriginalIterator());
    }

    @Test
    void createRandomIterator() {
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList(0, 2, 5, 10)).createRandomIterator(new TestRandom(2, 0)), 5,
                0);
        assertElementsOfIterator(
                new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)).createRandomIterator(new TestRandom(2, 0)), 5,
                100);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList(-15, 25, 0)).createRandomIterator(new TestRandom(2, 0)), 0,
                -15);
        assertElementsOfIterator(new ListValueRange<>(Arrays.asList("b", "z", "a")).createRandomIterator(new TestRandom(2, 0)),
                "a",
                "b");
        assertAllElementsOfIterator(new ListValueRange<>(Collections.emptyList()).createRandomIterator(new Random(0)));
    }

}
