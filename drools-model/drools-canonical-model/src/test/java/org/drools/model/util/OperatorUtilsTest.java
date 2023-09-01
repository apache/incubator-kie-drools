/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.util;

import java.math.BigDecimal;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OperatorUtilsTest {

    @Test
    public void areEqualWithNumbers() {
        assertThat(OperatorUtils.areEqual(1, 1)).isTrue();
        assertThat(OperatorUtils.areEqual(1L, 1L)).isTrue();
        assertThat(OperatorUtils.areEqual(1.0f, 1.0f)).isTrue();
        assertThat(OperatorUtils.areEqual(1.0, 1.0)).isTrue();

        assertThat(OperatorUtils.areEqual(1, 2)).isFalse();
        assertThat(OperatorUtils.areEqual(1L, 2L)).isFalse();
        assertThat(OperatorUtils.areEqual(1.0f, 2.0f)).isFalse();
        assertThat(OperatorUtils.areEqual(1.0, 2.0)).isFalse();

        assertThat(OperatorUtils.areEqual(1, "1")).isFalse();
        assertThat(OperatorUtils.areEqual(1L, "1")).isFalse();
        assertThat(OperatorUtils.areEqual(1.0f, "1")).isFalse();
        assertThat(OperatorUtils.areEqual(1.0, "1")).isFalse();

        assertThat(OperatorUtils.areEqual(1, new BigDecimal("1"))).isTrue();
        assertThat(OperatorUtils.areEqual(1, new BigDecimal("1.0"))).isTrue();
        assertThat(OperatorUtils.areEqual(1, new BigDecimal("1.00"))).isTrue();
    }

    @Test
    public void areEqualWithObjects() {
        assertThat(OperatorUtils.areEqual(null, null)).isTrue();
        assertThat(OperatorUtils.areEqual("test", "test")).isTrue();

        assertThat(OperatorUtils.areEqual(new Object(), new Object())).isFalse();
        assertThat(OperatorUtils.areEqual(null, "test")).isFalse();
        assertThat(OperatorUtils.areEqual("test", null)).isFalse();
        assertThat(OperatorUtils.areEqual(new Object(), null)).isFalse();
        assertThat(OperatorUtils.areEqual(null, new Object())).isFalse();
        assertThat(OperatorUtils.areEqual(new Object(), "test")).isFalse();
        assertThat(OperatorUtils.areEqual("test", new Object())).isFalse();
    }

    @Test
    public void areNumericEqualWithNumbers() {
        assertThat(OperatorUtils.areNumericEqual(1, 1)).isTrue();
        assertThat(OperatorUtils.areNumericEqual(1L, 1L)).isTrue();
        assertThat(OperatorUtils.areNumericEqual(1.0f, 1.0f)).isTrue();
        assertThat(OperatorUtils.areNumericEqual(1.0, 1.0)).isTrue();

        assertThat(OperatorUtils.areNumericEqual(1, 2)).isFalse();
        assertThat(OperatorUtils.areNumericEqual(1L, 2L)).isFalse();
        assertThat(OperatorUtils.areNumericEqual(1.0f, 2.0f)).isFalse();
        assertThat(OperatorUtils.areNumericEqual(1.0, 2.0)).isFalse();

        assertThat(OperatorUtils.areNumericEqual(1, new BigDecimal("1"))).isTrue();
        assertThat(OperatorUtils.areNumericEqual(1, new BigDecimal("1.0"))).isTrue();
        assertThat(OperatorUtils.areNumericEqual(1, new BigDecimal("1.00"))).isTrue();

        assertThat(OperatorUtils.areNumericEqual(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.1))).isTrue();
        assertThat(OperatorUtils.areNumericEqual(BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.100000001))).isFalse();

        assertThat(OperatorUtils.areNumericEqual(new BigDecimal("1.0"), new BigDecimal("1.00"))).isTrue();
    }

    @Test
    public void compareWithNumbers() {
        assertThat(OperatorUtils.compare(1, 1)).isZero();
        assertThat(OperatorUtils.compare(1L, 1L)).isZero();
        assertThat(OperatorUtils.compare(1.0f, 1.0f)).isZero();
        assertThat(OperatorUtils.compare(1.0, 1.0)).isZero();

        assertThat(OperatorUtils.compare(1, 2)).isNegative();
        assertThat(OperatorUtils.compare(1L, 2L)).isNegative();
        assertThat(OperatorUtils.compare(1.0f, 2.0f)).isNegative();
        assertThat(OperatorUtils.compare(1.0, 2.0)).isNegative();

        assertThat(OperatorUtils.compare(2, 1)).isPositive();
        assertThat(OperatorUtils.compare(2L, 1L)).isPositive();
        assertThat(OperatorUtils.compare(2.0f, 1.0f)).isPositive();
        assertThat(OperatorUtils.compare(2.0, 1.0)).isPositive();

        assertThat(OperatorUtils.compare(1, new BigDecimal("0.99"))).isPositive();
        assertThat(OperatorUtils.compare(1, new BigDecimal("1.0"))).isZero();
        assertThat(OperatorUtils.compare(1, new BigDecimal("1.01"))).isNegative();

        assertThat(OperatorUtils.compare(new BigDecimal("1.00"), new BigDecimal("0.99"))).isPositive();
        assertThat(OperatorUtils.compare(new BigDecimal("1.00"), new BigDecimal("1.0"))).isZero();
        assertThat(OperatorUtils.compare(new BigDecimal("1.00"), new BigDecimal("1.01"))).isNegative();
    }

    @Test
    public void compareWithString() {
        assertThat(OperatorUtils.compare("ABC", "AAA")).isPositive();
        assertThat(OperatorUtils.compare("ABC", "ABC")).isZero();
        assertThat(OperatorUtils.compare("ABC", "XYZ")).isNegative();
    }

    @Test
    public void asBigDecimal() {
        assertThat(OperatorUtils.asBigDecimal(1)).isEqualTo(new BigDecimal("1.0"));
        assertThat(OperatorUtils.asBigDecimal(1.0)).isEqualTo(new BigDecimal("1.0"));
        assertThat(OperatorUtils.asBigDecimal(new BigDecimal("1.0"))).isEqualTo(new BigDecimal("1.0"));
    }
}
