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

package org.optaplanner.core.api.score.buildin.hardsoftdouble;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class HardSoftDoubleScoreTest extends AbstractScoreTest {

    @Test
    public void of() {
        assertEquals(HardSoftDoubleScore.of(-147.2, 0.0),
                HardSoftDoubleScore.ofHard(-147.2));
        assertEquals(HardSoftDoubleScore.of(0.0, -258.3),
                HardSoftDoubleScore.ofSoft(-258.3));
    }

    @Test
    public void parseScore() {
        assertEquals(HardSoftDoubleScore.of(-147.2, -258.3),
                HardSoftDoubleScore.parseScore("-147.2hard/-258.3soft"));
        assertEquals(HardSoftDoubleScore.ofUninitialized(-7, -147.2, -258.3),
                HardSoftDoubleScore.parseScore("-7init/-147.2hard/-258.3soft"));
        assertEquals(HardSoftDoubleScore.of(-147.2, Double.MIN_VALUE),
                HardSoftDoubleScore.parseScore("-147.2hard/*soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", HardSoftDoubleScore.of(0.0, 0.0).toShortString());
        assertEquals("-258.3soft", HardSoftDoubleScore.of(0.0, -258.3).toShortString());
        assertEquals("-147.2hard", HardSoftDoubleScore.of(-147.2, 0.0).toShortString());
        assertEquals("-147.2hard/-258.3soft", HardSoftDoubleScore.of(-147.2, -258.3).toShortString());
        assertEquals("-7init", HardSoftDoubleScore.ofUninitialized(-7, 0.0, 0.0).toShortString());
        assertEquals("-7init/-258.3soft", HardSoftDoubleScore.ofUninitialized(-7, 0.0, -258.3).toShortString());
        assertEquals("-7init/-147.2hard/-258.3soft", HardSoftDoubleScore.ofUninitialized(-7, -147.2, -258.3).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0.0hard/-258.3soft", HardSoftDoubleScore.of(0.0, -258.3).toString());
        assertEquals("-147.2hard/-258.3soft", HardSoftDoubleScore.of(-147.2, -258.3).toString());
        assertEquals("-7init/-147.2hard/-258.3soft", HardSoftDoubleScore.ofUninitialized(-7, -147.2, -258.3).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftDoubleScore.parseScore("-147.2");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardSoftDoubleScore.of(-147.2, -258.3),
                HardSoftDoubleScore.of(-147.2, -258.3).toInitializedScore());
        assertEquals(HardSoftDoubleScore.of(-147.2, -258.3),
                HardSoftDoubleScore.ofUninitialized(-7, -147.2, -258.3).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(HardSoftDoubleScore.ofUninitialized(-7, -147.2, -258.3),
                HardSoftDoubleScore.of(-147.2, -258.3).withInitScore(-7));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftDoubleScore.of(-5.0, -300.0),
                HardSoftDoubleScore.of(-5.0, 4000.0),
                HardSoftDoubleScore.of(-0.007, 4000.0),
                HardSoftDoubleScore.ofUninitialized(-7, -5.0, -300.0),
                HardSoftDoubleScore.ofUninitialized(-7, 0.0, -300.0)
        );
        assertScoreFeasible(
                HardSoftDoubleScore.of(0.0, -300.007),
                HardSoftDoubleScore.of(0.0, -300.0),
                HardSoftDoubleScore.of(2.0, -300.0),
                HardSoftDoubleScore.ofUninitialized(0, 0.0, -300.0)
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftDoubleScore.of(19.0, -320.0),
                HardSoftDoubleScore.of(20.0, -20.0).add(
                        HardSoftDoubleScore.of(-1.0, -300.0)));
        assertEquals(HardSoftDoubleScore.ofUninitialized(-77, 19.0, -320.0),
                HardSoftDoubleScore.ofUninitialized(-70, 20.0, -20.0).add(
                        HardSoftDoubleScore.ofUninitialized(-7, -1.0, -300.0)));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftDoubleScore.of(21.0, 280.0),
                HardSoftDoubleScore.of(20.0, -20.0).subtract(
                        HardSoftDoubleScore.of(-1.0, -300.0)));
        assertEquals(HardSoftDoubleScore.ofUninitialized(-63, 21.0, 280.0),
                HardSoftDoubleScore.ofUninitialized(-70, 20.0, -20.0).subtract(
                        HardSoftDoubleScore.ofUninitialized(-7, -1.0, -300.0)));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftDoubleScore.of(6.0, -6.0),
                HardSoftDoubleScore.of(5.0, -5.0).multiply(1.2));
        assertEquals(HardSoftDoubleScore.of(1.2, -1.2),
                HardSoftDoubleScore.of(1.0, -1.0).multiply(1.2));
        assertEquals(HardSoftDoubleScore.of(4.8, -4.8),
                HardSoftDoubleScore.of(4.0, -4.0).multiply(1.2));
        assertEquals(HardSoftDoubleScore.ofUninitialized(-14, 8.6, -10.4),
                HardSoftDoubleScore.ofUninitialized(-7, 4.3, -5.2).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftDoubleScore.of(5.0, -5.0),
                HardSoftDoubleScore.of(25.0, -25.0).divide(5.0));
        assertEquals(HardSoftDoubleScore.of(4.2, -4.2),
                HardSoftDoubleScore.of(21.0, -21.0).divide(5.0));
        assertEquals(HardSoftDoubleScore.of(4.8, -4.8),
                HardSoftDoubleScore.of(24.0, -24.0).divide(5.0));
        assertEquals(HardSoftDoubleScore.ofUninitialized(-7, 4.3, -5.2),
                HardSoftDoubleScore.ofUninitialized(-14, 8.6, -10.4).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftDoubleScore.of(16.0, 2.25),
                HardSoftDoubleScore.of(-4.0, 1.5).power(2.0));
        assertEquals(HardSoftDoubleScore.of(4.0, 1.5),
                HardSoftDoubleScore.of(16.0, 2.25).power(0.5));
        assertEquals(HardSoftDoubleScore.ofUninitialized(-343, -64.0, 125.0),
                HardSoftDoubleScore.ofUninitialized(-7, -4.0, 5.0).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftDoubleScore.of(-4.0, 1.5),
                HardSoftDoubleScore.of(4.0, -1.5).negate());
        assertEquals(HardSoftDoubleScore.of(4.0, -1.5),
                HardSoftDoubleScore.of(-4.0, 1.5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardSoftDoubleScore.of(-10.0, -200.0),
                HardSoftDoubleScore.of(-10.0, -200.0),
                HardSoftDoubleScore.ofUninitialized(0, -10.0, -200.0)
        );
        PlannerAssert.assertObjectsAreEqual(
                HardSoftDoubleScore.ofUninitialized(-7, -10.0, -200.0),
                HardSoftDoubleScore.ofUninitialized(-7, -10.0, -200.0)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                HardSoftDoubleScore.of(-10.0, -200.0),
                HardSoftDoubleScore.of(-30.0, -200.0),
                HardSoftDoubleScore.of(-10.0, -400.0),
                HardSoftDoubleScore.ofUninitialized(-7, -10.0, -200.0)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftDoubleScore.ofUninitialized(-8, 0.0, 0.0),
                HardSoftDoubleScore.ofUninitialized(-7, -20.0, -20.0),
                HardSoftDoubleScore.ofUninitialized(-7, -1.0, -300.0),
                HardSoftDoubleScore.ofUninitialized(-7, 0.0, 0.0),
                HardSoftDoubleScore.ofUninitialized(-7, 0.0, 1.0),
                HardSoftDoubleScore.of(-20.06, -20.0),
                HardSoftDoubleScore.of(-20.007, -20.0),
                HardSoftDoubleScore.of(-20.0, -Double.MAX_VALUE),
                HardSoftDoubleScore.of(-20.0, -20.06),
                HardSoftDoubleScore.of(-20.0, -20.007),
                HardSoftDoubleScore.of(-20.0, -20.0),
                HardSoftDoubleScore.of(-1.0, -300.0),
                HardSoftDoubleScore.of(-1.0, 4000.0),
                HardSoftDoubleScore.of(0.0, -1.0),
                HardSoftDoubleScore.of(0.0, 0.0),
                HardSoftDoubleScore.of(0.0, 1.0)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftDoubleScore.of(-12.3, 3400.5),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(-12.3, output.getHardScore(), 0.0);
                    assertEquals(3400.5, output.getSoftScore(), 0.0);
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftDoubleScore.ofUninitialized(-7, -12.3, 3400.5),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(-12.3, output.getHardScore(), 0.0);
                    assertEquals(3400.5, output.getSoftScore(), 0.0);
                }
        );
    }

}
