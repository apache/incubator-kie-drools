/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvelcompiler;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import org.drools.mvel.parser.MvelParser;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.context.MvelCompilerContext;

import static com.github.javaparser.ast.NodeList.nodeList;

/* A special case of compiler in that compiles constraints, that is
    every variable can be implicitly a field of the root object
    no LHS
    converted FieldToAccessor prepend a this expr
 */
public class ConstraintCompiler {

    private final MvelCompilerContext mvelCompilerContext;

    public ConstraintCompiler(MvelCompilerContext mvelCompilerContext) {
        this.mvelCompilerContext = mvelCompilerContext;
    }

    public CompiledExpressionResult compileExpression(String mvelExpressionString) {
        Expression parsedExpression = MvelParser.parseExpression(mvelExpressionString);
        Node compiled = compileExpression(parsedExpression);

        return new CompiledExpressionResult((Expression) compiled);
    }

    // Avoid processing the LHS as it's not present while compiling an expression
    private Node compileExpression(Node n) {
        TypedExpression rhs = new RHSPhase(mvelCompilerContext).invoke(n);
        return rhs.toJavaExpression();
    }
}
