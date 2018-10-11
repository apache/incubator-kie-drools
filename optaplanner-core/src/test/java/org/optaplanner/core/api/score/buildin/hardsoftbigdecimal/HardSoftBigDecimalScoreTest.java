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
    public void of() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("0.0")),
                HardSoftBigDecimalScore.ofHard(new BigDecimal("-147.2")));
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("-0.0"), new BigDecimal("-3.2")),
                HardSoftBigDecimalScore.ofSoft(new BigDecimal("-3.2")));
    }

    @Test
    public void parseScore() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.parseScore("-147.2hard/-258.3soft"));
        assertEquals(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.parseScore("-7init/-147.2hard/-258.3soft"));
    }

    @Test
    public void toShortString() {
        assertEquals("0",
                HardSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("0.0")).toShortString());
        assertEquals("-258.3soft",
                HardSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("-258.3")).toShortString());
        assertEquals("-147.2hard",
                HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("0.0")).toShortString());
        assertEquals("-147.2hard/-258.3soft",
                HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")).toShortString());
        assertEquals("-7init",
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0.0"), new BigDecimal("0.0")).toShortString());
        assertEquals("-7init/-258.3soft",
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0.0"), new BigDecimal("-258.3")).toShortString());
        assertEquals("-7init/-147.2hard/-258.3soft",
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")).toShortString());
    }

    @Test
    public void testToString() {
        assertEquals("0.0hard/-258.3soft",
                HardSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("-258.3")).toString());
        assertEquals("-147.2hard/-258.3soft",
                HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")).toString());
        assertEquals("-7init/-147.2hard/-258.3soft",
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")).toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftBigDecimalScore.parseScore("-147.2");
    }

    @Test
    public void toInitializedScore() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")).toInitializedScore());
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")).toInitializedScore());
    }

    @Test
    public void withInitScore() {
        assertEquals(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-258.3")).withInitScore(-7));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftBigDecimalScore.of(new BigDecimal("-5"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.of(new BigDecimal("-5"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.of(new BigDecimal("-0.007"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-5"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("-300"))
        );
        assertScoreFeasible(
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("-300.007")),
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.of(new BigDecimal("2"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.ofUninitialized(0, new BigDecimal("0"), new BigDecimal("-300"))
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("19"), new BigDecimal("-320")),
                HardSoftBigDecimalScore.of(new BigDecimal("20"), new BigDecimal("-20")).add(
                        HardSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("-300"))));
        assertEquals(HardSoftBigDecimalScore.ofUninitialized(-77, new BigDecimal("19"), new BigDecimal("-320")),
                HardSoftBigDecimalScore.ofUninitialized(-70, new BigDecimal("20"), new BigDecimal("-20")).add(
                        HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-300"))));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("21"), new BigDecimal("280")),
                HardSoftBigDecimalScore.of(new BigDecimal("20"), new BigDecimal("-20")).subtract(
                        HardSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("-300"))));
        assertEquals(HardSoftBigDecimalScore.ofUninitialized(-63, new BigDecimal("21"), new BigDecimal("280")),
                HardSoftBigDecimalScore.ofUninitialized(-70, new BigDecimal("20"), new BigDecimal("-20")).subtract(
                        HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-300"))));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("6.0"), new BigDecimal("-6.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("5.0"), new BigDecimal("-5.0")).multiply(1.2));
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("1.2"), new BigDecimal("-1.2")),
                HardSoftBigDecimalScore.of(new BigDecimal("1.0"), new BigDecimal("-1.0")).multiply(1.2));
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("4.8"), new BigDecimal("-4.8")),
                HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-4.0")).multiply(1.2));
        assertEquals(HardSoftBigDecimalScore.ofUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-10.4")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-5.2")).multiply(2.0));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("5.0"), new BigDecimal("-5.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("25.0"), new BigDecimal("-25.0")).divide(5.0));
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("4.2"), new BigDecimal("-4.2")),
                HardSoftBigDecimalScore.of(new BigDecimal("21.0"), new BigDecimal("-21.0")).divide(5.0));
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("4.8"), new BigDecimal("-4.8")),
                HardSoftBigDecimalScore.of(new BigDecimal("24.0"), new BigDecimal("-24.0")).divide(5.0));
        assertEquals(HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-5.2")),
                HardSoftBigDecimalScore.ofUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-10.4")).divide(2.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("16.0"), new BigDecimal("25.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("5.0")).power(2.0));
        assertEquals(HardSoftBigDecimalScore.ofUninitialized(-343, new BigDecimal("-64.0"), new BigDecimal("125.0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-4.0"), new BigDecimal("5.0")).power(3.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("5.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-5.0")).negate());
        assertEquals(HardSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-5.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("5.0")).negate());
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-10.000"), new BigDecimal("-200.000")),
                HardSoftBigDecimalScore.ofUninitialized(0, new BigDecimal("-10.0"), new BigDecimal("-200.0"))
        );
        PlannerAssert.assertObjectsAreEqual(
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0"))
        );
        PlannerAssert.assertObjectsAreNotEqual(
                HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-30.0"), new BigDecimal("-200.0")),
                HardSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-400.0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("-200.0"))
        );
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftBigDecimalScore.ofUninitialized(-8, new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-20"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("1")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20.06"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20.007"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("-20.06")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("-20.007")),
                HardSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("-1")),
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("1"))
        );
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftBigDecimalScore.of(new BigDecimal("-12.3"), new BigDecimal("3400.5")),
                output -> {
                    assertEquals(0, output.getInitScore());
                    assertEquals(new BigDecimal("-12.3"), output.getHardScore());
                    assertEquals(new BigDecimal("3400.5"), output.getSoftScore());
                }
        );
        PlannerTestUtils.serializeAndDeserializeWithAll(
                HardSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-12.3"), new BigDecimal("3400.5")),
                output -> {
                    assertEquals(-7, output.getInitScore());
                    assertEquals(new BigDecimal("-12.3"), output.getHardScore());
                    assertEquals(new BigDecimal("3400.5"), output.getSoftScore());
                }
        );
    }

}
