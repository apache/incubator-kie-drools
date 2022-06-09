package org.optaplanner.core.impl.domain.valuerange.buildin.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange;
import org.optaplanner.core.impl.testutil.TestRandom;

class NullableCountableValueRangeTest {

    @Test
    void getSize() {
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).getSize()).isEqualTo(5L);
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8))).getSize())
                .isEqualTo(6L);
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(-15, 25, 0))).getSize()).isEqualTo(4L);
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).getSize())
                .isEqualTo(4L);
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Collections.emptyList())).getSize()).isEqualTo(1L);
    }

    @Test
    void get() {
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).get(2L).intValue())
                .isEqualTo(5);
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).get(4L)).isEqualTo(null);
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a", "c", "g", "d"))).get(3L))
                .isEqualTo("c");
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a", "c", "g", "d"))).get(6L))
                .isEqualTo(null);
    }

    @Test
    void contains() {
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).contains(5)).isTrue();
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).contains(4)).isFalse();
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).contains(null)).isTrue();
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).contains("a"))
                .isTrue();
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).contains("n"))
                .isFalse();
        assertThat(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).contains(null))
                .isTrue();
    }

    @Test
    void createOriginalIterator() {
        assertAllElementsOfIterator(
                new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5, 10))).createOriginalIterator(),
                null, 0, 2, 5, 10);
        assertAllElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(100, 120, 5, 7, 8)))
                .createOriginalIterator(), null, 100, 120, 5, 7, 8);
        assertAllElementsOfIterator(
                new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(-15, 25, 0))).createOriginalIterator(),
                null, -15, 25, 0);
        assertAllElementsOfIterator(
                new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a"))).createOriginalIterator(),
                null, "b", "z", "a");
        assertAllElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Collections.emptyList()))
                .createOriginalIterator(), new String[] { null });
    }

    @Test
    void createRandomIterator() {
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(0, 2, 5)))
                .createRandomIterator(new TestRandom(3, 0)), null, 0);
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(100, 120, 5)))
                .createRandomIterator(new TestRandom(3, 0)), null, 100);
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList(-15, 25, 0)))
                .createRandomIterator(new TestRandom(3, 0)), null, -15);
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Arrays.asList("b", "z", "a")))
                .createRandomIterator(new TestRandom(3, 0)), null, "b");
        assertElementsOfIterator(new NullableCountableValueRange<>(new ListValueRange<>(Collections.emptyList()))
                .createRandomIterator(new TestRandom(0)), new String[] { null });
    }

}
