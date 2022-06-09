package org.optaplanner.core.api.score.buildin.hardsoft;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class HardSoftScoreTest extends AbstractScoreTest {

    @Test
    void of() {
        assertThat(HardSoftScore.ofHard(-147)).isEqualTo(HardSoftScore.of(-147, 0));
        assertThat(HardSoftScore.ofSoft(-258)).isEqualTo(HardSoftScore.of(0, -258));
    }

    @Test
    void parseScore() {
        assertThat(HardSoftScore.parseScore("-147hard/-258soft")).isEqualTo(HardSoftScore.of(-147, -258));
        assertThat(HardSoftScore.parseScore("-7init/-147hard/-258soft"))
                .isEqualTo(HardSoftScore.ofUninitialized(-7, -147, -258));
        assertThat(HardSoftScore.parseScore("-147hard/*soft")).isEqualTo(HardSoftScore.of(-147, Integer.MIN_VALUE));
    }

    @Test
    void toShortString() {
        assertThat(HardSoftScore.of(0, 0).toShortString()).isEqualTo("0");
        assertThat(HardSoftScore.of(0, -258).toShortString()).isEqualTo("-258soft");
        assertThat(HardSoftScore.of(-147, 0).toShortString()).isEqualTo("-147hard");
        assertThat(HardSoftScore.of(-147, -258).toShortString()).isEqualTo("-147hard/-258soft");
        assertThat(HardSoftScore.ofUninitialized(-7, 0, 0).toShortString()).isEqualTo("-7init");
        assertThat(HardSoftScore.ofUninitialized(-7, 0, -258).toShortString()).isEqualTo("-7init/-258soft");
        assertThat(HardSoftScore.ofUninitialized(-7, -147, -258).toShortString()).isEqualTo("-7init/-147hard/-258soft");
    }

    @Test
    void testToString() {
        assertThat(HardSoftScore.of(0, -258).toString()).isEqualTo("0hard/-258soft");
        assertThat(HardSoftScore.of(-147, -258).toString()).isEqualTo("-147hard/-258soft");
        assertThat(HardSoftScore.ofUninitialized(-7, -147, -258).toString()).isEqualTo("-7init/-147hard/-258soft");
    }

    @Test
    void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> HardSoftScore.parseScore("-147"));
    }

    @Test
    void withInitScore() {
        assertThat(HardSoftScore.of(-147, -258).withInitScore(-7)).isEqualTo(HardSoftScore.ofUninitialized(-7, -147, -258));
    }

    @Test
    void feasible() {
        assertScoreNotFeasible(
                HardSoftScore.of(-5, -300),
                HardSoftScore.ofUninitialized(-7, -5, -300),
                HardSoftScore.ofUninitialized(-7, 0, -300));
        assertScoreFeasible(
                HardSoftScore.of(0, -300),
                HardSoftScore.of(2, -300),
                HardSoftScore.ofUninitialized(0, 0, -300));
    }

    @Test
    void add() {
        assertThat(HardSoftScore.of(20, -20).add(
                HardSoftScore.of(-1, -300))).isEqualTo(HardSoftScore.of(19, -320));
        assertThat(HardSoftScore.ofUninitialized(-70, 20, -20).add(
                HardSoftScore.ofUninitialized(-7, -1, -300))).isEqualTo(HardSoftScore.ofUninitialized(-77, 19, -320));
    }

    @Test
    void subtract() {
        assertThat(HardSoftScore.of(20, -20).subtract(
                HardSoftScore.of(-1, -300))).isEqualTo(HardSoftScore.of(21, 280));
        assertThat(HardSoftScore.ofUninitialized(-70, 20, -20).subtract(
                HardSoftScore.ofUninitialized(-7, -1, -300))).isEqualTo(HardSoftScore.ofUninitialized(-63, 21, 280));
    }

    @Test
    void multiply() {
        assertThat(HardSoftScore.of(5, -5).multiply(1.2)).isEqualTo(HardSoftScore.of(6, -6));
        assertThat(HardSoftScore.of(1, -1).multiply(1.2)).isEqualTo(HardSoftScore.of(1, -2));
        assertThat(HardSoftScore.of(4, -4).multiply(1.2)).isEqualTo(HardSoftScore.of(4, -5));
        assertThat(HardSoftScore.ofUninitialized(-7, 4, -5).multiply(2.0))
                .isEqualTo(HardSoftScore.ofUninitialized(-14, 8, -10));
    }

    @Test
    void divide() {
        assertThat(HardSoftScore.of(25, -25).divide(5.0)).isEqualTo(HardSoftScore.of(5, -5));
        assertThat(HardSoftScore.of(21, -21).divide(5.0)).isEqualTo(HardSoftScore.of(4, -5));
        assertThat(HardSoftScore.of(24, -24).divide(5.0)).isEqualTo(HardSoftScore.of(4, -5));
        assertThat(HardSoftScore.ofUninitialized(-14, 8, -10).divide(2.0)).isEqualTo(HardSoftScore.ofUninitialized(-7, 4, -5));
    }

    @Test
    void power() {
        assertThat(HardSoftScore.of(-4, 5).power(2.0)).isEqualTo(HardSoftScore.of(16, 25));
        assertThat(HardSoftScore.of(16, 25).power(0.5)).isEqualTo(HardSoftScore.of(4, 5));
        assertThat(HardSoftScore.ofUninitialized(-7, 4, 5).power(3.0)).isEqualTo(HardSoftScore.ofUninitialized(-343, 64, 125));
    }

    @Test
    void negate() {
        assertThat(HardSoftScore.of(-4, 5).negate()).isEqualTo(HardSoftScore.of(4, -5));
        assertThat(HardSoftScore.of(4, -5).negate()).isEqualTo(HardSoftScore.of(-4, 5));
    }

    @Test
    void zero() {
        HardSoftScore manualZero = HardSoftScore.of(0, 0);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(manualZero.zero()).isEqualTo(manualZero);
            softly.assertThatObject(manualZero.isZero()).isEqualTo(true);
            HardSoftScore manualOne = HardSoftScore.of(0, 1);
            softly.assertThat(manualOne.isZero())
                    .isEqualTo(false);
        });
    }

    @Test
    void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardSoftScore.of(-10, -200),
                HardSoftScore.of(-10, -200),
                HardSoftScore.ofUninitialized(0, -10, -200));
        PlannerAssert.assertObjectsAreEqual(
                HardSoftScore.ofUninitialized(-7, -10, -200),
                HardSoftScore.ofUninitialized(-7, -10, -200));
        PlannerAssert.assertObjectsAreNotEqual(
                HardSoftScore.of(-10, -200),
                HardSoftScore.of(-30, -200),
                HardSoftScore.of(-10, -400),
                HardSoftScore.ofUninitialized(-7, -10, -200));
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftScore.ofUninitialized(-8, 0, 0),
                HardSoftScore.ofUninitialized(-7, -20, -20),
                HardSoftScore.ofUninitialized(-7, -1, -300),
                HardSoftScore.ofUninitialized(-7, 0, 0),
                HardSoftScore.ofUninitialized(-7, 0, 1),
                HardSoftScore.of(-20, Integer.MIN_VALUE),
                HardSoftScore.of(-20, -20),
                HardSoftScore.of(-1, -300),
                HardSoftScore.of(-1, 4000),
                HardSoftScore.of(0, -1),
                HardSoftScore.of(0, 0),
                HardSoftScore.of(0, 1));
    }
}
