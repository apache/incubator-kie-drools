/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.io.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ByteArrayResourceToStringTest {

    static Stream<Arguments> parameters() {
        return Stream.of(
                arguments(Arrays.asList(new Byte[]{10, 20, 30, 40}), null,
                          "ByteArrayResource[bytes=[10, 20, 30, 40], encoding=null]"),
                arguments(Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}), null,
                          "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100], encoding=null]"),
                arguments(Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120}), null,
                          "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100, ...], encoding=null]"),
                arguments(Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120}), "UTF-8",
                          "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100, ...], encoding=UTF-8]")
        );
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testToString(List<Byte> bytes, final String encoding, final String expectedString) {
        byte[] byteArray = toPrimitive(bytes.toArray(new Byte[0]));
        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray, encoding);
        assertEquals(expectedString, byteArrayResource.toString());
    }
    
    protected byte[] toPrimitive(final Byte[] array) {
        if (array == null) {
            return null;
        } else if (array.length == 0) {
            return new byte[0];
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[i].byteValue();
        }
        return result;
    }

}
