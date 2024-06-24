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
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.stmt.BlockStmt;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.parser.feel11.ASTBuilderVisitor;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.util.NumberEvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.codegen.feel11.ProcessedUnaryTest.TEMPLATE_CLASS;
import static org.kie.dmn.feel.codegen.feel11.ProcessedUnaryTest.TEMPLATE_RESOURCE;

public class CodegenFEELUnaryTestsTest {

    public static final Logger LOG = LoggerFactory.getLogger(CodegenFEELUnaryTestsTest.class);

    @Test
    void dash() {
        assertThat(parseCompileEvaluate("-", 1)).containsExactly(Boolean.TRUE);
        assertThat(parseCompileEvaluate("-, -", 1)).isEmpty();
    }

    @Test
    void positive_unary_test_ineq() {
        assertThat(parseCompileEvaluate("<47", 1)).containsExactly(Boolean.TRUE);
        assertThat(parseCompileEvaluate("<47, <100", 1)).containsExactly(Boolean.TRUE, Boolean.TRUE);
        assertThat(parseCompileEvaluate("<47, <100, <-47", 1)).containsExactly(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE);
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 0)).containsExactly(Boolean.FALSE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 1)).containsExactly(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 2)).containsExactly(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE);
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 3)).containsExactly(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 4)).containsExactly(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 5)).containsExactly(Boolean.TRUE, Boolean.TRUE, Boolean.FALSE, Boolean.FALSE);
        assertThat(parseCompileEvaluate("!=1, !=42", 1)).containsExactly(Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    void positive_unary_test_ineq_for_eq() {
        assertThat(parseCompileEvaluate("<47, =1", 1)).containsExactly(Boolean.TRUE, Boolean.TRUE);
        assertThat(parseCompileEvaluate("<47, =47", 1)).containsExactly(Boolean.TRUE, Boolean.FALSE);
        assertThat(parseCompileEvaluate("<47, 1", 1)).containsExactly(Boolean.TRUE, Boolean.TRUE);
        assertThat(parseCompileEvaluate("<47, 47", 1)).containsExactly(Boolean.TRUE, Boolean.FALSE);
    }

    @Test
    void not() {
        assertThat(parseCompileEvaluate("not(=47), not(<1), not(!=1)", 1)).isEmpty();
    }

    @Test
    void simple_unary_test_for_range() {
        assertThat(parseCompileEvaluate("[1..2]", 1)).containsExactly(Boolean.TRUE);
        assertThat(parseCompileEvaluate("[1..2], [2..3]", 1)).containsExactly(Boolean.TRUE, Boolean.FALSE);
        assertThat(parseCompileEvaluate("(1..2], [2..3]", 1)).containsExactly(Boolean.FALSE, Boolean.FALSE);
        assertThat(parseCompileEvaluate("(1..2], [2..3]", 2)).containsExactly(Boolean.TRUE, Boolean.TRUE);
    }

    @Test
    void t2() {
        assertThat(parseCompileEvaluate("\"asd\"", "asd")).containsExactly(Boolean.TRUE);
    }


    private List<Boolean> parseCompileEvaluate(String feelLiteralExpression, Object l) {
        Object left = NumberEvalHelper.coerceNumber(l);
        FEELEventListenersManager mgr = new FEELEventListenersManager();
        SyntaxErrorListener listener = new SyntaxErrorListener();
        mgr.addListener(listener);
        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext(mgr);
        CompiledFEELUnaryTests compiledUnaryTests = parse(feelLiteralExpression, mgr, listener);
        LOG.debug("{}", compiledUnaryTests);
        List<Boolean> result = compiledUnaryTests.getUnaryTests()
                .stream()
                .map(ut -> ut.apply(emptyContext, left))
                .collect(Collectors.toList());
        if (listener.isError()) {
            LOG.debug("{}", listener.event());
            return Collections.emptyList();
        }
        LOG.debug("{}", result);
        return result;
    }

    private CompiledFEELUnaryTests parse(String input, FEELEventListenersManager mgr, SyntaxErrorListener listener) {
        return parse( input, Collections.emptyMap(), mgr, listener );
    }

    private CompiledFEELUnaryTests parse(String input, Map<String, Type> inputTypes, FEELEventListenersManager mgr, SyntaxErrorListener listener) {
        FEEL_1_1Parser parser = FEELParser.parse(mgr, input, inputTypes, Collections.emptyMap(), Collections.emptyList(), Collections.emptyList(), null);

        ParseTree tree = parser.unaryTestsRoot();
        ASTCompilerVisitor compilerVisitor = new ASTCompilerVisitor();
        BlockStmt directCodegenResult;

        if (listener.isError()) {
            directCodegenResult = compilerVisitor.returnError(listener.event().getMessage());
        } else {
            ASTBuilderVisitor v = new ASTBuilderVisitor(inputTypes, null);
            BaseNode node = v.visit(tree);
            BaseNode transformed = node.accept(new ASTUnaryTestTransform()).node();
            directCodegenResult = transformed.accept(compilerVisitor);
        }

        CompilerBytecodeLoader compilerBytecodeLoader = new CompilerBytecodeLoader();
        String packageName = compilerBytecodeLoader.generateRandomPackage();
        CompilationUnit cu = new CompilerBytecodeLoader()
                .getCompilationUnit(
                        TEMPLATE_RESOURCE,
                        packageName,
                        TEMPLATE_CLASS,
                        input,
                        directCodegenResult,
                        compilerVisitor.getLastVariableName());

        return compilerBytecodeLoader.compileUnit(packageName, TEMPLATE_CLASS, cu);
    }
    

}
