/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.hardsoftlong;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class HardSoftLongScoreTest extends AbstractScoreTest {

    @Test
    public void feasible() {
        assertEquals(true, HardSoftLongScore.valueOf(0L, -300L).isFeasible());
        assertEquals(false, HardSoftLongScore.valueOf(-5L, -300L).isFeasible());
        assertEquals(true, HardSoftLongScore.valueOf(2L, -300L).isFeasible());
    }

    @Test
    public void add() {
        assertEquals(HardSoftLongScore.valueOf(19L, -320L),
                HardSoftLongScore.valueOf(20L, -20L).add(
                        HardSoftLongScore.valueOf(-1L, -300L)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftLongScore.valueOf(21L, 280L),
                HardSoftLongScore.valueOf(20L, -20L).subtract(
                        HardSoftLongScore.valueOf(-1L, -300L)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftLongScore.valueOf(6L, -6L),
                HardSoftLongScore.valueOf(5L, -5L).multiply(1.2));
        assertEquals(HardSoftLongScore.valueOf(1L, -2L),
                HardSoftLongScore.valueOf(1L, -1L).multiply(1.2));
        assertEquals(HardSoftLongScore.valueOf(4L, -5L),
                HardSoftLongScore.valueOf(4L, -4L).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftLongScore.valueOf(5L, -5L),
                HardSoftLongScore.valueOf(25L, -25L).divide(5.0));
        assertEquals(HardSoftLongScore.valueOf(4L, -5L),
                HardSoftLongScore.valueOf(21L, -21L).divide(5.0));
        assertEquals(HardSoftLongScore.valueOf(4L, -5L),
                HardSoftLongScore.valueOf(24L, -24L).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardSoftLongScore.valueOf(-10L, -20L),
                HardSoftLongScore.valueOf(-10L, -20L)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                HardSoftLongScore.valueOf(-20L, Long.MIN_VALUE),
                HardSoftLongScore.valueOf(-20L, -20L),
                HardSoftLongScore.valueOf(-1L, -300L),
                HardSoftLongScore.valueOf(-1L, 4000L),
                HardSoftLongScore.valueOf(0L, -1L),
                HardSoftLongScore.valueOf(0L, 0L),
                HardSoftLongScore.valueOf(0L, 1L)
        );
    }

}
