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

package org.optaplanner.core.api.score.buildin.simplelong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class SimpleLongScoreTest extends AbstractScoreTest {

    @Test
    public void parseScore() {
        assertThat(SimpleLongScore.parseScore("-147")).isEqualTo(SimpleLongScore.of(-147L));
        assertThat(SimpleLongScore.parseScore("-7init/-147")).isEqualTo(SimpleLongScore.ofUninitialized(-7, -147L));
        assertThat(SimpleLongScore.parseScore("*")).isEqualTo(SimpleLongScore.of(Long.MIN_VALUE));
    }

    @Test
    public void toShortString() {
        assertThat(SimpleLongScore.of(0L).toShortString()).isEqualTo("0");
        assertThat(SimpleLongScore.of(-147L).toShortString()).isEqualTo("-147");
        assertThat(SimpleLongScore.ofUninitialized(-7, -147L).toShortString()).isEqualTo("-7init/-147");
        assertThat(SimpleLongScore.ofUninitialized(-7, 0L).toShortString()).isEqualTo("-7init");
    }

    @Test
    public void testToString() {
        assertThat(SimpleLongScore.of(0).toString()).isEqualTo("0");
        assertThat(SimpleLongScore.of(-147L).toString()).isEqualTo("-147");
        assertThat(SimpleLongScore.ofUninitialized(-7, -147L).toString()).isEqualTo("-7init/-147");
    }

    @Test
    public void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> SimpleLongScore.parseScore("-147hard/-258soft"));
    }

    @Test
    public void withInitScore() {
        assertThat(SimpleLongScore.of(-147L).withInitScore(-7)).isEqualTo(SimpleLongScore.ofUninitialized(-7, -147L));
    }

    @Test
    public void add() {
        assertThat(SimpleLongScore.of(20L).add(
                SimpleLongScore.of(-1L))).isEqualTo(SimpleLongScore.of(19L));
        assertThat(SimpleLongScore.ofUninitialized(-70, 20L).add(
                SimpleLongScore.ofUninitialized(-7, -1L))).isEqualTo(SimpleLongScore.ofUninitialized(-77, 19L));
    }

    @Test
    public void subtract() {
        assertThat(SimpleLongScore.of(20L).subtract(
                SimpleLongScore.of(-1L))).isEqualTo(SimpleLongScore.of(21L));
        assertThat(SimpleLongScore.ofUninitialized(-70, 20L).subtract(
                SimpleLongScore.ofUninitialized(-7, -1L))).isEqualTo(SimpleLongScore.ofUninitialized(-63, 21L));
    }

    @Test
    public void multiply() {
        assertThat(SimpleLongScore.of(5L).multiply(1.2)).isEqualTo(SimpleLongScore.of(6L));
        assertThat(SimpleLongScore.of(1L).multiply(1.2)).isEqualTo(SimpleLongScore.of(1L));
        assertThat(SimpleLongScore.of(4L).multiply(1.2)).isEqualTo(SimpleLongScore.of(4L));
        assertThat(SimpleLongScore.ofUninitialized(-7, 4L).multiply(2.0)).isEqualTo(SimpleLongScore.ofUninitialized(-14, 8L));
    }

    @Test
    public void divide() {
        assertThat(SimpleLongScore.of(25L).divide(5.0)).isEqualTo(SimpleLongScore.of(5L));
        assertThat(SimpleLongScore.of(21L).divide(5.0)).isEqualTo(SimpleLongScore.of(4L));
        assertThat(SimpleLongScore.of(24L).divide(5.0)).isEqualTo(SimpleLongScore.of(4L));
        assertThat(SimpleLongScore.ofUninitialized(-14, 8L).divide(2.0)).isEqualTo(SimpleLongScore.ofUninitialized(-7, 4L));
    }

    @Test
    public void power() {
        assertThat(SimpleLongScore.of(5L).power(2.0)).isEqualTo(SimpleLongScore.of(25L));
        assertThat(SimpleLongScore.of(25L).power(0.5)).isEqualTo(SimpleLongScore.of(5L));
        assertThat(SimpleLongScore.ofUninitialized(-7, 5L).power(3.0)).isEqualTo(SimpleLongScore.ofUninitialized(-343, 125L));
    }

    @Test
    public void negate() {
        assertThat(SimpleLongScore.of(5L).negate()).isEqualTo(SimpleLongScore.of(-5L));
        assertThat(SimpleLongScore.of(-5L).negate()).isEqualTo(SimpleLongScore.of(5L));
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                SimpleLongScore.of(-10L),
                SimpleLongScore.of(-10L),
                SimpleLongScore.ofUninitialized(0, -10L));
        PlannerAssert.assertObjectsAreEqual(
                SimpleLongScore.ofUninitialized(-7, -10L),
                SimpleLongScore.ofUninitialized(-7, -10L));
        PlannerAssert.assertObjectsAreNotEqual(
                SimpleLongScore.of(-10L),
                SimpleLongScore.of(-30L),
                SimpleLongScore.ofUninitialized(-7, -10L));
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                SimpleLongScore.ofUninitialized(-8, 0L),
                SimpleLongScore.ofUninitialized(-7, -20L),
                SimpleLongScore.ofUninitialized(-7, -1L),
                SimpleLongScore.ofUninitialized(-7, 0L),
                SimpleLongScore.ofUninitialized(-7, 1L),
                SimpleLongScore.of(((long) Integer.MIN_VALUE) - 4000L),
                SimpleLongScore.of(-300L),
                SimpleLongScore.of(-20L),
                SimpleLongScore.of(-1L),
                SimpleLongScore.of(0L),
                SimpleLongScore.of(1L),
                SimpleLongScore.of(((long) Integer.MAX_VALUE) + 4000L));
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                SimpleLongScore.of(123L),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(0);
                    assertThat(output.getScore()).isEqualTo(123L);
                });
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                SimpleLongScore.ofUninitialized(-7, 123L),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(-7);
                    assertThat(output.getScore()).isEqualTo(123L);
                });
    }

}
