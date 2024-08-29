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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.jupiter.api.Test;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.FEEL_NUMBER;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.FEEL_STRING;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.getSchemaForSimpleType;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.getSimpleType;

class DMNTypeSchemasTest {

    @Test
    void populateSchemaWithConstraintsForAllowedValues() {
        List<String> enumBase = Arrays.asList("DMN", "PMML", "JBPMN", "DRL");
        List<Object> toEnum =
                enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String allowedValuesString = String.join(",",
                                                 toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        SimpleTypeImpl toRead = getSimpleType(allowedValuesString, null, FEEL_STRING, BuiltInType.STRING);
        AtomicReference<Schema> toPopulate = new AtomicReference<>(getSchemaForSimpleType(toRead));
        DMNTypeSchemas.populateSchemaWithConstraints(toPopulate.get(), toRead);
        assertThat(toPopulate.get().getEnumeration()).hasSameSizeAs(enumBase).containsAll(enumBase);
        assertThat(toPopulate.get().getExtensions()).containsKey(DMNOASConstants.X_DMN_ALLOWED_VALUES);
        String retrieved =
                ((String) toPopulate.get().getExtensions().get(DMNOASConstants.X_DMN_ALLOWED_VALUES)).replace(" ", "");
        assertThat(retrieved).isEqualTo(allowedValuesString);

        toEnum = Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(3), BigDecimal.valueOf(6), BigDecimal.valueOf(78));
        allowedValuesString = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());

        toRead = getSimpleType(allowedValuesString, null, FEEL_NUMBER, BuiltInType.NUMBER);
        toPopulate.set(getSchemaForSimpleType(toRead));
        DMNTypeSchemas.populateSchemaWithConstraints(toPopulate.get(), toRead);
        assertThat(toPopulate.get().getEnumeration()).hasSameSizeAs(toEnum).containsAll(toEnum);
        assertThat(toPopulate.get().getExtensions()).containsKey(DMNOASConstants.X_DMN_ALLOWED_VALUES);
        retrieved = ((String) toPopulate.get().getExtensions().get(DMNOASConstants.X_DMN_ALLOWED_VALUES)).replace(" "
                , "");
        assertThat(retrieved).isEqualTo(allowedValuesString);
    }

    @Test
    void populateSchemaWithConstraintsForTypeConstraints() {
        List<String> enumBase = Arrays.asList("DMN", "PMML", "JBPMN", "DRL");
        List<Object> toEnum =
                enumBase.stream().map(toMap -> String.format("\"%s\"", toMap)).collect(Collectors.toUnmodifiableList());
        String typeConstraintsString = String.join(",",
                                                   toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());
        SimpleTypeImpl toRead = getSimpleType(null, typeConstraintsString, FEEL_STRING, BuiltInType.STRING);
        AtomicReference<Schema> toPopulate = new AtomicReference<>(getSchemaForSimpleType(toRead));
        DMNTypeSchemas.populateSchemaWithConstraints(toPopulate.get(), toRead);
        assertThat(toPopulate.get().getEnumeration()).hasSameSizeAs(enumBase).containsAll(enumBase);
        assertThat(toPopulate.get().getExtensions()).containsKey(DMNOASConstants.X_DMN_TYPE_CONSTRAINTS);
        String retrieved =
                ((String) toPopulate.get().getExtensions().get(DMNOASConstants.X_DMN_TYPE_CONSTRAINTS)).replace(" ",
                                                                                                                "");
        assertThat(retrieved).isEqualTo(typeConstraintsString);

        toEnum = Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(3), BigDecimal.valueOf(6), BigDecimal.valueOf(78));
        typeConstraintsString = String.join(",", toEnum.stream().map(toMap -> String.format("%s", toMap)).toList());

        toRead = getSimpleType(null, typeConstraintsString, FEEL_NUMBER, BuiltInType.NUMBER);
        toPopulate.set(getSchemaForSimpleType(toRead));
        DMNTypeSchemas.populateSchemaWithConstraints(toPopulate.get(), toRead);
        assertThat(toPopulate.get().getEnumeration()).hasSameSizeAs(toEnum).containsAll(toEnum);
        assertThat(toPopulate.get().getExtensions()).containsKey(DMNOASConstants.X_DMN_TYPE_CONSTRAINTS);
        retrieved = ((String) toPopulate.get().getExtensions().get(DMNOASConstants.X_DMN_TYPE_CONSTRAINTS)).replace(
                " ", "");
        assertThat(retrieved).isEqualTo(typeConstraintsString);
    }

    @Test
    void populateSchemaWithRangesForAllowedValues() {
        List<Object> toRange = Arrays.asList("(>1)", "(<=10)");
        String allowedValuesString = String.join(",",
                                                 toRange.stream().map(toMap -> String.format("%s", toMap)).toList());
        SimpleTypeImpl toRead = getSimpleType(allowedValuesString, null, FEEL_STRING, BuiltInType.STRING);
        AtomicReference<Schema> toPopulate = new AtomicReference<>(getSchemaForSimpleType(toRead));
        DMNTypeSchemas.populateSchemaWithConstraints(toPopulate.get(), toRead);
        assertThat(toPopulate.get().getMinimum()).isEqualTo(BigDecimal.ONE);
        assertThat(toPopulate.get().getExclusiveMinimum()).isTrue();
        assertThat(toPopulate.get().getMaximum()).isEqualTo(BigDecimal.TEN);
        assertThat(toPopulate.get().getExclusiveMaximum()).isFalse();
        assertThat(toPopulate.get().getExtensions()).containsKey(DMNOASConstants.X_DMN_ALLOWED_VALUES);
        String retrieved =
                ((String) toPopulate.get().getExtensions().get(DMNOASConstants.X_DMN_ALLOWED_VALUES)).replace(" ", "");
        String expected = allowedValuesString.replace("(", "").replace(")", "");
        assertThat(retrieved).isEqualTo(expected);
    }

    @Test
    void populateSchemaWithRangesForTypeConstraints() {
        List<Object> toRange = Arrays.asList("(>1)", "(<=10)");
        String typeConstraintsString = String.join(",",
                                                   toRange.stream().map(toMap -> String.format("%s", toMap)).toList());
        SimpleTypeImpl toRead = getSimpleType(null, typeConstraintsString, FEEL_STRING, BuiltInType.STRING);
        AtomicReference<Schema> toPopulate = new AtomicReference<>(getSchemaForSimpleType(toRead));
        DMNTypeSchemas.populateSchemaWithConstraints(toPopulate.get(), toRead);
        assertThat(toPopulate.get().getMinimum()).isEqualTo(BigDecimal.ONE);
        assertThat(toPopulate.get().getExclusiveMinimum()).isTrue();
        assertThat(toPopulate.get().getMaximum()).isEqualTo(BigDecimal.TEN);
        assertThat(toPopulate.get().getExclusiveMaximum()).isFalse();
        assertThat(toPopulate.get().getExtensions().containsKey(DMNOASConstants.X_DMN_TYPE_CONSTRAINTS)).isTrue();
        String retrieved =
                ((String) toPopulate.get().getExtensions().get(DMNOASConstants.X_DMN_TYPE_CONSTRAINTS)).replace(" ",
                                                                                                                "");
        String expected = typeConstraintsString.replace("(", "").replace(")", "");
        assertThat(retrieved).isEqualTo(expected);
    }
}