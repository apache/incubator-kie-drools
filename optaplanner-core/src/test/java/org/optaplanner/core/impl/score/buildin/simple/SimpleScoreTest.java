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

package org.optaplanner.core.impl.score.buildin.simple;

import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleScoreTest extends AbstractScoreTest {

    @Test
    public void add() {
        assertEquals(SimpleScore.valueOf(19),
                SimpleScore.valueOf(20).add(
                        SimpleScore.valueOf(-1)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleScore.valueOf(21),
                SimpleScore.valueOf(20).subtract(
                        SimpleScore.valueOf(-1)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleScore.valueOf(6),
                SimpleScore.valueOf(5).multiply(1.2));
        assertEquals(SimpleScore.valueOf(1),
                SimpleScore.valueOf(1).multiply(1.2));
        assertEquals(SimpleScore.valueOf(4),
                SimpleScore.valueOf(4).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(SimpleScore.valueOf(5),
                SimpleScore.valueOf(25).divide(5.0));
        assertEquals(SimpleScore.valueOf(4),
                SimpleScore.valueOf(21).divide(5.0));
        assertEquals(SimpleScore.valueOf(4),
                SimpleScore.valueOf(24).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleScore.valueOf(-10),
                SimpleScore.valueOf(-10)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                SimpleScore.valueOf(-300),
                SimpleScore.valueOf(-20),
                SimpleScore.valueOf(-1),
                SimpleScore.valueOf(0),
                SimpleScore.valueOf(1)
        );
    }

}
