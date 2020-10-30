/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.commons.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ConverterTypeUtilTest {

    private static Map<String, Object> CONVERTIBLE_FROM_STRING;
    private static Map<String, Object> UNCONVERTIBLE_FROM_STRING;
    private static Map<Object, String> CONVERTIBLE_TO_STRING;

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
    }

    @Test
    public void convertConvertibleToString() {
        CONVERTIBLE_TO_STRING.forEach((s, o) -> {
            Class<?> expectedClass = o.getClass();
            Object retrieved = ConverterTypeUtil.convert(expectedClass, s);
            assertEquals(retrieved, o);
        });
    }

    @Test
    public void convertConvertibleFromString() {
        CONVERTIBLE_FROM_STRING.forEach((s, o) -> {
            Class<?> expectedClass = o.getClass();
            Object retrieved = ConverterTypeUtil.convert(expectedClass, s);
            assertEquals(retrieved, o);
        });
    }

    @Test
    public void convertUnconvertible() {
        UNCONVERTIBLE_FROM_STRING.forEach((s, o) -> {
            Class<?> expectedClass = o.getClass();
            try {
                ConverterTypeUtil.convert(expectedClass, s);
                fail(String.format("Expecting KiePMMLException for %s %s", s, o));
            } catch (Exception e) {
                assertEquals(KiePMMLException.class, e.getClass());
            }
        });
    }
}