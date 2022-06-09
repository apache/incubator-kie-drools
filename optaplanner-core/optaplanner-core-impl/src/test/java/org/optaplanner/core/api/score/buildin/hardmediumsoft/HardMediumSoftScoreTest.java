package org.optaplanner.core.api.score.buildin.hardmediumsoft;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class HardMediumSoftScoreTest extends AbstractScoreTest {

    @Test
    void of() {
        assertThat(HardMediumSoftScore.ofHard(-147)).isEqualTo(HardMediumSoftScore.of(-147, 0, 0));
        assertThat(HardMediumSoftScore.ofMedium(-258)).isEqualTo(HardMediumSoftScore.of(0, -258, 0));
        assertThat(HardMediumSoftScore.ofSoft(-369)).isEqualTo(HardMediumSoftScore.of(0, 0, -369));
    }

    @Test
    void parseScore() {
        assertThat(HardMediumSoftScore.parseScore("-147hard/-258medium/-369soft"))
                .isEqualTo(HardMediumSoftScore.of(-147, -258, -369));
        assertThat(HardMediumSoftScore.parseScore("-7init/-147hard/-258medium/-369soft"))
                .isEqualTo(HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369));
        assertThat(HardMediumSoftScore.parseScore("-147hard/-258medium/*soft"))
                .isEqualTo(HardMediumSoftScore.of(-147, -258, Integer.MIN_VALUE));
        assertThat(HardMediumSoftScore.parseScore("-147hard/*medium/-369soft"))
                .isEqualTo(HardMediumSoftScore.of(-147, Integer.MIN_VALUE, -369));
    }

    @Test
    void toShortString() {
        assertThat(HardMediumSoftScore.of(0, 0, 0).toShortString()).isEqualTo("0");
        assertThat(HardMediumSoftScore.of(0, 0, -369).toShortString()).isEqualTo("-369soft");
        assertThat(HardMediumSoftScore.of(0, -258, 0).toShortString()).isEqualTo("-258medium");
        assertThat(HardMediumSoftScore.of(0, -258, -369).toShortString()).isEqualTo("-258medium/-369soft");
        assertThat(HardMediumSoftScore.of(-147, -258, -369).toShortString()).isEqualTo("-147hard/-258medium/-369soft");
        assertThat(HardMediumSoftScore.ofUninitialized(-7, 0, -258, 0).toShortString()).isEqualTo("-7init/-258medium");
        assertThat(HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369).toShortString())
                .isEqualTo("-7init/-147hard/-258medium/-369soft");
    }

    @Test
    void testToString() {
        assertThat(HardMediumSoftScore.of(0, -258, -369).toString()).isEqualTo("0hard/-258medium/-369soft");
        assertThat(HardMediumSoftScore.of(-147, -258, -369).toString()).isEqualTo("-147hard/-258medium/-369soft");
        assertThat(HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369).toString())
                .isEqualTo("-7init/-147hard/-258medium/-369soft");
    }

    @Test
    void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> HardMediumSoftScore.parseScore("-147"));
    }

    @Test
    void withInitScore() {
        assertThat(HardMediumSoftScore.of(-147, -258, -369).withInitScore(-7))
                .isEqualTo(HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369));
    }

    @Test
    void feasible() {
        assertScoreNotFeasible(
                HardMediumSoftScore.of(-5, -300, -4000),
                HardMediumSoftScore.ofUninitialized(-7, -5, -300, -4000),
                HardMediumSoftScore.ofUninitialized(-7, 0, -300, -4000));
        assertScoreFeasible(
                HardMediumSoftScore.of(0, -300, -4000),
                HardMediumSoftScore.of(2, -300, -4000),
                HardMediumSoftScore.ofUninitialized(0, 0, -300, -4000));
    }

    @Test
    void add() {
        assertThat(HardMediumSoftScore.of(20, -20, -4000).add(
                HardMediumSoftScore.of(-1, -300, 4000))).isEqualTo(HardMediumSoftScore.of(19, -320, 0));
        assertThat(HardMediumSoftScore.ofUninitialized(-70, 20, -20, -4000).add(
                HardMediumSoftScore.ofUninitialized(-7, -1, -300, 4000)))
                        .isEqualTo(HardMediumSoftScore.ofUninitialized(-77, 19, -320, 0));
    }

    @Test
    void subtract() {
        assertThat(HardMediumSoftScore.of(20, -20, -4000).subtract(
                HardMediumSoftScore.of(-1, -300, 4000))).isEqualTo(HardMediumSoftScore.of(21, 280, -8000));
        assertThat(HardMediumSoftScore.ofUninitialized(-70, 20, -20, -4000).subtract(
                HardMediumSoftScore.ofUninitialized(-7, -1, -300, 4000)))
                        .isEqualTo(HardMediumSoftScore.ofUninitialized(-63, 21, 280, -8000));
    }

    @Test
    void multiply() {
        assertThat(HardMediumSoftScore.of(5, -5, 5).multiply(1.2)).isEqualTo(HardMediumSoftScore.of(6, -6, 6));
        assertThat(HardMediumSoftScore.of(1, -1, 1).multiply(1.2)).isEqualTo(HardMediumSoftScore.of(1, -2, 1));
        assertThat(HardMediumSoftScore.of(4, -4, 4).multiply(1.2)).isEqualTo(HardMediumSoftScore.of(4, -5, 4));
        assertThat(HardMediumSoftScore.ofUninitialized(-7, 4, -5, 6).multiply(2.0))
                .isEqualTo(HardMediumSoftScore.ofUninitialized(-14, 8, -10, 12));
    }

    @Test
    void divide() {
        assertThat(HardMediumSoftScore.of(25, -25, 25).divide(5.0)).isEqualTo(HardMediumSoftScore.of(5, -5, 5));
        assertThat(HardMediumSoftScore.of(21, -21, 21).divide(5.0)).isEqualTo(HardMediumSoftScore.of(4, -5, 4));
        assertThat(HardMediumSoftScore.of(24, -24, 24).divide(5.0)).isEqualTo(HardMediumSoftScore.of(4, -5, 4));
        assertThat(HardMediumSoftScore.ofUninitialized(-14, 8, -10, 12).divide(2.0))
                .isEqualTo(HardMediumSoftScore.ofUninitialized(-7, 4, -5, 6));
    }

    @Test
    void power() {
        assertThat(HardMediumSoftScore.of(3, -4, 5).power(2.0)).isEqualTo(HardMediumSoftScore.of(9, 16, 25));
        assertThat(HardMediumSoftScore.of(9, 16, 25).power(0.5)).isEqualTo(HardMediumSoftScore.of(3, 4, 5));
        assertThat(HardMediumSoftScore.ofUninitialized(-7, 3, -4, 5).power(3.0))
                .isEqualTo(HardMediumSoftScore.ofUninitialized(-343, 27, -64, 125));
    }

    @Test
    void negate() {
        assertThat(HardMediumSoftScore.of(3, -4, 5).negate()).isEqualTo(HardMediumSoftScore.of(-3, 4, -5));
        assertThat(HardMediumSoftScore.of(-3, 4, -5).negate()).isEqualTo(HardMediumSoftScore.of(3, -4, 5));
    }

    @Test
    void zero() {
        HardMediumSoftScore manualZero = HardMediumSoftScore.of(0, 0, 0);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(manualZero.zero()).isEqualTo(manualZero);
            softly.assertThatObject(manualZero.isZero()).isEqualTo(true);
            HardMediumSoftScore manualOne = HardMediumSoftScore.of(0, 0, 1);
            softly.assertThat(manualOne.isZero())
                    .isEqualTo(false);
        });
    }

    @Test
    void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftScore.of(-10, -200, -3000),
                HardMediumSoftScore.of(-10, -200, -3000),
                HardMediumSoftScore.ofUninitialized(0, -10, -200, -3000));
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftScore.ofUninitialized(-7, -10, -200, -3000),
                HardMediumSoftScore.ofUninitialized(-7, -10, -200, -3000));
        PlannerAssert.assertObjectsAreNotEqual(
                HardMediumSoftScore.of(-10, -200, -3000),
                HardMediumSoftScore.of(-30, -200, -3000),
                HardMediumSoftScore.of(-10, -400, -3000),
                HardMediumSoftScore.of(-10, -400, -5000),
                HardMediumSoftScore.ofUninitialized(-7, -10, -200, -3000));
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardMediumSoftScore.ofUninitialized(-8, 0, 0, 0),
                HardMediumSoftScore.ofUninitialized(-7, -20, -20, -20),
                HardMediumSoftScore.ofUninitialized(-7, -1, -300, -4000),
                HardMediumSoftScore.ofUninitialized(-7, 0, 0, 0),
                HardMediumSoftScore.ofUninitialized(-7, 0, 0, 1),
                HardMediumSoftScore.ofUninitialized(-7, 0, 1, 0),
                HardMediumSoftScore.of(-20, Integer.MIN_VALUE, Integer.MIN_VALUE),
                HardMediumSoftScore.of(-20, Integer.MIN_VALUE, -20),
                HardMediumSoftScore.of(-20, Integer.MIN_VALUE, 1),
                HardMediumSoftScore.of(-20, -300, -4000),
                HardMediumSoftScore.of(-20, -300, -300),
                HardMediumSoftScore.of(-20, -300, -20),
                HardMediumSoftScore.of(-20, -300, 300),
                HardMediumSoftScore.of(-20, -20, -300),
                HardMediumSoftScore.of(-20, -20, 0),
                HardMediumSoftScore.of(-20, -20, 1),
                HardMediumSoftScore.of(-1, -300, -4000),
                HardMediumSoftScore.of(-1, -300, -20),
                HardMediumSoftScore.of(-1, -20, -300),
                HardMediumSoftScore.of(1, Integer.MIN_VALUE, -20),
                HardMediumSoftScore.of(1, -20, Integer.MIN_VALUE));
    }
}
