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
package org.drools.core.io.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.drools.io.ByteArrayResource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ByteArrayResourceToStringTest {

    public static Stream<Arguments> parameters() {
        List<Arguments> parameters = List.of(
               arguments(
                        Arrays.asList(new Byte[]{10, 20, 30, 40}),
                        null,
                        "ByteArrayResource[bytes=[10, 20, 30, 40], encoding=null]"
                ),
                arguments(
                        Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}),
                        null,
                        "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100], encoding=null]"
                ),
                arguments(
                        Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120}),
                        null,
                        "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100, ...], encoding=null]"
                ),
                // non-null encoding
                arguments(
                        Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120}),
                        "UTF-8",
                        "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100, ...], encoding=UTF-8]"
                ));
        return parameters.stream();
    }

    // using List<Byte> instead of directly byte[] to make sure the bytes are printed as part of the test name
    // see above ({index}: bytes[{0}], encoding[{1}]) -- Array.toString only return object id

    @ParameterizedTest(name = "{index}: bytes={0}, encoding={1}")
    @MethodSource("parameters")
    public void testToString(List<Byte> bytes, String encoding, String expectedString) {
        byte[] byteArray = ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray, encoding);
        assertThat(byteArrayResource.toString()).isEqualTo(expectedString);
    }

}
