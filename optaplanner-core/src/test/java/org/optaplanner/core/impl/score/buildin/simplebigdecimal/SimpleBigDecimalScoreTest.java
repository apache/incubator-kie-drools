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

package org.optaplanner.core.impl.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.impl.score.buildin.AbstractScoreTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    public void add() {
        assertEquals(SimpleBigDecimalScore.valueOf(new BigDecimal("19")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("20")).add(
                        SimpleBigDecimalScore.valueOf(new BigDecimal("-1"))));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleBigDecimalScore.valueOf(new BigDecimal("21")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("20")).subtract(
                        SimpleBigDecimalScore.valueOf(new BigDecimal("-1"))));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleBigDecimalScore.valueOf(new BigDecimal("6.0")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("5.0")).multiply(1.2));
        assertEquals(SimpleBigDecimalScore.valueOf(new BigDecimal("1.2")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("1.0")).multiply(1.2));
        assertEquals(SimpleBigDecimalScore.valueOf(new BigDecimal("4.8")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("4.0")).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(SimpleBigDecimalScore.valueOf(new BigDecimal("5.0")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("25.0")).divide(5.0));
        assertEquals(SimpleBigDecimalScore.valueOf(new BigDecimal("4.2")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("21.0")).divide(5.0));
        assertEquals(SimpleBigDecimalScore.valueOf(new BigDecimal("4.8")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("24.0")).divide(5.0));
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleBigDecimalScore.valueOf(new BigDecimal("-10")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-10"))
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                SimpleBigDecimalScore.valueOf(new BigDecimal("-300.5")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-300")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-20.067")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-20.007")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-20")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("-1")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("0")),
                SimpleBigDecimalScore.valueOf(new BigDecimal("1"))
        );
    }

}
