/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score.buildin.bendablelong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.score.buildin.bendablelong.BendableLongScoreDefinition;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class BendableLongScoreTest extends AbstractScoreTest {

    private BendableLongScoreDefinition scoreDefinitionHSS = new BendableLongScoreDefinition(1, 2);

    @Test
    public void of() {
        assertEquals(scoreDefinitionHSS.createScore(-147L, 0L, 0L),
                BendableLongScore.ofHard(1, 2, 0, -147L));
        assertEquals(scoreDefinitionHSS.createScore(0L, -258L, 0L),
                BendableLongScore.ofSoft(1, 2, 0, -258L));
        assertEquals(scoreDefinitionHSS.createScore(0L, 0L, -369L),
                BendableLongScore.ofSoft(1, 2, 1, -369L));
    }

    @Test
    public void parseScore() {
        assertEquals(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L),
                scoreDefinitionHSS.parseScore("[-5432109876]hard/[-9876543210/-3456789012]soft"));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L),
                scoreDefinitionHSS.parseScore("-7init/[-5432109876]hard/[-9876543210/-3456789012]soft"));
        assertEquals(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, Long.MIN_VALUE),
                scoreDefinitionHSS.parseScore("[-5432109876]hard/[-9876543210/*]soft"));
        assertEquals(scoreDefinitionHSS.createScore(-5432109876L, Long.MIN_VALUE, -3456789012L),
                scoreDefinitionHSS.parseScore("[-5432109876]hard/[*/-3456789012]soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("[0/-3456789012]soft",
                scoreDefinitionHSS.createScore(0L, 0L, -3456789012L).toShortString());
        assertEquals("[-9876543210/-3456789012]soft",
                scoreDefinitionHSS.createScore(0L, -9876543210L, -3456789012L).toShortString());
        assertEquals("[-5432109876]hard",
                scoreDefinitionHSS.createScore(-5432109876L, 0L, -0L).toShortString());
        assertEquals("[-5432109876]hard/[-9876543210/-3456789012]soft",
                scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L).toShortString());
        assertEquals("-7init/[-5432109876]hard/[-9876543210/-3456789012]soft",
                scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("[0]hard/[-9876543210/-3456789012]soft",
                scoreDefinitionHSS.createScore(0L, -9876543210L, -3456789012L).toString());
        assertEquals("[-5432109876]hard/[-9876543210/-3456789012]soft",
                scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L).toString());
        assertEquals("[-5432109876/-9876543210]hard/[-3456789012]soft",
                new BendableLongScoreDefinition(2, 1).createScore(-5432109876L, -9876543210L, -3456789012L).toString());
        assertEquals("-7init/[-5432109876]hard/[-9876543210/-3456789012]soft",
                scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L).toString());
        assertEquals("[]hard/[]soft",
                new BendableLongScoreDefinition(0, 0).createScore().toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        scoreDefinitionHSS.parseScore("-147");
    }

    @Test
    public void getHardOrSoftScore() {
        BendableLongScore initializedScore = scoreDefinitionHSS.createScore(-5L, -10L, -200L);
        assertEquals(-5L, initializedScore.getHardOrSoftScore(0));
        assertEquals(-10L, initializedScore.getHardOrSoftScore(1));
        assertEquals(-200L, initializedScore.getHardOrSoftScore(2));
    }

    @Test
    public void toInitializedScoreHSS() {
        assertEquals(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L),
                scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L).toInitializedScore());
        assertEquals(scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-7, -5432109876L, -9876543210L, -3456789012L),
                scoreDefinitionHSS.createScore(-5432109876L, -9876543210L, -3456789012L).withInitScore(-7));
    }

    @Test
    public void feasibleHSS() {
        assertScoreNotFeasible(
                scoreDefinitionHSS.createScore(-5L, -300L, -9876543210L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -5L, -300L, -9876543210L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 0L, -300L, -9876543210L)
        );
        assertScoreFeasible(
                scoreDefinitionHSS.createScore(0L, -300L, -9876543210L),
                scoreDefinitionHSS.createScore(2L, -300L, -9876543210L),
                scoreDefinitionHSS.createScoreUninitialized(0, 0L, -300L, -9876543210L)
        );
    }

    @Test
    public void addHSS() {
        assertEquals(scoreDefinitionHSS.createScore(3333333333L, -320L, 0L),
                scoreDefinitionHSS.createScore(1111111111L, -20L, -9876543210L).add(
                        scoreDefinitionHSS.createScore(2222222222L, -300L, 9876543210L)));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-77, 3333333333L, -320L, 0L),
                scoreDefinitionHSS.createScoreUninitialized(-70, 1111111111L, -20L, -9876543210L).add(
                        scoreDefinitionHSS.createScoreUninitialized(-7, 2222222222L, -300L, 9876543210L)));
    }

    @Test
    public void subtractHSS() {
        assertEquals(scoreDefinitionHSS.createScore(2222222222L, 280L, -8888888888L),
                scoreDefinitionHSS.createScore(3333333333L, -20L, -5555555555L).subtract(
                        scoreDefinitionHSS.createScore(1111111111L, -300L, 3333333333L)));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-63, 2222222222L, 280L, -8888888888L),
                scoreDefinitionHSS.createScoreUninitialized(-70, 3333333333L, -20L, -5555555555L).subtract(
                        scoreDefinitionHSS.createScoreUninitialized(-7, 1111111111L, -300L, 3333333333L)));
    }

    @Test
    public void multiplyHSS() {
        assertEquals(scoreDefinitionHSS.createScore(6000000000L, -6000000000L, 6000000000L),
                scoreDefinitionHSS.createScore(5000000000L, -5000000000L, 5000000000L).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScore(1L, -2L, 1L),
                scoreDefinitionHSS.createScore(1L, -1L, 1L).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScore(4L, -5L, 4L),
                scoreDefinitionHSS.createScore(4L, -4L, 4L).multiply(1.2));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-14, 8L, -10L, 12L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 4L, -5L, 6L).multiply(2.0));
    }

    @Test
    public void divideHSS() {
        assertEquals(scoreDefinitionHSS.createScore(5000000000L, -5000000000L, 5000000000L),
                scoreDefinitionHSS.createScore(25000000000L, -25000000000L, 25000000000L).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(4L, -5L, 4L),
                scoreDefinitionHSS.createScore(21L, -21L, 21L).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScore(4L, -5L, 4L),
                scoreDefinitionHSS.createScore(24L, -24L, 24L).divide(5.0));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-7, 4L, -5L, 6L),
                scoreDefinitionHSS.createScoreUninitialized(-14, 8L, -10L, 12L).divide(2.0));
    }

    @Test
    public void powerHSS() {
        assertEquals(scoreDefinitionHSS.createScore(90000000000L, 160000000000L, 250000000000L),
                scoreDefinitionHSS.createScore(300000L, -400000L, 500000L).power(2.0));
        assertEquals(scoreDefinitionHSS.createScore(300000L, 400000L, 500000L),
                scoreDefinitionHSS.createScore(90000000000L, 160000000000L, 250000000000L).power(0.5));
        assertEquals(scoreDefinitionHSS.createScoreUninitialized(-343, 27L, -64L, 125L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 3L, -4L, 5L).power(3.0));
    }

    @Test
    public void negateHSS() {
        assertEquals(scoreDefinitionHSS.createScore(-3000000000L, 4000000000L, -5000000000L),
                scoreDefinitionHSS.createScore(3000000000L, -4000000000L, 5000000000L).negate());
        assertEquals(scoreDefinitionHSS.createScore(3000000000L, -4000000000L, 5000000000L),
                scoreDefinitionHSS.createScore(-3000000000L, 4000000000L, -5000000000L).negate());
    }

    @Test
    public void equalsAndHashCodeHSS() {
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHSS.createScore(-10L, -200L, -3000L),
                scoreDefinitionHSS.createScore(-10L, -200L, -3000L),
                scoreDefinitionHSS.createScoreUninitialized(0, -10L, -200L, -3000L)
        );
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHSS.createScoreUninitialized(-7, -10L, -200L, -3000L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -10L, -200L, -3000L)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                scoreDefinitionHSS.createScore(-10L, -200L, -3000L),
                scoreDefinitionHSS.createScore(-30L, -200L, -3000L),
                scoreDefinitionHSS.createScore(-10L, -400L, -3000L),
                scoreDefinitionHSS.createScore(-10L, -400L, -5000L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -10L, -200L, -3000L)
        );
    }

    @Test
    public void compareToHSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHSS.createScoreUninitialized(-8, 0L, 0L, 0L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -20L, -20L, -20L),
                scoreDefinitionHSS.createScoreUninitialized(-7, -1L, -300L, -4000L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 0L, 0L, 0L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 0L, 0L, 1L),
                scoreDefinitionHSS.createScoreUninitialized(-7, 0L, 1L, 0L),
                scoreDefinitionHSS.createScore(-20L, Long.MIN_VALUE, Long.MIN_VALUE),
                scoreDefinitionHSS.createScore(-20L, Long.MIN_VALUE, -20L),
                scoreDefinitionHSS.createScore(-20L, Long.MIN_VALUE, 1L),
                scoreDefinitionHSS.createScore(-20L, -300L, -4000L),
                scoreDefinitionHSS.createScore(-20L, -300L, -300L),
                scoreDefinitionHSS.createScore(-20L, -300L, -20L),
                scoreDefinitionHSS.createScore(-20L, -300L, 300L),
                scoreDefinitionHSS.createScore(-20L, -20L, -300L),
                scoreDefinitionHSS.createScore(-20L, -20L, 0L),
                scoreDefinitionHSS.createScore(-20L, -20L, 1L),
                scoreDefinitionHSS.createScore(-1L, -300L, -4000L),
                scoreDefinitionHSS.createScore(-1L, -300L, -20L),
                scoreDefinitionHSS.createScore(-1L, -20L, -300L),
                scoreDefinitionHSS.createScore(1L, Long.MIN_VALUE, -20L),
                scoreDefinitionHSS.createScore(1L, -20L, Long.MIN_VALUE)
        );
    }

    private BendableLongScoreDefinition scoreDefinitionHHSSS = new BendableLongScoreDefinition(2, 3);

    @Test
    public void feasibleHHSSS() {
        assertScoreNotFeasible(
                scoreDefinitionHHSSS.createScore(-5L, 0L, -300L, -4000000000L, -5000L),
                scoreDefinitionHHSSS.createScore(0L, -5000000000L, -300L, -4000L, -5000L),
                scoreDefinitionHHSSS.createScore(1L, -2L, -300L, -4000L, -5000L)
        );
        assertScoreFeasible(
                scoreDefinitionHHSSS.createScore(0L, 0L, -300000000000L, -4000L, -5000L),
                scoreDefinitionHHSSS.createScore(0L, 2L, -300L, -4000L, -50000000000L),
                scoreDefinitionHHSSS.createScore(2000000000L, 0L, -300L, -4000L, -5000L),
                scoreDefinitionHHSSS.createScore(1L, 2L, -300L, -4000L, -5000L)
        );
    }

    @Test
    public void addHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(19000000000L, -320000000000L, 0L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(20000000000L, -20000000000L, -4000000000000L, 0L, 0L).add(
                        scoreDefinitionHHSSS.createScore(-1000000000L, -300000000000L, 4000000000000L, 0L, 0L)));
    }

    @Test
    public void subtractHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(21000000000L, 280000000000L, -8000000000000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(20000000000L, -20000000000L, -4000000000000L, 0L, 0L).subtract(
                        scoreDefinitionHHSSS.createScore(-1000000000L, -300000000000L, 4000000000000L, 0L, 0L)));
    }

    @Test
    public void multiplyHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(6000000000L, -6000000000L, 6000000000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(5000000000L, -5000000000L, 5000000000L, 0L, 0L).multiply(1.2));
        assertEquals(scoreDefinitionHHSSS.createScore(1, -2, 1, 0, 0),
                scoreDefinitionHHSSS.createScore(1, -1, 1, 0, 0).multiply(1.2));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(4, -4, 4, 0, 0).multiply(1.2));
    }

    @Test
    public void divideHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(5000000000L, -5000000000L, 5000000000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(25000000000L, -25000000000L, 25000000000L, 0L, 0L).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(21, -21, 21, 0, 0).divide(5.0));
        assertEquals(scoreDefinitionHHSSS.createScore(4, -5, 4, 0, 0),
                scoreDefinitionHHSSS.createScore(24, -24, 24, 0, 0).divide(5.0));
    }

    @Test
    public void powerHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(40000000000L, 160000000000L, 250000000000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(200000L, -400000L, 500000L, 0L, 0L).power(2.0));
        assertEquals(scoreDefinitionHHSSS.createScore(3L, 4L, 5L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(9L, 16L, 25L, 0L, 0L).power(0.5));
    }

    @Test
    public void negateHHSSS() {
        assertEquals(scoreDefinitionHHSSS.createScore(-3000000000L, 4000000000L, -5000000000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(3000000000L, -4000000000L, 5000000000L, 0L, 0L).negate());
        assertEquals(scoreDefinitionHHSSS.createScore(3L, -4L, 5L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-3L, 4L, -5L, 0L, 0L).negate());
    }

    @Test
    public void equalsAndHashCodeHHSSS() {
        PlannerAssert.assertObjectsAreEqual(
                scoreDefinitionHHSSS.createScore(-10000000000L, -20000000000L, -30000000000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-10000000000L, -20000000000L, -30000000000L, 0L, 0L)
        );
    }

    @Test
    public void compareToHHSSS() {
        PlannerAssert.assertCompareToOrder(
                scoreDefinitionHHSSS.createScore(-20L, Long.MIN_VALUE, Long.MIN_VALUE, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, Long.MIN_VALUE, -20L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, Long.MIN_VALUE, 1L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -300L, -4000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -300L, -300L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -300L, -20L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -300L, 300L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -20L, -300L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -20L, 0L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-20L, -20L, 1L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-1L, -300L, -4000L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-1L, -300L, -20L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(-1L, -20L, -300L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(1L, Long.MIN_VALUE, -20L, 0L, 0L),
                scoreDefinitionHHSSS.createScore(1L, -20L, Long.MIN_VALUE, 0L, 0L)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                scoreDefinitionHSS.createScore(-12L, 3400L, -56L),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12L, output.getHardScore(0));
                    assertEquals(3400L, output.getSoftScore(0));
                    assertEquals(-56L, output.getSoftScore(1));
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                scoreDefinitionHSS.createScoreUninitialized(-7, -12L, 3400L, -56L),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12L, output.getHardScore(0));
                    assertEquals(3400L, output.getSoftScore(0));
                    assertEquals(-56L, output.getSoftScore(1));
                }
        );
    }

}
