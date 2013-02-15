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

package org.drools.planner.core.score.buildin.bendable;

import org.drools.planner.core.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class BendableScoreTest extends AbstractScoreTest {

    private BendableScoreDefinition scoreDefinition = new BendableScoreDefinition(1, 2);

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                scoreDefinition.scoreValueOf(-5, -300, -4000)
        );
        assertScoreFeasible(
                scoreDefinition.scoreValueOf(0, -300, -4000),
                scoreDefinition.scoreValueOf(2, -300, -4000)
        );
    }

    @Test
    public void add() {
        assertEquals(scoreDefinition.scoreValueOf(19, -320, 0),
                scoreDefinition.scoreValueOf(20, -20, -4000).add(
                        scoreDefinition.scoreValueOf(-1, -300, 4000)));
    }

    @Test
    public void subtract() {
        assertEquals(scoreDefinition.scoreValueOf(21, 280, -8000),
                scoreDefinition.scoreValueOf(20, -20, -4000).subtract(
                        scoreDefinition.scoreValueOf(-1, -300, 4000)));
    }

    @Test
    public void multiply() {
        assertEquals(scoreDefinition.scoreValueOf(6, -6, 6),
                scoreDefinition.scoreValueOf(5, -5, 5).multiply(1.2));
        assertEquals(scoreDefinition.scoreValueOf(1, -2, 1),
                scoreDefinition.scoreValueOf(1, -1, 1).multiply(1.2));
        assertEquals(scoreDefinition.scoreValueOf(4, -5, 4),
                scoreDefinition.scoreValueOf(4, -4, 4).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(scoreDefinition.scoreValueOf(5, -5, 5),
                scoreDefinition.scoreValueOf(25, -25, 25).divide(5.0));
        assertEquals(scoreDefinition.scoreValueOf(4, -5, 4),
                scoreDefinition.scoreValueOf(21, -21, 21).divide(5.0));
        assertEquals(scoreDefinition.scoreValueOf(4, -5, 4),
                scoreDefinition.scoreValueOf(24, -24, 24).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                scoreDefinition.scoreValueOf(-10, -20, -30),
                scoreDefinition.scoreValueOf(-10, -20, -30)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                scoreDefinition.scoreValueOf(-20, Integer.MIN_VALUE, Integer.MIN_VALUE),
                scoreDefinition.scoreValueOf(-20, Integer.MIN_VALUE, -20),
                scoreDefinition.scoreValueOf(-20, Integer.MIN_VALUE, 1),
                scoreDefinition.scoreValueOf(-20, -300, -4000),
                scoreDefinition.scoreValueOf(-20, -300, -300),
                scoreDefinition.scoreValueOf(-20, -300, -20),
                scoreDefinition.scoreValueOf(-20, -300, 300),
                scoreDefinition.scoreValueOf(-20, -20, -300),
                scoreDefinition.scoreValueOf(-20, -20, 0),
                scoreDefinition.scoreValueOf(-20, -20, 1),
                scoreDefinition.scoreValueOf(-1, -300, -4000),
                scoreDefinition.scoreValueOf(-1, -300, -20),
                scoreDefinition.scoreValueOf(-1, -20, -300),
                scoreDefinition.scoreValueOf(1, Integer.MIN_VALUE, -20),
                scoreDefinition.scoreValueOf(1, -20, Integer.MIN_VALUE)
        );
    }

}
