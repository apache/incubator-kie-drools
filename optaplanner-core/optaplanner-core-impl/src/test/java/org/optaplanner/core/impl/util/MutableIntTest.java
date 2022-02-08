package org.optaplanner.core.impl.util;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class MutableIntTest {

    @Test
    void arithmetic() {
        MutableInt mutableInt = new MutableInt(1);
        int result = mutableInt.increment();
        assertSoftly(softly -> {
            softly.assertThat(result).isEqualTo(2);
            softly.assertThat(mutableInt.intValue()).isEqualTo(2);
        });
        int result2 = mutableInt.decrement();
        assertSoftly(softly -> {
            softly.assertThat(result2).isEqualTo(1);
            softly.assertThat(mutableInt.intValue()).isEqualTo(1);
        });
    }

    @Test
    void comparison() {
        MutableInt mutableInt1 = new MutableInt(1);
        MutableInt mutableInt2 = new MutableInt(2);
        assertSoftly(softly -> {
            softly.assertThat(mutableInt1)
                    .isEqualTo(mutableInt1);
            softly.assertThat(mutableInt1)
                    .isNotEqualTo(mutableInt2);
            softly.assertThat(mutableInt1)
                    .usingComparator(MutableInt::compareTo)
                    .isEqualByComparingTo(mutableInt1);
            softly.assertThat(mutableInt1)
                    .usingComparator(MutableInt::compareTo)
                    .isLessThan(mutableInt2);
            softly.assertThat(mutableInt2)
                    .usingComparator(MutableInt::compareTo)
                    .isGreaterThan(mutableInt1);
        });
    }

    @Test
    void values() {
        MutableInt mutableInt = new MutableInt(Integer.MAX_VALUE);
        assertSoftly(softly -> {
            softly.assertThat(mutableInt.intValue()).isEqualTo(Integer.MAX_VALUE);
            softly.assertThat(mutableInt.longValue()).isEqualTo(Integer.MAX_VALUE);
            softly.assertThat(mutableInt.floatValue()).isEqualTo(Integer.MAX_VALUE);
            softly.assertThat(mutableInt.doubleValue()).isEqualTo(Integer.MAX_VALUE);
        });
    }
}
