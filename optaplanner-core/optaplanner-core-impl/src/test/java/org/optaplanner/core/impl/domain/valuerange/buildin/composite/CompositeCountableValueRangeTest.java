package org.optaplanner.core.impl.domain.valuerange.buildin.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.domain.valuerange.buildin.collection.ListValueRange;
import org.optaplanner.core.impl.testutil.TestRandom;

class CompositeCountableValueRangeTest {

    @SafeVarargs
    private static <T> CompositeCountableValueRange<T> createValueRange(List<T>... lists) {
        List<ListValueRange<T>> childValueRangeList = new ArrayList<>(lists.length);
        for (List<T> list : lists) {
            childValueRangeList.add(new ListValueRange<>(list));
        }
        return new CompositeCountableValueRange<>(childValueRangeList);
    }

    @Test
    void getSize() {
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).getSize()).isEqualTo(7L);
        assertThat(createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).getSize()).isEqualTo(4L);
        assertThat(createValueRange(Collections.emptyList()).getSize()).isEqualTo(0L);
    }

    @Test
    void get() {
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).get(2L).intValue()).isEqualTo(5);
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).get(4L).intValue()).isEqualTo(-15);
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).get(6L).intValue()).isEqualTo(-1);
        assertThat(createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).get(2L)).isEqualTo("c");
    }

    @Test
    void contains() {
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).contains(5)).isTrue();
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).contains(4)).isFalse();
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).contains(-15)).isTrue();
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).contains(-14)).isFalse();
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).contains(-1)).isTrue();
        assertThat(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1)).contains(1)).isFalse();
        assertThat(createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).contains("c")).isTrue();
        assertThat(createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d")).contains("n")).isFalse();
    }

    @Test
    void createOriginalIterator() {
        assertAllElementsOfIterator(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1))
                .createOriginalIterator(), 0, 2, 5, 10, -15, 25, -1);
        assertAllElementsOfIterator(createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d"))
                .createOriginalIterator(), "a", "b", "c", "d");
        assertAllElementsOfIterator(createValueRange(Collections.emptyList())
                .createOriginalIterator());
    }

    @Test
    void createRandomIterator() {
        assertElementsOfIterator(createValueRange(Arrays.asList(0, 2, 5, 10), Arrays.asList(-15, 25, -1))
                .createRandomIterator(new TestRandom(3, 0)), 10, 0);
        assertElementsOfIterator(createValueRange(Arrays.asList("a", "b"), Arrays.asList("c"), Arrays.asList("d"))
                .createRandomIterator(new TestRandom(3, 0)), "d", "a");
        assertElementsOfIterator(createValueRange(Collections.emptyList())
                .createRandomIterator(new TestRandom(0)));
    }

}
