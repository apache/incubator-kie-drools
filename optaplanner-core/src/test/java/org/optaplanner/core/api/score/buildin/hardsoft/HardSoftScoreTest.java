/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardsoft;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class HardSoftScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(HardSoftScore.valueOfInitialized(-147, -258), HardSoftScore.parseScore("-147hard/-258soft"));
        assertEquals(HardSoftScore.valueOf(-7, -147, -258), HardSoftScore.parseScore("-7init/-147hard/-258soft"));
    }

    @Test
    public void testToString() {
        assertEquals("-147hard/-258soft", HardSoftScore.valueOfInitialized(-147, -258).toString());
        assertEquals("-7init/-147hard/-258soft", HardSoftScore.valueOf(-7, -147, -258).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftScore.parseScore("-147");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardSoftScore.valueOfInitialized(-147, -258),
                HardSoftScore.valueOfInitialized(-147, -258).toInitializedScore());
        assertEquals(HardSoftScore.valueOfInitialized(-147, -258),
                HardSoftScore.valueOf(-7, -147, -258).toInitializedScore());
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftScore.valueOfInitialized(-5, -300),
                HardSoftScore.valueOf(-7, -5, -300),
                HardSoftScore.valueOf(-7, 0, -300)
        );
        assertScoreFeasible(
                HardSoftScore.valueOfInitialized(0, -300),
                HardSoftScore.valueOfInitialized(2, -300),
                HardSoftScore.valueOf(0, 0, -300)
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftScore.valueOfInitialized(19, -320),
                HardSoftScore.valueOfInitialized(20, -20).add(
                        HardSoftScore.valueOfInitialized(-1, -300)));
        assertEquals(HardSoftScore.valueOf(-77, 19, -320),
                HardSoftScore.valueOf(-70, 20, -20).add(
                        HardSoftScore.valueOf(-7, -1, -300)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftScore.valueOfInitialized(21, 280),
                HardSoftScore.valueOfInitialized(20, -20).subtract(
                        HardSoftScore.valueOfInitialized(-1, -300)));
        assertEquals(HardSoftScore.valueOf(-63, 21, 280),
                HardSoftScore.valueOf(-70, 20, -20).subtract(
                        HardSoftScore.valueOf(-7, -1, -300)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftScore.valueOfInitialized(6, -6),
                HardSoftScore.valueOfInitialized(5, -5).multiply(1.2));
        assertEquals(HardSoftScore.valueOfInitialized(1, -2),
                HardSoftScore.valueOfInitialized(1, -1).multiply(1.2));
        assertEquals(HardSoftScore.valueOfInitialized(4, -5),
                HardSoftScore.valueOfInitialized(4, -4).multiply(1.2));
        assertEquals(HardSoftScore.valueOf(-14, 8, -10),
                HardSoftScore.valueOf(-7, 4, -5).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftScore.valueOfInitialized(5, -5),
                HardSoftScore.valueOfInitialized(25, -25).divide(5.0));
        assertEquals(HardSoftScore.valueOfInitialized(4, -5),
                HardSoftScore.valueOfInitialized(21, -21).divide(5.0));
        assertEquals(HardSoftScore.valueOfInitialized(4, -5),
                HardSoftScore.valueOfInitialized(24, -24).divide(5.0));
        assertEquals(HardSoftScore.valueOf(-7, 4, -5),
                HardSoftScore.valueOf(-14, 8, -10).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftScore.valueOfInitialized(16, 25),
                HardSoftScore.valueOfInitialized(-4, 5).power(2.0));
        assertEquals(HardSoftScore.valueOfInitialized(4, 5),
                HardSoftScore.valueOfInitialized(16, 25).power(0.5));
        assertEquals(HardSoftScore.valueOf(-343, 64, 125),
                HardSoftScore.valueOf(-7, 4, 5).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftScore.valueOfInitialized(4, -5),
                HardSoftScore.valueOfInitialized(-4, 5).negate());
        assertEquals(HardSoftScore.valueOfInitialized(-4, 5),
                HardSoftScore.valueOfInitialized(4, -5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardSoftScore.valueOfInitialized(-10, -200),
                HardSoftScore.valueOfInitialized(-10, -200),
                HardSoftScore.valueOf(0, -10, -200)
        );
        assertScoresEqualsAndHashCode(
                HardSoftScore.valueOf(-7, -10, -200),
                HardSoftScore.valueOf(-7, -10, -200)
        );
        assertScoresNotEquals(
                HardSoftScore.valueOfInitialized(-10, -200),
                HardSoftScore.valueOfInitialized(-30, -200),
                HardSoftScore.valueOfInitialized(-10, -400),
                HardSoftScore.valueOf(-7, -10, -200)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftScore.valueOf(-8, 0, 0),
                HardSoftScore.valueOf(-7, -20, -20),
                HardSoftScore.valueOf(-7, -1, -300),
                HardSoftScore.valueOf(-7, 0, 0),
                HardSoftScore.valueOf(-7, 0, 1),
                HardSoftScore.valueOfInitialized(-20, Integer.MIN_VALUE),
                HardSoftScore.valueOfInitialized(-20, -20),
                HardSoftScore.valueOfInitialized(-1, -300),
                HardSoftScore.valueOfInitialized(-1, 4000),
                HardSoftScore.valueOfInitialized(0, -1),
                HardSoftScore.valueOfInitialized(0, 0),
                HardSoftScore.valueOfInitialized(0, 1)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftScore.valueOfInitialized(-12, 3400),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftScore.valueOf(-7, -12, 3400),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getSoftScore());
                }
        );
    }

}
