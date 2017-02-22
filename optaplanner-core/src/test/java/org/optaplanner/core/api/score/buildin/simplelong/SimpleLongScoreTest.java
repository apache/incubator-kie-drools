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

package org.optaplanner.core.api.score.buildin.simplelong;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class SimpleLongScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(SimpleLongScore.valueOf(-147L),
                SimpleLongScore.parseScore("-147"));
        assertEquals(SimpleLongScore.valueOfUninitialized(-7, -147L),
                SimpleLongScore.parseScore("-7init/-147"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", SimpleLongScore.valueOf(0L).toShortString());
        assertEquals("-147", SimpleLongScore.valueOf(-147L).toShortString());
        assertEquals("-7init/-147", SimpleLongScore.valueOfUninitialized(-7, -147L).toShortString());
        assertEquals("-7init", SimpleLongScore.valueOfUninitialized(-7, 0L).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0", SimpleLongScore.valueOf(0).toString());
        assertEquals("-147", SimpleLongScore.valueOf(-147L).toString());
        assertEquals("-7init/-147", SimpleLongScore.valueOfUninitialized(-7, -147L).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        SimpleLongScore.parseScore("-147hard/-258soft");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(SimpleLongScore.valueOf(-147L),
                SimpleLongScore.valueOf(-147L).toInitializedScore());
        assertEquals(SimpleLongScore.valueOf(-147L),
                SimpleLongScore.valueOfUninitialized(-7, -147L).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(SimpleLongScore.valueOfUninitialized(-7, -147L),
                SimpleLongScore.valueOf(-147L).withInitScore(-7));
    }

    @Test
    public void add() {
        assertEquals(SimpleLongScore.valueOf(19L),
                SimpleLongScore.valueOf(20L).add(
                        SimpleLongScore.valueOf(-1L)));
        assertEquals(SimpleLongScore.valueOfUninitialized(-77, 19L),
                SimpleLongScore.valueOfUninitialized(-70, 20L).add(
                        SimpleLongScore.valueOfUninitialized(-7, -1L)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleLongScore.valueOf(21L),
                SimpleLongScore.valueOf(20L).subtract(
                        SimpleLongScore.valueOf(-1L)));
        assertEquals(SimpleLongScore.valueOfUninitialized(-63, 21L),
                SimpleLongScore.valueOfUninitialized(-70, 20L).subtract(
                        SimpleLongScore.valueOfUninitialized(-7, -1L)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleLongScore.valueOf(6L),
                SimpleLongScore.valueOf(5L).multiply(1.2));
        assertEquals(SimpleLongScore.valueOf(1L),
                SimpleLongScore.valueOf(1L).multiply(1.2));
        assertEquals(SimpleLongScore.valueOf(4L),
                SimpleLongScore.valueOf(4L).multiply(1.2));
        assertEquals(SimpleLongScore.valueOfUninitialized(-14, 8L),
                SimpleLongScore.valueOfUninitialized(-7, 4L).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(SimpleLongScore.valueOf(5L),
                SimpleLongScore.valueOf(25L).divide(5.0));
        assertEquals(SimpleLongScore.valueOf(4L),
                SimpleLongScore.valueOf(21L).divide(5.0));
        assertEquals(SimpleLongScore.valueOf(4L),
                SimpleLongScore.valueOf(24L).divide(5.0));
        assertEquals(SimpleLongScore.valueOfUninitialized(-7, 4L),
                SimpleLongScore.valueOfUninitialized(-14, 8L).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(SimpleLongScore.valueOf(25L),
                SimpleLongScore.valueOf(5L).power(2.0));
        assertEquals(SimpleLongScore.valueOf(5L),
                SimpleLongScore.valueOf(25L).power(0.5));
        assertEquals(SimpleLongScore.valueOfUninitialized(-343, 125L),
                SimpleLongScore.valueOfUninitialized(-7, 5L).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(SimpleLongScore.valueOf(-5L),
                SimpleLongScore.valueOf(5L).negate());
        assertEquals(SimpleLongScore.valueOf(5L),
                SimpleLongScore.valueOf(-5L).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleLongScore.valueOf(-10L),
                SimpleLongScore.valueOf(-10L),
                SimpleLongScore.valueOfUninitialized(0, -10L)
        );
        assertScoresEqualsAndHashCode(
                SimpleLongScore.valueOfUninitialized(-7, -10L),
                SimpleLongScore.valueOfUninitialized(-7, -10L)
        );
        assertScoresNotEquals(
                SimpleLongScore.valueOf(-10L),
                SimpleLongScore.valueOf(-30L),
                SimpleLongScore.valueOfUninitialized(-7, -10L)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleLongScore.valueOfUninitialized(-8, 0L),
                SimpleLongScore.valueOfUninitialized(-7, -20L),
                SimpleLongScore.valueOfUninitialized(-7, -1L),
                SimpleLongScore.valueOfUninitialized(-7, 0L),
                SimpleLongScore.valueOfUninitialized(-7, 1L),
                SimpleLongScore.valueOf(((long) Integer.MIN_VALUE) - 4000L),
                SimpleLongScore.valueOf(-300L),
                SimpleLongScore.valueOf(-20L),
                SimpleLongScore.valueOf(-1L),
                SimpleLongScore.valueOf(0L),
                SimpleLongScore.valueOf(1L),
                SimpleLongScore.valueOf(((long) Integer.MAX_VALUE) + 4000L)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleLongScore.valueOf(123L),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(123L, output.getScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleLongScore.valueOfUninitialized(-7, 123L),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(123L, output.getScore());
                }
        );
    }

}
