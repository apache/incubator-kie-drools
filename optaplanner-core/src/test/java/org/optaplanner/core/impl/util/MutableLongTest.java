package org.optaplanner.core.impl.util;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;

class MutableLongTest {

    @Test
    void arithmetic() {
        MutableLong mutableLong = new MutableLong(1);
        long result = mutableLong.increment();
        assertSoftly(softly -> {
            softly.assertThat(result).isEqualTo(2);
            softly.assertThat(mutableLong.longValue()).isEqualTo(2L);
        });
        long result2 = mutableLong.decrement();
        assertSoftly(softly -> {
            softly.assertThat(result2).isEqualTo(1);
            softly.assertThat(mutableLong.longValue()).isEqualTo(1L);
        });
    }

    @Test
    void comparison() {
        MutableLong mutableLong1 = new MutableLong(1);
        MutableLong mutableLong2 = new MutableLong(2);
        assertSoftly(softly -> {
            softly.assertThat(mutableLong1)
                    .isEqualTo(mutableLong1);
            softly.assertThat(mutableLong1)
                    .isNotEqualTo(mutableLong2);
            softly.assertThat(mutableLong1)
                    .usingComparator(MutableLong::compareTo)
                    .isEqualByComparingTo(mutableLong1);
            softly.assertThat(mutableLong1)
                    .usingComparator(MutableLong::compareTo)
                    .isLessThan(mutableLong2);
            softly.assertThat(mutableLong2)
                    .usingComparator(MutableLong::compareTo)
                    .isGreaterThan(mutableLong1);
        });
    }

    @Test
    void values() {
        MutableLong mutableLong = new MutableLong(Long.MAX_VALUE);
        assertSoftly(softly -> {
            softly.assertThat(mutableLong.intValue()).isEqualTo(-1); // Cast.
            softly.assertThat(mutableLong.longValue()).isEqualTo(Long.MAX_VALUE);
            softly.assertThat(mutableLong.floatValue()).isEqualTo(Long.MAX_VALUE);
            softly.assertThat(mutableLong.doubleValue()).isEqualTo(Long.MAX_VALUE);
        });
    }

}
