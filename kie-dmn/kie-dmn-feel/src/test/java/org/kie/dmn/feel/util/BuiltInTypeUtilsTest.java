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
import org.kie.dmn.feel.lang.types.BuiltInType;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class BuiltInTypeUtilsTest {

    @Test
    void determineTypesFromClass() {
        Object toTest = new Object[4];
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(BuiltInType.LIST);
        toTest = 4;
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(BuiltInType.NUMBER);

        toTest = "Test string";
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(BuiltInType.STRING);

        toTest = true;
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(BuiltInType.BOOLEAN);

        toTest = Duration.ofHours(1);
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(BuiltInType.DURATION);

        toTest = java.util.Map.of("key", "value");
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(1)
                .containsExactly(BuiltInType.CONTEXT);

        toTest = java.time.LocalDate.now();
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(BuiltInType.DATE, BuiltInType.DATE_TIME, BuiltInType.TIME)
                .filteredOn(type -> type == BuiltInType.DATE)
                .hasSize(1);

        toTest = java.time.ZonedDateTime.now();
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(BuiltInType.DATE, BuiltInType.DATE_TIME, BuiltInType.TIME)
                .filteredOn(type -> type == BuiltInType.DATE_TIME)
                .hasSize(1);

        toTest = java.time.OffsetDateTime.now();
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(BuiltInType.DATE, BuiltInType.DATE_TIME, BuiltInType.TIME)
                .filteredOn(type -> type == BuiltInType.DATE_TIME)
                .hasSize(1);

        toTest = java.time.OffsetTime.now();
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(BuiltInType.DATE, BuiltInType.DATE_TIME, BuiltInType.TIME)
                .filteredOn(type -> type == BuiltInType.TIME)
                .hasSize(1);

        toTest = java.time.LocalTime.now();
        assertThat(BuiltInTypeUtils.determineTypesFromClass(toTest.getClass()))
                .isNotEmpty()
                .hasSize(3)
                .containsExactlyInAnyOrder(BuiltInType.DATE, BuiltInType.DATE_TIME, BuiltInType.TIME)
                .filteredOn(type -> type == BuiltInType.TIME)
                .hasSize(1);
    }
}