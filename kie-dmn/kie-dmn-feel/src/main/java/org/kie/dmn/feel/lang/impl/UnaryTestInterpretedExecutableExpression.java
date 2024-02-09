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
package org.kie.dmn.feel.lang.impl;

import java.util.Collections;
import java.util.List;

import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.runtime.UnaryTest;

public class UnaryTestInterpretedExecutableExpression implements CompiledFEELExpression {

    public static final UnaryTestInterpretedExecutableExpression EMPTY = new UnaryTestInterpretedExecutableExpression(null) {
        @Override
        public List<UnaryTest> apply(EvaluationContext evaluationContext) {
            return Collections.emptyList();
        }

        @Override
        public ASTNode getASTNode() {
            throw new UnsupportedOperationException("Unsupported for EMPTY");
        }
    };
    private final CompiledExpressionImpl expr;

    public UnaryTestInterpretedExecutableExpression(CompiledExpressionImpl expr) {
        this.expr = expr;
    }

    public List<UnaryTest> apply(EvaluationContext evaluationContext) {
        return (List<UnaryTest>) expr.apply(evaluationContext);
    }

    /**
     * Internals helper methods.
     */
    public ASTNode getASTNode() {
        return expr.getExpression();
    }

}
