/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.functions.CountFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.FEEL_NUMBER;
import static org.kie.dmn.openapi.impl.SchemaMapperTestUtils.getSchemaForSimpleType;

class FEELFunctionSchemaMapperTest {

    @ParameterizedTest
    @EnumSource(InfixOperator.class)
    void populateSchemaFromFEELFunction(InfixOperator operator) {
        List<Integer> toEnum = Arrays.asList(1, 3, 6, 78);
        toEnum.forEach(rightValue -> {
            Schema toPopulate = getSchemaForSimpleType(null, null, FEEL_NUMBER, BuiltInType.NUMBER);
            FEELFunctionSchemaMapper.populateSchemaFromFEELFunction(CountFunction.INSTANCE, operator,
                                                                    BigDecimal.valueOf(rightValue), toPopulate);
            Integer expectedMinimum = null;
            Integer expectedMaximum = null;
            switch (operator) {
                case GT -> expectedMinimum = rightValue + 1;
                case GTE -> expectedMinimum = rightValue;
                case LT -> expectedMaximum = rightValue - 1;
                case LTE -> expectedMaximum = rightValue;
                case EQ -> {
                    expectedMinimum = rightValue;
                    expectedMaximum = rightValue;
                }
            }
            assertEquals(expectedMinimum, toPopulate.getMinItems());
            assertEquals(expectedMaximum, toPopulate.getMaxItems());
        });
    }
}