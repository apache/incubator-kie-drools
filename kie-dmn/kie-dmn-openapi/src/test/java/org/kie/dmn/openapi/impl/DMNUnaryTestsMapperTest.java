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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.Test;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.core.compiler.DMNTypeRegistryV15;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.openapi.impl.DMNUnaryTestsMapper.getUnaryEvaluationNodesFromUnaryTests;
import static org.kie.dmn.openapi.impl.DMNUnaryTestsMapper.populateSchemaFromBaseNode;
import static org.kie.dmn.openapi.impl.DMNUnaryTestsMapper.populateSchemaFromUnaryTests;

public class DMNUnaryTestsMapperTest {

    private static final FEEL feel = FEEL.newInstance();

    private static final DMNTypeRegistry typeRegistry = new DMNTypeRegistryV15(Collections.emptyMap());
    private static final DMNType FEEL_STRING = typeRegistry.resolveType(KieDMNModelInstrumentedBase.URI_FEEL, "string");
    private static final DMNType FEEL_NUMBER = typeRegistry.resolveType(KieDMNModelInstrumentedBase.URI_FEEL, "number");

    @Test
    public void testPopulateSchemaFromUnaryTestsForEnumsWithoutNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList("ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        expression += ", count (?) > 1";
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        populateSchemaFromUnaryTests(toPopulate, unaryTests);
        assertFalse(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedStrings.size(), toPopulate.getEnumeration().size());
        expectedStrings.forEach(expectedString -> assertTrue(toPopulate.getEnumeration().contains(expectedString)));
    }

    @Test
    public void testPopulateSchemaFromUnaryTestsForEnumsWithNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList(null, "ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toFormat -> toFormat == null ? "null": String.format("\"%s\"", toFormat)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        populateSchemaFromUnaryTests(toPopulate, unaryTests);
        assertTrue(toPopulate.getNullable());
        assertNotNull(toPopulate.getEnumeration());
        assertEquals(expectedStrings.size(), toPopulate.getEnumeration().size());
        expectedStrings.stream().filter(Objects::nonNull).forEach(expectedString -> assertTrue(toPopulate.getEnumeration().contains(expectedString)));
    }

    @Test
    public void testPopulateSchemaFromUnaryTestsForEnumSucceed() {
        List<String> enumBase = Arrays.asList("DMN", "PMML", "JBPMN", "DRL");
        List<Object> toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        AtomicReference<Schema> schemaRef = new AtomicReference<>(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
        assertNull(schemaRef.get().getEnumeration());
        populateSchemaFromUnaryTests(schemaRef.get(), toCheck);
        assertEquals(enumBase.size(), schemaRef.get().getEnumeration().size());
        enumBase.forEach(en -> assertTrue(schemaRef.get().getEnumeration().contains(en)));

        toEnum = Arrays.asList(1, 3, 6, 78);
        expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        schemaRef.set(getSchemaForSimpleType(null, expression, FEEL_NUMBER, BuiltInType.NUMBER));
        assertNull(schemaRef.get().getEnumeration());
        populateSchemaFromUnaryTests(schemaRef.get(), toCheck);
        assertEquals(enumBase.size(), schemaRef.get().getEnumeration().size());
        toEnum.stream().map(i -> BigDecimal.valueOf((int)i)).forEach(en -> assertTrue(schemaRef.get().getEnumeration().contains(en)));

        schemaRef.set(OASFactory.createObject(Schema.class));
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(), toFormat.getDayOfMonth()))
                .toList();
        expression = String.join(",", formattedDates.stream().map(toMap -> String.format("%s", toMap)).toList());
        toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertNull(schemaRef.get().getNullable());
        populateSchemaFromUnaryTests(schemaRef.get(), toCheck);
        assertNotNull(schemaRef.get().getEnumeration());
        assertEquals(expectedDates.size(), schemaRef.get().getEnumeration().size());
        expectedDates.forEach(expectedDate -> assertTrue(schemaRef.get().getEnumeration().contains(expectedDate)));
        
    }

    @Test
    public void testPopulateSchemaFromUnaryTestsFails() {
        List<Object> toEnum = Arrays.asList(null, null, "@\"2024-01-01\"");
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertEquals(toEnum.size(), toCheck.size());
        Schema schema = getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING);
        assertThrows(IllegalArgumentException.class, () -> populateSchemaFromUnaryTests(schema, toCheck));
    }

    @Test
    public void testPopulateSchemaFromBaseNodeSucceed() {
        List<String> enumBase = Arrays.asList("DMN");
        List<String> toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).toList();
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> dmnUnaryTests =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        AtomicReference<Schema> schemaRef = new AtomicReference<>(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
        assertNull(schemaRef.get().getEnumeration());
        BaseNode toCheck = getUnaryEvaluationNodesFromUnaryTests(dmnUnaryTests).get(0);
        populateSchemaFromBaseNode(schemaRef.get(), toCheck);
        assertEquals(enumBase.size(), schemaRef.get().getEnumeration().size());
        enumBase.forEach(en -> assertTrue(schemaRef.get().getEnumeration().contains(en)));
    }

    private Schema getSchemaForSimpleType(String allowedValuesString, String typeConstraintString, DMNType baseType, BuiltInType builtInType) {
        List<UnaryTest> allowedValues = allowedValuesString != null && !allowedValuesString.isEmpty() ?  feel.evaluateUnaryTests(allowedValuesString) : null;
        List<UnaryTest> typeConstraint = typeConstraintString != null && !typeConstraintString.isEmpty() ?  feel.evaluateUnaryTests(typeConstraintString) : null;
        DMNType dmnType = new SimpleTypeImpl("testNS", "tName", null, true, allowedValues, typeConstraint, baseType, builtInType);
        return  FEELBuiltinTypeSchemaMapper.from(dmnType);
    }
}