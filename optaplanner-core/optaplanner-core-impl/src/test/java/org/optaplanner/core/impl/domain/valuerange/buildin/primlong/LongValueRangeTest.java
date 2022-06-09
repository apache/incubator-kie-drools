package org.optaplanner.core.impl.domain.valuerange.buildin.primlong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testutil.TestRandom;

class LongValueRangeTest {

    @Test
    void getSize() {
        assertThat(new LongValueRange(0L, 10L).getSize()).isEqualTo(10L);
        assertThat(new LongValueRange(100L, 120L).getSize()).isEqualTo(20L);
        assertThat(new LongValueRange(-15L, 25L).getSize()).isEqualTo(40L);
        assertThat(new LongValueRange(7L, 7L).getSize()).isEqualTo(0L);
        assertThat(new LongValueRange(-1000L, Long.MAX_VALUE - 3000L).getSize()).isEqualTo(Long.MAX_VALUE - 2000L);
        // IncrementUnit
        assertThat(new LongValueRange(0L, 10L, 2L).getSize()).isEqualTo(5L);
        assertThat(new LongValueRange(-1L, 9L, 2L).getSize()).isEqualTo(5L);
        assertThat(new LongValueRange(100L, 120L, 5L).getSize()).isEqualTo(4L);
    }

    @Test
    void get() {
        assertThat((long) new LongValueRange(0L, 10L).get(3L).intValue()).isEqualTo(3L);
        assertThat((long) new LongValueRange(100L, 120L).get(3L).intValue()).isEqualTo(103L);
        assertThat((long) new LongValueRange(-5L, 25L).get(1L).intValue()).isEqualTo(-4L);
        assertThat((long) new LongValueRange(-5L, 25L).get(6L).intValue()).isEqualTo(1L);
        assertThat((long) new LongValueRange(-1000L, Long.MAX_VALUE - 3000L).get(1004L).intValue()).isEqualTo(4L);
        // IncrementUnit
        assertThat((long) new LongValueRange(0L, 10L, 2L).get(3L).intValue()).isEqualTo(6L);
        assertThat((long) new LongValueRange(-1L, 9L, 2L).get(3L).intValue()).isEqualTo(5L);
        assertThat((long) new LongValueRange(100L, 120L, 5L).get(3L).intValue()).isEqualTo(115L);
    }

    @Test
    void contains() {
        assertThat(new LongValueRange(0L, 10L).contains(3L)).isTrue();
        assertThat(new LongValueRange(0L, 10L).contains(10L)).isFalse();
        assertThat(new LongValueRange(0L, 10L).contains(null)).isFalse();
        assertThat(new LongValueRange(100L, 120L).contains(100L)).isTrue();
        assertThat(new LongValueRange(100L, 120L).contains(99L)).isFalse();
        assertThat(new LongValueRange(-5L, 25L).contains(-4L)).isTrue();
        assertThat(new LongValueRange(-5L, 25L).contains(-20L)).isFalse();
        // IncrementUnit
        assertThat(new LongValueRange(0L, 10L, 2L).contains(2L)).isTrue();
        assertThat(new LongValueRange(0L, 10L, 2L).contains(3L)).isFalse();
        assertThat(new LongValueRange(-1L, 9L, 2L).contains(1L)).isTrue();
        assertThat(new LongValueRange(-1L, 9L, 2L).contains(2L)).isFalse();
        assertThat(new LongValueRange(100L, 120L, 5L).contains(115L)).isTrue();
        assertThat(new LongValueRange(100L, 120L, 5L).contains(114L)).isFalse();
    }

    @Test
    void createOriginalIterator() {
        assertAllElementsOfIterator(new LongValueRange(0L, 7L).createOriginalIterator(), 0L, 1L, 2L, 3L, 4L, 5L, 6L);
        assertAllElementsOfIterator(new LongValueRange(100L, 104L).createOriginalIterator(), 100L, 101L, 102L, 103L);
        assertAllElementsOfIterator(new LongValueRange(-4L, 3L).createOriginalIterator(), -4L, -3L, -2L, -1L, 0L, 1L, 2L);
        assertAllElementsOfIterator(new LongValueRange(7L, 7L).createOriginalIterator());
        // IncrementUnit
        assertAllElementsOfIterator(new LongValueRange(0L, 10L, 2L).createOriginalIterator(), 0L, 2L, 4L, 6L, 8L);
        assertAllElementsOfIterator(new LongValueRange(-1L, 9L, 2L).createOriginalIterator(), -1L, 1L, 3L, 5L, 7L);
        assertAllElementsOfIterator(new LongValueRange(100L, 120L, 5L).createOriginalIterator(), 100L, 105L, 110L, 115L);
    }

    @Test
    void createRandomIterator() {
        assertElementsOfIterator(new LongValueRange(0L, 7L).createRandomIterator(new TestRandom(3, 0)), 3L, 0L);
        assertElementsOfIterator(new LongValueRange(100L, 104L).createRandomIterator(new TestRandom(3, 0)), 103L, 100L);
        assertElementsOfIterator(new LongValueRange(-4L, 3L).createRandomIterator(new TestRandom(3, 0)), -1L, -4L);
        assertAllElementsOfIterator(new LongValueRange(7L, 7L).createRandomIterator(new TestRandom(0)));
        // IncrementUnit
        assertElementsOfIterator(new LongValueRange(0L, 10L, 2L).createRandomIterator(new TestRandom(3, 0)), 6L, 0L);
        assertElementsOfIterator(new LongValueRange(-1L, 9L, 2L).createRandomIterator(new TestRandom(3, 0)), 5L, -1L);
        assertElementsOfIterator(new LongValueRange(100L, 120L, 5L).createRandomIterator(new TestRandom(3, 0)), 115L, 100L);
    }

}
