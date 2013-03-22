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

package org.optaplanner.core.impl.score.buildin.simplelong;

import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleLongScoreTest extends AbstractScoreTest {

    @Test
    public void add() {
        assertEquals(SimpleLongScore.valueOf(19L),
                SimpleLongScore.valueOf(20L).add(
                        SimpleLongScore.valueOf(-1L)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleLongScore.valueOf(21L),
                SimpleLongScore.valueOf(20L).subtract(
                        SimpleLongScore.valueOf(-1L)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleLongScore.valueOf(6L),
                SimpleLongScore.valueOf(5L).multiply(1.2));
        assertEquals(SimpleLongScore.valueOf(1L),
                SimpleLongScore.valueOf(1L).multiply(1.2));
        assertEquals(SimpleLongScore.valueOf(4L),
                SimpleLongScore.valueOf(4L).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(SimpleLongScore.valueOf(5L),
                SimpleLongScore.valueOf(25L).divide(5.0));
        assertEquals(SimpleLongScore.valueOf(4L),
                SimpleLongScore.valueOf(21L).divide(5.0));
        assertEquals(SimpleLongScore.valueOf(4L),
                SimpleLongScore.valueOf(24L).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleLongScore.valueOf(-10L),
                SimpleLongScore.valueOf(-10L)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                SimpleLongScore.valueOf(((long) Integer.MIN_VALUE) - 4000L),
                SimpleLongScore.valueOf(-300L),
                SimpleLongScore.valueOf(-20L),
                SimpleLongScore.valueOf(-1L),
                SimpleLongScore.valueOf(0L),
                SimpleLongScore.valueOf(1L),
                SimpleLongScore.valueOf(((long) Integer.MAX_VALUE) + 4000L)
        );
    }

}
