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
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.EvaluationContext;

public class TemplateCompiledFEELExpression implements org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression {

    private BaseNode BASE_NODE;

    @Override
    public Object apply(EvaluationContext feelExprCtx) {
        try {
            return getBaseNode().evaluate(feelExprCtx);
        } catch (IllegalStateException e) {
            return notifyCompilationError(feelExprCtx, e.getMessage());
        }
    }

    private static TemplateCompiledFEELExpression INSTANCE;

    public static TemplateCompiledFEELExpression getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TemplateCompiledFEELExpression();
        }
        return INSTANCE;
    }

    private BaseNode getBaseNode() {
        if (BASE_NODE == null) {
            BASE_NODE = createBaseNode();
        }
        return BASE_NODE;
    }

    private BaseNode createBaseNode() {
        return null;
    }

}
