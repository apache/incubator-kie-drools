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

package org.optaplanner.core.api.score.buildin.hardsoftlong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class HardSoftLongScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(HardSoftLongScore.valueOf(-147L, -258L),
                HardSoftLongScore.parseScore("-147hard/-258soft"));
        assertEquals(HardSoftLongScore.valueOfUninitialized(-7, -147L, -258L),
                HardSoftLongScore.parseScore("-7init/-147hard/-258soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", HardSoftLongScore.valueOf(0L, 0L).toShortString());
        assertEquals("-258soft", HardSoftLongScore.valueOf(0L, -258L).toShortString());
        assertEquals("-147hard", HardSoftLongScore.valueOf(-147L, 0L).toShortString());
        assertEquals("-147hard/-258soft", HardSoftLongScore.valueOf(-147L, -258L).toShortString());
        assertEquals("-7init", HardSoftLongScore.valueOfUninitialized(-7, 0L, 0L).toShortString());
        assertEquals("-7init/-258soft", HardSoftLongScore.valueOfUninitialized(-7, 0L, -258L).toShortString());
        assertEquals("-7init/-147hard/-258soft", HardSoftLongScore.valueOfUninitialized(-7, -147L, -258L).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0hard/-258soft", HardSoftLongScore.valueOf(0L, -258L).toString());
        assertEquals("-147hard/-258soft", HardSoftLongScore.valueOf(-147L, -258L).toString());
        assertEquals("-7init/-147hard/-258soft", HardSoftLongScore.valueOfUninitialized(-7, -147L, -258L).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftLongScore.parseScore("-147");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardSoftLongScore.valueOf(-147L, -258L),
                HardSoftLongScore.valueOf(-147L, -258L).toInitializedScore());
        assertEquals(HardSoftLongScore.valueOf(-147L, -258L),
                HardSoftLongScore.valueOfUninitialized(-7, -147L, -258L).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(HardSoftLongScore.valueOfUninitialized(-7, -147L, -258L),
                HardSoftLongScore.valueOf(-147L, -258L).withInitScore(-7));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftLongScore.valueOf(-5L, -300L),
                HardSoftLongScore.valueOfUninitialized(-7, -5L, -300L),
                HardSoftLongScore.valueOfUninitialized(-7, 0L, -300L)
        );
        assertScoreFeasible(
                HardSoftLongScore.valueOf(0L, -300L),
                HardSoftLongScore.valueOf(2L, -300L),
                HardSoftLongScore.valueOfUninitialized(0, 0L, -300L)
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftLongScore.valueOf(19L, -320L),
                HardSoftLongScore.valueOf(20L, -20L).add(
                        HardSoftLongScore.valueOf(-1L, -300L)));
        assertEquals(HardSoftLongScore.valueOfUninitialized(-77, 19L, -320L),
                HardSoftLongScore.valueOfUninitialized(-70, 20L, -20L).add(
                        HardSoftLongScore.valueOfUninitialized(-7, -1L, -300L)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftLongScore.valueOf(21L, 280L),
                HardSoftLongScore.valueOf(20L, -20L).subtract(
                        HardSoftLongScore.valueOf(-1L, -300L)));
        assertEquals(HardSoftLongScore.valueOfUninitialized(-63, 21L, 280L),
                HardSoftLongScore.valueOfUninitialized(-70, 20L, -20L).subtract(
                        HardSoftLongScore.valueOfUninitialized(-7, -1L, -300L)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftLongScore.valueOf(6L, -6L),
                HardSoftLongScore.valueOf(5L, -5L).multiply(1.2));
        assertEquals(HardSoftLongScore.valueOf(1L, -2L),
                HardSoftLongScore.valueOf(1L, -1L).multiply(1.2));
        assertEquals(HardSoftLongScore.valueOf(4L, -5L),
                HardSoftLongScore.valueOf(4L, -4L).multiply(1.2));
        assertEquals(HardSoftLongScore.valueOfUninitialized(-14, 8L, -10L),
                HardSoftLongScore.valueOfUninitialized(-7, 4L, -5L).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftLongScore.valueOf(5L, -5L),
                HardSoftLongScore.valueOf(25L, -25L).divide(5.0));
        assertEquals(HardSoftLongScore.valueOf(4L, -5L),
                HardSoftLongScore.valueOf(21L, -21L).divide(5.0));
        assertEquals(HardSoftLongScore.valueOf(4L, -5L),
                HardSoftLongScore.valueOf(24L, -24L).divide(5.0));
        assertEquals(HardSoftLongScore.valueOfUninitialized(-7, 4L, -5L),
                HardSoftLongScore.valueOfUninitialized(-14, 8L, -10L).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftLongScore.valueOf(16L, 25L),
                HardSoftLongScore.valueOf(-4L, 5L).power(2.0));
        assertEquals(HardSoftLongScore.valueOf(4L, 5L),
                HardSoftLongScore.valueOf(16L, 25L).power(0.5));
        assertEquals(HardSoftLongScore.valueOfUninitialized(-343, -64L, 125L),
                HardSoftLongScore.valueOfUninitialized(-7, -4L, 5L).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftLongScore.valueOf(-4L, 5L),
                HardSoftLongScore.valueOf(4L, -5L).negate());
        assertEquals(HardSoftLongScore.valueOf(4L, -5L),
                HardSoftLongScore.valueOf(-4L, 5L).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardSoftLongScore.valueOf(-10L, -200L),
                HardSoftLongScore.valueOf(-10L, -200L),
                HardSoftLongScore.valueOfUninitialized(0, -10L, -200L)
        );
        assertScoresEqualsAndHashCode(
                HardSoftLongScore.valueOfUninitialized(-7, -10L, -200L),
                HardSoftLongScore.valueOfUninitialized(-7, -10L, -200L)
        );
        assertScoresNotEquals(
                HardSoftLongScore.valueOf(-10L, -200L),
                HardSoftLongScore.valueOf(-30L, -200L),
                HardSoftLongScore.valueOf(-10L, -400L),
                HardSoftLongScore.valueOfUninitialized(-7, -10L, -200L)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftLongScore.valueOfUninitialized(-8, 0L, 0L),
                HardSoftLongScore.valueOfUninitialized(-7, -20L, -20L),
                HardSoftLongScore.valueOfUninitialized(-7, -1L, -300L),
                HardSoftLongScore.valueOfUninitialized(-7, 0L, 0L),
                HardSoftLongScore.valueOfUninitialized(-7, 0L, 1L),
                HardSoftLongScore.valueOf(-20L, Long.MIN_VALUE),
                HardSoftLongScore.valueOf(-20L, -20L),
                HardSoftLongScore.valueOf(-1L, -300L),
                HardSoftLongScore.valueOf(-1L, 4000L),
                HardSoftLongScore.valueOf(0L, -1L),
                HardSoftLongScore.valueOf(0L, 0L),
                HardSoftLongScore.valueOf(0L, 1L)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftLongScore.valueOf(-12, 3400L),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12L, output.getHardScore());
                    assertEquals(3400L, output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftLongScore.valueOfUninitialized(-7, -12L, 3400L),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12L, output.getHardScore());
                    assertEquals(3400L, output.getSoftScore());
                }
        );
    }

}
