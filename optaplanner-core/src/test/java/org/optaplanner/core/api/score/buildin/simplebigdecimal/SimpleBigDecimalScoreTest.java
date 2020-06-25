/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class SimpleBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertThat(SimpleBigDecimalScore.parseScore("-147.2")).isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-147.2")));
        assertThat(SimpleBigDecimalScore.parseScore("-7init/-147.2"))
                .isEqualTo(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2")));
    }

    @Test
    public void toShortString() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("0.0")).toShortString()).isEqualTo("0");
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("-147.2")).toShortString()).isEqualTo("-147.2");
        assertThat(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2")).toShortString())
                .isEqualTo("-7init/-147.2");
        assertThat(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("0.0")).toShortString()).isEqualTo("-7init");
    }

    @Test
    public void testToString() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("0.0")).toString()).isEqualTo("0.0");
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("-147.2")).toString()).isEqualTo("-147.2");
        assertThat(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2")).toString()).isEqualTo("-7init/-147.2");
    }

    @Test
    public void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> SimpleBigDecimalScore.parseScore("-147.2hard/-258.3soft"));
    }

    @Test
    public void withInitScore() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("-147.2")).withInitScore(-7))
                .isEqualTo(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2")));
    }

    @Test
    public void add() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("20")).add(
                SimpleBigDecimalScore.of(new BigDecimal("-1")))).isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("19")));
        assertThat(SimpleBigDecimalScore.ofUninitialized(-70, new BigDecimal("20")).add(
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"))))
                        .isEqualTo(SimpleBigDecimalScore.ofUninitialized(-77, new BigDecimal("19")));
    }

    @Test
    public void subtract() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("20")).subtract(
                SimpleBigDecimalScore.of(new BigDecimal("-1")))).isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("21")));
        assertThat(SimpleBigDecimalScore.ofUninitialized(-70, new BigDecimal("20")).subtract(
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"))))
                        .isEqualTo(SimpleBigDecimalScore.ofUninitialized(-63, new BigDecimal("21")));
    }

    @Test
    public void multiply() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("5.0")).multiply(1.2))
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("6.0")));
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("1.0")).multiply(1.2))
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("1.2")));
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("4.0")).multiply(1.2))
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("4.8")));
        assertThat(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("4.3")).multiply(2.0))
                .isEqualTo(SimpleBigDecimalScore.ofUninitialized(-14, new BigDecimal("8.6")));
    }

    @Test
    public void divide() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("25.0")).divide(5.0))
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("5.0")));
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("21.0")).divide(5.0))
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("4.2")));
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("24.0")).divide(5.0))
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("4.8")));
        assertThat(SimpleBigDecimalScore.ofUninitialized(-14, new BigDecimal("8.6")).divide(2.0))
                .isEqualTo(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("4.3")));
    }

    @Test
    public void power() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("5.0")).power(2.0))
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("25.0")));
        assertThat(SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("5.0")).power(3.0))
                .isEqualTo(SimpleBigDecimalScore.ofUninitialized(-343, new BigDecimal("125.0")));
    }

    @Test
    public void negate() {
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("5.0")).negate())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("-5.0")));
        assertThat(SimpleBigDecimalScore.of(new BigDecimal("-5.0")).negate())
                .isEqualTo(SimpleBigDecimalScore.of(new BigDecimal("5.0")));
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleBigDecimalScore.of(new BigDecimal("-10.0")),
                SimpleBigDecimalScore.of(new BigDecimal("-10.0")),
                SimpleBigDecimalScore.of(new BigDecimal("-10.000")),
                SimpleBigDecimalScore.ofUninitialized(0, new BigDecimal("-10.0")));
        PlannerAssert.assertObjectsAreEqual(
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0")),
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0")));
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleBigDecimalScore.of(new BigDecimal("-10.0")),
                SimpleBigDecimalScore.of(new BigDecimal("-30.0")),
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0")));
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleBigDecimalScore.ofUninitialized(-8, new BigDecimal("0.0")),
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-20.0")),
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1.0")),
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("0.0")),
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("1.0")),
                SimpleBigDecimalScore.of(new BigDecimal("-300.5")),
                SimpleBigDecimalScore.of(new BigDecimal("-300")),
                SimpleBigDecimalScore.of(new BigDecimal("-20.067")),
                SimpleBigDecimalScore.of(new BigDecimal("-20.007")),
                SimpleBigDecimalScore.of(new BigDecimal("-20")),
                SimpleBigDecimalScore.of(new BigDecimal("-1")),
                SimpleBigDecimalScore.of(new BigDecimal("0")),
                SimpleBigDecimalScore.of(new BigDecimal("1")));
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                SimpleBigDecimalScore.of(new BigDecimal("123.4")),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(0);
                    assertThat(output.getScore()).isEqualTo(new BigDecimal("123.4"));
                });
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                SimpleBigDecimalScore.ofUninitialized(-7, new BigDecimal("123.4")),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(-7);
                    assertThat(output.getScore()).isEqualTo(new BigDecimal("123.4"));
                });
    }

}
