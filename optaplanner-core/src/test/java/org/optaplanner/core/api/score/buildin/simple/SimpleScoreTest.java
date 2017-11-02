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
        assertEquals(SimpleScore.valueOf(-147), SimpleScore.parseScore("-147"));
        assertEquals(SimpleScore.valueOfUninitialized(-7, -147), SimpleScore.parseScore("-7init/-147"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", SimpleScore.valueOf(0).toShortString());
        assertEquals("-147", SimpleScore.valueOf(-147).toShortString());
        assertEquals("-7init/-147", SimpleScore.valueOfUninitialized(-7, -147).toShortString());
        assertEquals("-7init", SimpleScore.valueOfUninitialized(-7, 0).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0", SimpleScore.valueOf(0).toString());
        assertEquals("-147", SimpleScore.valueOf(-147).toString());
        assertEquals("-7init/-147", SimpleScore.valueOfUninitialized(-7, -147).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        SimpleScore.parseScore("-147hard/-258soft");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(SimpleScore.valueOf(-147),
                SimpleScore.valueOf(-147).toInitializedScore());
        assertEquals(SimpleScore.valueOf(-147),
                SimpleScore.valueOfUninitialized(-7, -147).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(SimpleScore.valueOfUninitialized(-7, -147),
                SimpleScore.valueOf(-147).withInitScore(-7));
    }

    @Test
    public void add() {
        assertEquals(SimpleScore.valueOf(19),
                SimpleScore.valueOf(20).add(
                        SimpleScore.valueOf(-1)));
        assertEquals(SimpleScore.valueOfUninitialized(-77, 19),
                SimpleScore.valueOfUninitialized(-70, 20).add(
                        SimpleScore.valueOfUninitialized(-7, -1)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleScore.valueOf(21),
                SimpleScore.valueOf(20).subtract(
                        SimpleScore.valueOf(-1)));
        assertEquals(SimpleScore.valueOfUninitialized(-63, 21),
                SimpleScore.valueOfUninitialized(-70, 20).subtract(
                        SimpleScore.valueOfUninitialized(-7, -1)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleScore.valueOf(6),
                SimpleScore.valueOf(5).multiply(1.2));
        assertEquals(SimpleScore.valueOf(1),
                SimpleScore.valueOf(1).multiply(1.2));
        assertEquals(SimpleScore.valueOf(4),
                SimpleScore.valueOf(4).multiply(1.2));
        assertEquals(SimpleScore.valueOfUninitialized(-14, 8),
                SimpleScore.valueOfUninitialized(-7, 4).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(SimpleScore.valueOf(5),
                SimpleScore.valueOf(25).divide(5.0));
        assertEquals(SimpleScore.valueOf(4),
                SimpleScore.valueOf(21).divide(5.0));
        assertEquals(SimpleScore.valueOf(4),
                SimpleScore.valueOf(24).divide(5.0));
        assertEquals(SimpleScore.valueOfUninitialized(-7, 4),
                SimpleScore.valueOfUninitialized(-14, 8).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(SimpleScore.valueOf(25),
                SimpleScore.valueOf(5).power(2.0));
        assertEquals(SimpleScore.valueOf(5),
                SimpleScore.valueOf(25).power(0.5));
        assertEquals(SimpleScore.valueOfUninitialized(-343, 125),
                SimpleScore.valueOfUninitialized(-7, 5).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(SimpleScore.valueOf(-5),
                SimpleScore.valueOf(5).negate());
        assertEquals(SimpleScore.valueOf(5),
                SimpleScore.valueOf(-5).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleScore.valueOf(-10),
                SimpleScore.valueOf(-10),
                SimpleScore.valueOfUninitialized(0, -10)
        );
        PlannerAssert.assertObjectsAreEqual(
                SimpleScore.valueOfUninitialized(-7, -10),
                SimpleScore.valueOfUninitialized(-7, -10)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleScore.valueOf(-10),
                SimpleScore.valueOf(-30),
                SimpleScore.valueOfUninitialized(-7, -10)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleScore.valueOfUninitialized(-8, 0),
                SimpleScore.valueOfUninitialized(-7, -20),
                SimpleScore.valueOfUninitialized(-7, -1),
                SimpleScore.valueOfUninitialized(-7, 0),
                SimpleScore.valueOfUninitialized(-7, 1),
                SimpleScore.valueOf(-300),
                SimpleScore.valueOf(-20),
                SimpleScore.valueOf(-1),
                SimpleScore.valueOf(0),
                SimpleScore.valueOf(1)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleScore.valueOf(123),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(123, output.getScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleScore.valueOfUninitialized(-7, 123),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(123, output.getScore());
                }
        );
    }

}
