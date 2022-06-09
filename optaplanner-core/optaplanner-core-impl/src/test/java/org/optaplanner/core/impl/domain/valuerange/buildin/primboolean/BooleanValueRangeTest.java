package org.optaplanner.core.impl.domain.valuerange.buildin.primboolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testutil.TestRandom;

class BooleanValueRangeTest {

    @Test
    void getSize() {
        assertThat(new BooleanValueRange().getSize()).isEqualTo(2L);
    }

    @Test
    void get() {
        assertThat(new BooleanValueRange().get(0L)).isEqualTo(Boolean.FALSE);
        assertThat(new BooleanValueRange().get(1L)).isEqualTo(Boolean.TRUE);
    }

    @Test
    void contains() {
        assertThat(new BooleanValueRange().contains(Boolean.FALSE)).isTrue();
        assertThat(new BooleanValueRange().contains(Boolean.TRUE)).isTrue();
        assertThat(new BooleanValueRange().contains(null)).isFalse();
    }

    @Test
    void createOriginalIterator() {
        assertAllElementsOfIterator(new BooleanValueRange().createOriginalIterator(), Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    void createRandomIterator() {
        Random workingRandom = new TestRandom(true, true, false, true);
        assertElementsOfIterator(new BooleanValueRange().createRandomIterator(workingRandom),
                Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    void getIndexNegative() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> new BooleanValueRange().get(-1));
    }

    @Test
    void getIndexGreaterThanSize() {
        assertThatExceptionOfType(IndexOutOfBoundsException.class).isThrownBy(() -> new BooleanValueRange().get(2));
    }

}
