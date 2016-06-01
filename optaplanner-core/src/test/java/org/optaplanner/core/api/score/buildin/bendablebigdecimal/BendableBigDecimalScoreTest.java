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
    public void parseScore() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.parseScore("[-147]hard/[-258/-369]soft"));
        assertEquals(scoreDefinitionHSS.createScore(-7,
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.parseScore("-7init/[-147]hard/[-258/-369]soft"));
    }

    @Test
    public void testToString() {
        assertEquals("[-147]hard/[-258/-369]soft",
                scoreDefinitionHSS.createScoreInitialized(
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString());
        assertEquals("-7init/[-147]hard/[-258/-369]soft",
                scoreDefinitionHSS.createScore(-7,
                BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        scoreDefinitionHSS.parseScore("-147");
    }

    @Test
    public void toInitializedScoreHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.createScoreInitialized(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toInitializedScore());
        assertEquals(scoreDefinitionHSS.createScoreInitialized(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)),
                scoreDefinitionHSS.createScore(-7, BigDecimal.valueOf(-147), BigDecimal.valueOf(-258), BigDecimal.valueOf(-369)).toInitializedScore());
    }

    @Test
    public void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScoreInitialized(MINUS_FIVE, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScore(-7, MINUS_FIVE, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScore(-7, ZERO, MINUS_300, MINUS_4000)
        );
        assertScoreFeasible(
                scoreDefinitionHSS.createScoreInitialized(ZERO, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreInitialized(TWO, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScore(0, ZERO, MINUS_300, MINUS_4000)
                );
    }

    @Test
    public void addHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(PLUS_19, MINUS_320, ZERO),
                scoreDefinitionHSS.createScoreInitialized(PLUS_20, MINUS_20, MINUS_4000).add(
                        scoreDefinitionHSS.createScoreInitialized(MINUS_ONE, MINUS_300, PLUS_4000)));
        assertEquals(scoreDefinitionHSS.createScore(-77, PLUS_19, MINUS_320, ZERO),
                scoreDefinitionHSS.createScore(-70, PLUS_20, MINUS_20, MINUS_4000).add(
                        scoreDefinitionHSS.createScore(-7, MINUS_ONE, MINUS_300, PLUS_4000)));
    }

    @Test
    public void subtractHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(PLUS_21, PLUS_280, MINUS_8000),
                scoreDefinitionHSS.createScoreInitialized(PLUS_20, MINUS_20, MINUS_4000).subtract(
                        scoreDefinitionHSS.createScoreInitialized(MINUS_ONE, MINUS_300, PLUS_4000)));
        assertEquals(scoreDefinitionHSS.createScore(-63, PLUS_21, PLUS_280, MINUS_8000),
                scoreDefinitionHSS.createScore(-70, PLUS_20, MINUS_20, MINUS_4000).subtract(
                        scoreDefinitionHSS.createScore(-7, MINUS_ONE, MINUS_300, PLUS_4000)));
    }

    @Test
    public void multiplyHSS() {
        assertEquals(scoreDefinitionHSS.createScore(-14, new BigDecimal("8.6"), new BigDecimal("-10.4"), new BigDecimal("-12.2")),
                scoreDefinitionHSS.createScore(-7, new BigDecimal("4.3"), new BigDecimal("-5.2"), new BigDecimal("-6.1")).multiply(2.0));
    }

    @Test
    public void divideHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(FIVE, MINUS_FIVE, FIVE),
                scoreDefinitionHSS.createScoreInitialized(PLUS_25, MINUS_25, PLUS_25).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScoreInitialized(FOUR, MINUS_FIVE, FOUR),
                scoreDefinitionHSS.createScoreInitialized(PLUS_21, MINUS_21, PLUS_21).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScoreInitialized(FOUR, MINUS_FIVE, FOUR),
                scoreDefinitionHSS.createScoreInitialized(PLUS_24, MINUS_24, PLUS_24).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(-7, new BigDecimal("4.3"), new BigDecimal("-5.2"), new BigDecimal("-6.1")),
                scoreDefinitionHSS.createScore(-14, new BigDecimal("8.6"), new BigDecimal("-10.4"), new BigDecimal("-12.2")).divide(2.0));
    }

    @Test
    @Ignore("The problem of BigDecimal ^ BigDecimal.")
    public void powerHSS() {
        // .multiply(1.0) is there to get the proper BigDecimal scale
        assertEquals(scoreDefinitionHSS.createScoreInitialized(NINE, PLUS_16, PLUS_25),
                scoreDefinitionHSS.createScoreInitialized(THREE, MINUS_FOUR, FIVE).power(2.0));
        assertEquals(scoreDefinitionHSS.createScoreInitialized(THREE, FOUR, FIVE),
                scoreDefinitionHSS.createScoreInitialized(NINE, PLUS_16, PLUS_25).power(0.5));
        assertEquals(scoreDefinitionHSS.createScore(-343, new BigDecimal("27"), new BigDecimal("-64"), new BigDecimal("125")),
                scoreDefinitionHSS.createScore(-7, THREE, MINUS_FOUR, FIVE).power(3.0));
    }

    @Test
    public void negateHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(MINUS_THREE, FOUR, MINUS_FIVE),
                scoreDefinitionHSS.createScoreInitialized(THREE, MINUS_FOUR, FIVE).negate());
        assertEquals(scoreDefinitionHSS.createScoreInitialized(THREE, MINUS_FOUR, FIVE),
                scoreDefinitionHSS.createScoreInitialized(MINUS_THREE, FOUR, MINUS_FIVE).negate());
    }

    @Test
    public void equalsAndHashCodeHSS() {
        assertScoresEqualsAndHashCode(
                scoreDefinitionHSS.createScoreInitialized(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScoreInitialized(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(0, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000"))
        );
        assertScoresEqualsAndHashCode(
                scoreDefinitionHSS.createScore(-7, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScore(-7, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000"))
        );
        assertScoresNotEquals(
                scoreDefinitionHSS.createScoreInitialized(new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScoreInitialized(new BigDecimal("-30"), new BigDecimal("-200"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScoreInitialized(new BigDecimal("-10"), new BigDecimal("-400"), new BigDecimal("-3000")),
                scoreDefinitionHSS.createScoreInitialized(new BigDecimal("-10"), new BigDecimal("-400"), new BigDecimal("-5000")),
                scoreDefinitionHSS.createScore(-7, new BigDecimal("-10"), new BigDecimal("-200"), new BigDecimal("-3000"))
        );
    }

    @Test
    public void compareToHSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHSS.createScore(-8, new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")),
                scoreDefinitionHSS.createScore(-7, new BigDecimal("-20"), new BigDecimal("-20"), new BigDecimal("-20")),
                scoreDefinitionHSS.createScore(-7, new BigDecimal("-1"), new BigDecimal("-300"), new BigDecimal("-4000")),
                scoreDefinitionHSS.createScore(-7, new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")),
                scoreDefinitionHSS.createScore(-7, new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("1")),
                scoreDefinitionHSS.createScore(-7, new BigDecimal("0"), new BigDecimal("1"), new BigDecimal("0")),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MIN_INTEGER, MIN_INTEGER),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MIN_INTEGER, MINUS_20),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MIN_INTEGER, ONE),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MINUS_300, MINUS_300),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MINUS_300, MINUS_20),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MINUS_300, PLUS_300),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MINUS_20, MINUS_300),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MINUS_20, ZERO),
                scoreDefinitionHSS.createScoreInitialized(MINUS_20, MINUS_20, ONE),
                scoreDefinitionHSS.createScoreInitialized(MINUS_ONE, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScoreInitialized(MINUS_ONE, MINUS_300, MINUS_20),
                scoreDefinitionHSS.createScoreInitialized(MINUS_ONE, MINUS_20, MINUS_300),
                scoreDefinitionHSS.createScoreInitialized(ONE, MIN_INTEGER, MINUS_20),
                scoreDefinitionHSS.createScoreInitialized(ONE, MINUS_20, MIN_INTEGER)
        );
    }

    private BendableBigDecimalScoreDefinition scoreDefinitionHHSSS = new BendableBigDecimalScoreDefinition(2, 3);

    @Test
    public void feasibleHHSSS() {
        assertScoreNotFeasible(
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_FIVE, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScoreInitialized(ZERO, MINUS_FIVE, MINUS_300, MINUS_4000, MINUS_5000)
        );
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScoreInitialized(ZERO, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScoreInitialized(ZERO, TWO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScoreInitialized(TWO, ZERO, MINUS_300, MINUS_4000, MINUS_5000)
        );
    }

    @Test
    public void addHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(PLUS_19, MINUS_320, ZERO, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(PLUS_20, MINUS_20, MINUS_4000, ZERO, ZERO).add(
                        scoreDefinitionHHSSS.createScoreInitialized(MINUS_ONE, MINUS_300, PLUS_4000, ZERO, ZERO)));
    }

    @Test
    public void subtractHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(PLUS_21, PLUS_280, MINUS_8000, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(PLUS_20, MINUS_20, MINUS_4000, ZERO, ZERO).subtract(
                        scoreDefinitionHHSSS.createScoreInitialized(MINUS_ONE, MINUS_300, PLUS_4000, ZERO, ZERO)));
    }

    @Test
    public void divideHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(FIVE, MINUS_FIVE, FIVE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(PLUS_25, MINUS_25, PLUS_25, ZERO, ZERO).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(FOUR, MINUS_FIVE, FOUR, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(PLUS_21, MINUS_21, PLUS_21, ZERO, ZERO).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(FOUR, MINUS_FIVE, FOUR, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(PLUS_24, MINUS_24, PLUS_24, ZERO, ZERO).divide(5.0));
    }

    @Test
    @Ignore("The problem of BigDecimal ^ BigDecimal.")
    public void powerHHSSS() {
        // .multiply(1.0) is there to get the proper BigDecimal scale
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(NINE, PLUS_16, PLUS_25, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(THREE, MINUS_FOUR, FIVE, ZERO, ZERO).power(2.0));
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(THREE, FOUR, FIVE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(NINE, PLUS_16, PLUS_25, ZERO, ZERO).power(0.5));
    }

    @Test
    public void negateHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(MINUS_THREE, FOUR, MINUS_FIVE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(THREE, MINUS_FOUR, FIVE, ZERO, ZERO).negate());
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(THREE, MINUS_FOUR, FIVE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_THREE, FOUR, MINUS_FIVE, ZERO, ZERO).negate());
    }

    @Test
    public void equalsAndHashCodeHHSSS() {
        assertScoresEqualsAndHashCode(
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_TEN, MINUS_20, MINUS_30, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_TEN, MINUS_20, MINUS_30, ZERO, ZERO)
        );
    }

    @Test
    public void compareToHHSSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MIN_INTEGER, MIN_INTEGER, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MIN_INTEGER, MINUS_20, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MIN_INTEGER, ONE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MINUS_300, MINUS_4000, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MINUS_300, MINUS_300, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MINUS_300, MINUS_20, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MINUS_300, PLUS_300, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MINUS_20, MINUS_300, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MINUS_20, ZERO, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_20, MINUS_20, ONE, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_ONE, MINUS_300, MINUS_4000, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_ONE, MINUS_300, MINUS_20, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(MINUS_ONE, MINUS_20, MINUS_300, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(ONE, MIN_INTEGER, MINUS_20, ZERO, ZERO),
                scoreDefinitionHHSSS.createScoreInitialized(ONE, MINUS_20, MIN_INTEGER, ZERO, ZERO)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                scoreDefinitionHSS.createScoreInitialized(new BigDecimal("-12"), new BigDecimal("3400"), new BigDecimal("-56")),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(new BigDecimal("-12"), output.getHardScore(0));
                    assertEquals(new BigDecimal("3400"), output.getSoftScore(0));
                    assertEquals(new BigDecimal("-56"), output.getSoftScore(1));
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                scoreDefinitionHSS.createScore(-7, new BigDecimal("-12"), new BigDecimal("3400"), new BigDecimal("-56")),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(new BigDecimal("-12"), output.getHardScore(0));
                    assertEquals(new BigDecimal("3400"), output.getSoftScore(0));
                    assertEquals(new BigDecimal("-56"), output.getSoftScore(1));
                }
        );
    }

}
