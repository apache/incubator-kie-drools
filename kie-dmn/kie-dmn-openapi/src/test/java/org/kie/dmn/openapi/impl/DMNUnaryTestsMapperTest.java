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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
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
        assertThat(toPopulate.getNullable()).isFalse();
        assertThat(toPopulate.getEnumeration()).isNotNull();
        assertThat(toPopulate.getEnumeration()).hasSameSizeAs(expectedStrings).containsAll(expectedStrings);
    }

    @Test
    void populateSchemaFromUnaryTestsForEnumsWithNull() {
        Schema toPopulate = OASFactory.createObject(Schema.class);
        List<String> expectedStrings = Arrays.asList(null, "ONE", "TWO");
        List<Object> toEnum = expectedStrings.stream().map(toFormat -> toFormat == null ? "null": String.format("\"%s\"", toFormat)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap.toString())).toList());
        List<DMNUnaryTest> unaryTests = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate, unaryTests);
        assertThat(toPopulate.getNullable()).isTrue();
        assertThat(toPopulate.getEnumeration()).isNotNull();
        assertThat(toPopulate.getEnumeration()).hasSameSizeAs(expectedStrings).containsAll(expectedStrings);
    }

    @Test
    void populateSchemaFromUnaryTestsForEnumSucceed() {
        List<String> enumBase = Arrays.asList("DMN", "PMML", "JBPMN", "DRL");
        List<Object> toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        AtomicReference<Schema> toPopulate = new AtomicReference<>(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
        assertThat(toPopulate.get().getEnumeration()).isNull();;
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate.get(), toCheck);
        assertThat(toPopulate.get().getEnumeration()).hasSameSizeAs(enumBase).containsAll(enumBase);

        List<BigDecimal>toEnum1 = Arrays.asList(BigDecimal.valueOf(1L), BigDecimal.valueOf(3), BigDecimal.valueOf(6), BigDecimal.valueOf(78));
        expression = String.join(",", toEnum1.stream().map(toMap -> String.format("%s", toMap)).toList());
        toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        toPopulate.set(getSchemaForSimpleType(null, expression, FEEL_NUMBER, BuiltInType.NUMBER));
        assertThat(toPopulate.get().getEnumeration()).isNull();
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate.get(), toCheck);
        assertThat(toPopulate.get().getEnumeration()).hasSameSizeAs(enumBase).extracting(value -> ((BigDecimal)value)).containsAll(toEnum1);

        toPopulate.set(OASFactory.createObject(Schema.class));
        List<LocalDate> expectedDates = Arrays.asList(LocalDate.of(2022, 1, 1), LocalDate.of(2024, 1, 1));
        List<String> formattedDates = expectedDates.stream()
                .map(toFormat -> String.format("@\"%s-0%s-0%s\"", toFormat.getYear(), toFormat.getMonthValue(), toFormat.getDayOfMonth()))
                .toList();
        expression = String.join(",", formattedDates.stream().map(toMap -> String.format("%s", toMap)).toList());
        toCheck = feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertThat(toPopulate.get().getNullable()).isNull();
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate.get(), toCheck);
        assertThat(toPopulate.get().getEnumeration()).isNotNull();
        assertThat(toPopulate.get().getEnumeration()).hasSameSizeAs(expectedDates).containsAll(expectedDates);
    }

    @Test
    void populateSchemaFromUnaryTestsFails() {
        List<Object> toEnum = Arrays.asList(null, null, "@\"2024-01-01\"");
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> toCheck =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        assertThat(toCheck).hasSameSizeAs(toEnum);
        Schema toPopulate = getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING);
        assertThatIllegalArgumentException().isThrownBy(() -> DMNUnaryTestsMapper.populateSchemaFromUnaryTests(toPopulate, toCheck));
    }

    @Test
    void populateSchemaFromBaseNodeSucceed() {
        List<String> enumBase = List.of("DMN");
        List<String> toEnum = enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).toList();
        String expression = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        List<DMNUnaryTest> dmnUnaryTests =
                feel.evaluateUnaryTests(expression).stream().map(DMNUnaryTest.class::cast).toList();
        AtomicReference<Schema> schemaRef = new AtomicReference<>(getSchemaForSimpleType(null, expression, FEEL_STRING, BuiltInType.STRING));
        assertThat(schemaRef.get().getEnumeration()).isNull();
        BaseNode toCheck = getUnaryEvaluationNodesFromUnaryTests(dmnUnaryTests).get(0);
        DMNUnaryTestsMapper.populateSchemaFromBaseNode(schemaRef.get(), toCheck);
        assertThat(schemaRef.get().getEnumeration()).hasSameSizeAs(enumBase).containsAll(enumBase);
    }

}