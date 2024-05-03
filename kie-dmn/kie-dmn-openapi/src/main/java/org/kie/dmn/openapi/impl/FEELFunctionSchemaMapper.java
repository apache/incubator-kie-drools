/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.CountFunction;
import org.drools.util.functions.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FEELFunctionSchemaMapper {

    private static final Logger LOG = LoggerFactory.getLogger(FEELFunctionSchemaMapper.class);


    static final Map< Class<? extends FEELFunction>, TriConsumer<InfixOperator, Object, Schema>> SCHEMA_MODIFIERS;

    private static TriConsumer<InfixOperator, Object, Schema> COUNT_CONSUMER = (o, rightValue, schema) -> {
        switch (o) {

            case GT ->
                    schema.setMinItems(((BigDecimal) rightValue).intValue() + 1);
            case GTE ->
                    schema.setMinItems(((BigDecimal) rightValue).intValue());
            case LT ->
                    schema.setMaxItems(((BigDecimal) rightValue).intValue() -1);
            case LTE ->
                    schema.setMaxItems(((BigDecimal) rightValue).intValue());
            case EQ -> {
                schema.setMinItems(((BigDecimal) rightValue).intValue());
                schema.setMaxItems(((BigDecimal) rightValue).intValue());
            }
            default -> LOG.debug("Ignore operator {}", o);
        }
    };

    static {
        SCHEMA_MODIFIERS = new HashMap<>();
        SCHEMA_MODIFIERS.put(CountFunction.class, COUNT_CONSUMER);
    }

    static void populateSchemaFromFEELFunction(FEELFunction function, InfixOperator operator, Object rightValue, Schema toPopulate  ) {
        if (SCHEMA_MODIFIERS.containsKey(function.getClass())) {
            SCHEMA_MODIFIERS.get(function.getClass()).accept(operator, rightValue, toPopulate);
        }
    }

    private FEELFunctionSchemaMapper() {
        // deliberate intention not to allow instantiation of this class.
    }
}