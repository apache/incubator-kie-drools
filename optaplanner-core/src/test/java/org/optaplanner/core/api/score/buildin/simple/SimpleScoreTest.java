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

package org.optaplanner.core.api.score.buildin.simple;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class SimpleScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertThat(SimpleScore.parseScore("-147")).isEqualTo(SimpleScore.of(-147));
        assertThat(SimpleScore.parseScore("-7init/-147")).isEqualTo(SimpleScore.ofUninitialized(-7, -147));
        assertThat(SimpleScore.parseScore("*")).isEqualTo(SimpleScore.of(Integer.MIN_VALUE));
    }

    @Test
    public void toShortString() {
        assertThat(SimpleScore.of(0).toShortString()).isEqualTo("0");
        assertThat(SimpleScore.of(-147).toShortString()).isEqualTo("-147");
        assertThat(SimpleScore.ofUninitialized(-7, -147).toShortString()).isEqualTo("-7init/-147");
        assertThat(SimpleScore.ofUninitialized(-7, 0).toShortString()).isEqualTo("-7init");
    }

    @Test
    public void testToString() {
        assertThat(SimpleScore.of(0).toString()).isEqualTo("0");
        assertThat(SimpleScore.of(-147).toString()).isEqualTo("-147");
        assertThat(SimpleScore.ofUninitialized(-7, -147).toString()).isEqualTo("-7init/-147");
    }

    @Test
    public void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> SimpleScore.parseScore("-147hard/-258soft"));
    }

    @Test
    public void withInitScore() {
        assertThat(SimpleScore.of(-147).withInitScore(-7)).isEqualTo(SimpleScore.ofUninitialized(-7, -147));
    }

    @Test
    public void add() {
        assertThat(SimpleScore.of(20).add(
                SimpleScore.of(-1))).isEqualTo(SimpleScore.of(19));
        assertThat(SimpleScore.ofUninitialized(-70, 20).add(
                SimpleScore.ofUninitialized(-7, -1))).isEqualTo(SimpleScore.ofUninitialized(-77, 19));
    }

    @Test
    public void subtract() {
        assertThat(SimpleScore.of(20).subtract(
                SimpleScore.of(-1))).isEqualTo(SimpleScore.of(21));
        assertThat(SimpleScore.ofUninitialized(-70, 20).subtract(
                SimpleScore.ofUninitialized(-7, -1))).isEqualTo(SimpleScore.ofUninitialized(-63, 21));
    }

    @Test
    public void multiply() {
        assertThat(SimpleScore.of(5).multiply(1.2)).isEqualTo(SimpleScore.of(6));
        assertThat(SimpleScore.of(1).multiply(1.2)).isEqualTo(SimpleScore.of(1));
        assertThat(SimpleScore.of(4).multiply(1.2)).isEqualTo(SimpleScore.of(4));
        assertThat(SimpleScore.ofUninitialized(-7, 4).multiply(2.0)).isEqualTo(SimpleScore.ofUninitialized(-14, 8));
    }

    @Test
    public void divide() {
        assertThat(SimpleScore.of(25).divide(5.0)).isEqualTo(SimpleScore.of(5));
        assertThat(SimpleScore.of(21).divide(5.0)).isEqualTo(SimpleScore.of(4));
        assertThat(SimpleScore.of(24).divide(5.0)).isEqualTo(SimpleScore.of(4));
        assertThat(SimpleScore.ofUninitialized(-14, 8).divide(2.0)).isEqualTo(SimpleScore.ofUninitialized(-7, 4));
    }

    @Test
    public void power() {
        assertThat(SimpleScore.of(5).power(2.0)).isEqualTo(SimpleScore.of(25));
        assertThat(SimpleScore.of(25).power(0.5)).isEqualTo(SimpleScore.of(5));
        assertThat(SimpleScore.ofUninitialized(-7, 5).power(3.0)).isEqualTo(SimpleScore.ofUninitialized(-343, 125));
    }

    @Test
    public void negate() {
        assertThat(SimpleScore.of(5).negate()).isEqualTo(SimpleScore.of(-5));
        assertThat(SimpleScore.of(-5).negate()).isEqualTo(SimpleScore.of(5));
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleScore.of(-10),
                SimpleScore.of(-10),
                SimpleScore.ofUninitialized(0, -10));
        PlannerAssert.assertObjectsAreEqual(
                SimpleScore.ofUninitialized(-7, -10),
                SimpleScore.ofUninitialized(-7, -10));
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleScore.of(-10),
                SimpleScore.of(-30),
                SimpleScore.ofUninitialized(-7, -10));
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleScore.ofUninitialized(-8, 0),
                SimpleScore.ofUninitialized(-7, -20),
                SimpleScore.ofUninitialized(-7, -1),
                SimpleScore.ofUninitialized(-7, 0),
                SimpleScore.ofUninitialized(-7, 1),
                SimpleScore.of(-300),
                SimpleScore.of(-20),
                SimpleScore.of(-1),
                SimpleScore.of(0),
                SimpleScore.of(1));
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                SimpleScore.of(123),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(0);
                    assertThat(output.getScore()).isEqualTo(123);
                });
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                SimpleScore.ofUninitialized(-7, 123),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(-7);
                    assertThat(output.getScore()).isEqualTo(123);
                });
    }

}
