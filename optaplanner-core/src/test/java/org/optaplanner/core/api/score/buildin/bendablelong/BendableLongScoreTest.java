/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.buildin.bendablelong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.score.buildin.bendablelong.BendableLongScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.assertEquals;

public class BendableLongScoreTest extends AbstractScoreTest {

    private BendableLongScoreDefinition scoreDefinitionHSS = new BendableLongScoreDefinition(1, 2);

    @Test
    public void parseScore() {
        assertEquals(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L),
                scoreDefinitionHSS.parseScore("-5432109876/-9876543210/-3456789012"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        scoreDefinitionHSS.parseScore("-147");
    }

    @Test
    public void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScore(-5, -300, -9876543210L)
        );
        assertScoreFeasible(
                scoreDefinitionHSS.createScore(0, -300, -9876543210L),
                scoreDefinitionHSS.createScore(2, -300, -9876543210L)
        );
    }

    @Test
    public void addHSS() {
        assertEquals(scoreDefinitionHSS.createScore(3333333333L, -320, 0),
                scoreDefinitionHSS.createScore(1111111111L, -20, -9876543210L).add(
                        scoreDefinitionHSS.createScore(2222222222L, -300, 9876543210L)));
    }

    @Test
    public void subtractHSS() {
        assertEquals(scoreDefinitionHSS.createScore(2222222222L, 280, -8888888888L),
                scoreDefinitionHSS.createScore(3333333333L, -20, -5555555555L).subtract(
                        scoreDefinitionHSS.createScore(1111111111, -300, 3333333333L)));
    }

    @Test
    public void multiplyHSS() {
        assertEquals(scoreDefinitionHSS.createScore(6000000000L, -6000000000L, 6000000000L),
                scoreDefinitionHSS.createScore(5000000000L, -5000000000L, 5000000000L).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScore(1, -2, 1),
                scoreDefinitionHSS.createScore(1, -1, 1).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScore(4, -5, 4),
                scoreDefinitionHSS.createScore(4, -4, 4).multiply(1.2));
    }

    @Test
    public void divideHSS() {
        assertEquals(scoreDefinitionHSS.createScore(5000000000L, -5000000000L, 5000000000L),
                scoreDefinitionHSS.createScore(25000000000L, -25000000000L, 25000000000L).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(4, -5, 4),
                scoreDefinitionHSS.createScore(21, -21, 21).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(4, -5, 4),
                scoreDefinitionHSS.createScore(24, -24, 24).divide(5.0));
    }

    @Test
    public void powerHSS() {
        assertEquals(scoreDefinitionHSS.createScore(90000000000L, 160000000000L, 250000000000L),
                scoreDefinitionHSS.createScore(300000L, -400000L, 500000L).power(2.0));
        assertEquals(scoreDefinitionHSS.createScore(300000L, 400000L, 500000L),
                scoreDefinitionHSS.createScore(90000000000L, 160000000000L, 250000000000L).power(0.5));
    }

    @Test
    public void negateHSS() {
        assertEquals(scoreDefinitionHSS.createScore(-3000000000L, 4000000000L, -5000000000L),
                scoreDefinitionHSS.createScore(3000000000L, -4000000000L, 5000000000L).negate());
        assertEquals(scoreDefinitionHSS.createScore(3000000000L, -4000000000L, 5000000000L),
                scoreDefinitionHSS.createScore(-3000000000L, 4000000000L, -5000000000L).negate());
    }

    @Test
    public void equalsAndHashCodeHSS() {
        assertScoresEqualsAndHashCode(
                scoreDefinitionHSS.createScore(-10000000000L, -20000000000L, -30000000000L),
                scoreDefinitionHSS.createScore(-10000000000L, -20000000000L, -30000000000L)
        );
    }

    @Test
    public void compareToHSS() {
        assertScoreCompareToOrder(
                scoreDefinitionHSS.createScore(-20, Long.MIN_VALUE, Long.MIN_VALUE),
                scoreDefinitionHSS.createScore(-20, Long.MIN_VALUE, -20),
                scoreDefinitionHSS.createScore(-20, Long.MIN_VALUE, 1),
                scoreDefinitionHSS.createScore(-20, -300, -4000),
                scoreDefinitionHSS.createScore(-20, -300, -300),
                scoreDefinitionHSS.createScore(-20, -300, -20),
                scoreDefinitionHSS.createScore(-20, -300, 300),
                scoreDefinitionHSS.createScore(-20, -20, -300),
                scoreDefinitionHSS.createScore(-20, -20, 0),
                scoreDefinitionHSS.createScore(-20, -20, 1),
                scoreDefinitionHSS.createScore(-1, -300, -4000),
                scoreDefinitionHSS.createScore(-1, -300, -20),
                scoreDefinitionHSS.createScore(-1, -20, -300),
                scoreDefinitionHSS.createScore(1, Long.MIN_VALUE, -20),
                scoreDefinitionHSS.createScore(1, -20, Long.MIN_VALUE)
        );
    }

    private BendableLongScoreDefinition scoreDefinitionHHSSS = new BendableLongScoreDefinition(2, 3);

    @Test
    public void feasibleHHSSS() {
        assertScoreNotFeasible(
                scoreDefinitionHHSSS.createScore(-5, 0, -300, -4000000000L, -5000),
                scoreDefinitionHHSSS.createScore(0, -5000000000L, -300, -4000, -5000)
        );
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScore(0, 0, -300000000000L, -4000, -5000),
                scoreDefinitionHHSSS.createScore(0, 2, -300, -4000, -50000000000L),
                scoreDefinitionHHSSS.createScore(2000000000L, 0, -300, -4000, -5000)
        );
    }

    @Test
    public void addHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(19000000000L, -320000000000L, 0, 0, 0),
                scoreDefinitionHHSSS.createScore(20000000000L, -20000000000L, -4000000000000L, 0, 0).add(
                        scoreDefinitionHHSSS.createScore(-1000000000L, -300000000000L, 4000000000000L, 0, 0)));
    }

    @Test
    public void subtractHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(21000000000L, 280000000000L, -8000000000000L, 0, 0),
                scoreDefinitionHHSSS.createScore(20000000000L, -20000000000L, -4000000000000L, 0, 0).subtract(
                        scoreDefinitionHHSSS.createScore(-1000000000L, -300000000000L, 4000000000000L, 0, 0)));
    }

    @Test
    public void multiplyHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(6000000000L, -6000000000L, 6000000000L, 0, 0),
                scoreDefinitionHHSSS.createScore(5000000000L, -5000000000L, 5000000000L, 0, 0).multiply(1.2));
        assertEquals(scoreDefinitionHHSSS.createScore(1, -2, 1, 0, 0),
                scoreDefinitionHHSSS.createScore(1, -1, 1, 0, 0).multiply(1.2));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(4, -4, 4, 0, 0).multiply(1.2));
    }

    @Test
    public void divideHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(5000000000L, -5000000000L, 5000000000L, 0, 0),
                scoreDefinitionHHSSS.createScore(25000000000L, -25000000000L, 25000000000L, 0, 0).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(21, -21, 21, 0, 0).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(24, -24, 24, 0, 0).divide(5.0));
    }

    @Test
    public void powerHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(40000000000L, 160000000000L, 250000000000L, 0, 0),
                scoreDefinitionHHSSS.createScore(200000, -400000, 500000, 0, 0).power(2.0));
        assertEquals(scoreDefinitionHHSSS.createScore(3, 4, 5, 0, 0),
                scoreDefinitionHHSSS.createScore(9, 16, 25, 0, 0).power(0.5));
    }

    @Test
    public void negateHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(-3000000000L, 4000000000L, -5000000000L, 0, 0),
                scoreDefinitionHHSSS.createScore(3000000000L, -4000000000L, 5000000000L, 0, 0).negate());
        assertEquals(scoreDefinitionHHSSS.createScore(3, -4, 5, 0, 0),
                scoreDefinitionHHSSS.createScore(-3, 4, -5, 0, 0).negate());
    }

    @Test
    public void equalsAndHashCodeHHSSS() {
        assertScoresEqualsAndHashCode(
                scoreDefinitionHHSSS.createScore(-10000000000L, -20000000000L, -30000000000L, 0, 0),
                scoreDefinitionHHSSS.createScore(-10000000000L, -20000000000L, -30000000000L, 0, 0)
        );
    }

    @Test
    public void compareToHHSSS() {
        assertScoreCompareToOrder(
                scoreDefinitionHHSSS.createScore(-20, Long.MIN_VALUE, Long.MIN_VALUE, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, Long.MIN_VALUE, -20, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, Long.MIN_VALUE, 1, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -300, -4000, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -300, -300, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -300, -20, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -300, 300, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -20, -300, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -20, 0, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -20, 1, 0, 0),
                scoreDefinitionHHSSS.createScore(-1, -300, -4000, 0, 0),
                scoreDefinitionHHSSS.createScore(-1, -300, -20, 0, 0),
                scoreDefinitionHHSSS.createScore(-1, -20, -300, 0, 0),
                scoreDefinitionHHSSS.createScore(1, Long.MIN_VALUE, -20, 0, 0),
                scoreDefinitionHHSSS.createScore(1, -20, Long.MIN_VALUE, 0, 0)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        BendableLongScore input = scoreDefinitionHSS.createScore(-5000000000L, -300000000000L, -4000000000000L);
        PlannerTestUtils.serializeAndDeserializeWithAll(input,
                new PlannerTestUtils.OutputAsserter<BendableLongScore>() {
                    public void assertOutput(BendableLongScore output) {
                        assertEquals(1, output.getHardLevelsSize());
                        assertEquals(-5000000000L, output.getHardScore(0));
                        assertEquals(2, output.getSoftLevelsSize());
                        assertEquals(-300000000000L, output.getSoftScore(0));
                        assertEquals(-4000000000000L, output.getSoftScore(1));
                    }
                }
        );
    }

}
