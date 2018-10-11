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
    public void of() {
        assertEquals(HardSoftScore.of(-147, 0),
                HardSoftScore.ofHard(-147));
        assertEquals(HardSoftScore.of(0, -258),
                HardSoftScore.ofSoft(-258));
    }

    @Test
    public void parseScore() {
        assertEquals(HardSoftScore.of(-147, -258), HardSoftScore.parseScore("-147hard/-258soft"));
        assertEquals(HardSoftScore.ofUninitialized(-7, -147, -258), HardSoftScore.parseScore("-7init/-147hard/-258soft"));
        assertEquals(HardSoftScore.of(-147, Integer.MIN_VALUE), HardSoftScore.parseScore("-147hard/*soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", HardSoftScore.of(0, 0).toShortString());
        assertEquals("-258soft", HardSoftScore.of(0, -258).toShortString());
        assertEquals("-147hard", HardSoftScore.of(-147, 0).toShortString());
        assertEquals("-147hard/-258soft", HardSoftScore.of(-147, -258).toShortString());
        assertEquals("-7init", HardSoftScore.ofUninitialized(-7, 0, 0).toShortString());
        assertEquals("-7init/-258soft", HardSoftScore.ofUninitialized(-7, 0, -258).toShortString());
        assertEquals("-7init/-147hard/-258soft", HardSoftScore.ofUninitialized(-7, -147, -258).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0hard/-258soft", HardSoftScore.of(0, -258).toString());
        assertEquals("-147hard/-258soft", HardSoftScore.of(-147, -258).toString());
        assertEquals("-7init/-147hard/-258soft", HardSoftScore.ofUninitialized(-7, -147, -258).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftScore.parseScore("-147");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardSoftScore.of(-147, -258),
                HardSoftScore.of(-147, -258).toInitializedScore());
        assertEquals(HardSoftScore.of(-147, -258),
                HardSoftScore.ofUninitialized(-7, -147, -258).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(HardSoftScore.ofUninitialized(-7, -147, -258),
                HardSoftScore.of(-147, -258).withInitScore(-7));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftScore.of(-5, -300),
                HardSoftScore.ofUninitialized(-7, -5, -300),
                HardSoftScore.ofUninitialized(-7, 0, -300)
        );
        assertScoreFeasible(
                HardSoftScore.of(0, -300),
                HardSoftScore.of(2, -300),
                HardSoftScore.ofUninitialized(0, 0, -300)
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftScore.of(19, -320),
                HardSoftScore.of(20, -20).add(
                        HardSoftScore.of(-1, -300)));
        assertEquals(HardSoftScore.ofUninitialized(-77, 19, -320),
                HardSoftScore.ofUninitialized(-70, 20, -20).add(
                        HardSoftScore.ofUninitialized(-7, -1, -300)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftScore.of(21, 280),
                HardSoftScore.of(20, -20).subtract(
                        HardSoftScore.of(-1, -300)));
        assertEquals(HardSoftScore.ofUninitialized(-63, 21, 280),
                HardSoftScore.ofUninitialized(-70, 20, -20).subtract(
                        HardSoftScore.ofUninitialized(-7, -1, -300)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftScore.of(6, -6),
                HardSoftScore.of(5, -5).multiply(1.2));
        assertEquals(HardSoftScore.of(1, -2),
                HardSoftScore.of(1, -1).multiply(1.2));
        assertEquals(HardSoftScore.of(4, -5),
                HardSoftScore.of(4, -4).multiply(1.2));
        assertEquals(HardSoftScore.ofUninitialized(-14, 8, -10),
                HardSoftScore.ofUninitialized(-7, 4, -5).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftScore.of(5, -5),
                HardSoftScore.of(25, -25).divide(5.0));
        assertEquals(HardSoftScore.of(4, -5),
                HardSoftScore.of(21, -21).divide(5.0));
        assertEquals(HardSoftScore.of(4, -5),
                HardSoftScore.of(24, -24).divide(5.0));
        assertEquals(HardSoftScore.ofUninitialized(-7, 4, -5),
                HardSoftScore.ofUninitialized(-14, 8, -10).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftScore.of(16, 25),
                HardSoftScore.of(-4, 5).power(2.0));
        assertEquals(HardSoftScore.of(4, 5),
                HardSoftScore.of(16, 25).power(0.5));
        assertEquals(HardSoftScore.ofUninitialized(-343, 64, 125),
                HardSoftScore.ofUninitialized(-7, 4, 5).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftScore.of(4, -5),
                HardSoftScore.of(-4, 5).negate());
        assertEquals(HardSoftScore.of(-4, 5),
                HardSoftScore.of(4, -5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardSoftScore.of(-10, -200),
                HardSoftScore.of(-10, -200),
                HardSoftScore.ofUninitialized(0, -10, -200)
        );
        PlannerAssert.assertObjectsAreEqual(
                HardSoftScore.ofUninitialized(-7, -10, -200),
                HardSoftScore.ofUninitialized(-7, -10, -200)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                HardSoftScore.of(-10, -200),
                HardSoftScore.of(-30, -200),
                HardSoftScore.of(-10, -400),
                HardSoftScore.ofUninitialized(-7, -10, -200)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftScore.ofUninitialized(-8, 0, 0),
                HardSoftScore.ofUninitialized(-7, -20, -20),
                HardSoftScore.ofUninitialized(-7, -1, -300),
                HardSoftScore.ofUninitialized(-7, 0, 0),
                HardSoftScore.ofUninitialized(-7, 0, 1),
                HardSoftScore.of(-20, Integer.MIN_VALUE),
                HardSoftScore.of(-20, -20),
                HardSoftScore.of(-1, -300),
                HardSoftScore.of(-1, 4000),
                HardSoftScore.of(0, -1),
                HardSoftScore.of(0, 0),
                HardSoftScore.of(0, 1)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftScore.of(-12, 3400),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftScore.ofUninitialized(-7, -12, 3400),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12, output.getHardScore());
                    assertEquals(3400, output.getSoftScore());
                }
        );
    }

}
