/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.model.util;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OperatorUtilsTest {

    @Test
    public void areEqual_sameType() {
        assertThat(OperatorUtils.areEqual("100", String.valueOf(100))).isTrue();

        assertThat(OperatorUtils.areEqual("Hello", String.valueOf(100))).isFalse();

        assertThat(OperatorUtils.areEqual(100, 100)).isTrue();
    }

    @Test
    public void areEqual_differentTypes() {
        assertThat(OperatorUtils.areEqual("100", 100)).isFalse();
    }

    @Test
    public void areNumericEqual_IntegerBigDecimal() {
        assertThat(OperatorUtils.areNumericEqual(1, new BigDecimal("1"))).isTrue();
        assertThat(OperatorUtils.areNumericEqual(1, new BigDecimal("1.0"))).isTrue();
        assertThat(OperatorUtils.areNumericEqual(1, new BigDecimal("1.00"))).isTrue();
    }

    @Test
    public void areNumericEqual_BigDecimalWithDifferentScale() {
        // DROOLS-7414
        assertThat(OperatorUtils.areNumericEqual(new BigDecimal("1.0"), new BigDecimal("1.00"))).isTrue();
    }

    @Test
    public void compare_IntegerBigDecimal() {
        assertThat(OperatorUtils.compare(1, new BigDecimal("0.99"))).isGreaterThan(0);
        assertThat(OperatorUtils.compare(1, new BigDecimal("1.0"))).isZero();
        assertThat(OperatorUtils.compare(1, new BigDecimal("1.01"))).isLessThan(0);
    }

    @Test
    public void compare_BigDecimalWithDifferentScale() {
        assertThat(OperatorUtils.compare(new BigDecimal("1.00"), new BigDecimal("0.99"))).isGreaterThan(0);
        assertThat(OperatorUtils.compare(new BigDecimal("1.00"), new BigDecimal("1.0"))).isZero();
        assertThat(OperatorUtils.compare(new BigDecimal("1.00"), new BigDecimal("1.01"))).isLessThan(0);
    }

    @Test
    public void compare_String() {
        assertThat(OperatorUtils.compare("ABC", "AAA")).isGreaterThan(0);
        assertThat(OperatorUtils.compare("ABC", "ABC")).isZero();
        assertThat(OperatorUtils.compare("ABC", "XYZ")).isLessThan(0);
    }

    @Test
    public void asBigDecimal() {
        assertThat(OperatorUtils.asBigDecimal(1)).isEqualTo(new BigDecimal("1.0"));
        assertThat(OperatorUtils.asBigDecimal(1.0)).isEqualTo(new BigDecimal("1.0"));
        assertThat(OperatorUtils.asBigDecimal(new BigDecimal("1.0"))).isEqualTo(new BigDecimal("1.0"));
    }
}
