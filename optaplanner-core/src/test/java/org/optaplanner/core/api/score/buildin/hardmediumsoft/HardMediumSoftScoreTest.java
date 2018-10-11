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
    public void of() {
        assertEquals(HardMediumSoftScore.of(-147, 0, 0),
                HardMediumSoftScore.ofHard(-147));
        assertEquals(HardMediumSoftScore.of(0, -258, 0),
                HardMediumSoftScore.ofMedium(-258));
        assertEquals(HardMediumSoftScore.of(0, 0, -369),
                HardMediumSoftScore.ofSoft(-369));
    }

    @Test
    public void parseScore() {
        assertEquals(HardMediumSoftScore.of(-147, -258, -369),
                HardMediumSoftScore.parseScore("-147hard/-258medium/-369soft"));
        assertEquals(HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369),
                HardMediumSoftScore.parseScore("-7init/-147hard/-258medium/-369soft"));
        assertEquals(HardMediumSoftScore.of(-147, -258, Integer.MIN_VALUE),
                HardMediumSoftScore.parseScore("-147hard/-258medium/*soft"));
        assertEquals(HardMediumSoftScore.of(-147, Integer.MIN_VALUE, -369),
                HardMediumSoftScore.parseScore("-147hard/*medium/-369soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("0",
                HardMediumSoftScore.of(0, 0, 0).toShortString());
        assertEquals("-369soft",
                HardMediumSoftScore.of(0, 0, -369).toShortString());
        assertEquals("-258medium",
                HardMediumSoftScore.of(0, -258, 0).toShortString());
        assertEquals("-258medium/-369soft",
                HardMediumSoftScore.of(0, -258, -369).toShortString());
        assertEquals("-147hard/-258medium/-369soft",
                HardMediumSoftScore.of(-147, -258, -369).toShortString());
        assertEquals("-7init/-258medium",
                HardMediumSoftScore.ofUninitialized(-7, 0, -258, 0).toShortString());
        assertEquals("-7init/-147hard/-258medium/-369soft",
                HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0hard/-258medium/-369soft",
                HardMediumSoftScore.of(0, -258, -369).toString());
        assertEquals("-147hard/-258medium/-369soft",
                HardMediumSoftScore.of(-147, -258, -369).toString());
        assertEquals("-7init/-147hard/-258medium/-369soft",
                HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardMediumSoftScore.parseScore("-147");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardMediumSoftScore.of(-147, -258, -369),
                HardMediumSoftScore.of(-147, -258, -369).toInitializedScore());
        assertEquals(HardMediumSoftScore.of(-147, -258, -369),
                HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(HardMediumSoftScore.ofUninitialized(-7, -147, -258, -369),
                HardMediumSoftScore.of(-147, -258, -369).withInitScore(-7));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardMediumSoftScore.of(-5, -300, -4000),
                HardMediumSoftScore.ofUninitialized(-7, -5, -300, -4000),
                HardMediumSoftScore.ofUninitialized(-7, 0, -300, -4000)
        );
        assertScoreFeasible(
                HardMediumSoftScore.of(0, -300, -4000),
                HardMediumSoftScore.of(2, -300, -4000),
                HardMediumSoftScore.ofUninitialized(0, 0, -300, -4000)
        );
    }

    @Test
    public void add() {
        assertEquals(HardMediumSoftScore.of(19, -320, 0),
                HardMediumSoftScore.of(20, -20, -4000).add(
                        HardMediumSoftScore.of(-1, -300, 4000)));
        assertEquals(HardMediumSoftScore.ofUninitialized(-77, 19, -320, 0),
                HardMediumSoftScore.ofUninitialized(-70, 20, -20, -4000).add(
                        HardMediumSoftScore.ofUninitialized(-7, -1, -300, 4000)));
    }

    @Test
    public void subtract() {
        assertEquals(HardMediumSoftScore.of(21, 280, -8000),
                HardMediumSoftScore.of(20, -20, -4000).subtract(
                        HardMediumSoftScore.of(-1, -300, 4000)));
        assertEquals(HardMediumSoftScore.ofUninitialized(-63, 21, 280, -8000),
                HardMediumSoftScore.ofUninitialized(-70, 20, -20, -4000).subtract(
                        HardMediumSoftScore.ofUninitialized(-7, -1, -300, 4000)));
    }

    @Test
    public void multiply() {
        assertEquals(HardMediumSoftScore.of(6, -6, 6),
                HardMediumSoftScore.of(5, -5, 5).multiply(1.2));
        assertEquals(HardMediumSoftScore.of(1, -2, 1),
                HardMediumSoftScore.of(1, -1, 1).multiply(1.2));
        assertEquals(HardMediumSoftScore.of(4, -5, 4),
                HardMediumSoftScore.of(4, -4, 4).multiply(1.2));
        assertEquals(HardMediumSoftScore.ofUninitialized(-14, 8, -10, 12),
                HardMediumSoftScore.ofUninitialized(-7, 4, -5, 6).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardMediumSoftScore.of(5, -5, 5),
                HardMediumSoftScore.of(25, -25, 25).divide(5.0));
        assertEquals(HardMediumSoftScore.of(4, -5, 4),
                HardMediumSoftScore.of(21, -21, 21).divide(5.0));
        assertEquals(HardMediumSoftScore.of(4, -5, 4),
                HardMediumSoftScore.of(24, -24, 24).divide(5.0));
        assertEquals(HardMediumSoftScore.ofUninitialized(-7, 4, -5, 6),
                HardMediumSoftScore.ofUninitialized(-14, 8, -10, 12).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardMediumSoftScore.of(9, 16, 25),
                HardMediumSoftScore.of(3, -4, 5).power(2.0));
        assertEquals(HardMediumSoftScore.of(3, 4, 5),
                HardMediumSoftScore.of(9, 16, 25).power(0.5));
        assertEquals(HardMediumSoftScore.ofUninitialized(-343, 27, -64, 125),
                HardMediumSoftScore.ofUninitialized(-7, 3, -4, 5).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardMediumSoftScore.of(-3, 4, -5),
                HardMediumSoftScore.of(3, -4, 5).negate());
        assertEquals(HardMediumSoftScore.of(3, -4, 5),
                HardMediumSoftScore.of(-3, 4, -5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftScore.of(-10, -200, -3000),
                HardMediumSoftScore.of(-10, -200, -3000),
                HardMediumSoftScore.ofUninitialized(0, -10, -200, -3000)
        );
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftScore.ofUninitialized(-7, -10, -200, -3000),
                HardMediumSoftScore.ofUninitialized(-7, -10, -200, -3000)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                HardMediumSoftScore.of(-10, -200, -3000),
                HardMediumSoftScore.of(-30, -200, -3000),
                HardMediumSoftScore.of(-10, -400, -3000),
                HardMediumSoftScore.of(-10, -400, -5000),
                HardMediumSoftScore.ofUninitialized(-7, -10, -200, -3000)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardMediumSoftScore.ofUninitialized(-8, 0, 0, 0),
                HardMediumSoftScore.ofUninitialized(-7, -20, -20, -20),
                HardMediumSoftScore.ofUninitialized(-7, -1, -300, -4000),
                HardMediumSoftScore.ofUninitialized(-7, 0, 0, 0),
                HardMediumSoftScore.ofUninitialized(-7, 0, 0, 1),
                HardMediumSoftScore.ofUninitialized(-7, 0, 1, 0),
                HardMediumSoftScore.of(-20, Integer.MIN_VALUE, Integer.MIN_VALUE),
                HardMediumSoftScore.of(-20, Integer.MIN_VALUE, -20),
                HardMediumSoftScore.of(-20, Integer.MIN_VALUE, 1),
                HardMediumSoftScore.of(-20, -300, -4000),
                HardMediumSoftScore.of(-20, -300, -300),
                HardMediumSoftScore.of(-20, -300, -20),
                HardMediumSoftScore.of(-20, -300, 300),
                HardMediumSoftScore.of(-20, -20, -300),
                HardMediumSoftScore.of(-20, -20, 0),
                HardMediumSoftScore.of(-20, -20, 1),
                HardMediumSoftScore.of(-1, -300, -4000),
                HardMediumSoftScore.of(-1, -300, -20),
                HardMediumSoftScore.of(-1, -20, -300),
                HardMediumSoftScore.of(1, Integer.MIN_VALUE, -20),
                HardMediumSoftScore.of(1, -20, Integer.MIN_VALUE)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardMediumSoftScore.of(-12, 3400, -56),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getMediumScore());
                    assertEquals(-56, output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardMediumSoftScore.ofUninitialized(-7, -12, 3400, -56),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getMediumScore());
                    assertEquals(-56, output.getSoftScore());
                }
        );
    }

}
