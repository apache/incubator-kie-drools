/*
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
package org.kie.dmn.feel.util;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.FEELDialect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.BooleanEvalHelper.getBooleanOrDialectDefault;

class BooleanEvalHelperTest {

    //TODO Will move to FeelDialectTest clss
    /*
     * @Test
     * void numericValuesComparative() {
     * assertThat(BooleanEvalHelper.compare(BigDecimal.valueOf(1), BigDecimal.valueOf(2), (l, r) -> l.compareTo(r) < 0, () -> null, () -> null)).isTrue();
     * assertThat(BooleanEvalHelper.compare(1.0, 2.0, (l, r) -> l.compareTo(r) < 0, () -> null, () -> null)).isTrue();
     * assertThat(BooleanEvalHelper.compare(1, 2, (l, r) -> l.compareTo(r) > 0, () -> null, () -> null)).isFalse();
     * assertThat(BooleanEvalHelper.compare(BigDecimal.valueOf(1), 2, (l, r) -> l.compareTo(r) > 0, () -> null, () -> null)).isFalse();
     * assertThat(BooleanEvalHelper.compare(1, BigDecimal.valueOf(2), (l, r) -> l.compareTo(r) < 0, () -> null, () -> null)).isTrue();
     * assertThat(BooleanEvalHelper.compare(BigDecimal.valueOf(1), 2.3, (l, r) -> l.compareTo(r) == 0, () -> null, () -> null)).isFalse();
     * assertThat(BooleanEvalHelper.compare(1.2, BigDecimal.valueOf(1.2), (l, r) -> l.compareTo(r) == 0, () -> null, () -> null)).isTrue();
     * assertThat(BooleanEvalHelper.compare(BigDecimal.valueOf(1), 0L, (l, r) -> l.compareTo(r) > 0, () -> null, () -> null)).isTrue();
     * assertThat(BooleanEvalHelper.compare(10L, BigDecimal.valueOf(2), (l, r) -> l.compareTo(r) < 0, () -> null, () -> null)).isFalse();
     * assertThat(BooleanEvalHelper.compare(BigInteger.valueOf(1), BigInteger.valueOf(2), (l, r) -> l.compareTo(r) == 0, () -> null, () -> null)).isFalse();
     * assertThat(BooleanEvalHelper.compare(BigInteger.valueOf(1), 2, (l, r) -> l.compareTo(r) < 0, () -> null, () -> null)).isTrue();
     * assertThat(BooleanEvalHelper.compare(BigInteger.valueOf(1), 2.3, (l, r) -> l.compareTo(r) == 0, () -> null, () -> null)).isFalse();
     * }
     */

    @Test
    void getBooleanOrDialectDefaultFEEL() {
        assertThat(getBooleanOrDialectDefault(false, FEELDialect.FEEL)).isEqualTo(Boolean.FALSE);
        assertThat(getBooleanOrDialectDefault(true, FEELDialect.FEEL)).isEqualTo(Boolean.TRUE);
        assertThat(getBooleanOrDialectDefault("true", FEELDialect.FEEL)).isNull();
        assertThat(getBooleanOrDialectDefault(null, FEELDialect.FEEL)).isNull();
    }

    @Test
    void getBooleanOrDialectDefaultBFEEL() {
        assertThat(getBooleanOrDialectDefault(false, FEELDialect.BFEEL)).isEqualTo(Boolean.FALSE);
        assertThat(getBooleanOrDialectDefault(true, FEELDialect.BFEEL)).isEqualTo(Boolean.TRUE);
        assertThat(getBooleanOrDialectDefault("true", FEELDialect.BFEEL)).isEqualTo(Boolean.FALSE);
        assertThat(getBooleanOrDialectDefault(null, FEELDialect.BFEEL)).isEqualTo(Boolean.FALSE);
    }

    @Test
    void isEqualsSCWithStringValue() {
        assertThat(BooleanEvalHelper.isEqualsStringCompare("", "")).isTrue();
        assertThat(BooleanEvalHelper.isEqualsStringCompare("String", "String")).isTrue();
        assertThat(BooleanEvalHelper.isEqualsStringCompare("String", "stRing")).isFalse();
        assertThat(BooleanEvalHelper.isEqualsStringCompare("String", null)).isFalse();
        assertThat(BooleanEvalHelper.isEqualsStringCompare("", null)).isFalse();
        assertThat(BooleanEvalHelper.isEqualsStringCompare("3", 3)).isFalse();
    }

    @Test
    void isEqualsSCWithNoStringValue() {
        assertThat(BooleanEvalHelper.isEqualsStringCompare(3, "3")).isFalse();
        assertThat(BooleanEvalHelper.isEqualsStringCompare(null, null)).isTrue();
        assertThat(BooleanEvalHelper.isEqualsStringCompare(34, 34)).isTrue();
    }

}
