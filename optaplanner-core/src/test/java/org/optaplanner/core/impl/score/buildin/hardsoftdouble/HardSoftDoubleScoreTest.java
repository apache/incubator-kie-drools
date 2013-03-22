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

package org.optaplanner.core.impl.score.buildin.hardsoftdouble;

import org.optaplanner.core.api.score.buildin.hardsoftdouble.HardSoftDoubleScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class HardSoftDoubleScoreTest extends AbstractScoreTest {

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftDoubleScore.valueOf(-5.0, -300.0),
                HardSoftDoubleScore.valueOf(-5.0, 4000.0),
                HardSoftDoubleScore.valueOf(-0.007, 4000.0)
        );
        assertScoreFeasible(
                HardSoftDoubleScore.valueOf(0.0, -300.007),
                HardSoftDoubleScore.valueOf(0.0, -300.0),
                HardSoftDoubleScore.valueOf(2.0, -300.0)
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftDoubleScore.valueOf(19.0, -320.0),
                HardSoftDoubleScore.valueOf(20.0, -20.0).add(
                        HardSoftDoubleScore.valueOf(-1.0, -300.0)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftDoubleScore.valueOf(21.0, 280.0),
                HardSoftDoubleScore.valueOf(20.0, -20.0).subtract(
                        HardSoftDoubleScore.valueOf(-1.0, -300.0)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftDoubleScore.valueOf(6.0, -6.0),
                HardSoftDoubleScore.valueOf(5.0, -5.0).multiply(1.2));
        assertEquals(HardSoftDoubleScore.valueOf(1.2, -1.2),
                HardSoftDoubleScore.valueOf(1.0, -1.0).multiply(1.2));
        assertEquals(HardSoftDoubleScore.valueOf(4.8, -4.8),
                HardSoftDoubleScore.valueOf(4.0, -4.0).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftDoubleScore.valueOf(5.0, -5.0),
                HardSoftDoubleScore.valueOf(25.0, -25.0).divide(5.0));
        assertEquals(HardSoftDoubleScore.valueOf(4.2, -4.2),
                HardSoftDoubleScore.valueOf(21.0, -21.0).divide(5.0));
        assertEquals(HardSoftDoubleScore.valueOf(4.8, -4.8),
                HardSoftDoubleScore.valueOf(24.0, -24.0).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardSoftDoubleScore.valueOf(-10.0, -20.0),
                HardSoftDoubleScore.valueOf(-10.0, -20.0)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                HardSoftDoubleScore.valueOf(-20.06, -20.0),
                HardSoftDoubleScore.valueOf(-20.007, -20.0),
                HardSoftDoubleScore.valueOf(-20.0, -Double.MAX_VALUE),
                HardSoftDoubleScore.valueOf(-20.0, -20.06),
                HardSoftDoubleScore.valueOf(-20.0, -20.007),
                HardSoftDoubleScore.valueOf(-20.0, -20.0),
                HardSoftDoubleScore.valueOf(-1.0, -300.0),
                HardSoftDoubleScore.valueOf(-1.0, 4000.0),
                HardSoftDoubleScore.valueOf(0.0, -1.0),
                HardSoftDoubleScore.valueOf(0.0, 0.0),
                HardSoftDoubleScore.valueOf(0.0, 1.0)
        );
    }

}
