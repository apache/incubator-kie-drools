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

package org.optaplanner.core.api.score.buildin.hardmediumsoft;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class HardMediumSoftScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(HardMediumSoftScore.valueOfInitialized(-147, -258, -369),
                HardMediumSoftScore.parseScore("-147hard/-258medium/-369soft"));
        assertEquals(HardMediumSoftScore.valueOf(-7, -147, -258, -369),
                HardMediumSoftScore.parseScore("-7init/-147hard/-258medium/-369soft"));
    }

    @Test
    public void testToString() {
        assertEquals("-147hard/-258medium/-369soft",
                HardMediumSoftScore.valueOfInitialized(-147, -258, -369).toString());
        assertEquals("-7init/-147hard/-258medium/-369soft",
                HardMediumSoftScore.valueOf(-7, -147, -258, -369).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardMediumSoftScore.parseScore("-147");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardMediumSoftScore.valueOfInitialized(-147, -258, -369),
                HardMediumSoftScore.valueOfInitialized(-147, -258, -369).toInitializedScore());
        assertEquals(HardMediumSoftScore.valueOfInitialized(-147, -258, -369),
                HardMediumSoftScore.valueOf(-7, -147, -258, -369).toInitializedScore());
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardMediumSoftScore.valueOfInitialized(-5, -300, -4000),
                HardMediumSoftScore.valueOf(-7, -5, -300, -4000),
                HardMediumSoftScore.valueOf(-7, 0, -300, -4000)
        );
        assertScoreFeasible(
                HardMediumSoftScore.valueOfInitialized(0, -300, -4000),
                HardMediumSoftScore.valueOfInitialized(2, -300, -4000),
                HardMediumSoftScore.valueOf(0, 0, -300, -4000)
        );
    }

    @Test
    public void add() {
        assertEquals(HardMediumSoftScore.valueOfInitialized(19, -320, 0),
                HardMediumSoftScore.valueOfInitialized(20, -20, -4000).add(
                        HardMediumSoftScore.valueOfInitialized(-1, -300, 4000)));
        assertEquals(HardMediumSoftScore.valueOf(-77, 19, -320, 0),
                HardMediumSoftScore.valueOf(-70, 20, -20, -4000).add(
                        HardMediumSoftScore.valueOf(-7, -1, -300, 4000)));
    }

    @Test
    public void subtract() {
        assertEquals(HardMediumSoftScore.valueOfInitialized(21, 280, -8000),
                HardMediumSoftScore.valueOfInitialized(20, -20, -4000).subtract(
                        HardMediumSoftScore.valueOfInitialized(-1, -300, 4000)));
        assertEquals(HardMediumSoftScore.valueOf(-63, 21, 280, -8000),
                HardMediumSoftScore.valueOf(-70, 20, -20, -4000).subtract(
                        HardMediumSoftScore.valueOf(-7, -1, -300, 4000)));
    }

    @Test
    public void multiply() {
        assertEquals(HardMediumSoftScore.valueOfInitialized(6, -6, 6),
                HardMediumSoftScore.valueOfInitialized(5, -5, 5).multiply(1.2));
        assertEquals(HardMediumSoftScore.valueOfInitialized(1, -2, 1),
                HardMediumSoftScore.valueOfInitialized(1, -1, 1).multiply(1.2));
        assertEquals(HardMediumSoftScore.valueOfInitialized(4, -5, 4),
                HardMediumSoftScore.valueOfInitialized(4, -4, 4).multiply(1.2));
        assertEquals(HardMediumSoftScore.valueOf(-14, 8, -10, 12),
                HardMediumSoftScore.valueOf(-7, 4, -5, 6).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardMediumSoftScore.valueOfInitialized(5, -5, 5),
                HardMediumSoftScore.valueOfInitialized(25, -25, 25).divide(5.0));
        assertEquals(HardMediumSoftScore.valueOfInitialized(4, -5, 4),
                HardMediumSoftScore.valueOfInitialized(21, -21, 21).divide(5.0));
        assertEquals(HardMediumSoftScore.valueOfInitialized(4, -5, 4),
                HardMediumSoftScore.valueOfInitialized(24, -24, 24).divide(5.0));
        assertEquals(HardMediumSoftScore.valueOf(-7, 4, -5, 6),
                HardMediumSoftScore.valueOf(-14, 8, -10, 12).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardMediumSoftScore.valueOfInitialized(9, 16, 25),
                HardMediumSoftScore.valueOfInitialized(3, -4, 5).power(2.0));
        assertEquals(HardMediumSoftScore.valueOfInitialized(3, 4, 5),
                HardMediumSoftScore.valueOfInitialized(9, 16, 25).power(0.5));
        assertEquals(HardMediumSoftScore.valueOf(-343, 27, -64, 125),
                HardMediumSoftScore.valueOf(-7, 3, -4, 5).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardMediumSoftScore.valueOfInitialized(-3, 4, -5),
                HardMediumSoftScore.valueOfInitialized(3, -4, 5).negate());
        assertEquals(HardMediumSoftScore.valueOfInitialized(3, -4, 5),
                HardMediumSoftScore.valueOfInitialized(-3, 4, -5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardMediumSoftScore.valueOfInitialized(-10, -200, -3000),
                HardMediumSoftScore.valueOfInitialized(-10, -200, -3000),
                HardMediumSoftScore.valueOf(0, -10, -200, -3000)
        );
        assertScoresEqualsAndHashCode(
                HardMediumSoftScore.valueOf(-7, -10, -200, -3000),
                HardMediumSoftScore.valueOf(-7, -10, -200, -3000)
        );
        assertScoresNotEquals(
                HardMediumSoftScore.valueOfInitialized(-10, -200, -3000),
                HardMediumSoftScore.valueOfInitialized(-30, -200, -3000),
                HardMediumSoftScore.valueOfInitialized(-10, -400, -3000),
                HardMediumSoftScore.valueOfInitialized(-10, -400, -5000),
                HardMediumSoftScore.valueOf(-7, -10, -200, -3000)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardMediumSoftScore.valueOf(-8, 0, 0, 0),
                HardMediumSoftScore.valueOf(-7, -20, -20, -20),
                HardMediumSoftScore.valueOf(-7, -1, -300, -4000),
                HardMediumSoftScore.valueOf(-7, 0, 0, 0),
                HardMediumSoftScore.valueOf(-7, 0, 0, 1),
                HardMediumSoftScore.valueOf(-7, 0, 1, 0),
                HardMediumSoftScore.valueOfInitialized(-20, Integer.MIN_VALUE, Integer.MIN_VALUE),
                HardMediumSoftScore.valueOfInitialized(-20, Integer.MIN_VALUE, -20),
                HardMediumSoftScore.valueOfInitialized(-20, Integer.MIN_VALUE, 1),
                HardMediumSoftScore.valueOfInitialized(-20, -300, -4000),
                HardMediumSoftScore.valueOfInitialized(-20, -300, -300),
                HardMediumSoftScore.valueOfInitialized(-20, -300, -20),
                HardMediumSoftScore.valueOfInitialized(-20, -300, 300),
                HardMediumSoftScore.valueOfInitialized(-20, -20, -300),
                HardMediumSoftScore.valueOfInitialized(-20, -20, 0),
                HardMediumSoftScore.valueOfInitialized(-20, -20, 1),
                HardMediumSoftScore.valueOfInitialized(-1, -300, -4000),
                HardMediumSoftScore.valueOfInitialized(-1, -300, -20),
                HardMediumSoftScore.valueOfInitialized(-1, -20, -300),
                HardMediumSoftScore.valueOfInitialized(1, Integer.MIN_VALUE, -20),
                HardMediumSoftScore.valueOfInitialized(1, -20, Integer.MIN_VALUE)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardMediumSoftScore.valueOfInitialized(-12, 3400, -56),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getMediumScore());
                    assertEquals(-56, output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardMediumSoftScore.valueOf(-7, -12, 3400, -56),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getMediumScore());
                    assertEquals(-56, output.getSoftScore());
                }
        );
    }

}
