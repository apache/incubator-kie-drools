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

import java.util.Collections;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.visitor.ASTTemporalConstantVisitor;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.feel.lang.impl.UnaryTestCompiledExecutableExpression;
import org.kie.dmn.feel.lang.impl.UnaryTestInterpretedExecutableExpression;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.runtime.UnaryTest;

public class ProcessedUnaryTest extends ProcessedFEELUnit {

    private static final String TEMPLATE_RESOURCE = "/TemplateCompiledFEELUnaryTests.java";
    private static final String TEMPLATE_CLASS = "TemplateCompiledFEELUnaryTests";

    private final BaseNode ast;
    private DirectCompilerResult compiledExpression;

    public ProcessedUnaryTest(String expressions,
                              CompilerContext ctx, List<FEELProfile> profiles) {
        super(expressions, ctx, Collections.emptyList());
        ParseTree tree = getFEELParser(expression, ctx, profiles).unaryTestsRoot();
        ASTBuilderVisitor astVisitor = new ASTBuilderVisitor(ctx.getInputVariableTypes(), ctx.getFEELFeelTypeRegistry());
        BaseNode initialAst = tree.accept(astVisitor);
        ast = initialAst.accept(new ASTUnaryTestTransform()).node();
        if (astVisitor.isVisitedTemporalCandidate()) {
            ast.accept(new ASTTemporalConstantVisitor(ctx));
        }
    }

    private DirectCompilerResult getCompilerResult() {
        if (compiledExpression == null) {
            if (errorListener.isError()) {
                compiledExpression = CompiledFEELSupport.compiledErrorUnaryTest(
                        errorListener.event().getMessage());
            } else {
                try {
                    compiledExpression = ast.accept(new ASTCompilerVisitor());
                } catch (FEELCompilationError e) {
                    compiledExpression = CompiledFEELSupport.compiledErrorUnaryTest(e.getMessage());
                }
            }
        }
        return compiledExpression;
    }

    public CompilationUnit getSourceCode() {
        DirectCompilerResult compilerResult = getCompilerResult();
        return compiler.getCompilationUnit(
                CompiledFEELUnaryTests.class,
                TEMPLATE_RESOURCE,
                packageName,
                TEMPLATE_CLASS,
                expression,
                compilerResult.getExpression(),
                compilerResult.getFieldDeclarations());
    }

    public UnaryTestInterpretedExecutableExpression getInterpreted() {
        if (errorListener.isError()) {
            return UnaryTestInterpretedExecutableExpression.EMPTY;
        } else {
            return new UnaryTestInterpretedExecutableExpression(new CompiledExpressionImpl(ast));
        }
    }

    public UnaryTestCompiledExecutableExpression getCompiled() {
        CompiledFEELUnaryTests compiledFEELExpression =
                compiler.compileUnit(
                        packageName,
                        TEMPLATE_CLASS,
                        getSourceCode());

        return new UnaryTestCompiledExecutableExpression(compiledFEELExpression);
    }

    @Override
    public List<UnaryTest> apply(EvaluationContext evaluationContext) {
        return getInterpreted().apply(evaluationContext);
    }
}
