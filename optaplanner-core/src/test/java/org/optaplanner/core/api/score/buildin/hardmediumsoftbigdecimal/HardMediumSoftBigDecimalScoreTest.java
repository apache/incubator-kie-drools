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

package org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.AbstractScoreTest;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class HardMediumSoftBigDecimalScoreTest extends AbstractScoreTest {

    @Test
    public void of() {
        assertThat(HardMediumSoftBigDecimalScore.ofHard(new BigDecimal("-147.2"))).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("0.0"), new BigDecimal("0.0")));
        assertThat(HardMediumSoftBigDecimalScore.ofMedium(new BigDecimal("-3.2"))).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-0.0"), new BigDecimal("-3.2"), new BigDecimal("0.0")));
        assertThat(HardMediumSoftBigDecimalScore.ofSoft(new BigDecimal("-258.3"))).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("-258.3")));
    }

    @Test
    public void parseScore() {
        assertThat(HardMediumSoftBigDecimalScore.parseScore("-147.2hard/-3.2medium/-258.3soft")).isEqualTo(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-3.2"), new BigDecimal("-258.3")));
        assertThat(HardMediumSoftBigDecimalScore.parseScore("-7init/-147.2hard/-3.2medium/-258.3soft"))
                .isEqualTo(HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-3.2"),
                        new BigDecimal("-258.3")));
    }

    @Test
    public void toShortString() {
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"))
                .toShortString()).isEqualTo("0");
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("-258.3"))
                .toShortString()).isEqualTo("-258.3soft");
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("-3.20"), new BigDecimal("0.0"))
                .toShortString()).isEqualTo("-3.20medium");
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("-3.20"), new BigDecimal("-258.3"))
                .toShortString()).isEqualTo("-3.20medium/-258.3soft");
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-3.20"), new BigDecimal("-258.3"))
                .toShortString()).isEqualTo("-147.2hard/-3.20medium/-258.3soft");
        assertThat(HardMediumSoftBigDecimalScore
                .ofUninitialized(-7, new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"))
                .toShortString()).isEqualTo("-7init");
        assertThat(HardMediumSoftBigDecimalScore
                .ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-3.20"), new BigDecimal("-258.3"))
                .toShortString()).isEqualTo("-7init/-147.2hard/-3.20medium/-258.3soft");
    }

    @Test
    public void testToString() {
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("0.0"), new BigDecimal("-3.20"), new BigDecimal("-258.3"))
                .toString()).isEqualTo("0.0hard/-3.20medium/-258.3soft");
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-3.20"), new BigDecimal("-258.3"))
                .toString()).isEqualTo("-147.2hard/-3.20medium/-258.3soft");
        assertThat(HardMediumSoftBigDecimalScore
                .ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-3.20"), new BigDecimal("-258.3"))
                .toString()).isEqualTo("-7init/-147.2hard/-3.20medium/-258.3soft");
    }

    @Test
    public void parseScoreIllegalArgument() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> HardMediumSoftBigDecimalScore.parseScore("-147.2"));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> HardMediumSoftBigDecimalScore.parseScore("-147.2hard/-258.3soft"));
    }

    @Test
    public void withInitScore() {
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("-147.2"), new BigDecimal("-3.20"), new BigDecimal("-258.3"))
                .withInitScore(-7)).isEqualTo(
                        HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-147.2"), new BigDecimal("-3.20"),
                                new BigDecimal("-258.3")));
    }

    @Test
    public void feasible() {
        assertScoreNotFeasible(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-5"), new BigDecimal("-3.20"), new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-5"), new BigDecimal("3.20"), new BigDecimal("4000")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-0.007"), new BigDecimal("-3.20"), new BigDecimal("4000")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-5"), new BigDecimal("-32"),
                        new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("32"),
                        new BigDecimal("-300")));
        assertScoreFeasible(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("-32"), new BigDecimal("-300.007")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("-32.3"), new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("2"), new BigDecimal("-7.3"), new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.ofUninitialized(0, new BigDecimal("0"), new BigDecimal("-321"),
                        new BigDecimal("-300")));
    }

    @Test
    public void add() {
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("20"), new BigDecimal("-32"), new BigDecimal("-20")).add(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("27"), new BigDecimal("-300"))))
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("19"), new BigDecimal("-5"),
                                new BigDecimal("-320")));
        assertThat(HardMediumSoftBigDecimalScore
                .ofUninitialized(-70, new BigDecimal("20"), new BigDecimal("-20"), new BigDecimal("-20")).add(
                        HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-12"),
                                new BigDecimal("-300"))))
                                        .isEqualTo(HardMediumSoftBigDecimalScore.ofUninitialized(-77, new BigDecimal("19"),
                                                new BigDecimal("-32"),
                                                new BigDecimal("-320")));
    }

    @Test
    public void subtract() {
        assertThat(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("20"), new BigDecimal("-30"), new BigDecimal("-20")).subtract(
                        HardMediumSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("2"), new BigDecimal("-300"))))
                                .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("21"), new BigDecimal("-32"),
                                        new BigDecimal("280")));
        assertThat(HardMediumSoftBigDecimalScore
                .ofUninitialized(-70, new BigDecimal("20"), new BigDecimal("-32"), new BigDecimal("-20")).subtract(
                        HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-10"),
                                new BigDecimal("-300"))))
                                        .isEqualTo(HardMediumSoftBigDecimalScore.ofUninitialized(-63, new BigDecimal("21"),
                                                new BigDecimal("-22"),
                                                new BigDecimal("280")));
    }

    @Test
    public void multiply() {
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("5.0"), new BigDecimal("-4.0"), new BigDecimal("-5.0"))
                .multiply(1.2))
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("6.0"), new BigDecimal("-4.8"),
                                new BigDecimal("-6.0")));
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("1.0"), new BigDecimal("-2.0"), new BigDecimal("-1.0"))
                .multiply(1.2))
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("1.2"), new BigDecimal("-2.4"),
                                new BigDecimal("-1.2")));
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-8.0"), new BigDecimal("-4.0"))
                .multiply(1.2))
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("4.8"), new BigDecimal("-9.6"),
                                new BigDecimal("-4.8")));
        assertThat(HardMediumSoftBigDecimalScore
                .ofUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("2.0"), new BigDecimal("-5.2"))
                .multiply(2.0)).isEqualTo(
                        HardMediumSoftBigDecimalScore.ofUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("4.0"),
                                new BigDecimal("-10.4")));
    }

    @Test
    public void divide() {
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("25.0"), new BigDecimal("-50.0"), new BigDecimal("-25.0"))
                .divide(5.0))
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("5.0"), new BigDecimal("-10.0"),
                                new BigDecimal("-5.0")));
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("21.0"), new BigDecimal("-10.5"), new BigDecimal("-21.0"))
                .divide(5.0))
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("4.2"), new BigDecimal("-2.1"),
                                new BigDecimal("-4.2")));
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("24.0"), new BigDecimal("12.0"), new BigDecimal("-24.0"))
                .divide(5.0)).isEqualTo(
                        HardMediumSoftBigDecimalScore.of(new BigDecimal("4.8"), new BigDecimal("2.4"), new BigDecimal("-4.8")));
        assertThat(HardMediumSoftBigDecimalScore
                .ofUninitialized(-14, new BigDecimal("8.6"), new BigDecimal("-50.6"), new BigDecimal("-10.4"))
                .divide(2.0)).isEqualTo(
                        HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("4.3"), new BigDecimal("-25.3"),
                                new BigDecimal("-5.2")));
    }

    @Test
    public void power() {
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("-8.0"), new BigDecimal("5.0"))
                .power(2.0))
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("16.0"), new BigDecimal("64.0"),
                                new BigDecimal("25.0")));
        assertThat(HardMediumSoftBigDecimalScore
                .ofUninitialized(-7, new BigDecimal("-4.0"), new BigDecimal("-3.0"), new BigDecimal("5.0")).power(3.0))
                        .isEqualTo(HardMediumSoftBigDecimalScore.ofUninitialized(-343, new BigDecimal("-64.0"),
                                new BigDecimal("-27.0"),
                                new BigDecimal("125.0")));
    }

    @Test
    public void negate() {
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("25.0"), new BigDecimal("-5.0"))
                .negate())
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("-25.0"),
                                new BigDecimal("5.0")));
        assertThat(HardMediumSoftBigDecimalScore.of(new BigDecimal("-4.0"), new BigDecimal("3.0"), new BigDecimal("5.0"))
                .negate())
                        .isEqualTo(HardMediumSoftBigDecimalScore.of(new BigDecimal("4.0"), new BigDecimal("-3.0"),
                                new BigDecimal("-5.0")));
    }

    @Test
    public void equalsAndHashCode() {
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("3.0"), new BigDecimal("-200.0")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("3.0"), new BigDecimal("-200.0")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.000"), new BigDecimal("3.000"),
                        new BigDecimal("-200.000")),
                HardMediumSoftBigDecimalScore.ofUninitialized(0, new BigDecimal("-10.0"), new BigDecimal("3.0"),
                        new BigDecimal("-200.0")));
        PlannerAssert.assertObjectsAreEqual(
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("3.0"),
                        new BigDecimal("-200.0")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("3.0"),
                        new BigDecimal("-200.0")));
        PlannerAssert.assertObjectsAreNotEqual(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-30.0"), new BigDecimal("-200.0")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-30.0"), new BigDecimal("-30.0"), new BigDecimal("-200.0")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-10.0"), new BigDecimal("-200.0")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-30.0"), new BigDecimal("-400.0")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-10.0"), new BigDecimal("-400.0"), new BigDecimal("-30.0")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-10.0"), new BigDecimal("-30.0"),
                        new BigDecimal("-200.0")));
    }

    @Test
    public void compareTo() {
        PlannerAssert.assertCompareToOrder(
                HardMediumSoftBigDecimalScore.ofUninitialized(-8, new BigDecimal("0"), new BigDecimal("0"),
                        new BigDecimal("0")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-20"), new BigDecimal("0"),
                        new BigDecimal("-20")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-30"),
                        new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-1"), new BigDecimal("-20.0"),
                        new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("-10.0"),
                        new BigDecimal("0")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("-2.0"),
                        new BigDecimal("0")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("0"),
                        new BigDecimal("0")),
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("0"), new BigDecimal("0"),
                        new BigDecimal("1")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20.06"), new BigDecimal("-2.3"), new BigDecimal("-20")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20.06"), new BigDecimal("0"), new BigDecimal("-20")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20.007"), new BigDecimal("-2.3"), new BigDecimal("-20")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20.007"), new BigDecimal("-2.03"), new BigDecimal("-20")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20.007"), new BigDecimal("0"), new BigDecimal("-20")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20.007"), new BigDecimal("2.3"), new BigDecimal("-20")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("0"), new BigDecimal("-20.06")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("0"), new BigDecimal("-20.007")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-20"), new BigDecimal("0"), new BigDecimal("-20")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("-30"), new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("-20"), new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("0"), new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("1"), new BigDecimal("-300")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-1"), new BigDecimal("1"), new BigDecimal("4000")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("-1")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("0")),
                HardMediumSoftBigDecimalScore.of(new BigDecimal("0"), new BigDecimal("0"), new BigDecimal("1")));
    }

    @Test
    public void serializeAndDeserialize() {
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                HardMediumSoftBigDecimalScore.of(new BigDecimal("-12.3"), new BigDecimal("-43.3"), new BigDecimal("3400.5")),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(0);
                    assertThat(output.getHardScore()).isEqualTo(new BigDecimal("-12.3"));
                    assertThat(output.getMediumScore()).isEqualTo(new BigDecimal("-43.3"));
                    assertThat(output.getSoftScore()).isEqualTo(new BigDecimal("3400.5"));
                });
        PlannerTestUtils.serializeAndDeserializeWithXStream(
                HardMediumSoftBigDecimalScore.ofUninitialized(-7, new BigDecimal("-12.3"), new BigDecimal("-43.3"),
                        new BigDecimal("3400.5")),
                output -> {
                    assertThat(output.getInitScore()).isEqualTo(-7);
                    assertThat(output.getHardScore()).isEqualTo(new BigDecimal("-12.3"));
                    assertThat(output.getMediumScore()).isEqualTo(new BigDecimal("-43.3"));
                    assertThat(output.getSoftScore()).isEqualTo(new BigDecimal("3400.5"));
                });
    }

}
