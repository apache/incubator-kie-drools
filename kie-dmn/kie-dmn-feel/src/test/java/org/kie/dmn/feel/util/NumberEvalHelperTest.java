/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.util;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class NumberEvalHelperTest {


    @Test
    void getBigDecimalOrNull() {
        assertThat(NumberEvalHelper.getBigDecimalOrNull(10d)).isEqualTo(new BigDecimal("10"));
        assertThat(NumberEvalHelper.getBigDecimalOrNull(10.00000000D)).isEqualTo(new BigDecimal("10"));
        assertThat(NumberEvalHelper.getBigDecimalOrNull(10000000000.5D)).isEqualTo(new BigDecimal("10000000000.5"));
    }

    @Test
    void testCoerceNumber_withBigDecimal() {
        Object result = NumberEvalHelper.coerceIntegerNumber(new BigDecimal("99.99"));
        assertInstanceOf(Integer.class, result, "Expected Integer");
        assertEquals(99, result);
    }

    @Test
    void testCoerceNumber_withDouble() {
        Object result = NumberEvalHelper.coerceIntegerNumber(42.5);
        assertInstanceOf(Integer.class, result, "Expected Integer");
        assertEquals(42, result);
    }

    @Test
    void testCoerceNumber_withString() {
        Object result = NumberEvalHelper.coerceIntegerNumber("123");
        assertInstanceOf(String.class, result, "Expected String");
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("123");
    }

    @Test
    void testCoerceNumber_withInteger() {
        Object result = NumberEvalHelper.coerceIntegerNumber(42);
        assertInstanceOf(Integer.class, result, "Expected Integer");
        assertEquals(42, result);
    }

}
