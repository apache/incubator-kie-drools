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
        assertEquals(SimpleLongScore.valueOfInitialized(-147L),
                SimpleLongScore.parseScore("-147"));
        assertEquals(SimpleLongScore.valueOf(-7, -147L),
                SimpleLongScore.parseScore("-7init/-147"));
    }

    @Test
    public void testToString() {
        assertEquals("-147", SimpleLongScore.valueOfInitialized(-147L).toString());
        assertEquals("-7init/-147", SimpleLongScore.valueOf(-7, -147L).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        SimpleLongScore.parseScore("-147hard/-258soft");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(SimpleLongScore.valueOfInitialized(-147L),
                SimpleLongScore.valueOfInitialized(-147L).toInitializedScore());
        assertEquals(SimpleLongScore.valueOfInitialized(-147L),
                SimpleLongScore.valueOf(-7, -147L).toInitializedScore());
    }

    @Test
    public void add() {
        assertEquals(SimpleLongScore.valueOfInitialized(19L),
                SimpleLongScore.valueOfInitialized(20L).add(
                        SimpleLongScore.valueOfInitialized(-1L)));
        assertEquals(SimpleLongScore.valueOf(-77, 19L),
                SimpleLongScore.valueOf(-70, 20L).add(
                        SimpleLongScore.valueOf(-7, -1L)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleLongScore.valueOfInitialized(21L),
                SimpleLongScore.valueOfInitialized(20L).subtract(
                        SimpleLongScore.valueOfInitialized(-1L)));
        assertEquals(SimpleLongScore.valueOf(-63, 21L),
                SimpleLongScore.valueOf(-70, 20L).subtract(
                        SimpleLongScore.valueOf(-7, -1L)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleLongScore.valueOfInitialized(6L),
                SimpleLongScore.valueOfInitialized(5L).multiply(1.2));
        assertEquals(SimpleLongScore.valueOfInitialized(1L),
                SimpleLongScore.valueOfInitialized(1L).multiply(1.2));
        assertEquals(SimpleLongScore.valueOfInitialized(4L),
                SimpleLongScore.valueOfInitialized(4L).multiply(1.2));
        assertEquals(SimpleLongScore.valueOf(-14, 8L),
                SimpleLongScore.valueOf(-7, 4L).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(SimpleLongScore.valueOfInitialized(5L),
                SimpleLongScore.valueOfInitialized(25L).divide(5.0));
        assertEquals(SimpleLongScore.valueOfInitialized(4L),
                SimpleLongScore.valueOfInitialized(21L).divide(5.0));
        assertEquals(SimpleLongScore.valueOfInitialized(4L),
                SimpleLongScore.valueOfInitialized(24L).divide(5.0));
        assertEquals(SimpleLongScore.valueOf(-7, 4L),
                SimpleLongScore.valueOf(-14, 8L).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(SimpleLongScore.valueOfInitialized(25L),
                SimpleLongScore.valueOfInitialized(5L).power(2.0));
        assertEquals(SimpleLongScore.valueOfInitialized(5L),
                SimpleLongScore.valueOfInitialized(25L).power(0.5));
        assertEquals(SimpleLongScore.valueOf(-343, 125L),
                SimpleLongScore.valueOf(-7, 5L).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(SimpleLongScore.valueOfInitialized(-5L),
                SimpleLongScore.valueOfInitialized(5L).negate());
        assertEquals(SimpleLongScore.valueOfInitialized(5L),
                SimpleLongScore.valueOfInitialized(-5L).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleLongScore.valueOfInitialized(-10L),
                SimpleLongScore.valueOfInitialized(-10L),
                SimpleLongScore.valueOf(0, -10L)
        );
        assertScoresEqualsAndHashCode(
                SimpleLongScore.valueOf(-7, -10L),
                SimpleLongScore.valueOf(-7, -10L)
        );
        assertScoresNotEquals(
                SimpleLongScore.valueOfInitialized(-10L),
                SimpleLongScore.valueOfInitialized(-30L),
                SimpleLongScore.valueOf(-7, -10L)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleLongScore.valueOf(-8, 0L),
                SimpleLongScore.valueOf(-7, -20L),
                SimpleLongScore.valueOf(-7, -1L),
                SimpleLongScore.valueOf(-7, 0L),
                SimpleLongScore.valueOf(-7, 1L),
                SimpleLongScore.valueOfInitialized(((long) Integer.MIN_VALUE) - 4000L),
                SimpleLongScore.valueOfInitialized(-300L),
                SimpleLongScore.valueOfInitialized(-20L),
                SimpleLongScore.valueOfInitialized(-1L),
                SimpleLongScore.valueOfInitialized(0L),
                SimpleLongScore.valueOfInitialized(1L),
                SimpleLongScore.valueOfInitialized(((long) Integer.MAX_VALUE) + 4000L)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleLongScore.valueOfInitialized(123L),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(123L, output.getScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleLongScore.valueOf(-7, 123L),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(123L, output.getScore());
                }
        );
    }

}
