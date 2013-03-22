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

package org.optaplanner.core.impl.score.buildin.hardmediumsoft;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HardMediumSoftScoreTest extends AbstractScoreTest {

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardMediumSoftScore.valueOf(-5, -300, -4000)
        );
        assertScoreFeasible(
                HardMediumSoftScore.valueOf(0, -300, -4000),
                HardMediumSoftScore.valueOf(2, -300, -4000)
        );
    }

    @Test
    public void add() {
        assertEquals(HardMediumSoftScore.valueOf(19, -320, 0),
                HardMediumSoftScore.valueOf(20, -20, -4000).add(
                        HardMediumSoftScore.valueOf(-1, -300, 4000)));
    }

    @Test
    public void subtract() {
        assertEquals(HardMediumSoftScore.valueOf(21, 280, -8000),
                HardMediumSoftScore.valueOf(20, -20, -4000).subtract(
                        HardMediumSoftScore.valueOf(-1, -300, 4000)));
    }

    @Test
    public void multiply() {
        assertEquals(HardMediumSoftScore.valueOf(6, -6, 6),
                HardMediumSoftScore.valueOf(5, -5, 5).multiply(1.2));
        assertEquals(HardMediumSoftScore.valueOf(1, -2, 1),
                HardMediumSoftScore.valueOf(1, -1, 1).multiply(1.2));
        assertEquals(HardMediumSoftScore.valueOf(4, -5, 4),
                HardMediumSoftScore.valueOf(4, -4, 4).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(HardMediumSoftScore.valueOf(5, -5, 5),
                HardMediumSoftScore.valueOf(25, -25, 25).divide(5.0));
        assertEquals(HardMediumSoftScore.valueOf(4, -5, 4),
                HardMediumSoftScore.valueOf(21, -21, 21).divide(5.0));
        assertEquals(HardMediumSoftScore.valueOf(4, -5, 4),
                HardMediumSoftScore.valueOf(24, -24, 24).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardMediumSoftScore.valueOf(-10, -20, -30),
                HardMediumSoftScore.valueOf(-10, -20, -30)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                HardMediumSoftScore.valueOf(-20, Integer.MIN_VALUE, Integer.MIN_VALUE),
                HardMediumSoftScore.valueOf(-20, Integer.MIN_VALUE, -20),
                HardMediumSoftScore.valueOf(-20, Integer.MIN_VALUE, 1),
                HardMediumSoftScore.valueOf(-20, -300, -4000),
                HardMediumSoftScore.valueOf(-20, -300, -300),
                HardMediumSoftScore.valueOf(-20, -300, -20),
                HardMediumSoftScore.valueOf(-20, -300, 300),
                HardMediumSoftScore.valueOf(-20, -20, -300),
                HardMediumSoftScore.valueOf(-20, -20, 0),
                HardMediumSoftScore.valueOf(-20, -20, 1),
                HardMediumSoftScore.valueOf(-1, -300, -4000),
                HardMediumSoftScore.valueOf(-1, -300, -20),
                HardMediumSoftScore.valueOf(-1, -20, -300),
                HardMediumSoftScore.valueOf(1, Integer.MIN_VALUE, -20),
                HardMediumSoftScore.valueOf(1, -20, Integer.MIN_VALUE)
        );
    }

}
