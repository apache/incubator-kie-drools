/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.buildin.bendablebigdecimal;

import java.math.BigDecimal;

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.score.buildin.bendablebigdecimal.BendableBigDecimalScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class BendableBigDecimalScoreTest extends AbstractScoreTest {

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
    private static final BigDecimal SIX = BigDecimal.valueOf(6);
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
    public void of() {
        assertEquals(scoreDefinitionHSS.createScore(BigDecimal.valueOf(-147), ZERO, ZERO),
                BendableBigDecimalScore.ofHard(1, 2, 0, BigDecimal.valueOf(-147)));
        assertEquals(scoreDefinitionHSS.createScore(ZERO, BigDecimal.valueOf(-258), ZERO),
                BendableBigDecimalScore.ofSoft(1, 2, 0, BigDecimal.valueOf(-258)));
        assertEquals(scoreDefinitionHSS.createScore(ZERO, ZERO, BigDecimal.valueOf(-369)),
                BendableBigDecimalScore.ofSoft(1, 2, 1, BigDecimal.valueOf(-369)));
    }

    @Test
    public void parseScore() {
        assertEquals(scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.parseScore("[-147]hard/[-258/-369]soft"));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-7,
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.parseScore("-7init/[-147]hard/[-258/-369]soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("[0/-369]soft",
                scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(0), BigDecimal.valueOf(0), BigDecimal.valueOf(-369)).toShortString());
        assertEquals("[-258/-369]soft",
                scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(0), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toShortString());
        assertEquals("[-147]hard",
                scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(0), BigDecimal.valueOf(0)).toShortString());
        assertEquals("[-147]hard/[-258/-369]soft",
                scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toShortString());
        assertEquals("-7init/[-147]hard/[-258/-369]soft",
                scoreDefinitionHSS.createScoreUninitialized(-7,
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("[0]hard/[-258/-369]soft",
                scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(0), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString());
        assertEquals("[-147]hard/[-258/-369]soft",
                scoreDefinitionHSS.createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString());
        assertEquals("[-147/-258]hard/[-369]soft",
                new BendableBigDecimalScoreDefinition(2, 1).createScore(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString());
        assertEquals("-7init/[-147]hard/[-258/-369]soft",
                scoreDefinitionHSS.createScoreUninitialized(-7,
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString());
        assertEquals("[]hard/[]soft",
                new BendableBigDecimalScoreDefinition(0, 0).createScore().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        scoreDefinitionHSS.parseScore("-147");
    }

    @Test
    public void getHardOrSoftScore() {
        BendableBigDecimalScore initializedScore = scoreDefinitionHSS.createScore(BigDecimal.valueOf(-5), BigDecimal.valueOf(-10), BigDecimal.valueOf(-200));
        assertEquals(BigDecimal.valueOf(-5), initializedScore.getHardOrSoftScore(0));
        assertEquals(BigDecimal.valueOf(-10), initializedScore.getHardOrSoftScore(1));
        assertEquals(BigDecimal.valueOf(-200), initializedScore.getHardOrSoftScore(2));
    }

    @Test
    public void toInitializedScoreHSS() {
        assertEquals(scoreDefinitionHSS.createScore(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.createScore(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toInitializedScore());
        assertEquals(scoreDefinitionHSS.createScore(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.createScoreUninitialized(-7, BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-7, BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.createScore(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).withInitScore(-7));
    }

    @Test
    public void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScore(MINUS_FIVE, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreUninitialized(-7, MINUS_FIVE, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreUninitialized(-7, ZERO, MINUS_300, MINUS_4000)
        );
        assertScoreFeasible(
                scoreDefinitionHSS.createScore(ZERO, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScore(TWO, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreUninitialized(0, ZERO, MINUS_300, MINUS_4000)
                );
    }

    @Test
    public void addHSS() {
        assertEquals(scoreDefinitionHSS.createScore(PLUS_19, MINUS_320, ZERO),
                scoreDefinitionHSS.createScore(PLUS_20, MINUS_20, MINUS_4000).add(
                        scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000)));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-77, PLUS_19, MINUS_320, ZERO),
                scoreDefinitionHSS.createScoreUninitialized(-70, PLUS_20, MINUS_20, MINUS_4000).add(
                        scoreDefinitionHSS.createScoreUninitialized(-7, MINUS_ONE, MINUS_300, PLUS_4000)));
    }

    @Test
    public void subtractHSS() {
        assertEquals(scoreDefinitionHSS.createScore(PLUS_21, PLUS_280, MINUS_8000),
                scoreDefinitionHSS.createScore(PLUS_20, MINUS_20, MINUS_4000).subtract(
                        scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000)));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-63, PLUS_21, PLUS_280, MINUS_8000),
                scoreDefinitionHSS.createScoreUninitialized(-70, PLUS_20, MINUS_20, MINUS_4000).subtract(
                        scoreDefinitionHSS.createScoreUninitialized(-7, MINUS_ONE, MINUS_300, PLUS_4000)));
    }

    @Test
    public void multiplyHSS() {
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-10.4"), new BigDecimal("-12.2")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-5.2"), new BigDecimal("-6.1")).multiply(2.0));
    }

    @Test
    public void divideHSS() {
        assertEquals(scoreDefinitionHSS.createScore(FIVE, MINUS_FIVE, FIVE),
                scoreDefinitionHSS.createScore(PLUS_25, MINUS_25, PLUS_25).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(FOUR, MINUS_FIVE, FOUR),
                scoreDefinitionHSS.createScore(PLUS_21, MINUS_21, PLUS_21).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(FOUR, MINUS_FIVE, FOUR),
                scoreDefinitionHSS.createScore(PLUS_24, MINUS_24, PLUS_24).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-5.2"), new BigDecimal("-6.1")),
                scoreDefinitionHSS.createScoreUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-10.4"), new BigDecimal("-12.2")).divide(2.0));
    }

    @Test
    @Ignore("The problem of BigDecimal ^ BigDecimal.")
    public void powerHSS() {
        // .multiply(1.0) is there to get the proper BigDecimal scale
        assertEquals(scoreDefinitionHSS.createScore(NINE, PLUS_16, PLUS_25),
                scoreDefinitionHSS.createScore(THREE, MINUS_FOUR, FIVE).power(2.0));
        assertEquals(scoreDefinitionHSS.createScore(THREE, FOUR, FIVE),
                scoreDefinitionHSS.createScore(NINE, PLUS_16, PLUS_25).power(0.5));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-343, new BigDecimal("27"), new BigDecimal("-64"), new BigDecimal("125")),
                scoreDefinitionHSS.createScoreUninitialized(-7, THREE, MINUS_FOUR, FIVE).power(3.0));
    }

    @Test
    public void negateHSS() {
        assertEquals(scoreDefinitionHSS.createScore(MINUS_THREE, FOUR, MINUS_FIVE),
                scoreDefinitionHSS.createScore(THREE, MINUS_FOUR, FIVE).negate());
        assertEquals(scoreDefinitionHSS.createScore(THREE, MINUS_FOUR, FIVE),
                scoreDefinitionHSS.createScore(MINUS_THREE, FOUR, MINUS_FIVE).negate());
    }

    @Test
    public void equalsAndHashCodeHSS() {
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-10.000"), new BigDecimal("-200.000"), new BigDecimal("-3000.000")),
                scoreDefinitionHSS.createScoreUninitialized(0, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000"))
        );
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000"))
        );
        PlannerAssert.assertObjectsAreNotEqual(
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-30"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-400"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(new BigDecimal("-10"), new BigDecimal("-400"), new BigDecimal("-5000")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000"))
        );
    }

    @Test
    public void compareToHSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHSS.createScoreUninitialized(-8, new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-20"), new BigDecimal("-20"), new BigDecimal("-20")),
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-300"), new BigDecimal("-4000")),
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
                scoreDefinitionHSS.createScore(ONE, MINUS_20, MIN_INTEGER)
        );
    }

    private BendableBigDecimalScoreDefinition scoreDefinitionHHSSS = new BendableBigDecimalScoreDefinition(2, 3);

    @Test
    public void feasibleHHSSS() {
        assertScoreNotFeasible(
                scoreDefinitionHHSSS.createScore(MINUS_FIVE, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ZERO, MINUS_FIVE, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ONE, MINUS_FIVE, MINUS_300, MINUS_4000, MINUS_5000)
        );
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScore(ZERO, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ZERO, TWO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(TWO, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ONE, TWO, MINUS_300, MINUS_4000, MINUS_5000)
        );
    }

    @Test
    public void addHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(PLUS_19, MINUS_320, ZERO, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(PLUS_20, MINUS_20, MINUS_4000, ZERO, ZERO).add(
                        scoreDefinitionHHSSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000, ZERO, ZERO)));
    }

    @Test
    public void subtractHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(PLUS_21, PLUS_280, MINUS_8000, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(PLUS_20, MINUS_20, MINUS_4000, ZERO, ZERO).subtract(
                        scoreDefinitionHHSSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000, ZERO, ZERO)));
    }

    @Test
    public void divideHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(FIVE, MINUS_FIVE, FIVE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(PLUS_25, MINUS_25, PLUS_25, ZERO, ZERO).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScore(FOUR, MINUS_FIVE, FOUR, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(PLUS_21, MINUS_21, PLUS_21, ZERO, ZERO).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScore(FOUR, MINUS_FIVE, FOUR, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(PLUS_24, MINUS_24, PLUS_24, ZERO, ZERO).divide(5.0));
    }

    @Test
    @Ignore("The problem of BigDecimal ^ BigDecimal.")
    public void powerHHSSS() {
        // .multiply(1.0) is there to get the proper BigDecimal scale
        assertEquals(scoreDefinitionHHSSS.createScore(NINE, PLUS_16, PLUS_25, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(THREE, MINUS_FOUR, FIVE, ZERO, ZERO).power(2.0));
        assertEquals(scoreDefinitionHHSSS.createScore(THREE, FOUR, FIVE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(NINE, PLUS_16, PLUS_25, ZERO, ZERO).power(0.5));
    }

    @Test
    public void negateHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(MINUS_THREE, FOUR, MINUS_FIVE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(THREE, MINUS_FOUR, FIVE, ZERO, ZERO).negate());
        assertEquals(scoreDefinitionHHSSS.createScore(THREE, MINUS_FOUR, FIVE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_THREE, FOUR, MINUS_FIVE, ZERO, ZERO).negate());
    }

    @Test
    public void equalsAndHashCodeHHSSS() {
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHHSSS.createScore(MINUS_TEN, MINUS_20, MINUS_30, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_TEN, MINUS_20, MINUS_30, ZERO, ZERO)
        );
    }

    @Test
    public void compareToHHSSS() {
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
                scoreDefinitionHHSSS.createScore(ONE, MINUS_20, MIN_INTEGER, ZERO, ZERO)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                scoreDefinitionHSS.createScore(new BigDecimal("-12"), new BigDecimal("3400"), new BigDecimal("-56")),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(new BigDecimal("-12"), output.getHardScore(0));
                    assertEquals(new BigDecimal("3400"), output.getSoftScore(0));
                    assertEquals(new BigDecimal("-56"), output.getSoftScore(1));
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                scoreDefinitionHSS.createScoreUninitialized(-7, new BigDecimal("-12"), new BigDecimal("3400"), new BigDecimal("-56")),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(new BigDecimal("-12"), output.getHardScore(0));
                    assertEquals(new BigDecimal("3400"), output.getSoftScore(0));
                    assertEquals(new BigDecimal("-56"), output.getSoftScore(1));
                }
        );
    }

}
