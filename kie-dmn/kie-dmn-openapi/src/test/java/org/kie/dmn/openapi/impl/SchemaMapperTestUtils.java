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

import java.util.Collections;
import java.util.List;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.compiler.DMNTypeRegistry;
import org.kie.dmn.core.compiler.DMNTypeRegistryV15;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.codegen.feel11.ProcessedExpression;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.impl.FEELBuilder;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.model.v1_5.KieDMNModelInstrumentedBase;

class SchemaMapperTestUtils {

    static final FEEL feel = FEELBuilder.builder().build();

    static final DMNTypeRegistry typeRegistry = new DMNTypeRegistryV15(Collections.emptyMap());
    static final DMNType FEEL_STRING = typeRegistry.resolveType(KieDMNModelInstrumentedBase.URI_FEEL, "string");
    static final DMNType FEEL_NUMBER = typeRegistry.resolveType(KieDMNModelInstrumentedBase.URI_FEEL, "number");

    static Schema getSchemaForSimpleType(String allowedValuesString, String typeConstraintString, DMNType baseType,
                                         BuiltInType builtInType) {
        DMNType dmnType = getSimpleType(allowedValuesString, typeConstraintString, baseType, builtInType);
        return FEELBuiltinTypeSchemaMapper.from(dmnType);
    }

    static Schema getSchemaForSimpleType(DMNType dmnType) {
        return FEELBuiltinTypeSchemaMapper.from(dmnType);
    }

    static SimpleTypeImpl getSimpleType(String allowedValuesString, String typeConstraintString, DMNType baseType,
                                        BuiltInType builtInType) {
        List<UnaryTest> allowedValues = allowedValuesString != null && !allowedValuesString.isEmpty() ?
                feel.evaluateUnaryTests(allowedValuesString) : null;
        List<UnaryTest> typeConstraint = typeConstraintString != null && !typeConstraintString.isEmpty() ?
                feel.evaluateUnaryTests(typeConstraintString) : null;
        return new SimpleTypeImpl("testNS", "tName", null, true, allowedValues, typeConstraint, baseType, builtInType);
    }

    static <T extends BaseNode> List<T> getBaseNodes(List<String> toRange, Class<T> clazz) {
        return toRange.stream().map(expression -> getBaseNode(expression, clazz))
                .toList();
    }

    static <T extends BaseNode> T getBaseNode(String expression, Class<T> clazz) {
        ProcessedExpression processedExpression = (ProcessedExpression) feel.compile(expression, feel.newCompilerContext());
        return clazz.cast(processedExpression.getInterpreted().getASTNode());

    }

    private SchemaMapperTestUtils() {
    }
}   