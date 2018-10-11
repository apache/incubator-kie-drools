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

package org.optaplanner.core.api.score.buildin.simpledouble;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class SimpleDoubleScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(SimpleDoubleScore.of(-147.2),
                SimpleDoubleScore.parseScore("-147.2"));
        assertEquals(SimpleDoubleScore.ofUninitialized(-7, -147.2),
                SimpleDoubleScore.parseScore("-7init/-147.2"));
        assertEquals(SimpleDoubleScore.of(Double.MIN_VALUE),
                SimpleDoubleScore.parseScore("*"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", SimpleDoubleScore.of(0.0).toShortString());
        assertEquals("-147.2", SimpleDoubleScore.of(-147.2).toShortString());
        assertEquals("-7init/-147.2", SimpleDoubleScore.ofUninitialized(-7, -147.2).toShortString());
        assertEquals("-7init", SimpleDoubleScore.ofUninitialized(-7, 0.0).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0.0", SimpleDoubleScore.of(0.0).toString());
        assertEquals("-147.2", SimpleDoubleScore.of(-147.2).toString());
        assertEquals("-7init/-147.2", SimpleDoubleScore.ofUninitialized(-7, -147.2).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        SimpleDoubleScore.parseScore("-147.2hard/-258.3soft");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(SimpleDoubleScore.of(-147.2),
                SimpleDoubleScore.of(-147.2).toInitializedScore());
        assertEquals(SimpleDoubleScore.of(-147.2),
                SimpleDoubleScore.ofUninitialized(-7, -147.2).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(SimpleDoubleScore.ofUninitialized(-7, -147.2),
                SimpleDoubleScore.of(-147.2).withInitScore(-7));
    }

    @Test
    public void add() {
        assertEquals(SimpleDoubleScore.of(19.0),
                SimpleDoubleScore.of(20.0).add(
                        SimpleDoubleScore.of(-1.0)));
        assertEquals(SimpleDoubleScore.ofUninitialized(-77, 19.0),
                SimpleDoubleScore.ofUninitialized(-70, 20.0).add(
                        SimpleDoubleScore.ofUninitialized(-7, -1.0)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleDoubleScore.of(21.0),
                SimpleDoubleScore.of(20.0).subtract(
                        SimpleDoubleScore.of(-1.0)));
        assertEquals(SimpleDoubleScore.ofUninitialized(-63, 21.0),
                SimpleDoubleScore.ofUninitialized(-70, 20.0).subtract(
                        SimpleDoubleScore.ofUninitialized(-7, -1.0)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleDoubleScore.of(6.0),
                SimpleDoubleScore.of(5.0).multiply(1.2));
        assertEquals(SimpleDoubleScore.of(1.2),
                SimpleDoubleScore.of(1.0).multiply(1.2));
        assertEquals(SimpleDoubleScore.of(4.8),
                SimpleDoubleScore.of(4.0).multiply(1.2));
        assertEquals(SimpleDoubleScore.ofUninitialized(-14, 8.6),
                SimpleDoubleScore.ofUninitialized(-7, 4.3).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(SimpleDoubleScore.of(5.0),
                SimpleDoubleScore.of(25.0).divide(5.0));
        assertEquals(SimpleDoubleScore.of(4.2),
                SimpleDoubleScore.of(21.0).divide(5.0));
        assertEquals(SimpleDoubleScore.of(4.8),
                SimpleDoubleScore.of(24.0).divide(5.0));
        assertEquals(SimpleDoubleScore.ofUninitialized(-7, 4.3),
                SimpleDoubleScore.ofUninitialized(-14, 8.6).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(SimpleDoubleScore.of(2.25),
                SimpleDoubleScore.of(1.5).power(2.0));
        assertEquals(SimpleDoubleScore.of(1.5),
                SimpleDoubleScore.of(2.25).power(0.5));
        assertEquals(SimpleDoubleScore.ofUninitialized(-343, 125.0),
                SimpleDoubleScore.ofUninitialized(-7, 5.0).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(SimpleDoubleScore.of(-1.5),
                SimpleDoubleScore.of(1.5).negate());
        assertEquals(SimpleDoubleScore.of(1.5),
                SimpleDoubleScore.of(-1.5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleDoubleScore.of(-10.0),
                SimpleDoubleScore.of(-10.0),
                SimpleDoubleScore.ofUninitialized(0, -10.0)
        );
        PlannerAssert.assertObjectsAreEqual(
                SimpleDoubleScore.ofUninitialized(-7, -10.0),
                SimpleDoubleScore.ofUninitialized(-7, -10.0)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleDoubleScore.of(-10.0),
                SimpleDoubleScore.of(-30.0),
                SimpleDoubleScore.ofUninitialized(-7, -10.0)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleDoubleScore.ofUninitialized(-8, -0.0),
                SimpleDoubleScore.ofUninitialized(-7, -20.0),
                SimpleDoubleScore.ofUninitialized(-7, -1.0),
                SimpleDoubleScore.ofUninitialized(-7, 0.0),
                SimpleDoubleScore.ofUninitialized(-7, 1.0),
                SimpleDoubleScore.of(-300.5),
                SimpleDoubleScore.of(-300.0),
                SimpleDoubleScore.of(-20.06),
                SimpleDoubleScore.of(-20.007),
                SimpleDoubleScore.of(-20.0),
                SimpleDoubleScore.of(-1.0),
                SimpleDoubleScore.of(0.0),
                SimpleDoubleScore.of(1.0)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleDoubleScore.of(123.4),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(123.4, output.getScore(), 0.0);
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleDoubleScore.ofUninitialized(-7, 123.4),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(123.4, output.getScore(), 0.0);
                }
        );
    }

}
