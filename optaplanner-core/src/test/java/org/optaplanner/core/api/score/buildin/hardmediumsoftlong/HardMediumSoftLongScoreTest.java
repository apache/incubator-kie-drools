/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.buildin.hardmediumsoftlong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class HardMediumSoftLongScoreTest extends AbstractScoreTest {

    @Test
    public void of() {
        assertEquals(HardMediumSoftLongScore.of(-147L, 0L, 0L),
                HardMediumSoftLongScore.ofHard(-147L));
        assertEquals(HardMediumSoftLongScore.of(0L, -258L, 0L),
                HardMediumSoftLongScore.ofMedium(-258L));
        assertEquals(HardMediumSoftLongScore.of(0L, 0L, -369L),
                HardMediumSoftLongScore.ofSoft(-369L));
    }

    @Test
    public void parseScore() {
        assertEquals(HardMediumSoftLongScore.of(-147L, -258L, -369L),
                HardMediumSoftLongScore.parseScore("-147hard/-258medium/-369soft"));
        assertEquals(HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L),
                HardMediumSoftLongScore.parseScore("-7init/-147hard/-258medium/-369soft"));
        assertEquals(HardMediumSoftLongScore.of(-147L, -258L, Long.MIN_VALUE),
                HardMediumSoftLongScore.parseScore("-147hard/-258medium/*soft"));
        assertEquals(HardMediumSoftLongScore.of(-147L, Long.MIN_VALUE, -369L),
                HardMediumSoftLongScore.parseScore("-147hard/*medium/-369soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("0",
                HardMediumSoftLongScore.of(0L, 0L, 0L).toShortString());
        assertEquals("-369soft",
                HardMediumSoftLongScore.of(0L, 0L, -369L).toShortString());
        assertEquals("-258medium",
                HardMediumSoftLongScore.of(0L, -258L, 0L).toShortString());
        assertEquals("-258medium/-369soft",
                HardMediumSoftLongScore.of(0L, -258L, -369L).toShortString());
        assertEquals("-147hard/-258medium/-369soft",
                HardMediumSoftLongScore.of(-147L, -258L, -369L).toShortString());
        assertEquals("-7init/-258medium",
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, -258L, 0L).toShortString());
        assertEquals("-7init/-147hard/-258medium/-369soft",
                HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0hard/-258medium/-369soft",
                HardMediumSoftLongScore.of(0L, -258L, -369L).toString());
        assertEquals("-147hard/-258medium/-369soft",
                HardMediumSoftLongScore.of(-147L, -258L, -369L).toString());
        assertEquals("-7init/-147hard/-258medium/-369soft",
                HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardMediumSoftLongScore.parseScore("-147");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardMediumSoftLongScore.of(-147L, -258L, -369L),
                HardMediumSoftLongScore.of(-147L, -258L, -369L).toInitializedScore());
        assertEquals(HardMediumSoftLongScore.of(-147L, -258L, -369L),
                HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(HardMediumSoftLongScore.ofUninitialized(-7, -147L, -258L, -369L),
                HardMediumSoftLongScore.of(-147L, -258L, -369L).withInitScore(-7));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardMediumSoftLongScore.of(-5L, -300L, -4000L),
                HardMediumSoftLongScore.ofUninitialized(-7, -5L, -300L, -4000L),
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, -300L, -4000L)
        );
        assertScoreFeasible(
                HardMediumSoftLongScore.of(0L, -300L, -4000L),
                HardMediumSoftLongScore.of(2L, -300L, -4000L),
                HardMediumSoftLongScore.ofUninitialized(0, 0L, -300L, -4000L)
        );
    }

    @Test
    public void add() {
        assertEquals(HardMediumSoftLongScore.of(19L, -320L, 0L),
                HardMediumSoftLongScore.of(20L, -20L, -4000L).add(
                        HardMediumSoftLongScore.of(-1L, -300L, 4000L)));
        assertEquals(HardMediumSoftLongScore.ofUninitialized(-77, 19L, -320L, 0L),
                HardMediumSoftLongScore.ofUninitialized(-70, 20L, -20L, -4000L).add(
                        HardMediumSoftLongScore.ofUninitialized(-7, -1L, -300L, 4000L)));
    }

    @Test
    public void subtract() {
        assertEquals(HardMediumSoftLongScore.of(21L, 280L, -8000L),
                HardMediumSoftLongScore.of(20L, -20L, -4000L).subtract(
                        HardMediumSoftLongScore.of(-1L, -300L, 4000L)));
        assertEquals(HardMediumSoftLongScore.ofUninitialized(-63, 21L, 280L, -8000L),
                HardMediumSoftLongScore.ofUninitialized(-70, 20L, -20L, -4000L).subtract(
                        HardMediumSoftLongScore.ofUninitialized(-7, -1L, -300L, 4000L)));
    }

    @Test
    public void multiply() {
        assertEquals(HardMediumSoftLongScore.of(6L, -6L, 6L),
                HardMediumSoftLongScore.of(5L, -5L, 5L).multiply(1.2));
        assertEquals(HardMediumSoftLongScore.of(1L, -2L, 1L),
                HardMediumSoftLongScore.of(1L, -1L, 1L).multiply(1.2));
        assertEquals(HardMediumSoftLongScore.of(4L, -5L, 4L),
                HardMediumSoftLongScore.of(4L, -4L, 4L).multiply(1.2));
        assertEquals(HardMediumSoftLongScore.ofUninitialized(-14, 8L, -10L, 12L),
                HardMediumSoftLongScore.ofUninitialized(-7, 4L, -5L, 6L).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardMediumSoftLongScore.of(5L, -5L, 5L),
                HardMediumSoftLongScore.of(25L, -25L, 25L).divide(5.0));
        assertEquals(HardMediumSoftLongScore.of(4L, -5L, 4L),
                HardMediumSoftLongScore.of(21L, -21L, 21L).divide(5.0));
        assertEquals(HardMediumSoftLongScore.of(4L, -5L, 4L),
                HardMediumSoftLongScore.of(24L, -24L, 24L).divide(5.0));
        assertEquals(HardMediumSoftLongScore.ofUninitialized(-7, 4L, -5L, 6L),
                HardMediumSoftLongScore.ofUninitialized(-14, 8L, -10L, 12L).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardMediumSoftLongScore.of(9L, 16L, 25L),
                HardMediumSoftLongScore.of(3L, -4L, 5L).power(2.0));
        assertEquals(HardMediumSoftLongScore.of(3L, 4L, 5L),
                HardMediumSoftLongScore.of(9L, 16L, 25L).power(0.5));
        assertEquals(HardMediumSoftLongScore.ofUninitialized(-343, 27L, -64L, 125L),
                HardMediumSoftLongScore.ofUninitialized(-7, 3L, -4L, 5L).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardMediumSoftLongScore.of(-3L, 4L, -5L),
                HardMediumSoftLongScore.of(3L, -4L, 5L).negate());
        assertEquals(HardMediumSoftLongScore.of(3L, -4L, 5L),
                HardMediumSoftLongScore.of(-3L, 4L, -5L).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftLongScore.of(-10L, -200L, -3000L),
                HardMediumSoftLongScore.of(-10L, -200L, -3000L),
                HardMediumSoftLongScore.ofUninitialized(0, -10L, -200L, -3000L)
        );
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftLongScore.ofUninitialized(-7, -10L, -200L, -3000L),
                HardMediumSoftLongScore.ofUninitialized(-7, -10L, -200L, -3000L)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                HardMediumSoftLongScore.of(-10L, -200L, -3000L),
                HardMediumSoftLongScore.of(-30L, -200L, -3000L),
                HardMediumSoftLongScore.of(-10L, -400L, -3000L),
                HardMediumSoftLongScore.of(-10L, -400L, -5000L),
                HardMediumSoftLongScore.ofUninitialized(-7, -10L, -200L, -3000L)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardMediumSoftLongScore.ofUninitialized(-8, 0L, 0L, 0L),
                HardMediumSoftLongScore.ofUninitialized(-7, -20L, -20L, -20L),
                HardMediumSoftLongScore.ofUninitialized(-7, -1L, -300L, -4000L),
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, 0L, 0L),
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, 0L, 1L),
                HardMediumSoftLongScore.ofUninitialized(-7, 0L, 1L, 0L),
                HardMediumSoftLongScore.of(-20L, Long.MIN_VALUE, Long.MIN_VALUE),
                HardMediumSoftLongScore.of(-20L, Long.MIN_VALUE, -20L),
                HardMediumSoftLongScore.of(-20L, Long.MIN_VALUE, 1L),
                HardMediumSoftLongScore.of(-20L, -300L, -4000L),
                HardMediumSoftLongScore.of(-20L, -300L, -300L),
                HardMediumSoftLongScore.of(-20L, -300L, -20L),
                HardMediumSoftLongScore.of(-20L, -300L, 300L),
                HardMediumSoftLongScore.of(-20L, -20L, -300L),
                HardMediumSoftLongScore.of(-20L, -20L, 0L),
                HardMediumSoftLongScore.of(-20L, -20L, 1L),
                HardMediumSoftLongScore.of(-1L, -300L, -4000L),
                HardMediumSoftLongScore.of(-1L, -300L, -20L),
                HardMediumSoftLongScore.of(-1L, -20L, -300L),
                HardMediumSoftLongScore.of(1L, Long.MIN_VALUE, -20L),
                HardMediumSoftLongScore.of(1L, -20L, Long.MIN_VALUE)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardMediumSoftLongScore.of(-12L, 3400L, -56L),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12L, output.getHardScore());
                    assertEquals(3400L, output.getMediumScore());
                    assertEquals(-56L, output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardMediumSoftLongScore.ofUninitialized(-7, -12L, 3400L, -56L),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12L, output.getHardScore());
                    assertEquals(3400L, output.getMediumScore());
                    assertEquals(-56L, output.getSoftScore());
                }
        );
    }

}
