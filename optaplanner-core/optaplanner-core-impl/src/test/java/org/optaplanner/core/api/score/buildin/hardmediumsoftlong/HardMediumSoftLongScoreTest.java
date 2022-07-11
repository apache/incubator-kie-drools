package org.optaplanner.core.api.score.buildin.hardmediumsoftlong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class HardMediumSoftLongScoreTest extends AbstractScoreTest {

    @Test
    void of() {
        assertThat(HardMediumSoftLongScore.ofHard(-147L)).isEqualTo(HardMediumSoftLongScore.of(-147L, 0L, 0L));
        assertThat(HardMediumSoftLongScore.ofMedium(-258L)).isEqualTo(HardMediumSoftLongScore.of(0L, -258L, 0L));
        assertThat(HardMediumSoftLongScore.ofSoft(-369L)).isEqualTo(HardMediumSoftLongScore.of(0L, 0L, -369L));
    }

    @Test
    void parseScore() {
        assertThat(HardMediumSoftLongScore.parseScore("-147hard/-258medium/-369soft"))
                .isEqualTo(HardMediumSoftLongScore.of(-147L, -258L, -369L));
        assertThat(HardMediumSoftLongScore.parseScore("-7init/-147hard/-258medium/-369soft"))
                .isEqualTo(HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L));
        assertThat(HardMediumSoftLongScore.parseScore("-147hard/-258medium/*soft"))
                .isEqualTo(HardMediumSoftLongScore.of(-147L, -258L, Long.MIN_VALUE));
        assertThat(HardMediumSoftLongScore.parseScore("-147hard/*medium/-369soft"))
                .isEqualTo(HardMediumSoftLongScore.of(-147L, Long.MIN_VALUE, -369L));
    }

    @Test
    void toShortString() {
        assertThat(HardMediumSoftLongScore.of(0L, 0L, 0L).toShortString()).isEqualTo("0");
        assertThat(HardMediumSoftLongScore.of(0L, 0L, -369L).toShortString()).isEqualTo("-369soft");
        assertThat(HardMediumSoftLongScore.of(0L, -258L, 0L).toShortString()).isEqualTo("-258medium");
        assertThat(HardMediumSoftLongScore.of(0L, -258L, -369L).toShortString()).isEqualTo("-258medium/-369soft");
        assertThat(HardMediumSoftLongScore.of(-147L, -258L, -369L).toShortString()).isEqualTo("-147hard/-258medium/-369soft");
        assertThat(HardMediumSoftLongScore.ofUninitialized(-7, 0L, -258L, 0L).toShortString()).isEqualTo("-7init/-258medium");
        assertThat(HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L).toShortString())
                .isEqualTo("-7init/-147hard/-258medium/-369soft");
    }

    @Test
    void testToString() {
        assertThat(HardMediumSoftLongScore.of(0L, -258L, -369L).toString()).isEqualTo("0hard/-258medium/-369soft");
        assertThat(HardMediumSoftLongScore.of(-147L, -258L, -369L).toString()).isEqualTo("-147hard/-258medium/-369soft");
        assertThat(HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L).toString())
                .isEqualTo("-7init/-147hard/-258medium/-369soft");
    }

    @Test
    void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> HardMediumSoftLongScore.parseScore("-147"));
    }

    @Test
    void withInitScore() {
        assertThat(HardMediumSoftLongScore.of(-147L, -258L, -369L).withInitScore(-7))
                .isEqualTo(HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L));
    }

    @Test
    void feasible() {
        assertScoreNotFeasible(
                HardMediumSoftLongScore.of(-5L, -300L, -4000L),
                HardMediumSoftLongScore.ofUninitialized(-7, -5L, -300L, -4000L),
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, -300L, -4000L));
        assertScoreFeasible(
                HardMediumSoftLongScore.of(0L, -300L, -4000L),
                HardMediumSoftLongScore.of(2L, -300L, -4000L),
                HardMediumSoftLongScore.ofUninitialized(0, 0L, -300L, -4000L));
    }

    @Test
    void add() {
        assertThat(HardMediumSoftLongScore.of(20L, -20L, -4000L).add(
                HardMediumSoftLongScore.of(-1L, -300L, 4000L))).isEqualTo(HardMediumSoftLongScore.of(19L, -320L, 0L));
        assertThat(HardMediumSoftLongScore.ofUninitialized(-70, 20L, -20L, -4000L).add(
                HardMediumSoftLongScore.ofUninitialized(-7, -1L, -300L, 4000L)))
                        .isEqualTo(HardMediumSoftLongScore.ofUninitialized(-77, 19L, -320L, 0L));
    }

    @Test
    void subtract() {
        assertThat(HardMediumSoftLongScore.of(20L, -20L, -4000L).subtract(
                HardMediumSoftLongScore.of(-1L, -300L, 4000L))).isEqualTo(HardMediumSoftLongScore.of(21L, 280L, -8000L));
        assertThat(HardMediumSoftLongScore.ofUninitialized(-70, 20L, -20L, -4000L).subtract(
                HardMediumSoftLongScore.ofUninitialized(-7, -1L, -300L, 4000L)))
                        .isEqualTo(HardMediumSoftLongScore.ofUninitialized(-63, 21L, 280L, -8000L));
    }

    @Test
    void multiply() {
        assertThat(HardMediumSoftLongScore.of(5L, -5L, 5L).multiply(1.2)).isEqualTo(HardMediumSoftLongScore.of(6L, -6L, 6L));
        assertThat(HardMediumSoftLongScore.of(1L, -1L, 1L).multiply(1.2)).isEqualTo(HardMediumSoftLongScore.of(1L, -2L, 1L));
        assertThat(HardMediumSoftLongScore.of(4L, -4L, 4L).multiply(1.2)).isEqualTo(HardMediumSoftLongScore.of(4L, -5L, 4L));
        assertThat(HardMediumSoftLongScore.ofUninitialized(-7, 4L, -5L, 6L).multiply(2.0))
                .isEqualTo(HardMediumSoftLongScore.ofUninitialized(-14, 8L, -10L, 12L));
    }

    @Test
    void divide() {
        assertThat(HardMediumSoftLongScore.of(25L, -25L, 25L).divide(5.0)).isEqualTo(HardMediumSoftLongScore.of(5L, -5L, 5L));
        assertThat(HardMediumSoftLongScore.of(21L, -21L, 21L).divide(5.0)).isEqualTo(HardMediumSoftLongScore.of(4L, -5L, 4L));
        assertThat(HardMediumSoftLongScore.of(24L, -24L, 24L).divide(5.0)).isEqualTo(HardMediumSoftLongScore.of(4L, -5L, 4L));
        assertThat(HardMediumSoftLongScore.ofUninitialized(-14, 8L, -10L, 12L).divide(2.0))
                .isEqualTo(HardMediumSoftLongScore.ofUninitialized(-7, 4L, -5L, 6L));
    }

    @Test
    void power() {
        assertThat(HardMediumSoftLongScore.of(3L, -4L, 5L).power(2.0)).isEqualTo(HardMediumSoftLongScore.of(9L, 16L, 25L));
        assertThat(HardMediumSoftLongScore.of(9L, 16L, 25L).power(0.5)).isEqualTo(HardMediumSoftLongScore.of(3L, 4L, 5L));
        assertThat(HardMediumSoftLongScore.ofUninitialized(-7, 3L, -4L, 5L).power(3.0))
                .isEqualTo(HardMediumSoftLongScore.ofUninitialized(-343, 27L, -64L, 125L));
    }

    @Test
    void negate() {
        assertThat(HardMediumSoftLongScore.of(3L, -4L, 5L).negate()).isEqualTo(HardMediumSoftLongScore.of(-3L, 4L, -5L));
        assertThat(HardMediumSoftLongScore.of(-3L, 4L, -5L).negate()).isEqualTo(HardMediumSoftLongScore.of(3L, -4L, 5L));
    }

    @Test
    void abs() {
        assertThat(HardMediumSoftLongScore.of(3L, 4L, 5L).abs()).isEqualTo(HardMediumSoftLongScore.of(3L, 4L, 5L));
        assertThat(HardMediumSoftLongScore.of(3L, -4L, 5L).abs()).isEqualTo(HardMediumSoftLongScore.of(3L, 4L, 5L));
        assertThat(HardMediumSoftLongScore.of(-3L, 4L, -5L).abs()).isEqualTo(HardMediumSoftLongScore.of(3L, 4L, 5L));
        assertThat(HardMediumSoftLongScore.of(-3L, -4L, -5L).abs()).isEqualTo(HardMediumSoftLongScore.of(3L, 4L, 5L));
    }

    @Test
    void zero() {
        HardMediumSoftLongScore manualZero = HardMediumSoftLongScore.of(0, 0, 0);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(manualZero.zero()).isEqualTo(manualZero);
            softly.assertThatObject(manualZero.isZero()).isEqualTo(true);
            HardMediumSoftLongScore manualOne = HardMediumSoftLongScore.of(0, 0, 1);
            softly.assertThat(manualOne.isZero())
                    .isEqualTo(false);
        });
    }

    @Test
    void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftLongScore.of(-10L, -200L, -3000L),
                HardMediumSoftLongScore.of(-10L, -200L, -3000L),
                HardMediumSoftLongScore.ofUninitialized(0, -10L, -200L, -3000L));
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftLongScore.ofUninitialized(-7, -10L, -200L, -3000L),
                HardMediumSoftLongScore.ofUninitialized(-7, -10L, -200L, -3000L));
        PlannerAssert.assertObjectsAreNotEqual(
                HardMediumSoftLongScore.of(-10L, -200L, -3000L),
                HardMediumSoftLongScore.of(-30L, -200L, -3000L),
                HardMediumSoftLongScore.of(-10L, -400L, -3000L),
                HardMediumSoftLongScore.of(-10L, -400L, -5000L),
                HardMediumSoftLongScore.ofUninitialized(-7, -10L, -200L, -3000L));
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardMediumSoftLongScore.ofUninitialized(-8, 0L, 0L, 0L),
                HardMediumSoftLongScore.ofUninitialized(-7, -20L, -20L, -20L),
                HardMediumSoftLongScore.ofUninitialized(-7, -1L, -300L, -4000L),
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, 0L, 0L),
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, 0L, 1L),
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, 1L, 0L),
                HardMediumSoftLongScore.of(-20L, Long.MIN_VALUE, Long.MIN_VALUE),
                HardMediumSoftLongScore.of(-20L, Long.MIN_VALUE, -20L),
                HardMediumSoftLongScore.of(-20L, Long.MIN_VALUE, 1L),
                HardMediumSoftLongScore.of(-20L, -300L, -4000L),
                HardMediumSoftLongScore.of(-20L, -300L, -300L),
                HardMediumSoftLongScore.of(-20L, -300L, -20L),
                HardMediumSoftLongScore.of(-20L, -300L, 300L),
                HardMediumSoftLongScore.of(-20L, -20L, -300L),
                HardMediumSoftLongScore.of(-20L, -20L, 0L),
                HardMediumSoftLongScore.of(-20L, -20L, 1L),
                HardMediumSoftLongScore.of(-1L, -300L, -4000L),
                HardMediumSoftLongScore.of(-1L, -300L, -20L),
                HardMediumSoftLongScore.of(-1L, -20L, -300L),
                HardMediumSoftLongScore.of(1L, Long.MIN_VALUE, -20L),
                HardMediumSoftLongScore.of(1L, -20L, Long.MIN_VALUE));
    }
}
