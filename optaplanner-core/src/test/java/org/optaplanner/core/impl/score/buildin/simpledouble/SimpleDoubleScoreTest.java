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

package org.optaplanner.core.impl.score.buildin.simpledouble;

import org.optaplanner.core.api.score.buildin.simpledouble.SimpleDoubleScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleDoubleScoreTest extends AbstractScoreTest {

    @Test
    public void add() {
        assertEquals(SimpleDoubleScore.valueOf(19.0),
                SimpleDoubleScore.valueOf(20.0).add(
                        SimpleDoubleScore.valueOf(-1.0)));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleDoubleScore.valueOf(21.0),
                SimpleDoubleScore.valueOf(20.0).subtract(
                        SimpleDoubleScore.valueOf(-1.0)));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleDoubleScore.valueOf(6.0),
                SimpleDoubleScore.valueOf(5.0).multiply(1.2));
        assertEquals(SimpleDoubleScore.valueOf(1.2),
                SimpleDoubleScore.valueOf(1.0).multiply(1.2));
        assertEquals(SimpleDoubleScore.valueOf(4.8),
                SimpleDoubleScore.valueOf(4.0).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(SimpleDoubleScore.valueOf(5.0),
                SimpleDoubleScore.valueOf(25.0).divide(5.0));
        assertEquals(SimpleDoubleScore.valueOf(4.2),
                SimpleDoubleScore.valueOf(21.0).divide(5.0));
        assertEquals(SimpleDoubleScore.valueOf(4.8),
                SimpleDoubleScore.valueOf(24.0).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleDoubleScore.valueOf(-10.0),
                SimpleDoubleScore.valueOf(-10.0)
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                SimpleDoubleScore.valueOf(-300.5),
                SimpleDoubleScore.valueOf(-300.0),
                SimpleDoubleScore.valueOf(-20.06),
                SimpleDoubleScore.valueOf(-20.007),
                SimpleDoubleScore.valueOf(-20.0),
                SimpleDoubleScore.valueOf(-1.0),
                SimpleDoubleScore.valueOf(0.0),
                SimpleDoubleScore.valueOf(1.0)
        );
    }

}
