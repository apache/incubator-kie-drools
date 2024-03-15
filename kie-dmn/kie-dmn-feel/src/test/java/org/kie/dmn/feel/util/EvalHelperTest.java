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
package org.kie.dmn.feel.util;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.Test;
import org.kie.dmn.feel.lang.FEELProperty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.EvalHelper.getBigDecimalOrNull;
import static org.kie.dmn.feel.util.EvalHelper.normalizeVariableName;

public class EvalHelperTest {

    @Test
    public void testNormalizeSpace() {
        assertThat(normalizeVariableName(null)).isNull();
        assertThat(normalizeVariableName("")).isEqualTo("");
        assertThat(normalizeVariableName(" ")).isEqualTo("");
        assertThat(normalizeVariableName("\t")).isEqualTo("");
        assertThat(normalizeVariableName("\n")).isEqualTo("");
        assertThat(normalizeVariableName("\u0009")).isEqualTo("");
        assertThat(normalizeVariableName("\u000B")).isEqualTo("");
        assertThat(normalizeVariableName("\u000C")).isEqualTo("");
        assertThat(normalizeVariableName("\u001C")).isEqualTo("");
        assertThat(normalizeVariableName("\u001D")).isEqualTo("");
        assertThat(normalizeVariableName("\u001E")).isEqualTo("");
        assertThat(normalizeVariableName("\u001F")).isEqualTo("");
        assertThat(normalizeVariableName("\f")).isEqualTo("");
        assertThat(normalizeVariableName("\r")).isEqualTo("");
        assertThat(normalizeVariableName("  a  ")).isEqualTo("a");
        assertThat(normalizeVariableName("  a  b   c  ")).isEqualTo("a b c");
        assertThat(normalizeVariableName("a\t\f\r  b\u000B   c\n")).isEqualTo("a b c");
        assertThat(normalizeVariableName("a\t\f\r  \u00A0\u00A0b\u000B   c\n")).isEqualTo("a b c");
        assertThat(normalizeVariableName(" b")).isEqualTo("b");
        assertThat(normalizeVariableName("b ")).isEqualTo("b");
        assertThat(normalizeVariableName("ab c  ")).isEqualTo("ab c");
        assertThat(normalizeVariableName("a\u00A0b")).isEqualTo("a b");
    }

    @Test
    public void testGetBigDecimalOrNull() {
        assertThat(getBigDecimalOrNull(10d)).isEqualTo(new BigDecimal("10"));
        assertThat(getBigDecimalOrNull(10.00000000D)).isEqualTo(new BigDecimal("10"));
        assertThat(getBigDecimalOrNull(10000000000.5D)).isEqualTo(new BigDecimal("10000000000.5"));
    }

    @Test
    public void testGetGenericAccessor() throws NoSuchMethodException {
        Method expectedAccessor = TestPojo.class.getMethod("getAProperty");

        assertThat(EvalHelper.getGenericAccessor(TestPojo.class, "aProperty")).as("getGenericAccessor should work on Java bean accessors.").isEqualTo(expectedAccessor);

        assertThat(EvalHelper.getGenericAccessor(TestPojo.class, "feelPropertyIdentifier")).as("getGenericAccessor should work for methods annotated with '@FEELProperty'.").isEqualTo(expectedAccessor);
    }

    @Test
    public void testNumericValuesComparative() {
        assertThat(EvalHelper.compare(BigDecimal.valueOf(1), BigDecimal.valueOf(2), null, (l, r) -> l.compareTo(r) < 0)).isTrue();
        assertThat(EvalHelper.compare(1.0, 2.0, null, (l, r) -> l.compareTo(r) < 0)).isTrue();
        assertThat(EvalHelper.compare(1, 2, null, (l, r) -> l.compareTo(r) > 0)).isFalse();
        assertThat(EvalHelper.compare(BigDecimal.valueOf(1), 2, null, (l, r) -> l.compareTo(r) > 0)).isFalse();
        assertThat(EvalHelper.compare(1, BigDecimal.valueOf(2), null, (l, r) -> l.compareTo(r) < 0)).isTrue();
        assertThat(EvalHelper.compare(BigDecimal.valueOf(1), 2.3, null, (l, r) -> l.compareTo(r) == 0)).isFalse();
        assertThat(EvalHelper.compare(1.2, BigDecimal.valueOf(1.2), null, (l, r) -> l.compareTo(r) == 0)).isTrue();
        assertThat(EvalHelper.compare(BigDecimal.valueOf(1), 0L, null, (l, r) -> l.compareTo(r) > 0)).isTrue();
        assertThat(EvalHelper.compare(10L, BigDecimal.valueOf(2), null, (l, r) -> l.compareTo(r) < 0)).isFalse();
        assertThat(EvalHelper.compare(BigInteger.valueOf(1), BigInteger.valueOf(2), null, (l, r) -> l.compareTo(r) == 0)).isFalse();
        assertThat(EvalHelper.compare(BigInteger.valueOf(1), 2, null, (l, r) -> l.compareTo(r) < 0)).isTrue();
        assertThat(EvalHelper.compare(BigInteger.valueOf(1), 2.3, null, (l, r) -> l.compareTo(r) == 0)).isFalse();
    }

    private static class TestPojo {
        @FEELProperty("feelPropertyIdentifier")
        public String getAProperty() {
            return null;
        }
    }
}
