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
package org.kie.pmml.api.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ConverterTypeUtilTest {

    private static Map<String, Object> CONVERTIBLE_FROM_STRING;
    private static Map<String, Object> UNCONVERTIBLE_FROM_STRING;
    private static Map<Object, String> CONVERTIBLE_TO_STRING;
    private static Map<Double, Number> CONVERTIBLE_FROM_DOUBLE;
    private static Map<Double, Object> UNCONVERTIBLE_FROM_DOUBLE;
    private static Map<Integer, Number> CONVERTIBLE_FROM_INTEGER;
    private static Map<Integer, Object> UNCONVERTIBLE_FROM_INTEGER;

    static {
        CONVERTIBLE_FROM_STRING = new HashMap<>();
        CONVERTIBLE_FROM_STRING.put("true", true);
        CONVERTIBLE_FROM_STRING.put("false", false);
        CONVERTIBLE_FROM_STRING.put("23423", 23423);
        CONVERTIBLE_FROM_STRING.put("3476345444745745746", 3476345444745745746L);
        CONVERTIBLE_FROM_STRING.put("234.23", 234.23);
        CONVERTIBLE_FROM_STRING.put("234234.23", 234234.23F);
        CONVERTIBLE_FROM_STRING.put("A", 'A');
        CONVERTIBLE_FROM_STRING.put("2", (byte) 2);
        CONVERTIBLE_FROM_STRING.put("234", (short) 234);
        //
        UNCONVERTIBLE_FROM_STRING = new HashMap<>();
        UNCONVERTIBLE_FROM_STRING.put("true", 23423);
        UNCONVERTIBLE_FROM_STRING.put("false", 3476345444745745746L);
        UNCONVERTIBLE_FROM_STRING.put("23423", true);
        UNCONVERTIBLE_FROM_STRING.put("3476345444745745746", (byte) 2);
        UNCONVERTIBLE_FROM_STRING.put("234.23", 3476345444745745746L);
        UNCONVERTIBLE_FROM_STRING.put("234234.23", (short) 234);
        UNCONVERTIBLE_FROM_STRING.put("A", (byte) 2);
        UNCONVERTIBLE_FROM_STRING.put("Arwtrwetwe", 'A');
        //
        CONVERTIBLE_TO_STRING = new HashMap<>();
        CONVERTIBLE_TO_STRING.put(true, "true");
        CONVERTIBLE_TO_STRING.put(false, "false");
        CONVERTIBLE_TO_STRING.put(23423, "23423");
        CONVERTIBLE_TO_STRING.put(3476345444745745746L, "3476345444745745746");
        CONVERTIBLE_TO_STRING.put(234.23, "234.23");
        CONVERTIBLE_TO_STRING.put(234234.23F, "234234.23");
        CONVERTIBLE_TO_STRING.put('A', "A");
        CONVERTIBLE_TO_STRING.put((byte) 2, "2");
        CONVERTIBLE_TO_STRING.put((short) 234, "234");
        //
        CONVERTIBLE_FROM_DOUBLE = new HashMap<>();
        CONVERTIBLE_FROM_DOUBLE.put(23422.65, 23423);
        CONVERTIBLE_FROM_DOUBLE.put(347634544474.49, 347634544474L);
        CONVERTIBLE_FROM_DOUBLE.put(234234.23, 234234.23F);
        CONVERTIBLE_FROM_DOUBLE.put(2.345, (byte) 2);
        CONVERTIBLE_FROM_DOUBLE.put(233.789, (short) 234);
        //
        UNCONVERTIBLE_FROM_DOUBLE = new HashMap<>();
        UNCONVERTIBLE_FROM_DOUBLE.put(3476345444745745746.49, true);
        UNCONVERTIBLE_FROM_DOUBLE.put(234234.23, 'A');
        //
        CONVERTIBLE_FROM_INTEGER = new HashMap<>();
        CONVERTIBLE_FROM_INTEGER.put(23423, 23423.00);
        CONVERTIBLE_FROM_INTEGER.put(347634544, 347634544L);
        CONVERTIBLE_FROM_INTEGER.put(234234, 234234.00F);
        CONVERTIBLE_FROM_INTEGER.put(2, (byte) 2);
        CONVERTIBLE_FROM_INTEGER.put(233, (short) 233);
        //
        UNCONVERTIBLE_FROM_INTEGER = new HashMap<>();
        UNCONVERTIBLE_FROM_INTEGER.put(347634544, true);
        UNCONVERTIBLE_FROM_INTEGER.put(234234, 'A');
    }

    @Test
    void convertConvertibleToString() {
        CONVERTIBLE_TO_STRING.forEach((s, o) -> {
            Class<?> expectedClass = o.getClass();
            Object retrieved = ConverterTypeUtil.convert(expectedClass, s);
            assertThat(o).isEqualTo(retrieved);
        });
    }

    @Test
    void convertConvertibleFromString() {
        CONVERTIBLE_FROM_STRING.forEach((s, expected) -> {
            Class<?> expectedClass = expected.getClass();
            Object retrieved = ConverterTypeUtil.convert(expectedClass, s);
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test
    void convertUnconvertibleFromString() {
        UNCONVERTIBLE_FROM_STRING.forEach((s, o) -> {
            Class<?> expectedClass = o.getClass();
            try {
                ConverterTypeUtil.convert(expectedClass, s);
                fail(String.format("Expecting KiePMMLException for %s %s", s, o));
            } catch (Exception e) {
                assertThat(e.getClass()).isEqualTo(KiePMMLException.class);
            }
        });
    }

    @Test
    void convertConvertibleFromInteger() {
        CONVERTIBLE_FROM_INTEGER.forEach((s, expected) -> {
            Class<?> expectedClass = expected.getClass();
            Object retrieved = ConverterTypeUtil.convert(expectedClass, s);
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test
    void convertUnconvertibleFromInteger() {
        UNCONVERTIBLE_FROM_INTEGER.forEach((s, o) -> {
            Class<?> expectedClass = o.getClass();
            try {
                ConverterTypeUtil.convert(expectedClass, s);
                fail(String.format("Expecting KiePMMLException for %s %s", s, o));
            } catch (Exception e) {
                assertThat(e.getClass()).isEqualTo(KiePMMLException.class);
            }
        });
    }

    @Test
    void convertConvertibleFromDouble() {
        CONVERTIBLE_FROM_DOUBLE.forEach((s, expected) -> {
            Class<?> expectedClass = expected.getClass();
            Object retrieved = ConverterTypeUtil.convert(expectedClass, s);
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test
    void convertUnconvertibleFromDouble() {
        UNCONVERTIBLE_FROM_DOUBLE.forEach((s, o) -> {
            Class<?> expectedClass = o.getClass();
            try {
                ConverterTypeUtil.convert(expectedClass, s);
                fail(String.format("Expecting KiePMMLException for %s %s", s, o));
            } catch (Exception e) {
                assertThat(e.getClass()).isEqualTo(KiePMMLException.class);
            }
        });
    }
}