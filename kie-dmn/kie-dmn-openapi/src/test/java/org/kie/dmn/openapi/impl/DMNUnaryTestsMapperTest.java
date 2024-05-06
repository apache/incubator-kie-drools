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
package org.kie.dmn.openapi.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.dmn.openapi.impl.DMNUnaryTestsMapper.getUnaryEvaluationNodesFromUnaryTests;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.FEEL_NUMBER;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.FEEL_STRING;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.feel;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.getSchemaForSimpleType;

class DMNUnaryTestsMapperTest {

    @Test
    void populateSchemaFromUnaryTestsForEnumsWithoutNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList("ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        expression += ", count (?) > 1";
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate, unaryTests);
        assertFalse(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedStrings.size(), toPopulate.getEnumeration().size());
        expectedStrings.forEach(expectedString -> assertTrue(toPopulate.getEnumeration().contains(expectedString)));
    }

    @Test
    void populateSchemaFromUnaryTestsForEnumsWithNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList(null, "ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toFormat -> toFormat == null ? "null": String.format("\"%s\"", toFormat)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate, unaryTests);
        assertTrue(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedStrings.size(), toPopulate.getEnumeration().size());
        expectedStrings.stream().filter(Objects::nonNull).forEach(expectedString -> assertTrue(toPopulate.getEnumeration().contains(expectedString)));
    }

    @Test
    void populateSchemaFromUnaryTestsForEnumSucceed() {
        List<String> enumBase = Arrays.asList("DMN", "PMML", "JBPMN", "DRL");
        List<Object> toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        AtomicReference<Schema> toPopulate = new AtomicReference<>(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
        assertNull(toPopulate.get().getEnumeration());
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate.get(), toCheck);
        assertEquals(enumBase.size(), toPopulate.get().getEnumeration().size());
        enumBase.forEach(en -> assertTrue(toPopulate.get().getEnumeration().contains(en)));

        toEnum = Arrays.asList(1, 3, 6, 78);
        expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        toPopulate.set(getSchemaForSimpleType(null, expression, FEEL_NUMBER, BuiltInType.NUMBER));
        assertNull(toPopulate.get().getEnumeration());
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate.get(), toCheck);
        assertEquals(toEnum.size(), toPopulate.get().getEnumeration().size());
        toEnum.stream().map(i -> BigDecimal.valueOf((int)i)).forEach(en -> assertTrue(toPopulate.get().getEnumeration().contains(en)));

        toPopulate.set(OASFactory.createObject(Schema.class));
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(), toFormat.getDayOfMonth()))
                .toList();
        expression = String.join(",", formattedDates.stream().map(toMap -> String.format("%s", toMap)).toList());
        toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertNull(toPopulate.get().getNullable());
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate.get(), toCheck);
        assertNotNull(toPopulate.get().getEnumeration());
        assertEquals(expectedDates.size(), toPopulate.get().getEnumeration().size());
        expectedDates.forEach(expectedDate -> assertTrue(toPopulate.get().getEnumeration().contains(expectedDate)));
    }

    @Test
    void populateSchemaFromUnaryTestsFails() {
        List<Object> toEnum = Arrays.asList(null, null, "@\"2024-01-01\"");
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertEquals(toEnum.size(), toCheck.size());
        Schema toPopulate = getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING);
        assertThrows(IllegalArgumentException.class, () -> DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate, toCheck));
    }

    @Test
    void populateSchemaFromBaseNodeSucceed() {
        List<String> enumBase = List.of("DMN");
        List<String> toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).toList();
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> dmnUnaryTests =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        AtomicReference<Schema> schemaRef = new AtomicReference<>(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
        assertNull(schemaRef.get().getEnumeration());
        BaseNode toCheck = getUnaryEvaluationNodesFromUnaryTests(dmnUnaryTests).get(0);
        DMNUnaryTestsMapper.populateSchemaFromBaseNode(schemaRef.get(), toCheck);
        assertEquals(enumBase.size(), schemaRef.get().getEnumeration().size());
        enumBase.forEach(en -> assertTrue(schemaRef.get().getEnumeration().contains(en)));
    }

}