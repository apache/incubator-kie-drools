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
package org.kie.dmn.feel.codegen.feel11;

import org.kie.dmn.feel.codegen.feel11.CompiledCustomFEELFunction;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.EvaluationContext;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.runtime.UnaryTest;

public class TemplateCompiledFEELUnaryTests implements org.kie.dmn.feel.codegen.feel11.CompiledFEELUnaryTests {


    private org.kie.dmn.feel.lang.ast.UnaryTestListNode BASE_NODE;
    private java.util.List<org.kie.dmn.feel.runtime.UnaryTest> UNARY_TESTS;

    @Override
    public java.util.List<org.kie.dmn.feel.runtime.UnaryTest> getUnaryTests() {
        try {
            return getCompiledUnaryTests();
        } catch (IllegalStateException e) {
            org.kie.dmn.feel.runtime.UnaryTest unaryTest = (feelExprCtx, left) -> {
                notifyCompilationError(feelExprCtx, e.getMessage());
                return false;
            };
            return java.util.Collections.singletonList(unaryTest);
        }
    }

    private static TemplateCompiledFEELUnaryTests INSTANCE;

    public static TemplateCompiledFEELUnaryTests getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateCompiledFEELUnaryTests();
        }
        return INSTANCE;
    }

    private java.util.List<org.kie.dmn.feel.runtime.UnaryTest> getCompiledUnaryTests() {
        if (UNARY_TESTS == null) {
            UNARY_TESTS = getBaseNode().getCompiledUnaryTests();
        }
        return UNARY_TESTS;
    }

    private org.kie.dmn.feel.lang.ast.UnaryTestListNode getBaseNode() {
        if (BASE_NODE == null) {
            BASE_NODE = createBaseNode();
        }
        return BASE_NODE;
    }

    private org.kie.dmn.feel.lang.ast.UnaryTestListNode createBaseNode() {
        return null;
    }
}
