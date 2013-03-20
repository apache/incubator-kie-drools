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

package org.optaplanner.core.impl.score.buildin.bendable;

import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class BendableScoreTest extends AbstractScoreTest {

    private BendableScoreDefinition scoreDefinitionHSS = new BendableScoreDefinition(1, 2);

    @Test
    public void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScore(-5, -300, -4000)
        );
        assertScoreFeasible(
                scoreDefinitionHSS.createScore(0, -300, -4000),
                scoreDefinitionHSS.createScore(2, -300, -4000)
        );
    }

    @Test
    public void addHSS() {
        assertEquals(scoreDefinitionHSS.createScore(19, -320, 0),
                scoreDefinitionHSS.createScore(20, -20, -4000).add(
                        scoreDefinitionHSS.createScore(-1, -300, 4000)));
    }

    @Test
    public void subtractHSS() {
        assertEquals(scoreDefinitionHSS.createScore(21, 280, -8000),
                scoreDefinitionHSS.createScore(20, -20, -4000).subtract(
                        scoreDefinitionHSS.createScore(-1, -300, 4000)));
    }

    @Test
    public void multiplyHSS() {
        assertEquals(scoreDefinitionHSS.createScore(6, -6, 6),
                scoreDefinitionHSS.createScore(5, -5, 5).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScore(1, -2, 1),
                scoreDefinitionHSS.createScore(1, -1, 1).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScore(4, -5, 4),
                scoreDefinitionHSS.createScore(4, -4, 4).multiply(1.2));
    }

    @Test
    public void divideHSS() {
        assertEquals(scoreDefinitionHSS.createScore(5, -5, 5),
                scoreDefinitionHSS.createScore(25, -25, 25).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(4, -5, 4),
                scoreDefinitionHSS.createScore(21, -21, 21).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(4, -5, 4),
                scoreDefinitionHSS.createScore(24, -24, 24).divide(5.0));
    }

    @Test
    public void equalsAndHashCodeHSS() {
        assertScoresEqualsAndHashCode(
                scoreDefinitionHSS.createScore(-10, -20, -30),
                scoreDefinitionHSS.createScore(-10, -20, -30)
        );
    }

    @Test
    public void compareToHSS() {
        assertScoreCompareToOrder(
                scoreDefinitionHSS.createScore(-20, Integer.MIN_VALUE, Integer.MIN_VALUE),
                scoreDefinitionHSS.createScore(-20, Integer.MIN_VALUE, -20),
                scoreDefinitionHSS.createScore(-20, Integer.MIN_VALUE, 1),
                scoreDefinitionHSS.createScore(-20, -300, -4000),
                scoreDefinitionHSS.createScore(-20, -300, -300),
                scoreDefinitionHSS.createScore(-20, -300, -20),
                scoreDefinitionHSS.createScore(-20, -300, 300),
                scoreDefinitionHSS.createScore(-20, -20, -300),
                scoreDefinitionHSS.createScore(-20, -20, 0),
                scoreDefinitionHSS.createScore(-20, -20, 1),
                scoreDefinitionHSS.createScore(-1, -300, -4000),
                scoreDefinitionHSS.createScore(-1, -300, -20),
                scoreDefinitionHSS.createScore(-1, -20, -300),
                scoreDefinitionHSS.createScore(1, Integer.MIN_VALUE, -20),
                scoreDefinitionHSS.createScore(1, -20, Integer.MIN_VALUE)
        );
    }

    private BendableScoreDefinition scoreDefinitionHHSSS = new BendableScoreDefinition(2, 3);

    @Test
    public void feasibleHHSSS() {
        assertScoreNotFeasible(
                scoreDefinitionHHSSS.createScore(-5, 0, -300, -4000, -5000),
                scoreDefinitionHHSSS.createScore(0, -5, -300, -4000, -5000)
        );
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScore(0, 0, -300, -4000, -5000),
                scoreDefinitionHHSSS.createScore(0, 2, -300, -4000, -5000),
                scoreDefinitionHHSSS.createScore(2, 0, -300, -4000, -5000)
        );
    }

    @Test
    public void addHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(19, -320, 0, 0, 0),
                scoreDefinitionHHSSS.createScore(20, -20, -4000, 0, 0).add(
                        scoreDefinitionHHSSS.createScore(-1, -300, 4000, 0, 0)));
    }

    @Test
    public void subtractHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(21, 280, -8000, 0, 0),
                scoreDefinitionHHSSS.createScore(20, -20, -4000, 0, 0).subtract(
                        scoreDefinitionHHSSS.createScore(-1, -300, 4000, 0, 0)));
    }

    @Test
    public void multiplyHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(6, -6, 6, 0, 0),
                scoreDefinitionHHSSS.createScore(5, -5, 5, 0, 0).multiply(1.2));
        assertEquals(scoreDefinitionHHSSS.createScore(1, -2, 1, 0, 0),
                scoreDefinitionHHSSS.createScore(1, -1, 1, 0, 0).multiply(1.2));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(4, -4, 4, 0, 0).multiply(1.2));
    }

    @Test
    public void divideHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(5, -5, 5, 0, 0),
                scoreDefinitionHHSSS.createScore(25, -25, 25, 0, 0).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(21, -21, 21, 0, 0).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(24, -24, 24, 0, 0).divide(5.0));
    }

    @Test
    public void equalsAndHashCodeHHSSS() {
        assertScoresEqualsAndHashCode(
                scoreDefinitionHHSSS.createScore(-10, -20, -30, 0, 0),
                scoreDefinitionHHSSS.createScore(-10, -20, -30, 0, 0)
        );
    }

    @Test
    public void compareToHHSSS() {
        assertScoreCompareToOrder(
                scoreDefinitionHHSSS.createScore(-20, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, Integer.MIN_VALUE, -20, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, Integer.MIN_VALUE, 1, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -300, -4000, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -300, -300, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -300, -20, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -300, 300, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -20, -300, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -20, 0, 0, 0),
                scoreDefinitionHHSSS.createScore(-20, -20, 1, 0, 0),
                scoreDefinitionHHSSS.createScore(-1, -300, -4000, 0, 0),
                scoreDefinitionHHSSS.createScore(-1, -300, -20, 0, 0),
                scoreDefinitionHHSSS.createScore(-1, -20, -300, 0, 0),
                scoreDefinitionHHSSS.createScore(1, Integer.MIN_VALUE, -20, 0, 0),
                scoreDefinitionHHSSS.createScore(1, -20, Integer.MIN_VALUE, 0, 0)
        );
    }

}
