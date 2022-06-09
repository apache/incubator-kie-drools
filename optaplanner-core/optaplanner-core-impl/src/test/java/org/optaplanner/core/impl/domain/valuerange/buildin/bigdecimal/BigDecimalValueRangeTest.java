package org.optaplanner.core.impl.domain.valuerange.buildin.bigdecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertAllElementsOfIterator;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertElementsOfIterator;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.impl.testutil.TestRandom;

class BigDecimalValueRangeTest {

    @Test
    void getSize() {
        assertThat(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).getSize()).isEqualTo(10L);
        assertThat(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).getSize()).isEqualTo(200L);
        assertThat(new BigDecimalValueRange(new BigDecimal("-15.00"), new BigDecimal("25.07")).getSize()).isEqualTo(4007L);
        assertThat(new BigDecimalValueRange(new BigDecimal("7.0"), new BigDecimal("7.0")).getSize()).isEqualTo(0L);
        // IncrementUnit
        assertThat(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0")).getSize())
                .isEqualTo(5L);
        assertThat(new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0")).getSize())
                .isEqualTo(5L);
        assertThat(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1")).getSize())
                .isEqualTo(4L);
    }

    @Test
    void get() {
        assertThat(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).get(3L)).isEqualTo(new BigDecimal("3"));
        assertThat(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).get(3L))
                .isEqualTo(new BigDecimal("100.3"));
        assertThat(new BigDecimalValueRange(new BigDecimal("-5"), new BigDecimal("25")).get(1L))
                .isEqualTo(new BigDecimal("-4"));
        assertThat(new BigDecimalValueRange(new BigDecimal("-5.00"), new BigDecimal("25.00")).get(6L))
                .isEqualTo(new BigDecimal("-4.94"));
        // IncrementUnit
        assertThat(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0")).get(3L))
                .isEqualTo(new BigDecimal("6.0"));
        assertThat(new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0")).get(3L))
                .isEqualTo(new BigDecimal("5.0"));
        assertThat(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1")).get(3L))
                .isEqualTo(new BigDecimal("115.3"));
    }

    @Test
    void contains() {
        assertThat(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).contains(new BigDecimal("3"))).isTrue();
        assertThat(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).contains(new BigDecimal("10")))
                .isFalse();
        assertThat(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("10")).contains(null)).isFalse();
        assertThat(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).contains(new BigDecimal("100.0")))
                .isTrue();
        assertThat(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.0")).contains(new BigDecimal("99.9")))
                .isFalse();
        assertThat(new BigDecimalValueRange(new BigDecimal("-5.3"), new BigDecimal("25.2")).contains(new BigDecimal("-5.2")))
                .isTrue();
        assertThat(new BigDecimalValueRange(new BigDecimal("-5.3"), new BigDecimal("25.2")).contains(new BigDecimal("-5.4")))
                .isFalse();
        // IncrementUnit
        assertThat(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0"))
                .contains(new BigDecimal("2.0"))).isTrue();
        assertThat(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0"))
                .contains(new BigDecimal("3.0"))).isFalse();
        assertThat(new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0"))
                .contains(new BigDecimal("1.0"))).isTrue();
        assertThat(new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0"))
                .contains(new BigDecimal("2.0"))).isFalse();
        assertThat(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1"))
                .contains(new BigDecimal("115.3"))).isTrue();
        assertThat(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1"))
                .contains(new BigDecimal("115.0"))).isFalse();
    }

    @Test
    void createOriginalIterator() {
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("4"))
                .createOriginalIterator(), new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("104.0"))
                .createOriginalIterator(), new BigDecimal("100.0"), new BigDecimal("100.1"), new BigDecimal("100.2"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-4.00"), new BigDecimal("3.00"))
                .createOriginalIterator(), new BigDecimal("-4.00"), new BigDecimal("-3.99"), new BigDecimal("-3.98"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("7"), new BigDecimal("7"))
                .createOriginalIterator());
        // IncrementUnit
        assertAllElementsOfIterator(
                new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0"))
                        .createOriginalIterator(),
                new BigDecimal("0.0"), new BigDecimal("2.0"), new BigDecimal("4.0"),
                new BigDecimal("6.0"), new BigDecimal("8.0"));
        assertAllElementsOfIterator(
                new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0"))
                        .createOriginalIterator(),
                new BigDecimal("-1.0"), new BigDecimal("1.0"), new BigDecimal("3.0"),
                new BigDecimal("5.0"), new BigDecimal("7.0"));
        assertAllElementsOfIterator(
                new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1"))
                        .createOriginalIterator(),
                new BigDecimal("100.0"), new BigDecimal("105.1"), new BigDecimal("110.2"),
                new BigDecimal("115.3"));
    }

    @Test
    void createRandomIterator() {
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0"), new BigDecimal("4"))
                .createRandomIterator(new TestRandom(3, 0)), new BigDecimal("3"), new BigDecimal("0"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("104.0"))
                .createRandomIterator(new TestRandom(3, 0)), new BigDecimal("100.3"), new BigDecimal("100.0"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-4.00"), new BigDecimal("3.00"))
                .createRandomIterator(new TestRandom(3, 0)), new BigDecimal("-3.97"), new BigDecimal("-4.00"));
        assertAllElementsOfIterator(new BigDecimalValueRange(new BigDecimal("7"), new BigDecimal("7"))
                .createRandomIterator(new TestRandom(3, 0)));
        // IncrementUnit
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("0.0"), new BigDecimal("10.0"), new BigDecimal("2.0"))
                .createRandomIterator(new TestRandom(3, 0)), new BigDecimal("6.0"), new BigDecimal("0.0"));
        assertElementsOfIterator(new BigDecimalValueRange(new BigDecimal("-1.0"), new BigDecimal("9.0"), new BigDecimal("2.0"))
                .createRandomIterator(new TestRandom(3, 0)), new BigDecimal("5.0"), new BigDecimal("-1.0"));
        assertElementsOfIterator(
                new BigDecimalValueRange(new BigDecimal("100.0"), new BigDecimal("120.4"), new BigDecimal("5.1"))
                        .createRandomIterator(new TestRandom(3, 0)),
                new BigDecimal("115.3"), new BigDecimal("100.0"));
    }

}
