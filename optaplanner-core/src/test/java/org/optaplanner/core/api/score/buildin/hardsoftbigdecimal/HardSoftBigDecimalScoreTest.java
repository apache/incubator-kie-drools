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
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.junit.Assert.*;

public class HardSoftBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("-147.2"), new BigDecimal("-258.3")),
                HardSoftBigDecimalScore.parseScore("-147.2hard/-258.3soft"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseScoreIllegalArgument() {
        HardSoftBigDecimalScore.parseScore("-147.2");
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-5"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-5"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-0.007"), new BigDecimal("4000"))
        );
        assertScoreFeasible(
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("-300.007")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("2"), new BigDecimal("-300"))
        );
    }

    @Test
    public void add() {
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("19"), new BigDecimal("-320")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("20"), new BigDecimal("-20")).add(
                        HardSoftBigDecimalScore.valueOf(new BigDecimal("-1"), new BigDecimal("-300"))));
    }

    @Test
    public void subtract() {
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("21"), new BigDecimal("280")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("20"), new BigDecimal("-20")).subtract(
                        HardSoftBigDecimalScore.valueOf(new BigDecimal("-1"), new BigDecimal("-300"))));
    }

    @Test
    public void multiply() {
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("6.0"), new BigDecimal("-6.0")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("5.0"), new BigDecimal("-5.0")).multiply(1.2));
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("1.2"), new BigDecimal("-1.2")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("1.0"), new BigDecimal("-1.0")).multiply(1.2));
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("4.8"), new BigDecimal("-4.8")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("4.0"), new BigDecimal("-4.0")).multiply(1.2));
    }

    @Test
    public void divide() {
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("5.0"), new BigDecimal("-5.0")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("25.0"), new BigDecimal("-25.0")).divide(5.0));
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("4.2"), new BigDecimal("-4.2")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("21.0"), new BigDecimal("-21.0")).divide(5.0));
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("4.8"), new BigDecimal("-4.8")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("24.0"), new BigDecimal("-24.0")).divide(5.0));
    }

    @Test
    public void power() {
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("16.0"), new BigDecimal("25.0")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-4.0"), new BigDecimal("5.0")).power(2.0));
    }

    @Test
    public void negate() {
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("-4.0"), new BigDecimal("5.0")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("4.0"), new BigDecimal("-5.0")).negate());
        assertEquals(HardSoftBigDecimalScore.valueOf(new BigDecimal("4.0"), new BigDecimal("-5.0")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-4.0"), new BigDecimal("5.0")).negate());
    }

    @Test
    public void equalsAndHashCode() {
        assertScoresEqualsAndHashCode(
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-10"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-10"), new BigDecimal("-20"))
        );
    }

    @Test
    public void compareTo() {
        assertScoreCompareToOrder(
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20.06"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20.007"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20"), new BigDecimal("-20.06")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20"), new BigDecimal("-20.007")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-20"), new BigDecimal("-20")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-1"), new BigDecimal("-300")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("-1"), new BigDecimal("4000")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("-1")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("0")),
                HardSoftBigDecimalScore.valueOf(new BigDecimal("0"), new BigDecimal("1"))
        );
    }

    @Test
    public void serializeAndDeserialize() {
        HardSoftBigDecimalScore input = HardSoftBigDecimalScore.valueOf(
                new BigDecimal("-12.3"), new BigDecimal("3400.5"));
        PlannerTestUtils.serializeAndDeserializeWithAll(input,
                new PlannerTestUtils.OutputAsserter<HardSoftBigDecimalScore>() {
                    public void assertOutput(HardSoftBigDecimalScore output) {
                        assertEquals(new BigDecimal("-12.3"), output.getHardScore());
                        assertEquals(new BigDecimal("3400.5"), output.getSoftScore());
                    }
                }
        );
    }

}
