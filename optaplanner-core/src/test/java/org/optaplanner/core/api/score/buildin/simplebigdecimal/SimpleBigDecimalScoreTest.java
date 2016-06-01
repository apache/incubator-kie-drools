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

package org.optaplanner.core.api.score.buildin.simplebigdecimal;

import java.math.BigDecimal;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class SimpleBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2")),
                SimpleBigDecimalScore.parseScore("-147.2"));
        assertEquals(SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-147.2")),
                SimpleBigDecimalScore.parseScore("-7init/-147.2"));
    }

    @Test
    public void testToString() {
        assertEquals("-147.2", SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2")).toString());
        assertEquals("-7init/-147.2", SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-147.2")).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        SimpleBigDecimalScore.parseScore("-147.2hard/-258.3soft");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2")).toInitializedScore());
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-147.2")).toInitializedScore());
    }

    @Test
    public void add() {
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("19")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("20")).add(
                        SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-1"))));
        assertEquals(SimpleBigDecimalScore.valueOf(-77, new BigDecimal("19")),
                SimpleBigDecimalScore.valueOf(-70, new BigDecimal("20")).add(
                        SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-1"))));
    }

    @Test
    public void subtract() {
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("21")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("20")).subtract(
                        SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-1"))));
        assertEquals(SimpleBigDecimalScore.valueOf(-63, new BigDecimal("21")),
                SimpleBigDecimalScore.valueOf(-70, new BigDecimal("20")).subtract(
                        SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-1"))));
    }

    @Test
    public void multiply() {
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("6.0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("5.0")).multiply(1.2));
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("1.2")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("1.0")).multiply(1.2));
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("4.8")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("4.0")).multiply(1.2));
        assertEquals(SimpleBigDecimalScore.valueOf(-14, new BigDecimal("8.6")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("4.3")).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("5.0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("25.0")).divide(5.0));
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("4.2")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("21.0")).divide(5.0));
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("4.8")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("24.0")).divide(5.0));
        assertEquals(SimpleBigDecimalScore.valueOf(-7, new BigDecimal("4.3")),
                SimpleBigDecimalScore.valueOf(-14, new BigDecimal("8.6")).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("25.0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("5.0")).power(2.0));
        assertEquals(SimpleBigDecimalScore.valueOf(-343, new BigDecimal("125.0")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("5.0")).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-5.0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("5.0")).negate());
        assertEquals(SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("5.0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-5.0")).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-10.0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-10.0")),
                SimpleBigDecimalScore.valueOf(0, new BigDecimal("-10.0"))
        );
        assertScoresEqualsAndHashCode(
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-10.0")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-10.0"))
        );
        assertScoresNotEquals(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-10.0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-30.0")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-10.0"))
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleBigDecimalScore.valueOf(-8, new BigDecimal("0.0")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-20.0")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("-1.0")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("0.0")),
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("1.0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-300.5")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-300")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-20.067")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-20.007")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-20")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("-1")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("0")),
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("1"))
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleBigDecimalScore.valueOfInitialized(new BigDecimal("123.4")),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(new BigDecimal("123.4"), output.getScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleBigDecimalScore.valueOf(-7, new BigDecimal("123.4")),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(new BigDecimal("123.4"), output.getScore());
                }
        );
    }

}
