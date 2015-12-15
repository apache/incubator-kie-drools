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
        assertEquals(scoreDefinitionHSS.createScore(BigDecimal.valueOf(-147), BigDecimal.valueOf(-258),
                BigDecimal.valueOf(-369)), scoreDefinitionHSS.parseScore("-147/-258/-369"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        scoreDefinitionHSS.parseScore("-147");
    }

    @Test
    public void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScore(MINUS_FIVE, MINUS_300, MINUS_4000)
        );
        assertScoreFeasible(
                scoreDefinitionHSS.createScore(ZERO, MINUS_300, MINUS_4000),
                scoreDefinitionHSS.createScore(TWO, MINUS_300, MINUS_4000)
        );
    }

    @Test
    public void addHSS() {
        assertEquals(scoreDefinitionHSS.createScore(PLUS_19, MINUS_320, ZERO),
                scoreDefinitionHSS.createScore(PLUS_20, MINUS_20, MINUS_4000).add(
                        scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000)));
    }

    @Test
    public void subtractHSS() {
        assertEquals(scoreDefinitionHSS.createScore(PLUS_21, PLUS_280, MINUS_8000),
                scoreDefinitionHSS.createScore(PLUS_20, MINUS_20, MINUS_4000).subtract(
                        scoreDefinitionHSS.createScore(MINUS_ONE, MINUS_300, PLUS_4000)));
    }

    @Test
    public void divideHSS() {
        assertEquals(scoreDefinitionHSS.createScore(FIVE, MINUS_FIVE, FIVE),
                scoreDefinitionHSS.createScore(PLUS_25, MINUS_25, PLUS_25).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(FOUR, MINUS_FIVE, FOUR),
                scoreDefinitionHSS.createScore(PLUS_21, MINUS_21, PLUS_21).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(FOUR, MINUS_FIVE, FOUR),
                scoreDefinitionHSS.createScore(PLUS_24, MINUS_24, PLUS_24).divide(5.0));
    }

    @Test
    @Ignore("The problem of BigDecimal ^ BigDecimal.")
    public void powerHSS() {
        // .multiply(1.0) is there to get the proper BigDecimal scale
        assertEquals(scoreDefinitionHSS.createScore(NINE, PLUS_16, PLUS_25),
                scoreDefinitionHSS.createScore(THREE, MINUS_FOUR, FIVE).power(2.0));
        assertEquals(scoreDefinitionHSS.createScore(THREE, FOUR, FIVE),
                scoreDefinitionHSS.createScore(NINE, PLUS_16, PLUS_25).power(0.5));
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
        assertScoresEqualsAndHashCode(
                scoreDefinitionHSS.createScore(MINUS_TEN, MINUS_20, MINUS_30),
                scoreDefinitionHSS.createScore(MINUS_TEN, MINUS_20, MINUS_30)
        );
    }

    @Test
    public void compareToHSS() {
        assertScoreCompareToOrder(
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
                scoreDefinitionHHSSS.createScore(ZERO, MINUS_FIVE, MINUS_300, MINUS_4000, MINUS_5000)
        );
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScore(ZERO, ZERO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(ZERO, TWO, MINUS_300, MINUS_4000, MINUS_5000),
                scoreDefinitionHHSSS.createScore(TWO, ZERO, MINUS_300, MINUS_4000, MINUS_5000)
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
        assertScoresEqualsAndHashCode(
                scoreDefinitionHHSSS.createScore(MINUS_TEN, MINUS_20, MINUS_30, ZERO, ZERO),
                scoreDefinitionHHSSS.createScore(MINUS_TEN, MINUS_20, MINUS_30, ZERO, ZERO)
        );
    }

    @Test
    public void compareToHHSSS() {
        assertScoreCompareToOrder(
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
        BendableBigDecimalScore input = scoreDefinitionHSS.createScore(MINUS_FIVE, MINUS_300, MINUS_4000);
        PlannerTestUtils.serializeAndDeserializeWithAll(input,
                new PlannerTestUtils.OutputAsserter<BendableBigDecimalScore>() {
                    public void assertOutput(BendableBigDecimalScore output) {
                        assertEquals(1, output.getHardLevelsSize());
                        assertEquals(MINUS_FIVE, output.getHardScore(0));
                        assertEquals(2, output.getSoftLevelsSize());
                        assertEquals(MINUS_300, output.getSoftScore(0));
                        assertEquals(MINUS_4000, output.getSoftScore(1));
                    }
                }
        );
    }

}
