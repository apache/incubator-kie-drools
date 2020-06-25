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

package org.optaplanner.core.api.score.buildin.hardsoftlong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class HardSoftLongScoreTest extends AbstractScoreTest {

    @Test
    public void of() {
        assertThat(HardSoftLongScore.ofHard(-147L)).isEqualTo(HardSoftLongScore.of(-147L, 0L));
        assertThat(HardSoftLongScore.ofSoft(-258L)).isEqualTo(HardSoftLongScore.of(0L, -258L));
    }

    @Test
    public void parseScore() {
        assertThat(HardSoftLongScore.parseScore("-147hard/-258soft")).isEqualTo(HardSoftLongScore.of(-147L, -258L));
        assertThat(HardSoftLongScore.parseScore("-7init/-147hard/-258soft"))
                .isEqualTo(HardSoftLongScore.ofUninitialized(-7, -147L, -258L));
        assertThat(HardSoftLongScore.parseScore("-147hard/*soft")).isEqualTo(HardSoftLongScore.of(-147L, Long.MIN_VALUE));
    }

    @Test
    public void toShortString() {
        assertThat(HardSoftLongScore.of(0L, 0L).toShortString()).isEqualTo("0");
        assertThat(HardSoftLongScore.of(0L, -258L).toShortString()).isEqualTo("-258soft");
        assertThat(HardSoftLongScore.of(-147L, 0L).toShortString()).isEqualTo("-147hard");
        assertThat(HardSoftLongScore.of(-147L, -258L).toShortString()).isEqualTo("-147hard/-258soft");
        assertThat(HardSoftLongScore.ofUninitialized(-7, 0L, 0L).toShortString()).isEqualTo("-7init");
        assertThat(HardSoftLongScore.ofUninitialized(-7, 0L, -258L).toShortString()).isEqualTo("-7init/-258soft");
        assertThat(HardSoftLongScore.ofUninitialized(-7, -147L, -258L).toShortString()).isEqualTo("-7init/-147hard/-258soft");
    }

    @Test
    public void testToString() {
        assertThat(HardSoftLongScore.of(0L, -258L).toString()).isEqualTo("0hard/-258soft");
        assertThat(HardSoftLongScore.of(-147L, -258L).toString()).isEqualTo("-147hard/-258soft");
        assertThat(HardSoftLongScore.ofUninitialized(-7, -147L, -258L).toString()).isEqualTo("-7init/-147hard/-258soft");
    }

    @Test
    public void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException().isThrownBy(() -> HardSoftLongScore.parseScore("-147"));
    }

    @Test
    public void withInitScore() {
        assertThat(HardSoftLongScore.of(-147L, -258L).withInitScore(-7))
                .isEqualTo(HardSoftLongScore.ofUninitialized(-7, -147L, -258L));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardSoftLongScore.of(-5L, -300L),
                HardSoftLongScore.ofUninitialized(-7, -5L, -300L),
                HardSoftLongScore.ofUninitialized(-7, 0L, -300L));
        assertScoreFeasible(
                HardSoftLongScore.of(0L, -300L),
                HardSoftLongScore.of(2L, -300L),
                HardSoftLongScore.ofUninitialized(0, 0L, -300L));
    }

    @Test
    public void add() {
        assertThat(HardSoftLongScore.of(20L, -20L).add(
                HardSoftLongScore.of(-1L, -300L))).isEqualTo(HardSoftLongScore.of(19L, -320L));
        assertThat(HardSoftLongScore.ofUninitialized(-70, 20L, -20L).add(
                HardSoftLongScore.ofUninitialized(-7, -1L, -300L)))
                        .isEqualTo(HardSoftLongScore.ofUninitialized(-77, 19L, -320L));
    }

    @Test
    public void subtract() {
        assertThat(HardSoftLongScore.of(20L, -20L).subtract(
                HardSoftLongScore.of(-1L, -300L))).isEqualTo(HardSoftLongScore.of(21L, 280L));
        assertThat(HardSoftLongScore.ofUninitialized(-70, 20L, -20L).subtract(
                HardSoftLongScore.ofUninitialized(-7, -1L, -300L)))
                        .isEqualTo(HardSoftLongScore.ofUninitialized(-63, 21L, 280L));
    }

    @Test
    public void multiply() {
        assertThat(HardSoftLongScore.of(5L, -5L).multiply(1.2)).isEqualTo(HardSoftLongScore.of(6L, -6L));
        assertThat(HardSoftLongScore.of(1L, -1L).multiply(1.2)).isEqualTo(HardSoftLongScore.of(1L, -2L));
        assertThat(HardSoftLongScore.of(4L, -4L).multiply(1.2)).isEqualTo(HardSoftLongScore.of(4L, -5L));
        assertThat(HardSoftLongScore.ofUninitialized(-7, 4L, -5L).multiply(2.0))
                .isEqualTo(HardSoftLongScore.ofUninitialized(-14, 8L, -10L));
    }

    @Test
    public void divide() {
        assertThat(HardSoftLongScore.of(25L, -25L).divide(5.0)).isEqualTo(HardSoftLongScore.of(5L, -5L));
        assertThat(HardSoftLongScore.of(21L, -21L).divide(5.0)).isEqualTo(HardSoftLongScore.of(4L, -5L));
        assertThat(HardSoftLongScore.of(24L, -24L).divide(5.0)).isEqualTo(HardSoftLongScore.of(4L, -5L));
        assertThat(HardSoftLongScore.ofUninitialized(-14, 8L, -10L).divide(2.0))
                .isEqualTo(HardSoftLongScore.ofUninitialized(-7, 4L, -5L));
    }

    @Test
    public void power() {
        assertThat(HardSoftLongScore.of(-4L, 5L).power(2.0)).isEqualTo(HardSoftLongScore.of(16L, 25L));
        assertThat(HardSoftLongScore.of(16L, 25L).power(0.5)).isEqualTo(HardSoftLongScore.of(4L, 5L));
        assertThat(HardSoftLongScore.ofUninitialized(-7, -4L, 5L).power(3.0))
                .isEqualTo(HardSoftLongScore.ofUninitialized(-343, -64L, 125L));
    }

    @Test
    public void negate() {
        assertThat(HardSoftLongScore.of(4L, -5L).negate()).isEqualTo(HardSoftLongScore.of(-4L, 5L));
        assertThat(HardSoftLongScore.of(-4L, 5L).negate()).isEqualTo(HardSoftLongScore.of(4L, -5L));
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardSoftLongScore.of(-10L, -200L),
                HardSoftLongScore.of(-10L, -200L),
                HardSoftLongScore.ofUninitialized(0, -10L, -200L));
        PlannerAssert.assertObjectsAreEqual(
                HardSoftLongScore.ofUninitialized(-7, -10L, -200L),
                HardSoftLongScore.ofUninitialized(-7, -10L, -200L));
        PlannerAssert.assertObjectsAreNotEqual(
                HardSoftLongScore.of(-10L, -200L),
                HardSoftLongScore.of(-30L, -200L),
                HardSoftLongScore.of(-10L, -400L),
                HardSoftLongScore.ofUninitialized(-7, -10L, -200L));
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardSoftLongScore.ofUninitialized(-8, 0L, 0L),
                HardSoftLongScore.ofUninitialized(-7, -20L, -20L),
                HardSoftLongScore.ofUninitialized(-7, -1L, -300L),
                HardSoftLongScore.ofUninitialized(-7, 0L, 0L),
                HardSoftLongScore.ofUninitialized(-7, 0L, 1L),
                HardSoftLongScore.of(-20L, Long.MIN_VALUE),
                HardSoftLongScore.of(-20L, -20L),
                HardSoftLongScore.of(-1L, -300L),
                HardSoftLongScore.of(-1L, 4000L),
                HardSoftLongScore.of(0L, -1L),
                HardSoftLongScore.of(0L, 0L),
                HardSoftLongScore.of(0L, 1L));
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                HardSoftLongScore.of(-12, 3400L),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(0);
                    assertThat(output.getHardScore()).isEqualTo(-12L);
                    assertThat(output.getSoftScore()).isEqualTo(3400L);
                });
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                HardSoftLongScore.ofUninitialized(-7, -12L, 3400L),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(-7);
                    assertThat(output.getHardScore()).isEqualTo(-12L);
                    assertThat(output.getSoftScore()).isEqualTo(3400L);
                });
    }

}
