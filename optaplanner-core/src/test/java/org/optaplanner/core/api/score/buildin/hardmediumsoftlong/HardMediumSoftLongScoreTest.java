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
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class HardMediumSoftLongScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(HardMediumSoftLongScore.valueOf(-147L, -258L, -369L),
                HardMediumSoftLongScore.parseScore("-147hard/-258medium/-369soft"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardMediumSoftLongScore.parseScore("-147");
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardMediumSoftLongScore.valueOf(-5L, -300L, -4000L)
        );
        assertScoreFeasible(
                HardMediumSoftLongScore.valueOf(0L, -300L, -4000L),
                HardMediumSoftLongScore.valueOf(2L, -300L, -4000L)
        );
    }

    @Test
    public void add() {
        assertEquals(HardMediumSoftLongScore.valueOf(19L, -320L, 0L),
                HardMediumSoftLongScore.valueOf(20L, -20L, -4000L).add(
                        HardMediumSoftLongScore.valueOf(-1L, -300L, 4000L)));
    }

    @Test
    public void subtract() {
        assertEquals(HardMediumSoftLongScore.valueOf(21L, 280L, -8000L),
                HardMediumSoftLongScore.valueOf(20L, -20L, -4000L).subtract(
                        HardMediumSoftLongScore.valueOf(-1L, -300L, 4000L)));
    }

    @Test
    public void multiply() {
        assertEquals(HardMediumSoftLongScore.valueOf(6L, -6L, 6L),
                HardMediumSoftLongScore.valueOf(5L, -5L, 5L).multiply(1.2));
        assertEquals(HardMediumSoftLongScore.valueOf(1L, -2L, 1L),
                HardMediumSoftLongScore.valueOf(1L, -1L, 1L).multiply(1.2));
        assertEquals(HardMediumSoftLongScore.valueOf(4L, -5L, 4L),
                HardMediumSoftLongScore.valueOf(4L, -4L, 4L).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(HardMediumSoftLongScore.valueOf(5L, -5L, 5L),
                HardMediumSoftLongScore.valueOf(25L, -25L, 25L).divide(5.0));
        assertEquals(HardMediumSoftLongScore.valueOf(4L, -5L, 4L),
                HardMediumSoftLongScore.valueOf(21L, -21L, 21L).divide(5.0));
        assertEquals(HardMediumSoftLongScore.valueOf(4L, -5L, 4L),
                HardMediumSoftLongScore.valueOf(24L, -24L, 24L).divide(5.0));
    }

    @Test
    public void power() {
        assertEquals(HardMediumSoftLongScore.valueOf(9L, 16L, 25L),
                HardMediumSoftLongScore.valueOf(3L, -4L, 5L).power(2.0));
        assertEquals(HardMediumSoftLongScore.valueOf(3L, 4L, 5L),
                HardMediumSoftLongScore.valueOf(9L, 16L, 25L).power(0.5));
    }

    @Test
    public void negate() {
        assertEquals(HardMediumSoftLongScore.valueOf(-3L, 4L, -5L),
                HardMediumSoftLongScore.valueOf(3L, -4L, 5L).negate());
        assertEquals(HardMediumSoftLongScore.valueOf(3L, -4L, 5L),
                HardMediumSoftLongScore.valueOf(-3L, 4L, -5L).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardMediumSoftLongScore.valueOf(-10L, -20L, -30L),
                HardMediumSoftLongScore.valueOf(-10L, -20L, -30L)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                HardMediumSoftLongScore.valueOf(-20L, Long.MIN_VALUE, Long.MIN_VALUE),
                HardMediumSoftLongScore.valueOf(-20L, Long.MIN_VALUE, -20L),
                HardMediumSoftLongScore.valueOf(-20L, Long.MIN_VALUE, 1L),
                HardMediumSoftLongScore.valueOf(-20L, -300L, -4000L),
                HardMediumSoftLongScore.valueOf(-20L, -300L, -300L),
                HardMediumSoftLongScore.valueOf(-20L, -300L, -20L),
                HardMediumSoftLongScore.valueOf(-20L, -300L, 300L),
                HardMediumSoftLongScore.valueOf(-20L, -20L, -300L),
                HardMediumSoftLongScore.valueOf(-20L, -20L, 0L),
                HardMediumSoftLongScore.valueOf(-20L, -20L, 1L),
                HardMediumSoftLongScore.valueOf(-1L, -300L, -4000L),
                HardMediumSoftLongScore.valueOf(-1L, -300L, -20L),
                HardMediumSoftLongScore.valueOf(-1L, -20L, -300L),
                HardMediumSoftLongScore.valueOf(1L, Long.MIN_VALUE, -20L),
                HardMediumSoftLongScore.valueOf(1L, -20L, Long.MIN_VALUE)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        HardMediumSoftLongScore input = HardMediumSoftLongScore.valueOf(-12L, 3400L, -56L);
        PlannerTestUtils.serializeAndDeserializeWithAll(input,
                new PlannerTestUtils.OutputAsserter<HardMediumSoftLongScore>() {
                    public void assertOutput(HardMediumSoftLongScore output) {
                        assertEquals(-12L, output.getHardScore());
                        assertEquals(3400L, output.getMediumScore());
                        assertEquals(-56L, output.getSoftScore());
                    }
                }
        );
    }

}
