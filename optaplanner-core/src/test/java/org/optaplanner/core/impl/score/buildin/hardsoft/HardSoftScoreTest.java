/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.core.impl.score.buildin.hardsoft;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class HardSoftScoreTest extends AbstractScoreTest {

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftScore.valueOf(-5, -300)
        );
        assertScoreFeasible(
                HardSoftScore.valueOf(0, -300),
                HardSoftScore.valueOf(2, -300)
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftScore.valueOf(19, -320),
                HardSoftScore.valueOf(20, -20).add(
                        HardSoftScore.valueOf(-1, -300)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftScore.valueOf(21, 280),
                HardSoftScore.valueOf(20, -20).subtract(
                        HardSoftScore.valueOf(-1, -300)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftScore.valueOf(6, -6),
                HardSoftScore.valueOf(5, -5).multiply(1.2));
        assertEquals(HardSoftScore.valueOf(1, -2),
                HardSoftScore.valueOf(1, -1).multiply(1.2));
        assertEquals(HardSoftScore.valueOf(4, -5),
                HardSoftScore.valueOf(4, -4).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftScore.valueOf(5, -5),
                HardSoftScore.valueOf(25, -25).divide(5.0));
        assertEquals(HardSoftScore.valueOf(4, -5),
                HardSoftScore.valueOf(21, -21).divide(5.0));
        assertEquals(HardSoftScore.valueOf(4, -5),
                HardSoftScore.valueOf(24, -24).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardSoftScore.valueOf(-10, -20),
                HardSoftScore.valueOf(-10, -20)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                HardSoftScore.valueOf(-20, Integer.MIN_VALUE),
                HardSoftScore.valueOf(-20, -20),
                HardSoftScore.valueOf(-1, -300),
                HardSoftScore.valueOf(-1, 4000),
                HardSoftScore.valueOf(0, -1),
                HardSoftScore.valueOf(0, 0),
                HardSoftScore.valueOf(0, 1)
        );
    }

}
