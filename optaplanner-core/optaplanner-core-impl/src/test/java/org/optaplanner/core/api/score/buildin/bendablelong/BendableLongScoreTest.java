package org.optaplanner.core.api.score.buildin.bendablelong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.score.buildin.BendableLongScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class BendableLongScoreTest extends AbstractScoreTest {

    private BendableLongScoreDefinition scoreDefinitionHSS = new BendableLongScoreDefinition(1, 2);

    @Test
    void of() {
        assertThat(BendableLongScore.ofHard(1, 2, 0, -147L)).isEqualTo(scoreDefinitionHSS.createScore(-147L, 0L, 0L));
        assertThat(BendableLongScore.ofSoft(1, 2, 0, -258L)).isEqualTo(scoreDefinitionHSS.createScore(0L, -258L, 0L));
        assertThat(BendableLongScore.ofSoft(1, 2, 1, -369L)).isEqualTo(scoreDefinitionHSS.createScore(0L, 0L, -369L));
    }

    @Test
    void parseScore() {
        assertThat(scoreDefinitionHSS.parseScore("[-5432109876]hard/[-9876543210/-3456789012]soft"))
                .isEqualTo(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L));
        assertThat(scoreDefinitionHSS.parseScore("-7init/[-5432109876]hard/[-9876543210/-3456789012]soft"))
                .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L));
        assertThat(scoreDefinitionHSS.parseScore("[-5432109876]hard/[-9876543210/*]soft"))
                .isEqualTo(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, Long.MIN_VALUE));
        assertThat(scoreDefinitionHSS.parseScore("[-5432109876]hard/[*/-3456789012]soft"))
                .isEqualTo(scoreDefinitionHSS.createScore(-5432109876L, Long.MIN_VALUE, -3456789012L));
    }

    @Test
    void toShortString() {
        assertThat(scoreDefinitionHSS.createScore(0L, 0L, -3456789012L).toShortString()).isEqualTo("[0/-3456789012]soft");
        assertThat(scoreDefinitionHSS.createScore(0L, -9876543210L, -3456789012L).toShortString())
                .isEqualTo("[-9876543210/-3456789012]soft");
        assertThat(scoreDefinitionHSS.createScore(-5432109876L, 0L, -0L).toShortString()).isEqualTo("[-5432109876]hard");
        assertThat(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L).toShortString())
                .isEqualTo("[-5432109876]hard/[-9876543210/-3456789012]soft");
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L).toShortString())
                .isEqualTo("-7init/[-5432109876]hard/[-9876543210/-3456789012]soft");
    }

    @Test
    void testToString() {
        assertThat(scoreDefinitionHSS.createScore(0L, -9876543210L, -3456789012L).toString())
                .isEqualTo("[0]hard/[-9876543210/-3456789012]soft");
        assertThat(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L).toString())
                .isEqualTo("[-5432109876]hard/[-9876543210/-3456789012]soft");
        assertThat(new BendableLongScoreDefinition(2, 1).createScore(-5432109876L, -9876543210L, -3456789012L).toString())
                .isEqualTo("[-5432109876/-9876543210]hard/[-3456789012]soft");
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L).toString())
                .isEqualTo("-7init/[-5432109876]hard/[-9876543210/-3456789012]soft");
        assertThat(new BendableLongScoreDefinition(0, 0).createScore().toString()).isEqualTo("[]hard/[]soft");
    }

    @Test
    void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> scoreDefinitionHSS.parseScore("-147"));
    }

    @Test
    void getHardOrSoftScore() {
        BendableLongScore initializedScore = scoreDefinitionHSS.createScore(-5L, -10L, -200L);
        assertThat(initializedScore.getHardOrSoftScore(0)).isEqualTo(-5L);
        assertThat(initializedScore.getHardOrSoftScore(1)).isEqualTo(-10L);
        assertThat(initializedScore.getHardOrSoftScore(2)).isEqualTo(-200L);
    }

    @Test
    void withInitScore() {
        assertThat(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L).withInitScore(-7))
                .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L));
    }

    @Test
    void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScore(-5L, -300L, -9876543210L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -5L, -300L, -9876543210L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 0L, -300L, -9876543210L));
        assertScoreFeasible(
                scoreDefinitionHSS.createScore(0L, -300L, -9876543210L),
                scoreDefinitionHSS.createScore(2L, -300L, -9876543210L),
                scoreDefinitionHSS.createScoreUninitialized(0, 0L, -300L, -9876543210L));
    }

    @Test
    void addHSS() {
        assertThat(scoreDefinitionHSS.createScore(1111111111L, -20L, -9876543210L).add(
                scoreDefinitionHSS.createScore(2222222222L, -300L, 9876543210L)))
                        .isEqualTo(scoreDefinitionHSS.createScore(3333333333L, -320L, 0L));
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-70, 1111111111L, -20L, -9876543210L).add(
                scoreDefinitionHSS.createScoreUninitialized(-7, 2222222222L, -300L, 9876543210L)))
                        .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-77, 3333333333L, -320L, 0L));
    }

    @Test
    void subtractHSS() {
        assertThat(scoreDefinitionHSS.createScore(3333333333L, -20L, -5555555555L).subtract(
                scoreDefinitionHSS.createScore(1111111111L, -300L, 3333333333L)))
                        .isEqualTo(scoreDefinitionHSS.createScore(2222222222L, 280L, -8888888888L));
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-70, 3333333333L, -20L, -5555555555L).subtract(
                scoreDefinitionHSS.createScoreUninitialized(-7, 1111111111L, -300L, 3333333333L)))
                        .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-63, 2222222222L, 280L, -8888888888L));
    }

    @Test
    void multiplyHSS() {
        assertThat(scoreDefinitionHSS.createScore(5000000000L, -5000000000L, 5000000000L).multiply(1.2))
                .isEqualTo(scoreDefinitionHSS.createScore(6000000000L, -6000000000L, 6000000000L));
        assertThat(scoreDefinitionHSS.createScore(1L, -1L, 1L).multiply(1.2))
                .isEqualTo(scoreDefinitionHSS.createScore(1L, -2L, 1L));
        assertThat(scoreDefinitionHSS.createScore(4L, -4L, 4L).multiply(1.2))
                .isEqualTo(scoreDefinitionHSS.createScore(4L, -5L, 4L));
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-7, 4L, -5L, 6L).multiply(2.0))
                .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-14, 8L, -10L, 12L));
    }

    @Test
    void divideHSS() {
        assertThat(scoreDefinitionHSS.createScore(25000000000L, -25000000000L, 25000000000L).divide(5.0))
                .isEqualTo(scoreDefinitionHSS.createScore(5000000000L, -5000000000L, 5000000000L));
        assertThat(scoreDefinitionHSS.createScore(21L, -21L, 21L).divide(5.0))
                .isEqualTo(scoreDefinitionHSS.createScore(4L, -5L, 4L));
        assertThat(scoreDefinitionHSS.createScore(24L, -24L, 24L).divide(5.0))
                .isEqualTo(scoreDefinitionHSS.createScore(4L, -5L, 4L));
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-14, 8L, -10L, 12L).divide(2.0))
                .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-7, 4L, -5L, 6L));
    }

    @Test
    void powerHSS() {
        assertThat(scoreDefinitionHSS.createScore(300000L, -400000L, 500000L).power(2.0))
                .isEqualTo(scoreDefinitionHSS.createScore(90000000000L, 160000000000L, 250000000000L));
        assertThat(scoreDefinitionHSS.createScore(90000000000L, 160000000000L, 250000000000L).power(0.5))
                .isEqualTo(scoreDefinitionHSS.createScore(300000L, 400000L, 500000L));
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-7, 3L, -4L, 5L).power(3.0))
                .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-343, 27L, -64L, 125L));
    }

    @Test
    void negateHSS() {
        assertThat(scoreDefinitionHSS.createScore(3000000000L, -4000000000L, 5000000000L).negate())
                .isEqualTo(scoreDefinitionHSS.createScore(-3000000000L, 4000000000L, -5000000000L));
        assertThat(scoreDefinitionHSS.createScore(-3000000000L, 4000000000L, -5000000000L).negate())
                .isEqualTo(scoreDefinitionHSS.createScore(3000000000L, -4000000000L, 5000000000L));
    }

    @Test
    void absHSS() {
        assertThat(scoreDefinitionHSS.createScore(3000000000L, 4000000000L, 5000000000L).abs())
                .isEqualTo(scoreDefinitionHSS.createScore(3000000000L, 4000000000L, 5000000000L));
        assertThat(scoreDefinitionHSS.createScore(3000000000L, -4000000000L, 5000000000L).abs())
                .isEqualTo(scoreDefinitionHSS.createScore(3000000000L, 4000000000L, 5000000000L));
        assertThat(scoreDefinitionHSS.createScore(-3000000000L, 4000000000L, -5000000000L).abs())
                .isEqualTo(scoreDefinitionHSS.createScore(3000000000L, 4000000000L, 5000000000L));
        assertThat(scoreDefinitionHSS.createScore(-3000000000L, -4000000000L, -5000000000L).abs())
                .isEqualTo(scoreDefinitionHSS.createScore(3000000000L, 4000000000L, 5000000000L));
    }

    @Test
    void zero() {
        BendableLongScore manualZero = BendableLongScore.zero(0, 1);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(manualZero.zero()).isEqualTo(manualZero);
            softly.assertThatObject(manualZero.isZero()).isEqualTo(true);
            BendableLongScore manualOne = BendableLongScore.ofSoft(0, 1, 0, 1);
            softly.assertThat(manualOne.isZero())
                    .isEqualTo(false);
        });
    }

    @Test
    void equalsAndHashCodeHSS() {
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHSS.createScore(-10L, -200L, -3000L),
                scoreDefinitionHSS.createScore(-10L, -200L, -3000L),
                scoreDefinitionHSS.createScoreUninitialized(0, -10L, -200L, -3000L));
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHSS.createScoreUninitialized(-7, -10L, -200L, -3000L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -10L, -200L, -3000L));
        PlannerAssert.assertObjectsAreNotEqual(
                scoreDefinitionHSS.createScore(-10L, -200L, -3000L),
                scoreDefinitionHSS.createScore(-30L, -200L, -3000L),
                scoreDefinitionHSS.createScore(-10L, -400L, -3000L),
                scoreDefinitionHSS.createScore(-10L, -400L, -5000L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -10L, -200L, -3000L));
    }

    @Test
    void compareToHSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHSS.createScoreUninitialized(-8, 0L, 0L, 0L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -20L, -20L, -20L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -1L, -300L, -4000L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 0L, 0L, 0L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 0L, 0L, 1L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 0L, 1L, 0L),
                scoreDefinitionHSS.createScore(-20L, Long.MIN_VALUE, Long.MIN_VALUE),
                scoreDefinitionHSS.createScore(-20L, Long.MIN_VALUE, -20L),
                scoreDefinitionHSS.createScore(-20L, Long.MIN_VALUE, 1L),
                scoreDefinitionHSS.createScore(-20L, -300L, -4000L),
                scoreDefinitionHSS.createScore(-20L, -300L, -300L),
                scoreDefinitionHSS.createScore(-20L, -300L, -20L),
                scoreDefinitionHSS.createScore(-20L, -300L, 300L),
                scoreDefinitionHSS.createScore(-20L, -20L, -300L),
                scoreDefinitionHSS.createScore(-20L, -20L, 0L),
                scoreDefinitionHSS.createScore(-20L, -20L, 1L),
                scoreDefinitionHSS.createScore(-1L, -300L, -4000L),
                scoreDefinitionHSS.createScore(-1L, -300L, -20L),
                scoreDefinitionHSS.createScore(-1L, -20L, -300L),
                scoreDefinitionHSS.createScore(1L, Long.MIN_VALUE, -20L),
                scoreDefinitionHSS.createScore(1L, -20L, Long.MIN_VALUE));
    }

    private BendableLongScoreDefinition scoreDefinitionHHSSS = new BendableLongScoreDefinition(2, 3);

    @Test
    void feasibleHHSSS() {
        assertScoreNotFeasible(
                scoreDefinitionHHSSS.createScore(-5L, 0L, -300L, -4000000000L, -5000L),
                scoreDefinitionHHSSS.createScore(0L, -5000000000L, -300L, -4000L, -5000L),
                scoreDefinitionHHSSS.createScore(1L, -2L, -300L, -4000L, -5000L));
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScore(0L, 0L, -300000000000L, -4000L, -5000L),
                scoreDefinitionHHSSS.createScore(0L, 2L, -300L, -4000L, -50000000000L),
                scoreDefinitionHHSSS.createScore(2000000000L, 0L, -300L, -4000L, -5000L),
                scoreDefinitionHHSSS.createScore(1L, 2L, -300L, -4000L, -5000L));
    }

    @Test
    void addHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(20000000000L, -20000000000L, -4000000000000L, 0L, 0L).add(
                scoreDefinitionHHSSS.createScore(-1000000000L, -300000000000L, 4000000000000L, 0L, 0L)))
                        .isEqualTo(scoreDefinitionHHSSS.createScore(19000000000L, -320000000000L, 0L, 0L, 0L));
    }

    @Test
    void subtractHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(20000000000L, -20000000000L, -4000000000000L, 0L, 0L).subtract(
                scoreDefinitionHHSSS.createScore(-1000000000L, -300000000000L, 4000000000000L, 0L, 0L)))
                        .isEqualTo(scoreDefinitionHHSSS.createScore(21000000000L, 280000000000L, -8000000000000L, 0L, 0L));
    }

    @Test
    void multiplyHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(5000000000L, -5000000000L, 5000000000L, 0L, 0L).multiply(1.2))
                .isEqualTo(scoreDefinitionHHSSS.createScore(6000000000L, -6000000000L, 6000000000L, 0L, 0L));
        assertThat(scoreDefinitionHHSSS.createScore(1, -1, 1, 0, 0).multiply(1.2))
                .isEqualTo(scoreDefinitionHHSSS.createScore(1, -2, 1, 0, 0));
        assertThat(scoreDefinitionHHSSS.createScore(4, -4, 4, 0, 0).multiply(1.2))
                .isEqualTo(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0));
    }

    @Test
    void divideHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(25000000000L, -25000000000L, 25000000000L, 0L, 0L).divide(5.0))
                .isEqualTo(scoreDefinitionHHSSS.createScore(5000000000L, -5000000000L, 5000000000L, 0L, 0L));
        assertThat(scoreDefinitionHHSSS.createScore(21, -21, 21, 0, 0).divide(5.0))
                .isEqualTo(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0));
        assertThat(scoreDefinitionHHSSS.createScore(24, -24, 24, 0, 0).divide(5.0))
                .isEqualTo(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0));
    }

    @Test
    void powerHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(200000L, -400000L, 500000L, 0L, 0L).power(2.0))
                .isEqualTo(scoreDefinitionHHSSS.createScore(40000000000L, 160000000000L, 250000000000L, 0L, 0L));
        assertThat(scoreDefinitionHHSSS.createScore(9L, 16L, 25L, 0L, 0L).power(0.5))
                .isEqualTo(scoreDefinitionHHSSS.createScore(3L, 4L, 5L, 0L, 0L));
    }

    @Test
    void negateHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(3000000000L, -4000000000L, 5000000000L, 0L, 0L).negate())
                .isEqualTo(scoreDefinitionHHSSS.createScore(-3000000000L, 4000000000L, -5000000000L, 0L, 0L));
        assertThat(scoreDefinitionHHSSS.createScore(-3L, 4L, -5L, 0L, 0L).negate())
                .isEqualTo(scoreDefinitionHHSSS.createScore(3L, -4L, 5L, 0L, 0L));
    }

    @Test
    void absHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(3000000000L, 4000000000L, 5000000000L, 0L, 0L).abs())
                .isEqualTo(scoreDefinitionHHSSS.createScore(3000000000L, 4000000000L, 5000000000L, 0L, 0L));
        assertThat(scoreDefinitionHHSSS.createScore(3000000000L, -4000000000L, 5000000000L, 0L, 0L).abs())
                .isEqualTo(scoreDefinitionHHSSS.createScore(3000000000L, 4000000000L, 5000000000L, 0L, 0L));
        assertThat(scoreDefinitionHHSSS.createScore(-3L, 4L, -5L, 0L, 0L).abs())
                .isEqualTo(scoreDefinitionHHSSS.createScore(3L, 4L, 5L, 0L, 0L));
        assertThat(scoreDefinitionHHSSS.createScore(-3L, -4L, -5L, 0L, 0L).abs())
                .isEqualTo(scoreDefinitionHHSSS.createScore(3L, 4L, 5L, 0L, 0L));
    }

    @Test
    void equalsAndHashCodeHHSSS() {
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHHSSS.createScore(-10000000000L, -20000000000L, -30000000000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-10000000000L, -20000000000L, -30000000000L, 0L, 0L));
    }

    @Test
    void compareToHHSSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHHSSS.createScore(-20L, Long.MIN_VALUE, Long.MIN_VALUE, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, Long.MIN_VALUE, -20L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, Long.MIN_VALUE, 1L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -300L, -4000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -300L, -300L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -300L, -20L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -300L, 300L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -20L, -300L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -20L, 0L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -20L, 1L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-1L, -300L, -4000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-1L, -300L, -20L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-1L, -20L, -300L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(1L, Long.MIN_VALUE, -20L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(1L, -20L, Long.MIN_VALUE, 0L, 0L));
    }
}
