package org.optaplanner.core.api.score.buildin.hardsoftbigdecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class HardSoftBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    void of() {
        assertThat(HardSoftBigDecimalScore.ofHard(new BigDecimal("-147.2")))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("0.0")));
        assertThat(HardSoftBigDecimalScore.ofSoft(new BigDecimal("-3.2")))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-0.0"), new BigDecimal("-3.2")));
    }

    @Test
    void parseScore() {
        assertThat(HardSoftBigDecimalScore.parseScore("-147.2hard/-258.3soft"))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")));
        assertThat(HardSoftBigDecimalScore.parseScore("-7init/-147.2hard/-258.3soft"))
                .isEqualTo(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")));
    }

    @Test
    void toShortString() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("0.0")).toShortString()).isEqualTo("0");
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("-258.3")).toShortString())
                .isEqualTo("-258.3soft");
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("0.0")).toShortString())
                .isEqualTo("-147.2hard");
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")).toShortString())
                .isEqualTo("-147.2hard/-258.3soft");
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0.0"), new BigDecimal("0.0")).toShortString())
                .isEqualTo("-7init");
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0.0"), new BigDecimal("-258.3")).toShortString())
                .isEqualTo("-7init/-258.3soft");
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3"))
                .toShortString()).isEqualTo("-7init/-147.2hard/-258.3soft");
    }

    @Test
    void testToString() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("-258.3")).toString())
                .isEqualTo("0.0hard/-258.3soft");
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")).toString())
                .isEqualTo("-147.2hard/-258.3soft");
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")).toString())
                .isEqualTo("-7init/-147.2hard/-258.3soft");
    }

    @Test
    void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> HardSoftBigDecimalScore.parseScore("-147.2"));
    }

    @Test
    void withInitScore() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")).withInitScore(-7))
                .isEqualTo(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")));
    }

    @Test
    void feasible() {
        assertScoreNotFeasible(
                HardSoftBigDecimalScore.of(new BigDecimal("-5"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.of(new BigDecimal("-5"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.of(new BigDecimal("-0.007"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-5"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("-300")));
        assertScoreFeasible(
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("-300.007")),
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.of(new BigDecimal("2"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.ofUninitialized(0, new BigDecimal("0"), new BigDecimal("-300")));
    }

    @Test
    void add() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("20"), new BigDecimal("-20")).add(
                HardSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("-300"))))
                        .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("19"), new BigDecimal("-320")));
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-70, new BigDecimal("20"), new BigDecimal("-20")).add(
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-300"))))
                        .isEqualTo(HardSoftBigDecimalScore.ofUninitialized(-77, new BigDecimal("19"), new BigDecimal("-320")));
    }

    @Test
    void subtract() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("20"), new BigDecimal("-20")).subtract(
                HardSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("-300"))))
                        .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("21"), new BigDecimal("280")));
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-70, new BigDecimal("20"), new BigDecimal("-20")).subtract(
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-300"))))
                        .isEqualTo(HardSoftBigDecimalScore.ofUninitialized(-63, new BigDecimal("21"), new BigDecimal("280")));
    }

    @Test
    void multiply() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("5.0"), new BigDecimal("-5.0")).multiply(1.2))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("6.0"), new BigDecimal("-6.0")));
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("1.0"), new BigDecimal("-1.0")).multiply(1.2))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("1.2"), new BigDecimal("-1.2")));
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-4.0")).multiply(1.2))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("4.8"), new BigDecimal("-4.8")));
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-5.2")).multiply(2.0))
                .isEqualTo(HardSoftBigDecimalScore.ofUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-10.4")));
    }

    @Test
    void divide() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("25.0"), new BigDecimal("-25.0")).divide(5.0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("5.0"), new BigDecimal("-5.0")));
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("21.0"), new BigDecimal("-21.0")).divide(5.0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("4.2"), new BigDecimal("-4.2")));
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("24.0"), new BigDecimal("-24.0")).divide(5.0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("4.8"), new BigDecimal("-4.8")));
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-10.4")).divide(2.0))
                .isEqualTo(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-5.2")));
    }

    @Test
    void power() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("5.0")).power(2.0))
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("16.0"), new BigDecimal("25.0")));
        assertThat(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-4.0"), new BigDecimal("5.0")).power(3.0))
                .isEqualTo(HardSoftBigDecimalScore.ofUninitialized(-343, new BigDecimal("-64.0"), new BigDecimal("125.0")));
    }

    @Test
    void negate() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-5.0")).negate())
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("5.0")));
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("5.0")).negate())
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-5.0")));
    }

    @Test
    void abs() {
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("5.0")).abs())
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("5.0")));
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-5.0")).abs())
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("5.0")));
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("5.0")).abs())
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("5.0")));
        assertThat(HardSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("-5.0")).abs())
                .isEqualTo(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("5.0")));
    }

    @Test
    void zero() {
        HardSoftBigDecimalScore manualZero = HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ZERO);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(manualZero.zero()).isEqualTo(manualZero);
            softly.assertThatObject(manualZero.isZero()).isEqualTo(true);
            HardSoftBigDecimalScore manualOne = HardSoftBigDecimalScore.of(BigDecimal.ZERO, BigDecimal.ONE);
            softly.assertThat(manualOne.isZero())
                    .isEqualTo(false);
        });
    }

    @Test
    void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-10.000"), new BigDecimal("-200.000")),
                HardSoftBigDecimalScore.ofUninitialized(0, new BigDecimal("-10.0"), new BigDecimal("-200.0")));
        PlannerAssert.assertObjectsAreEqual(
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0")));
        PlannerAssert.assertObjectsAreNotEqual(
                HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-30.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-400.0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0")));
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftBigDecimalScore.ofUninitialized(-8, new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-20"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("1")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20.06"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20.007"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("-20.06")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("-20.007")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("-1")),
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("1")));
    }
}
