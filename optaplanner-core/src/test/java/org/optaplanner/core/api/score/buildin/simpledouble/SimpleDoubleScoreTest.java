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

package org.optaplanner.core.api.score.buildin.simpledouble;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.data.Offset.offset;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class SimpleDoubleScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertThat(SimpleDoubleScore.parseScore("-147.2")).isEqualTo(SimpleDoubleScore.of(-147.2));
        assertThat(SimpleDoubleScore.parseScore("-7init/-147.2")).isEqualTo(SimpleDoubleScore.ofUninitialized(-7, -147.2));
        assertThat(SimpleDoubleScore.parseScore("*")).isEqualTo(SimpleDoubleScore.of(Double.MIN_VALUE));
    }

    @Test
    public void toShortString() {
        assertThat(SimpleDoubleScore.of(0.0).toShortString()).isEqualTo("0");
        assertThat(SimpleDoubleScore.of(-147.2).toShortString()).isEqualTo("-147.2");
        assertThat(SimpleDoubleScore.ofUninitialized(-7, -147.2).toShortString()).isEqualTo("-7init/-147.2");
        assertThat(SimpleDoubleScore.ofUninitialized(-7, 0.0).toShortString()).isEqualTo("-7init");
    }

    @Test
    public void testToString() {
        assertThat(SimpleDoubleScore.of(0.0).toString()).isEqualTo("0.0");
        assertThat(SimpleDoubleScore.of(-147.2).toString()).isEqualTo("-147.2");
        assertThat(SimpleDoubleScore.ofUninitialized(-7, -147.2).toString()).isEqualTo("-7init/-147.2");
    }

    @Test
    public void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> SimpleDoubleScore.parseScore("-147.2hard/-258.3soft"));
    }

    @Test
    public void toInitializedScore() {
        assertThat(SimpleDoubleScore.of(-147.2).toInitializedScore()).isEqualTo(SimpleDoubleScore.of(-147.2));
        assertThat(SimpleDoubleScore.ofUninitialized(-7, -147.2).toInitializedScore()).isEqualTo(SimpleDoubleScore.of(-147.2));
    }

    @Test
    public void withInitScore() {
        assertThat(SimpleDoubleScore.of(-147.2).withInitScore(-7)).isEqualTo(SimpleDoubleScore.ofUninitialized(-7, -147.2));
    }

    @Test
    public void add() {
        assertThat(SimpleDoubleScore.of(20.0).add(
                SimpleDoubleScore.of(-1.0))).isEqualTo(SimpleDoubleScore.of(19.0));
        assertThat(SimpleDoubleScore.ofUninitialized(-70, 20.0).add(
                SimpleDoubleScore.ofUninitialized(-7, -1.0))).isEqualTo(SimpleDoubleScore.ofUninitialized(-77, 19.0));
    }

    @Test
    public void subtract() {
        assertThat(SimpleDoubleScore.of(20.0).subtract(
                SimpleDoubleScore.of(-1.0))).isEqualTo(SimpleDoubleScore.of(21.0));
        assertThat(SimpleDoubleScore.ofUninitialized(-70, 20.0).subtract(
                SimpleDoubleScore.ofUninitialized(-7, -1.0))).isEqualTo(SimpleDoubleScore.ofUninitialized(-63, 21.0));
    }

    @Test
    public void multiply() {
        assertThat(SimpleDoubleScore.of(5.0).multiply(1.2)).isEqualTo(SimpleDoubleScore.of(6.0));
        assertThat(SimpleDoubleScore.of(1.0).multiply(1.2)).isEqualTo(SimpleDoubleScore.of(1.2));
        assertThat(SimpleDoubleScore.of(4.0).multiply(1.2)).isEqualTo(SimpleDoubleScore.of(4.8));
        assertThat(SimpleDoubleScore.ofUninitialized(-7, 4.3).multiply(2.0))
                .isEqualTo(SimpleDoubleScore.ofUninitialized(-14, 8.6));
    }

    @Test
    public void divide() {
        assertThat(SimpleDoubleScore.of(25.0).divide(5.0)).isEqualTo(SimpleDoubleScore.of(5.0));
        assertThat(SimpleDoubleScore.of(21.0).divide(5.0)).isEqualTo(SimpleDoubleScore.of(4.2));
        assertThat(SimpleDoubleScore.of(24.0).divide(5.0)).isEqualTo(SimpleDoubleScore.of(4.8));
        assertThat(SimpleDoubleScore.ofUninitialized(-14, 8.6).divide(2.0))
                .isEqualTo(SimpleDoubleScore.ofUninitialized(-7, 4.3));
    }

    @Test
    public void power() {
        assertThat(SimpleDoubleScore.of(1.5).power(2.0)).isEqualTo(SimpleDoubleScore.of(2.25));
        assertThat(SimpleDoubleScore.of(2.25).power(0.5)).isEqualTo(SimpleDoubleScore.of(1.5));
        assertThat(SimpleDoubleScore.ofUninitialized(-7, 5.0).power(3.0))
                .isEqualTo(SimpleDoubleScore.ofUninitialized(-343, 125.0));
    }

    @Test
    public void negate() {
        assertThat(SimpleDoubleScore.of(1.5).negate()).isEqualTo(SimpleDoubleScore.of(-1.5));
        assertThat(SimpleDoubleScore.of(-1.5).negate()).isEqualTo(SimpleDoubleScore.of(1.5));
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleDoubleScore.of(-10.0),
                SimpleDoubleScore.of(-10.0),
                SimpleDoubleScore.ofUninitialized(0, -10.0));
        PlannerAssert.assertObjectsAreEqual(
                SimpleDoubleScore.ofUninitialized(-7, -10.0),
                SimpleDoubleScore.ofUninitialized(-7, -10.0));
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleDoubleScore.of(-10.0),
                SimpleDoubleScore.of(-30.0),
                SimpleDoubleScore.ofUninitialized(-7, -10.0));
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleDoubleScore.ofUninitialized(-8, -0.0),
                SimpleDoubleScore.ofUninitialized(-7, -20.0),
                SimpleDoubleScore.ofUninitialized(-7, -1.0),
                SimpleDoubleScore.ofUninitialized(-7, 0.0),
                SimpleDoubleScore.ofUninitialized(-7, 1.0),
                SimpleDoubleScore.of(-300.5),
                SimpleDoubleScore.of(-300.0),
                SimpleDoubleScore.of(-20.06),
                SimpleDoubleScore.of(-20.007),
                SimpleDoubleScore.of(-20.0),
                SimpleDoubleScore.of(-1.0),
                SimpleDoubleScore.of(0.0),
                SimpleDoubleScore.of(1.0));
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleDoubleScore.of(123.4),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(0);
                    assertThat(output.getScore()).isEqualTo(123.4, offset(0.0));
                });
        PlannerTestUtils.serializeAndDeserializeWithAll(
                SimpleDoubleScore.ofUninitialized(-7, 123.4),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(-7);
                    assertThat(output.getScore()).isEqualTo(123.4, offset(0.0));
                });
    }

}
