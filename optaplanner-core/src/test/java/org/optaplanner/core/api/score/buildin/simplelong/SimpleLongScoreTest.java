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
        assertEquals(SimpleLongScore.of(-147L),
                SimpleLongScore.parseScore("-147"));
        assertEquals(SimpleLongScore.ofUninitialized(-7, -147L),
                SimpleLongScore.parseScore("-7init/-147"));
        assertEquals(SimpleLongScore.of(Long.MIN_VALUE),
                SimpleLongScore.parseScore("*"));
    }

    @Test
    public void toShortString() {
        assertEquals("0", SimpleLongScore.of(0L).toShortString());
        assertEquals("-147", SimpleLongScore.of(-147L).toShortString());
        assertEquals("-7init/-147", SimpleLongScore.ofUninitialized(-7, -147L).toShortString());
        assertEquals("-7init", SimpleLongScore.ofUninitialized(-7, 0L).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0", SimpleLongScore.of(0).toString());
        assertEquals("-147", SimpleLongScore.of(-147L).toString());
        assertEquals("-7init/-147", SimpleLongScore.ofUninitialized(-7, -147L).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        SimpleLongScore.parseScore("-147hard/-258soft");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(SimpleLongScore.of(-147L),
                SimpleLongScore.of(-147L).toInitializedScore());
        assertEquals(SimpleLongScore.of(-147L),
                SimpleLongScore.ofUninitialized(-7, -147L).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(SimpleLongScore.ofUninitialized(-7, -147L),
                SimpleLongScore.of(-147L).withInitScore(-7));
    }

    @Test
    public void add() {
        assertEquals(SimpleLongScore.of(19L),
                SimpleLongScore.of(20L).add(
                        SimpleLongScore.of(-1L)));
        assertEquals(SimpleLongScore.ofUninitialized(-77, 19L),
                SimpleLongScore.ofUninitialized(-70, 20L).add(
                        SimpleLongScore.ofUninitialized(-7, -1L)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleLongScore.of(21L),
                SimpleLongScore.of(20L).subtract(
                        SimpleLongScore.of(-1L)));
        assertEquals(SimpleLongScore.ofUninitialized(-63, 21L),
                SimpleLongScore.ofUninitialized(-70, 20L).subtract(
                        SimpleLongScore.ofUninitialized(-7, -1L)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleLongScore.of(6L),
                SimpleLongScore.of(5L).multiply(1.2));
        assertEquals(SimpleLongScore.of(1L),
                SimpleLongScore.of(1L).multiply(1.2));
        assertEquals(SimpleLongScore.of(4L),
                SimpleLongScore.of(4L).multiply(1.2));
        assertEquals(SimpleLongScore.ofUninitialized(-14, 8L),
                SimpleLongScore.ofUninitialized(-7, 4L).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(SimpleLongScore.of(5L),
                SimpleLongScore.of(25L).divide(5.0));
        assertEquals(SimpleLongScore.of(4L),
                SimpleLongScore.of(21L).divide(5.0));
        assertEquals(SimpleLongScore.of(4L),
                SimpleLongScore.of(24L).divide(5.0));
        assertEquals(SimpleLongScore.ofUninitialized(-7, 4L),
                SimpleLongScore.ofUninitialized(-14, 8L).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(SimpleLongScore.of(25L),
                SimpleLongScore.of(5L).power(2.0));
        assertEquals(SimpleLongScore.of(5L),
                SimpleLongScore.of(25L).power(0.5));
        assertEquals(SimpleLongScore.ofUninitialized(-343, 125L),
                SimpleLongScore.ofUninitialized(-7, 5L).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(SimpleLongScore.of(-5L),
                SimpleLongScore.of(5L).negate());
        assertEquals(SimpleLongScore.of(5L),
                SimpleLongScore.of(-5L).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleLongScore.of(-10L),
                SimpleLongScore.of(-10L),
                SimpleLongScore.ofUninitialized(0, -10L)
        );
        PlannerAssert.assertObjectsAreEqual(
                SimpleLongScore.ofUninitialized(-7, -10L),
                SimpleLongScore.ofUninitialized(-7, -10L)
        );
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleLongScore.of(-10L),
                SimpleLongScore.of(-30L),
                SimpleLongScore.ofUninitialized(-7, -10L)
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleLongScore.ofUninitialized(-8, 0L),
                SimpleLongScore.ofUninitialized(-7, -20L),
                SimpleLongScore.ofUninitialized(-7, -1L),
                SimpleLongScore.ofUninitialized(-7, 0L),
                SimpleLongScore.ofUninitialized(-7, 1L),
                SimpleLongScore.of(((long) Integer.MIN_VALUE) - 4000L),
                SimpleLongScore.of(-300L),
                SimpleLongScore.of(-20L),
                SimpleLongScore.of(-1L),
                SimpleLongScore.of(0L),
                SimpleLongScore.of(1L),
                SimpleLongScore.of(((long) Integer.MAX_VALUE) + 4000L)
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleLongScore.of(123L),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(123L, output.getScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleLongScore.ofUninitialized(-7, 123L),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(123L, output.getScore());
                }
        );
    }

}
