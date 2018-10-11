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
    public void of() {
        assertEquals(HardSoftLongScore.of(-147L, 0L),
                HardSoftLongScore.ofHard(-147L));
        assertEquals(HardSoftLongScore.of(0L, -258L),
                HardSoftLongScore.ofSoft(-258L));
    }

    @Test
    public void parseScore() {
        assertEquals(HardSoftLongScore.of(-147L, -258L),
                HardSoftLongScore.parseScore("-147hard/-258soft"));
        assertEquals(HardSoftLongScore.ofUninitialized(-7, -147L, -258L),
                HardSoftLongScore.parseScore("-7init/-147hard/-258soft"));
        assertEquals(HardSoftLongScore.of(-147L, Long.MIN_VALUE),
                HardSoftLongScore.parseScore("-147hard/*soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", HardSoftLongScore.of(0L, 0L).toShortString());
        assertEquals("-258soft", HardSoftLongScore.of(0L, -258L).toShortString());
        assertEquals("-147hard", HardSoftLongScore.of(-147L, 0L).toShortString());
        assertEquals("-147hard/-258soft", HardSoftLongScore.of(-147L, -258L).toShortString());
        assertEquals("-7init", HardSoftLongScore.ofUninitialized(-7, 0L, 0L).toShortString());
        assertEquals("-7init/-258soft", HardSoftLongScore.ofUninitialized(-7, 0L, -258L).toShortString());
        assertEquals("-7init/-147hard/-258soft", HardSoftLongScore.ofUninitialized(-7, -147L, -258L).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0hard/-258soft", HardSoftLongScore.of(0L, -258L).toString());
        assertEquals("-147hard/-258soft", HardSoftLongScore.of(-147L, -258L).toString());
        assertEquals("-7init/-147hard/-258soft", HardSoftLongScore.ofUninitialized(-7, -147L, -258L).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftLongScore.parseScore("-147");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardSoftLongScore.of(-147L, -258L),
                HardSoftLongScore.of(-147L, -258L).toInitializedScore());
        assertEquals(HardSoftLongScore.of(-147L, -258L),
                HardSoftLongScore.ofUninitialized(-7, -147L, -258L).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(HardSoftLongScore.ofUninitialized(-7, -147L, -258L),
                HardSoftLongScore.of(-147L, -258L).withInitScore(-7));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftLongScore.of(-5L, -300L),
                HardSoftLongScore.ofUninitialized(-7, -5L, -300L),
                HardSoftLongScore.ofUninitialized(-7, 0L, -300L)
        );
        assertScoreFeasible(
                HardSoftLongScore.of(0L, -300L),
                HardSoftLongScore.of(2L, -300L),
                HardSoftLongScore.ofUninitialized(0, 0L, -300L)
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftLongScore.of(19L, -320L),
                HardSoftLongScore.of(20L, -20L).add(
                        HardSoftLongScore.of(-1L, -300L)));
        assertEquals(HardSoftLongScore.ofUninitialized(-77, 19L, -320L),
                HardSoftLongScore.ofUninitialized(-70, 20L, -20L).add(
                        HardSoftLongScore.ofUninitialized(-7, -1L, -300L)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftLongScore.of(21L, 280L),
                HardSoftLongScore.of(20L, -20L).subtract(
                        HardSoftLongScore.of(-1L, -300L)));
        assertEquals(HardSoftLongScore.ofUninitialized(-63, 21L, 280L),
                HardSoftLongScore.ofUninitialized(-70, 20L, -20L).subtract(
                        HardSoftLongScore.ofUninitialized(-7, -1L, -300L)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftLongScore.of(6L, -6L),
                HardSoftLongScore.of(5L, -5L).multiply(1.2));
        assertEquals(HardSoftLongScore.of(1L, -2L),
                HardSoftLongScore.of(1L, -1L).multiply(1.2));
        assertEquals(HardSoftLongScore.of(4L, -5L),
                HardSoftLongScore.of(4L, -4L).multiply(1.2));
        assertEquals(HardSoftLongScore.ofUninitialized(-14, 8L, -10L),
                HardSoftLongScore.ofUninitialized(-7, 4L, -5L).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftLongScore.of(5L, -5L),
                HardSoftLongScore.of(25L, -25L).divide(5.0));
        assertEquals(HardSoftLongScore.of(4L, -5L),
                HardSoftLongScore.of(21L, -21L).divide(5.0));
        assertEquals(HardSoftLongScore.of(4L, -5L),
                HardSoftLongScore.of(24L, -24L).divide(5.0));
        assertEquals(HardSoftLongScore.ofUninitialized(-7, 4L, -5L),
                HardSoftLongScore.ofUninitialized(-14, 8L, -10L).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftLongScore.of(16L, 25L),
                HardSoftLongScore.of(-4L, 5L).power(2.0));
        assertEquals(HardSoftLongScore.of(4L, 5L),
                HardSoftLongScore.of(16L, 25L).power(0.5));
        assertEquals(HardSoftLongScore.ofUninitialized(-343, -64L, 125L),
                HardSoftLongScore.ofUninitialized(-7, -4L, 5L).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftLongScore.of(-4L, 5L),
                HardSoftLongScore.of(4L, -5L).negate());
        assertEquals(HardSoftLongScore.of(4L, -5L),
                HardSoftLongScore.of(-4L, 5L).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardSoftLongScore.of(-10L, -200L),
                HardSoftLongScore.of(-10L, -200L),
                HardSoftLongScore.ofUninitialized(0, -10L, -200L)
        );
        PlannerAssert.assertObjectsAreEqual(
                HardSoftLongScore.ofUninitialized(-7, -10L, -200L),
                HardSoftLongScore.ofUninitialized(-7, -10L, -200L)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                HardSoftLongScore.of(-10L, -200L),
                HardSoftLongScore.of(-30L, -200L),
                HardSoftLongScore.of(-10L, -400L),
                HardSoftLongScore.ofUninitialized(-7, -10L, -200L)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftLongScore.ofUninitialized(-8, 0L, 0L),
                HardSoftLongScore.ofUninitialized(-7, -20L, -20L),
                HardSoftLongScore.ofUninitialized(-7, -1L, -300L),
                HardSoftLongScore.ofUninitialized(-7, 0L, 0L),
                HardSoftLongScore.ofUninitialized(-7, 0L, 1L),
                HardSoftLongScore.of(-20L, Long.MIN_VALUE),
                HardSoftLongScore.of(-20L, -20L),
                HardSoftLongScore.of(-1L, -300L),
                HardSoftLongScore.of(-1L, 4000L),
                HardSoftLongScore.of(0L, -1L),
                HardSoftLongScore.of(0L, 0L),
                HardSoftLongScore.of(0L, 1L)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftLongScore.of(-12, 3400L),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12L, output.getHardScore());
                    assertEquals(3400L, output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftLongScore.ofUninitialized(-7, -12L, 3400L),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12L, output.getHardScore());
                    assertEquals(3400L, output.getSoftScore());
                }
        );
    }

}
