package org.optaplanner.core.api.score.buildin.bendablebigdecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.score.buildin.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class BendableBigDecimalScoreTest extends AbstractScoreTest {

    private static final BigDecimal PLUS_4000 = BigDecimal.valueOf(4000);
    private static final BigDecimal PLUS_300 = BigDecimal.valueOf(300);
    private static final BigDecimal PLUS_280 = BigDecimal.valueOf(280);
    private static final BigDecimal PLUS_25 = BigDecimal.valueOf(25);
    private static final BigDecimal PLUS_24 = BigDecimal.valueOf(24);
    private static final BigDecimal PLUS_21 = BigDecimal.valueOf(21);
    private static final BigDecimal PLUS_20 = BigDecimal.valueOf(20);
    private static final BigDecimal PLUS_19 = BigDecimal.valueOf(19);
    private static final BigDecimal PLUS_16 = BigDecimal.valueOf(16);
    private static final BigDecimal NINE = BigDecimal.valueOf(9);
    private static final BigDecimal FIVE = BigDecimal.valueOf(5);
    private static final BigDecimal FOUR = BigDecimal.valueOf(4);
    private static final BigDecimal THREE = BigDecimal.valueOf(3);
    private static final BigDecimal TWO = BigDecimal.valueOf(2);
    private static final BigDecimal ONE = BigDecimal.ONE;
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal MINUS_ONE = ONE.negate();
    private static final BigDecimal MINUS_THREE = THREE.negate();
    private static final BigDecimal MINUS_FOUR = FOUR.negate();
    private static final BigDecimal MINUS_FIVE = FIVE.negate();
    private static final BigDecimal MINUS_TEN = BigDecimal.TEN.negate();
    private static final BigDecimal MINUS_20 = PLUS_20.negate();
    private static final BigDecimal MINUS_21 = PLUS_21.negate();
    private static final BigDecimal MINUS_24 = PLUS_24.negate();
    private static final BigDecimal MINUS_25 = PLUS_25.negate();
    private static final BigDecimal MINUS_30 = BigDecimal.valueOf(-30);
    private static final BigDecimal MINUS_300 = PLUS_300.negate();
    private static final BigDecimal MINUS_320 = BigDecimal.valueOf(-320);
    private static final BigDecimal MINUS_4000 = PLUS_4000.negate();
    private static final BigDecimal MINUS_5000 = BigDecimal.valueOf(-5000);
    private static final BigDecimal MINUS_8000 = BigDecimal.valueOf(-8000);
    private static final BigDecimal MIN_INTEGER = BigDecimal.valueOf(Integer.MIN_VALUE);

    private BendableBigDecimalScoreDefinition scoreDefinitionHSS = new BendableBigDecimalScoreDefinition(1, 2);

    @Test
    void of() {
        assertThat(BendableBigDecimalScore.ofHard(1, 2, 0, BigDecimal.valueOf(-147)))
                .isEqualTo(scoreDefinitionHSS.createScore(BigDecimal.valueOf(-147), ZERO, ZERO));
        assertThat(BendableBigDecimalScore.ofSoft(1, 2, 0, BigDecimal.valueOf(-258)))
                .isEqualTo(scoreDefinitionHSS.createScore(ZERO, BigDecimal.valueOf(-258), ZERO));
        assertThat(BendableBigDecimalScore.ofSoft(1, 2, 1, BigDecimal.valueOf(-369)))
                .isEqualTo(scoreDefinitionHSS.createScore(ZERO, ZERO, BigDecimal.valueOf(-369)));
    }

    @Test
    void parseScore() {
        assertThat(scoreDefinitionHSS.parseScore("[-147]hard/[-258/-369]soft")).isEqualTo(scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)));
        assertThat(scoreDefinitionHSS.parseScore("-7init/[-147]hard/[-258/-369]soft"))
                .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-7,
                        BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)));
    }

    @Test
    void toShortString() {
        assertThat(scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(-369)).toShortString())
                        .isEqualTo("[0/-369]soft");
        assertThat(scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(0), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toShortString())
                        .isEqualTo("[-258/-369]soft");
        assertThat(scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(0), BigDecimal.valueOf(0)).toShortString())
                        .isEqualTo("[-147]hard");
        assertThat(scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toShortString())
                        .isEqualTo("[-147]hard/[-258/-369]soft");
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-7,
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toShortString())
                        .isEqualTo("-7init/[-147]hard/[-258/-369]soft");
    }

    @Test
    void testToString() {
        assertThat(scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(0), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString())
                        .isEqualTo("[0]hard/[-258/-369]soft");
        assertThat(scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString())
                        .isEqualTo("[-147]hard/[-258/-369]soft");
        assertThat(new BendableBigDecimalScoreDefinition(2, 1).createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString())
                        .isEqualTo("[-147/-258]hard/[-369]soft");
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-7,
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString())
                        .isEqualTo("-7init/[-147]hard/[-258/-369]soft");
        assertThat(new BendableBigDecimalScoreDefinition(0, 0).createScore().toString()).isEqualTo("[]hard/[]soft");
    }

    @Test
    void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> scoreDefinitionHSS.parseScore("-147"));
    }

    @Test
    void getHardOrSoftScore() {
        BendableBigDecimalScore initializedScore = scoreDefinitionHSS.createScore(BigDecimal.valueOf(-5),
                BigDecimal.valueOf(-10), BigDecimal.valueOf(-200));
        assertThat(initializedScore.getHardOrSoftScore(0)).isEqualTo(BigDecimal.valueOf(-5));
        assertThat(initializedScore.getHardOrSoftScore(1)).isEqualTo(BigDecimal.valueOf(-10));
        assertThat(initializedScore.getHardOrSoftScore(2)).isEqualTo(BigDecimal.valueOf(-200));
    }

    @Test
    void withInitScore() {
        assertThat(scoreDefinitionHSS.createScore(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369))
                .withInitScore(-7)).isEqualTo(
                        scoreDefinitionHSS.createScoreUninitialized(-7, BigDecimal.valueOf(-147), BigDecimal.valueOf(-258),
                                BigDecimal.valueOf(-369)));
    }

    @Test
    void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScore(MINUS_FIVE, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreUninitialized(-7, MINUS_FIVE, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreUninitialized(-7, ZERO, MINUS_300, MINUS_4000));
        assertScoreFeasible(
                scoreDefinitionHSS.createScore(ZERO, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScore(TWO, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreUninitialized(0, ZERO, MINUS_300, MINUS_4000));
    }

    @Test
    void addHSS() {
        assertThat(scoreDefinitionHSS.createScore(PLUS_20, MINUS_20, MINUS_4000).add(
                scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000)))
                        .isEqualTo(scoreDefinitionHSS.createScore(PLUS_19, MINUS_320, ZERO));
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-70, PLUS_20, MINUS_20, MINUS_4000).add(
                scoreDefinitionHSS.createScoreUninitialized(-7, MINUS_ONE, MINUS_300, PLUS_4000)))
                        .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-77, PLUS_19, MINUS_320, ZERO));
    }

    @Test
    void subtractHSS() {
        assertThat(scoreDefinitionHSS.createScore(PLUS_20, MINUS_20, MINUS_4000).subtract(
                scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000)))
                        .isEqualTo(scoreDefinitionHSS.createScore(PLUS_21, PLUS_280, MINUS_8000));
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-70, PLUS_20, MINUS_20, MINUS_4000).subtract(
                scoreDefinitionHSS.createScoreUninitialized(-7, MINUS_ONE, MINUS_300, PLUS_4000)))
                        .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-63, PLUS_21, PLUS_280, MINUS_8000));
    }

    @Test
    void multiplyHSS() {
        assertThat(scoreDefinitionHSS
                .createScoreUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-5.2"), new BigDecimal("-6.1"))
                .multiply(2.0)).isEqualTo(
                        scoreDefinitionHSS.createScoreUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-10.4"),
                                new BigDecimal("-12.2")));
    }

    @Test
    void divideHSS() {
        assertThat(scoreDefinitionHSS.createScore(PLUS_25, MINUS_25, PLUS_25).divide(5.0))
                .isEqualTo(scoreDefinitionHSS.createScore(FIVE, MINUS_FIVE, FIVE));
        assertThat(scoreDefinitionHSS.createScore(PLUS_21, MINUS_21, PLUS_21).divide(5.0))
                .isEqualTo(scoreDefinitionHSS.createScore(FOUR, MINUS_FIVE, FOUR));
        assertThat(scoreDefinitionHSS.createScore(PLUS_24, MINUS_24, PLUS_24).divide(5.0))
                .isEqualTo(scoreDefinitionHSS.createScore(FOUR, MINUS_FIVE, FOUR));
        assertThat(scoreDefinitionHSS
                .createScoreUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-10.4"), new BigDecimal("-12.2"))
                .divide(2.0)).isEqualTo(
                        scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-5.2"),
                                new BigDecimal("-6.1")));
    }

    @Test
    @Disabled("The problem of BigDecimal ^ BigDecimal.")
    void powerHSS() {
        // .multiply(1.0) is there to get the proper BigDecimal scale
        assertThat(scoreDefinitionHSS.createScore(THREE, MINUS_FOUR, FIVE).power(2.0))
                .isEqualTo(scoreDefinitionHSS.createScore(NINE, PLUS_16, PLUS_25));
        assertThat(scoreDefinitionHSS.createScore(NINE, PLUS_16, PLUS_25).power(0.5))
                .isEqualTo(scoreDefinitionHSS.createScore(THREE, FOUR, FIVE));
        assertThat(scoreDefinitionHSS.createScoreUninitialized(-7, THREE, MINUS_FOUR, FIVE).power(3.0))
                .isEqualTo(scoreDefinitionHSS.createScoreUninitialized(-343, new BigDecimal("27"), new BigDecimal("-64"),
                        new BigDecimal("125")));
    }

    @Test
    void negateHSS() {
        assertThat(scoreDefinitionHSS.createScore(THREE, MINUS_FOUR, FIVE).negate())
                .isEqualTo(scoreDefinitionHSS.createScore(MINUS_THREE, FOUR, MINUS_FIVE));
        assertThat(scoreDefinitionHSS.createScore(MINUS_THREE, FOUR, MINUS_FIVE).negate())
                .isEqualTo(scoreDefinitionHSS.createScore(THREE, MINUS_FOUR, FIVE));
    }

    @Test
    void zero() {
        BendableBigDecimalScore manualZero = BendableBigDecimalScore.zero(0, 1);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(manualZero.zero()).isEqualTo(manualZero);
            softly.assertThatObject(manualZero.isZero()).isEqualTo(true);
            BendableBigDecimalScore manualOne = BendableBigDecimalScore.ofSoft(0, 1, 0, BigDecimal.ONE);
            softly.assertThat(manualOne.isZero())
                    .isEqualTo(false);
        });
    }

    @Test
    void equalsAndHashCodeHSS() {
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-10.000"), new BigDecimal("-200.000"),
                        new BigDecimal("-3000.000")),
                scoreDefinitionHSS.createScoreUninitialized(0, new BigDecimal("-10"), new BigDecimal("-200"),
                        new BigDecimal("-3000")));
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-10"), new BigDecimal("-200"),
                        new BigDecimal("-3000")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-10"), new BigDecimal("-200"),
                        new BigDecimal("-3000")));
        PlannerAssert.assertObjectsAreNotEqual(
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-30"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-400"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-400"), new BigDecimal("-5000")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-10"), new BigDecimal("-200"),
                        new BigDecimal("-3000")));
    }

    @Test
    void compareToHSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHSS.createScoreUninitialized(-8, new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-20"), new BigDecimal("-20"),
                        new BigDecimal("-20")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-300"),
                        new BigDecimal("-4000")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("1")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("0")),
                scoreDefinitionHSS.createScore(MINUS_20, MIN_INTEGER, MIN_INTEGER),
                scoreDefinitionHSS.createScore(MINUS_20, MIN_INTEGER, MINUS_20),
                scoreDefinitionHSS.createScore(MINUS_20, MIN_INTEGER, ONE),
                scoreDefinitionHSS.createScore(MINUS_20, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScore(MINUS_20, MINUS_300, MINUS_300),
                scoreDefinitionHSS.createScore(MINUS_20, MINUS_300, MINUS_20),
                scoreDefinitionHSS.createScore(MINUS_20, MINUS_300, PLUS_300),
                scoreDefinitionHSS.createScore(MINUS_20, MINUS_20, MINUS_300),
                scoreDefinitionHSS.createScore(MINUS_20, MINUS_20, ZERO),
                scoreDefinitionHSS.createScore(MINUS_20, MINUS_20, ONE),
                scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_300, MINUS_20),
                scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_20, MINUS_300),
                scoreDefinitionHSS.createScore(ONE, MIN_INTEGER, MINUS_20),
                scoreDefinitionHSS.createScore(ONE, MINUS_20, MIN_INTEGER));
    }

    private BendableBigDecimalScoreDefinition scoreDefinitionHHSSS = new BendableBigDecimalScoreDefinition(2, 3);

    @Test
    void feasibleHHSSS() {
        assertScoreNotFeasible(
                scoreDefinitionHHSSS.createScore(MINUS_FIVE, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ZERO, MINUS_FIVE, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ONE, MINUS_FIVE, MINUS_300, MINUS_4000, MINUS_5000));
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScore(ZERO, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ZERO, TWO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(TWO, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ONE, TWO, MINUS_300, MINUS_4000, MINUS_5000));
    }

    @Test
    void addHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(PLUS_20, MINUS_20, MINUS_4000, ZERO, ZERO).add(
                scoreDefinitionHHSSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000, ZERO, ZERO)))
                        .isEqualTo(scoreDefinitionHHSSS.createScore(PLUS_19, MINUS_320, ZERO, ZERO, ZERO));
    }

    @Test
    void subtractHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(PLUS_20, MINUS_20, MINUS_4000, ZERO, ZERO).subtract(
                scoreDefinitionHHSSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000, ZERO, ZERO)))
                        .isEqualTo(scoreDefinitionHHSSS.createScore(PLUS_21, PLUS_280, MINUS_8000, ZERO, ZERO));
    }

    @Test
    void divideHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(PLUS_25, MINUS_25, PLUS_25, ZERO, ZERO).divide(5.0))
                .isEqualTo(scoreDefinitionHHSSS.createScore(FIVE, MINUS_FIVE, FIVE, ZERO, ZERO));
        assertThat(scoreDefinitionHHSSS.createScore(PLUS_21, MINUS_21, PLUS_21, ZERO, ZERO).divide(5.0))
                .isEqualTo(scoreDefinitionHHSSS.createScore(FOUR, MINUS_FIVE, FOUR, ZERO, ZERO));
        assertThat(scoreDefinitionHHSSS.createScore(PLUS_24, MINUS_24, PLUS_24, ZERO, ZERO).divide(5.0))
                .isEqualTo(scoreDefinitionHHSSS.createScore(FOUR, MINUS_FIVE, FOUR, ZERO, ZERO));
    }

    @Test
    @Disabled("The problem of BigDecimal ^ BigDecimal.")
    void powerHHSSS() {
        // .multiply(1.0) is there to get the proper BigDecimal scale
        assertThat(scoreDefinitionHHSSS.createScore(THREE, MINUS_FOUR, FIVE, ZERO, ZERO).power(2.0))
                .isEqualTo(scoreDefinitionHHSSS.createScore(NINE, PLUS_16, PLUS_25, ZERO, ZERO));
        assertThat(scoreDefinitionHHSSS.createScore(NINE, PLUS_16, PLUS_25, ZERO, ZERO).power(0.5))
                .isEqualTo(scoreDefinitionHHSSS.createScore(THREE, FOUR, FIVE, ZERO, ZERO));
    }

    @Test
    void negateHHSSS() {
        assertThat(scoreDefinitionHHSSS.createScore(THREE, MINUS_FOUR, FIVE, ZERO, ZERO).negate())
                .isEqualTo(scoreDefinitionHHSSS.createScore(MINUS_THREE, FOUR, MINUS_FIVE, ZERO, ZERO));
        assertThat(scoreDefinitionHHSSS.createScore(MINUS_THREE, FOUR, MINUS_FIVE, ZERO, ZERO).negate())
                .isEqualTo(scoreDefinitionHHSSS.createScore(THREE, MINUS_FOUR, FIVE, ZERO, ZERO));
    }

    @Test
    void equalsAndHashCodeHHSSS() {
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHHSSS.createScore(MINUS_TEN, MINUS_20, MINUS_30, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_TEN, MINUS_20, MINUS_30, ZERO, ZERO));
    }

    @Test
    void compareToHHSSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHHSSS.createScore(MINUS_20, MIN_INTEGER, MIN_INTEGER, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MIN_INTEGER, MINUS_20, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MIN_INTEGER, ONE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MINUS_300, MINUS_4000, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MINUS_300, MINUS_300, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MINUS_300, MINUS_20, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MINUS_300, PLUS_300, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MINUS_20, MINUS_300, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MINUS_20, ZERO, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_20, MINUS_20, ONE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_ONE, MINUS_300, MINUS_4000, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_ONE, MINUS_300, MINUS_20, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_ONE, MINUS_20, MINUS_300, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(ONE, MIN_INTEGER, MINUS_20, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(ONE, MINUS_20, MIN_INTEGER, ZERO, ZERO));
    }
}
