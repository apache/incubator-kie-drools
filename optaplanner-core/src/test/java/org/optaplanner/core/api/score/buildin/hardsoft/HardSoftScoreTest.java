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
        assertEquals(HardSoftScore.valueOf(-147, -258), HardSoftScore.parseScore("-147hard/-258soft"));
        assertEquals(HardSoftScore.valueOfUninitialized(-7, -147, -258), HardSoftScore.parseScore("-7init/-147hard/-258soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", HardSoftScore.valueOf(0, 0).toShortString());
        assertEquals("-258soft", HardSoftScore.valueOf(0, -258).toShortString());
        assertEquals("-147hard", HardSoftScore.valueOf(-147, 0).toShortString());
        assertEquals("-147hard/-258soft", HardSoftScore.valueOf(-147, -258).toShortString());
        assertEquals("-7init", HardSoftScore.valueOfUninitialized(-7, 0, 0).toShortString());
        assertEquals("-7init/-258soft", HardSoftScore.valueOfUninitialized(-7, 0, -258).toShortString());
        assertEquals("-7init/-147hard/-258soft", HardSoftScore.valueOfUninitialized(-7, -147, -258).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0hard/-258soft", HardSoftScore.valueOf(0, -258).toString());
        assertEquals("-147hard/-258soft", HardSoftScore.valueOf(-147, -258).toString());
        assertEquals("-7init/-147hard/-258soft", HardSoftScore.valueOfUninitialized(-7, -147, -258).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftScore.parseScore("-147");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardSoftScore.valueOf(-147, -258),
                HardSoftScore.valueOf(-147, -258).toInitializedScore());
        assertEquals(HardSoftScore.valueOf(-147, -258),
                HardSoftScore.valueOfUninitialized(-7, -147, -258).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(HardSoftScore.valueOfUninitialized(-7, -147, -258),
                HardSoftScore.valueOf(-147, -258).withInitScore(-7));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftScore.valueOf(-5, -300),
                HardSoftScore.valueOfUninitialized(-7, -5, -300),
                HardSoftScore.valueOfUninitialized(-7, 0, -300)
        );
        assertScoreFeasible(
                HardSoftScore.valueOf(0, -300),
                HardSoftScore.valueOf(2, -300),
                HardSoftScore.valueOfUninitialized(0, 0, -300)
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftScore.valueOf(19, -320),
                HardSoftScore.valueOf(20, -20).add(
                        HardSoftScore.valueOf(-1, -300)));
        assertEquals(HardSoftScore.valueOfUninitialized(-77, 19, -320),
                HardSoftScore.valueOfUninitialized(-70, 20, -20).add(
                        HardSoftScore.valueOfUninitialized(-7, -1, -300)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftScore.valueOf(21, 280),
                HardSoftScore.valueOf(20, -20).subtract(
                        HardSoftScore.valueOf(-1, -300)));
        assertEquals(HardSoftScore.valueOfUninitialized(-63, 21, 280),
                HardSoftScore.valueOfUninitialized(-70, 20, -20).subtract(
                        HardSoftScore.valueOfUninitialized(-7, -1, -300)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftScore.valueOf(6, -6),
                HardSoftScore.valueOf(5, -5).multiply(1.2));
        assertEquals(HardSoftScore.valueOf(1, -2),
                HardSoftScore.valueOf(1, -1).multiply(1.2));
        assertEquals(HardSoftScore.valueOf(4, -5),
                HardSoftScore.valueOf(4, -4).multiply(1.2));
        assertEquals(HardSoftScore.valueOfUninitialized(-14, 8, -10),
                HardSoftScore.valueOfUninitialized(-7, 4, -5).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftScore.valueOf(5, -5),
                HardSoftScore.valueOf(25, -25).divide(5.0));
        assertEquals(HardSoftScore.valueOf(4, -5),
                HardSoftScore.valueOf(21, -21).divide(5.0));
        assertEquals(HardSoftScore.valueOf(4, -5),
                HardSoftScore.valueOf(24, -24).divide(5.0));
        assertEquals(HardSoftScore.valueOfUninitialized(-7, 4, -5),
                HardSoftScore.valueOfUninitialized(-14, 8, -10).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftScore.valueOf(16, 25),
                HardSoftScore.valueOf(-4, 5).power(2.0));
        assertEquals(HardSoftScore.valueOf(4, 5),
                HardSoftScore.valueOf(16, 25).power(0.5));
        assertEquals(HardSoftScore.valueOfUninitialized(-343, 64, 125),
                HardSoftScore.valueOfUninitialized(-7, 4, 5).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftScore.valueOf(4, -5),
                HardSoftScore.valueOf(-4, 5).negate());
        assertEquals(HardSoftScore.valueOf(-4, 5),
                HardSoftScore.valueOf(4, -5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardSoftScore.valueOf(-10, -200),
                HardSoftScore.valueOf(-10, -200),
                HardSoftScore.valueOfUninitialized(0, -10, -200)
        );
        assertScoresEqualsAndHashCode(
                HardSoftScore.valueOfUninitialized(-7, -10, -200),
                HardSoftScore.valueOfUninitialized(-7, -10, -200)
        );
        assertScoresNotEquals(
                HardSoftScore.valueOf(-10, -200),
                HardSoftScore.valueOf(-30, -200),
                HardSoftScore.valueOf(-10, -400),
                HardSoftScore.valueOfUninitialized(-7, -10, -200)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftScore.valueOfUninitialized(-8, 0, 0),
                HardSoftScore.valueOfUninitialized(-7, -20, -20),
                HardSoftScore.valueOfUninitialized(-7, -1, -300),
                HardSoftScore.valueOfUninitialized(-7, 0, 0),
                HardSoftScore.valueOfUninitialized(-7, 0, 1),
                HardSoftScore.valueOf(-20, Integer.MIN_VALUE),
                HardSoftScore.valueOf(-20, -20),
                HardSoftScore.valueOf(-1, -300),
                HardSoftScore.valueOf(-1, 4000),
                HardSoftScore.valueOf(0, -1),
                HardSoftScore.valueOf(0, 0),
                HardSoftScore.valueOf(0, 1)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftScore.valueOf(-12, 3400),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftScore.valueOfUninitialized(-7, -12, 3400),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getSoftScore());
                }
        );
    }

}
