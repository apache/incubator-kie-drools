/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.feel.codegen.feel11;

import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.visitor.ASTHeuristicCheckerVisitor;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.feel.lang.impl.InterpretedExecutableExpression;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;

import static org.kie.dmn.feel.codegen.feel11.ProcessedFEELUnit.DefaultMode.Compiled;

public abstract class ProcessedExpression extends ProcessedFEELUnit {

    private final BaseNode ast;
    private final DefaultMode defaultBackend;
    private CompiledFEELExpression defaultResult;

    public ProcessedExpression(final String expression,
                               final CompilerContext ctx,
                               final DefaultMode defaultBackend,
                               final List<FEELProfile> profiles) {

        super(expression, ctx, profiles);
        this.defaultBackend = defaultBackend;

        final ParseTree tree = getFEELParser(expression, ctx, profiles).compilation_unit();
        ast = tree.accept(new ASTBuilderVisitor(ctx.getInputVariableTypes(), ctx.getFEELFeelTypeRegistry()));

        final boolean isParseTreeAstInvalid = ast == null;
        if (isParseTreeAstInvalid) {
            // No need of further processing
            return;
        }

        final List<FEELEvent> heuristicChecks = ast.accept(new ASTHeuristicCheckerVisitor());
        if (!heuristicChecks.isEmpty()) {
            for (FEELEventListener listener : ctx.getListeners()) {
                heuristicChecks.forEach(listener::onEvent);
            }
        }
    }

    public CompiledFEELExpression getResult() {
        if (defaultBackend == Compiled) {
            throw new UnsupportedOperationException("Cannot jit class-load on this platform.");
        } else {
            // "legacy" interpreted AST compilation:
            defaultResult = getInterpreted();
        }

        return this;
    }

    public InterpretedExecutableExpression getInterpreted() {
        return new InterpretedExecutableExpression(new CompiledExpressionImpl(ast));
    }
}
