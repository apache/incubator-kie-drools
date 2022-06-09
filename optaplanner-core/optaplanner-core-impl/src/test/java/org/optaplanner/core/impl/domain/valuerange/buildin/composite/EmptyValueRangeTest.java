package org.optaplanner.core.impl.domain.valuerange.buildin.composite;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.util.Random;

import org.junit.jupiter.api.Test;

class EmptyValueRangeTest {

    @Test
    void getSize() {
        assertThat(new EmptyValueRange<Integer>().getSize()).isEqualTo(0L);
    }

    @Test
    void get() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class)
                .isThrownBy(() -> new EmptyValueRange<Integer>().get(0L));
    }

    @Test
    void contains() {
        assertThat(new EmptyValueRange<Integer>().contains(5)).isFalse();
        assertThat(new EmptyValueRange<Integer>().contains(null)).isFalse();
    }

    @Test
    void createOriginalIterator() {
        assertAllElementsOfIterator(new EmptyValueRange<Integer>().createOriginalIterator());
    }

    @Test
    void createRandomIterator() {
        Random workingRandom = new Random(0);
        assertElementsOfIterator(new EmptyValueRange<Integer>().createRandomIterator(workingRandom));
    }

}
