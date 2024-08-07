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
package org.kie.dmn.feel.util;

import java.util.Collections;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.antlr.v4.runtime.tree.ParseTree;
import org.kie.dmn.feel.codegen.feel11.ASTCompilerVisitor;
import org.kie.dmn.feel.codegen.feel11.CodegenTestUtil;
import org.kie.dmn.feel.codegen.feel11.CompiledFEELExpression;
import org.kie.dmn.feel.codegen.feel11.CompilerBytecodeLoader;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.impl.CompiledExpressionImpl;
import org.kie.dmn.feel.lang.impl.InterpretedExecutableExpression;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompilerUtils {

    public static final Logger LOG = LoggerFactory.getLogger(CompilerUtils.class);
    private static final String TEMPLATE_RESOURCE = "/TemplateCompiledFEELExpression.java";
    private static final String TEMPLATE_CLASS = "TemplateCompiledFEELExpression";

    public static Object parseCodegenCompileEvaluate(String feelLiteralExpression) {
        LOG.debug("{}", feelLiteralExpression);
        CompiledFEELExpression compiledExpression = parseCodegen(feelLiteralExpression );
        return evaluate(compiledExpression);
    }

    public static CompiledFEELExpression parseCodegen(String input) {
        return parseCodegen(input, Collections.emptyMap() );
    }

    public static CompiledFEELExpression parseCodegen(String input, Map<String, Type> inputTypes) {
        FEEL_1_1Parser parser = FEELParser.parse(null, input, inputTypes, Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);

        ParseTree tree = parser.compilation_unit();

        ASTBuilderVisitor v = new ASTBuilderVisitor(inputTypes, null);
        BaseNode node = v.visit(tree);
        ASTCompilerVisitor astVisitor = new ASTCompilerVisitor();

        BlockStmt directCodegenResult = node.accept(astVisitor);

        CompilerBytecodeLoader compilerBytecodeLoader = new CompilerBytecodeLoader();
        String packageName = compilerBytecodeLoader.generateRandomPackage();
        CompilationUnit cu = compilerBytecodeLoader.getCompilationUnit(
                TEMPLATE_RESOURCE,
                packageName,
                TEMPLATE_CLASS,
                input,
                directCodegenResult,
                astVisitor.getLastVariableName());
        return compilerBytecodeLoader.compileUnit(packageName, TEMPLATE_CLASS, cu);
    }

    public static Object parseInterpretedCompileEvaluate(String feelLiteralExpression) {
        LOG.debug("{}", feelLiteralExpression);
        CompiledFEELExpression compiledExpression = parseInterpreted(feelLiteralExpression );
        return evaluate(compiledExpression);
    }

    public static CompiledFEELExpression parseInterpreted(String input) {
        return parseInterpreted(input, Collections.emptyMap() );
    }

    public static CompiledFEELExpression parseInterpreted(String input, Map<String, Type> inputTypes) {
        FEEL_1_1Parser parser = FEELParser.parse(null, input, inputTypes, Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);

        ParseTree tree = parser.compilation_unit();

        ASTBuilderVisitor v = new ASTBuilderVisitor(inputTypes, null);
        BaseNode ast = tree.accept(v);
        return new InterpretedExecutableExpression(new CompiledExpressionImpl(ast));
    }

    public static Object evaluate(CompiledFEELExpression compiledExpression) {
        LOG.debug("{}", compiledExpression);
        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        LOG.debug("{}", result);
        return result;
    }

}