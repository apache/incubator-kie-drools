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

package org.optaplanner.core.api.score.buildin.simple;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class SimpleScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(SimpleScore.of(-147), SimpleScore.parseScore("-147"));
        assertEquals(SimpleScore.ofUninitialized(-7, -147), SimpleScore.parseScore("-7init/-147"));
        assertEquals(SimpleScore.of(Integer.MIN_VALUE), SimpleScore.parseScore("*"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", SimpleScore.of(0).toShortString());
        assertEquals("-147", SimpleScore.of(-147).toShortString());
        assertEquals("-7init/-147", SimpleScore.ofUninitialized(-7, -147).toShortString());
        assertEquals("-7init", SimpleScore.ofUninitialized(-7, 0).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0", SimpleScore.of(0).toString());
        assertEquals("-147", SimpleScore.of(-147).toString());
        assertEquals("-7init/-147", SimpleScore.ofUninitialized(-7, -147).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        SimpleScore.parseScore("-147hard/-258soft");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(SimpleScore.of(-147),
                SimpleScore.of(-147).toInitializedScore());
        assertEquals(SimpleScore.of(-147),
                SimpleScore.ofUninitialized(-7, -147).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(SimpleScore.ofUninitialized(-7, -147),
                SimpleScore.of(-147).withInitScore(-7));
    }

    @Test
    public void add() {
        assertEquals(SimpleScore.of(19),
                SimpleScore.of(20).add(
                        SimpleScore.of(-1)));
        assertEquals(SimpleScore.ofUninitialized(-77, 19),
                SimpleScore.ofUninitialized(-70, 20).add(
                        SimpleScore.ofUninitialized(-7, -1)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleScore.of(21),
                SimpleScore.of(20).subtract(
                        SimpleScore.of(-1)));
        assertEquals(SimpleScore.ofUninitialized(-63, 21),
                SimpleScore.ofUninitialized(-70, 20).subtract(
                        SimpleScore.ofUninitialized(-7, -1)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleScore.of(6),
                SimpleScore.of(5).multiply(1.2));
        assertEquals(SimpleScore.of(1),
                SimpleScore.of(1).multiply(1.2));
        assertEquals(SimpleScore.of(4),
                SimpleScore.of(4).multiply(1.2));
        assertEquals(SimpleScore.ofUninitialized(-14, 8),
                SimpleScore.ofUninitialized(-7, 4).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(SimpleScore.of(5),
                SimpleScore.of(25).divide(5.0));
        assertEquals(SimpleScore.of(4),
                SimpleScore.of(21).divide(5.0));
        assertEquals(SimpleScore.of(4),
                SimpleScore.of(24).divide(5.0));
        assertEquals(SimpleScore.ofUninitialized(-7, 4),
                SimpleScore.ofUninitialized(-14, 8).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(SimpleScore.of(25),
                SimpleScore.of(5).power(2.0));
        assertEquals(SimpleScore.of(5),
                SimpleScore.of(25).power(0.5));
        assertEquals(SimpleScore.ofUninitialized(-343, 125),
                SimpleScore.ofUninitialized(-7, 5).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(SimpleScore.of(-5),
                SimpleScore.of(5).negate());
        assertEquals(SimpleScore.of(5),
                SimpleScore.of(-5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleScore.of(-10),
                SimpleScore.of(-10),
                SimpleScore.ofUninitialized(0, -10)
        );
        PlannerAssert.assertObjectsAreEqual(
                SimpleScore.ofUninitialized(-7, -10),
                SimpleScore.ofUninitialized(-7, -10)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleScore.of(-10),
                SimpleScore.of(-30),
                SimpleScore.ofUninitialized(-7, -10)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleScore.ofUninitialized(-8, 0),
                SimpleScore.ofUninitialized(-7, -20),
                SimpleScore.ofUninitialized(-7, -1),
                SimpleScore.ofUninitialized(-7, 0),
                SimpleScore.ofUninitialized(-7, 1),
                SimpleScore.of(-300),
                SimpleScore.of(-20),
                SimpleScore.of(-1),
                SimpleScore.of(0),
                SimpleScore.of(1)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleScore.of(123),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(123, output.getScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleScore.ofUninitialized(-7, 123),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(123, output.getScore());
                }
        );
    }

}
