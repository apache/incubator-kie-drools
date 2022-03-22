/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.internal.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.kie.kogito.internal.utils.ConversionUtils.concatPaths;
import static org.kie.kogito.internal.utils.ConversionUtils.convert;
import static org.kie.kogito.internal.utils.ConversionUtils.toCamelCase;

class ConversionUtilsTest {

    @Test
    void testConvert() {
        assertEquals(5, convert("5", Integer.class));
        assertFalse(convert("5", Boolean.class));
    }

    @Test
    void testCamelCase() {
        assertEquals("myappCreate", toCamelCase("myapp.create"));
        assertEquals("getByName1", toCamelCase("getByName_1"));
        assertNotEquals("myappcreate", toCamelCase("myapp.create"));
    }

    @Test
    public void testConcatPaths() {
        final String expected = "http:localhost:8080/pepe/pepa/pepi";
        assertEquals(expected, concatPaths("http:localhost:8080/pepe/", "/pepa/pepi"));
        assertEquals(expected, concatPaths("http:localhost:8080/pepe", "pepa/pepi"));
        assertEquals(expected, concatPaths("http:localhost:8080/pepe/", "pepa/pepi"));
        assertEquals(expected, concatPaths("http:localhost:8080/pepe", "/pepa/pepi"));

    }
}
