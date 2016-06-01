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

package org.optaplanner.core.api.score.buildin.bendable;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.score.buildin.bendable.BendableScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class BendableScoreTest extends AbstractScoreTest {

    private BendableScoreDefinition scoreDefinitionHSS = new BendableScoreDefinition(1, 2);
    private BendableScoreDefinition scoreDefinitionHHH = new BendableScoreDefinition(3, 0);
    private BendableScoreDefinition scoreDefinitionSSS = new BendableScoreDefinition(0, 3);

    @Test
    public void parseScore() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(-147, -258, -369),
                scoreDefinitionHSS.parseScore("[-147]hard/[-258/-369]soft"));
        assertEquals(scoreDefinitionHHH.createScoreInitialized(-147, -258, -369),
                scoreDefinitionHHH.parseScore("[-147/-258/-369]hard/[]soft"));
        assertEquals(scoreDefinitionSSS.createScoreInitialized(-147, -258, -369),
                scoreDefinitionSSS.parseScore("[]hard/[-147/-258/-369]soft"));
        assertEquals(scoreDefinitionSSS.createScore(-7, -147, -258, -369),
                scoreDefinitionSSS.parseScore("-7init/[]hard/[-147/-258/-369]soft"));
    }

    @Test
    public void testToString() {
        assertEquals("[-147]hard/[-258/-369]soft", scoreDefinitionHSS.createScoreInitialized(-147, -258, -369).toString());
        assertEquals("[-147/-258/-369]hard/[]soft", scoreDefinitionHHH.createScoreInitialized(-147, -258, -369).toString());
        assertEquals("[]hard/[-147/-258/-369]soft", scoreDefinitionSSS.createScoreInitialized(-147, -258, -369).toString());
        assertEquals("-7init/[]hard/[-147/-258/-369]soft", scoreDefinitionSSS.createScore(-7, -147, -258, -369).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        scoreDefinitionHSS.parseScore("-147");
    }

    @Test
    public void toInitializedScoreHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(-147, -258, -369),
                scoreDefinitionHSS.createScoreInitialized(-147, -258, -369).toInitializedScore());
        assertEquals(scoreDefinitionHSS.createScoreInitialized(-147, -258, -369),
                scoreDefinitionHSS.createScore(-7, -147, -258, -369).toInitializedScore());
    }

    @Test
    public void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScoreInitialized(-5, -300, -4000),
                scoreDefinitionHSS.createScore(-7, -5, -300, -4000),
                scoreDefinitionHSS.createScore(-7, -5, -300, -4000)
        );
        assertScoreFeasible(
                scoreDefinitionHSS.createScoreInitialized(0, -300, -4000),
                scoreDefinitionHSS.createScoreInitialized(2, -300, -4000),
                scoreDefinitionHSS.createScore(0, 0, -300, -4000)
                );
    }

    @Test
    public void addHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(19, -320, 0),
                scoreDefinitionHSS.createScoreInitialized(20, -20, -4000).add(
                        scoreDefinitionHSS.createScoreInitialized(-1, -300, 4000)));
        assertEquals(scoreDefinitionHSS.createScore(-77, 19, -320, 0),
                scoreDefinitionHSS.createScore(-70, 20, -20, -4000).add(
                        scoreDefinitionHSS.createScore(-7, -1, -300, 4000)));
    }

    @Test
    public void subtractHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(21, 280, -8000),
                scoreDefinitionHSS.createScoreInitialized(20, -20, -4000).subtract(
                        scoreDefinitionHSS.createScoreInitialized(-1, -300, 4000)));
        assertEquals(scoreDefinitionHSS.createScore(-63, 21, 280, -8000),
                scoreDefinitionHSS.createScore(-70, 20, -20, -4000).subtract(
                        scoreDefinitionHSS.createScore(-7, -1, -300, 4000)));
    }

    @Test
    public void multiplyHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(6, -6, 6),
                scoreDefinitionHSS.createScoreInitialized(5, -5, 5).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScoreInitialized(1, -2, 1),
                scoreDefinitionHSS.createScoreInitialized(1, -1, 1).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScoreInitialized(4, -5, 4),
                scoreDefinitionHSS.createScoreInitialized(4, -4, 4).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScore(-14, 8, -10, 12),
                scoreDefinitionHSS.createScore(-7, 4, -5, 6).multiply(2.0));
    }

    @Test
    public void divideHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(5, -5, 5),
                scoreDefinitionHSS.createScoreInitialized(25, -25, 25).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScoreInitialized(4, -5, 4),
                scoreDefinitionHSS.createScoreInitialized(21, -21, 21).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScoreInitialized(4, -5, 4),
                scoreDefinitionHSS.createScoreInitialized(24, -24, 24).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(-7, 4, -5, 6),
                scoreDefinitionHSS.createScore(-14, 8, -10, 12).divide(2.0));
    }

    @Test
    public void powerHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(9, 16, 25),
                scoreDefinitionHSS.createScoreInitialized(3, -4, 5).power(2.0));
        assertEquals(scoreDefinitionHSS.createScoreInitialized(3, 4, 5),
                scoreDefinitionHSS.createScoreInitialized(9, 16, 25).power(0.5));
        assertEquals(scoreDefinitionHSS.createScore(-343, 27, -64, 125),
                scoreDefinitionHSS.createScore(-7, 3, -4, 5).power(3.0));
    }

    @Test
    public void negateHSS() {
        assertEquals(scoreDefinitionHSS.createScoreInitialized(-3, 4, -5),
                scoreDefinitionHSS.createScoreInitialized(3, -4, 5).negate());
        assertEquals(scoreDefinitionHSS.createScoreInitialized(3, -4, 5),
                scoreDefinitionHSS.createScoreInitialized(-3, 4, -5).negate());
    }

    @Test
    public void equalsAndHashCodeHSS() {
        assertScoresEqualsAndHashCode(
                scoreDefinitionHSS.createScoreInitialized(-10, -200, -3000),
                scoreDefinitionHSS.createScoreInitialized(-10, -200, -3000),
                scoreDefinitionHSS.createScore(0, -10, -200, -3000)
        );
        assertScoresEqualsAndHashCode(
                scoreDefinitionHSS.createScore(-7, -10, -200, -3000),
                scoreDefinitionHSS.createScore(-7, -10, -200, -3000)
        );
        assertScoresNotEquals(
                scoreDefinitionHSS.createScoreInitialized(-10, -200, -3000),
                scoreDefinitionHSS.createScoreInitialized(-30, -200, -3000),
                scoreDefinitionHSS.createScoreInitialized(-10, -400, -3000),
                scoreDefinitionHSS.createScoreInitialized(-10, -400, -5000),
                scoreDefinitionHSS.createScore(-7, -10, -200, -3000)
        );
    }

    @Test
    public void compareToHSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHSS.createScore(-8, 0, 0, 0),
                scoreDefinitionHSS.createScore(-7, -20, -20, -20),
                scoreDefinitionHSS.createScore(-7, -1, -300, -4000),
                scoreDefinitionHSS.createScore(-7, 0, 0, 0),
                scoreDefinitionHSS.createScore(-7, 0, 0, 1),
                scoreDefinitionHSS.createScore(-7, 0, 1, 0),
                scoreDefinitionHSS.createScoreInitialized(-20, Integer.MIN_VALUE, Integer.MIN_VALUE),
                scoreDefinitionHSS.createScoreInitialized(-20, Integer.MIN_VALUE, -20),
                scoreDefinitionHSS.createScoreInitialized(-20, Integer.MIN_VALUE, 1),
                scoreDefinitionHSS.createScoreInitialized(-20, -300, -4000),
                scoreDefinitionHSS.createScoreInitialized(-20, -300, -300),
                scoreDefinitionHSS.createScoreInitialized(-20, -300, -20),
                scoreDefinitionHSS.createScoreInitialized(-20, -300, 300),
                scoreDefinitionHSS.createScoreInitialized(-20, -20, -300),
                scoreDefinitionHSS.createScoreInitialized(-20, -20, 0),
                scoreDefinitionHSS.createScoreInitialized(-20, -20, 1),
                scoreDefinitionHSS.createScoreInitialized(-1, -300, -4000),
                scoreDefinitionHSS.createScoreInitialized(-1, -300, -20),
                scoreDefinitionHSS.createScoreInitialized(-1, -20, -300),
                scoreDefinitionHSS.createScoreInitialized(1, Integer.MIN_VALUE, -20),
                scoreDefinitionHSS.createScoreInitialized(1, -20, Integer.MIN_VALUE)
        );
    }

    private BendableScoreDefinition scoreDefinitionHHSSS = new BendableScoreDefinition(2, 3);

    @Test
    public void feasibleHHSSS() {
        assertScoreNotFeasible(
                scoreDefinitionHHSSS.createScoreInitialized(-5, 0, -300, -4000, -5000),
                scoreDefinitionHHSSS.createScoreInitialized(0, -5, -300, -4000, -5000)
        );
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScoreInitialized(0, 0, -300, -4000, -5000),
                scoreDefinitionHHSSS.createScoreInitialized(0, 2, -300, -4000, -5000),
                scoreDefinitionHHSSS.createScoreInitialized(2, 0, -300, -4000, -5000)
        );
    }

    @Test
    public void addHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(19, -320, 0, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(20, -20, -4000, 0, 0).add(
                        scoreDefinitionHHSSS.createScoreInitialized(-1, -300, 4000, 0, 0)));
    }

    @Test
    public void subtractHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(21, 280, -8000, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(20, -20, -4000, 0, 0).subtract(
                        scoreDefinitionHHSSS.createScoreInitialized(-1, -300, 4000, 0, 0)));
    }

    @Test
    public void multiplyHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(6, -6, 6, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(5, -5, 5, 0, 0).multiply(1.2));
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(1, -2, 1, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(1, -1, 1, 0, 0).multiply(1.2));
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(4, -4, 4, 0, 0).multiply(1.2));
    }

    @Test
    public void divideHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(5, -5, 5, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(25, -25, 25, 0, 0).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(21, -21, 21, 0, 0).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(24, -24, 24, 0, 0).divide(5.0));
    }

    @Test
    public void powerHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(9, 16, 25, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(3, -4, 5, 0, 0).power(2.0));
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(3, 4, 5, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(9, 16, 25, 0, 0).power(0.5));
    }

    @Test
    public void negateHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(-3, 4, -5, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(3, -4, 5, 0, 0).negate());
        assertEquals(scoreDefinitionHHSSS.createScoreInitialized(3, -4, 5, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-3, 4, -5, 0, 0).negate());
    }

    @Test
    public void equalsAndHashCodeHHSSS() {
        assertScoresEqualsAndHashCode(
                scoreDefinitionHHSSS.createScoreInitialized(-10, -20, -30, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-10, -20, -30, 0, 0)
        );
    }

    @Test
    public void compareToHHSSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHHSSS.createScoreInitialized(-20, Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, Integer.MIN_VALUE, -20, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, Integer.MIN_VALUE, 1, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, -300, -4000, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, -300, -300, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, -300, -20, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, -300, 300, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, -20, -300, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, -20, 0, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-20, -20, 1, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-1, -300, -4000, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-1, -300, -20, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(-1, -20, -300, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(1, Integer.MIN_VALUE, -20, 0, 0),
                scoreDefinitionHHSSS.createScoreInitialized(1, -20, Integer.MIN_VALUE, 0, 0)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                scoreDefinitionHSS.createScoreInitialized(-12, 3400, -56),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12, output.getHardScore(0));
                    assertEquals(3400, output.getSoftScore(0));
                    assertEquals(-56, output.getSoftScore(1));
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                scoreDefinitionHSS.createScore(-7, -12, 3400, -56),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12, output.getHardScore(0));
                    assertEquals(3400, output.getSoftScore(0));
                    assertEquals(-56, output.getSoftScore(1));
                }
        );
    }

}
