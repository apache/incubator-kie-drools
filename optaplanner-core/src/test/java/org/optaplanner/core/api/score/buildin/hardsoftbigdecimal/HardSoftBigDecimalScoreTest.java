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

package org.optaplanner.core.api.score.buildin.hardsoftbigdecimal;

import java.math.BigDecimal;

import org.junit.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class HardSoftBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.parseScore("-147.2hard/-258.3soft"));
        assertEquals(HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.parseScore("-7init/-147.2hard/-258.3soft"));
    }

    @Test
    public void testToString() {
        assertEquals("-147.2hard/-258.3soft",
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2"), new BigDecimal("-258.3")).toString());
        assertEquals("-7init/-147.2hard/-258.3soft",
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftBigDecimalScore.parseScore("-147.2");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2"), new BigDecimal("-258.3")).toInitializedScore());
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")).toInitializedScore());
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-5"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-5"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-0.007"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-5"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("0"), new BigDecimal("-300"))
        );
        assertScoreFeasible(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0"), new BigDecimal("-300.007")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("2"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(0, new BigDecimal("0"), new BigDecimal("-300"))
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("19"), new BigDecimal("-320")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("20"), new BigDecimal("-20")).add(
                        HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-1"), new BigDecimal("-300"))));
        assertEquals(HardSoftBigDecimalScore.valueOf(-77, new BigDecimal("19"), new BigDecimal("-320")),
                HardSoftBigDecimalScore.valueOf(-70, new BigDecimal("20"), new BigDecimal("-20")).add(
                        HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-1"), new BigDecimal("-300"))));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("21"), new BigDecimal("280")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("20"), new BigDecimal("-20")).subtract(
                        HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-1"), new BigDecimal("-300"))));
        assertEquals(HardSoftBigDecimalScore.valueOf(-63, new BigDecimal("21"), new BigDecimal("280")),
                HardSoftBigDecimalScore.valueOf(-70, new BigDecimal("20"), new BigDecimal("-20")).subtract(
                        HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-1"), new BigDecimal("-300"))));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("6.0"), new BigDecimal("-6.0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("5.0"), new BigDecimal("-5.0")).multiply(1.2));
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("1.2"), new BigDecimal("-1.2")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("1.0"), new BigDecimal("-1.0")).multiply(1.2));
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("4.8"), new BigDecimal("-4.8")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("4.0"), new BigDecimal("-4.0")).multiply(1.2));
        assertEquals(HardSoftBigDecimalScore.valueOf(-14, new BigDecimal("8.6"), new BigDecimal("-10.4")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("4.3"), new BigDecimal("-5.2")).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("5.0"), new BigDecimal("-5.0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("25.0"), new BigDecimal("-25.0")).divide(5.0));
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("4.2"), new BigDecimal("-4.2")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("21.0"), new BigDecimal("-21.0")).divide(5.0));
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("4.8"), new BigDecimal("-4.8")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("24.0"), new BigDecimal("-24.0")).divide(5.0));
        assertEquals(HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("4.3"), new BigDecimal("-5.2")),
                HardSoftBigDecimalScore.valueOf(-14, new BigDecimal("8.6"), new BigDecimal("-10.4")).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("16.0"), new BigDecimal("25.0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-4.0"), new BigDecimal("5.0")).power(2.0));
        assertEquals(HardSoftBigDecimalScore.valueOf(-343, new BigDecimal("-64.0"), new BigDecimal("125.0")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-4.0"), new BigDecimal("5.0")).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-4.0"), new BigDecimal("5.0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("4.0"), new BigDecimal("-5.0")).negate());
        assertEquals(HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("4.0"), new BigDecimal("-5.0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-4.0"), new BigDecimal("5.0")).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.valueOf(0, new BigDecimal("-10.0"), new BigDecimal("-200.0"))
        );
        assertScoresEqualsAndHashCode(
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0"))
        );
        assertScoresNotEquals(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-30.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-10.0"), new BigDecimal("-400.0")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0"))
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftBigDecimalScore.valueOf(-8, new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-20"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-1"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("0"), new BigDecimal("1")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-20.06"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-20.007"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-20"), new BigDecimal("-20.06")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-20"), new BigDecimal("-20.007")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-20"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-1"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-1"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0"), new BigDecimal("-1")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("0"), new BigDecimal("1"))
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftBigDecimalScore.valueOfInitialized(new BigDecimal("-12.3"), new BigDecimal("3400.5")),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(new BigDecimal("-12.3"), output.getHardScore());
                    assertEquals(new BigDecimal("3400.5"), output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftBigDecimalScore.valueOf(-7, new BigDecimal("-12.3"), new BigDecimal("3400.5")),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(new BigDecimal("-12.3"), output.getHardScore());
                    assertEquals(new BigDecimal("3400.5"), output.getSoftScore());
                }
        );
    }

}
