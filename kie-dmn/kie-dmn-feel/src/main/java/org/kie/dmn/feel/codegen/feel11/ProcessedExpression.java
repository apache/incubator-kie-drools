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

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.ast.visitor.ASTHeuristicCheckerVisitor;
import org.kie.dmn.feel.lang.ast.visitor.ASTTemporalConstantVisitor;
import org.kie.dmn.feel.lang.impl.CompiledExecutableExpression;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.feel.lang.impl.InterpretedExecutableExpression;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;

import static org.kie.dmn.feel.codegen.feel11.ProcessedFEELUnit.DefaultMode.Compiled;
import static org.kie.dmn.feel.util.ClassLoaderUtil.CAN_PLATFORM_CLASSLOAD;

public class ProcessedExpression extends ProcessedFEELUnit {

    private static final String TEMPLATE_RESOURCE = "/TemplateCompiledFEELExpression.java";
    private static final String TEMPLATE_CLASS = "TemplateCompiledFEELExpression";

    private final DefaultMode executionMode;

    private CompiledFEELExpression executableFEELExpression;

    public ProcessedExpression(
            String expression,
            CompilerContext ctx,
            ProcessedFEELUnit.DefaultMode executionMode,
            List<FEELProfile> profiles) {
        super(expression, ctx, profiles, TEMPLATE_RESOURCE, TEMPLATE_CLASS);
        this.executionMode = executionMode;
        ParseTree tree = getFEELParser(expression, ctx, profiles).compilation_unit();
        ASTBuilderVisitor astVisitor = new ASTBuilderVisitor(ctx.getInputVariableTypes(), ctx.getFEELFeelTypeRegistry());
        ast = tree.accept(astVisitor);
        if (ast == null) {
            return; // if parsetree/ast is invalid, no need of further processing and early return.
        }
        List<FEELEvent> heuristicChecks = ast.accept(new ASTHeuristicCheckerVisitor());
        if (!heuristicChecks.isEmpty()) {
            for (FEELEventListener listener : ctx.getListeners()) {
                heuristicChecks.forEach(listener::onEvent);
            }
        }
        if (astVisitor.isVisitedTemporalCandidate()) {
            ast.accept(new ASTTemporalConstantVisitor(ctx));
        }
    }

    public CompiledFEELExpression asCompiledFEELExpression() {
        if (executionMode == Compiled) {
            if (CAN_PLATFORM_CLASSLOAD) {
                executableFEELExpression = getCompiled();
            } else {
                throw new UnsupportedOperationException("Cannot jit classload on this platform.");
            }
        } else { // "legacy" interpreted AST compilation:
            executableFEELExpression = getInterpreted();
        }

        return this;
    }

    public InterpretedExecutableExpression getInterpreted() {
        return new InterpretedExecutableExpression(new CompiledExpressionImpl(ast));
    }

    public CompiledExecutableExpression getCompiled() {
        return new CompiledExecutableExpression(getCommonCompiled());
    }

    @Override
    public Object apply(EvaluationContext evaluationContext) {
        return executableFEELExpression.apply(evaluationContext);
    }

}
