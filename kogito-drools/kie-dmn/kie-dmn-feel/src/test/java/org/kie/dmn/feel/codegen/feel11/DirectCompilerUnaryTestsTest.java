/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.Expression;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.parser.feel11.FEEL_1_1Parser;
import org.kie.dmn.feel.util.EvalHelper;

public class DirectCompilerUnaryTestsTest {
    
    private List<Boolean> parseCompileEvaluate(String feelLiteralExpression, Object l) {
        Object left = EvalHelper.coerceNumber(l);
        CompiledFEELUnaryTests compiledUnaryTests = parse(feelLiteralExpression);
        System.out.println(compiledUnaryTests);
        
        EvaluationContext emptyContext = new EvaluationContextImpl(null);
        List<Boolean> result = compiledUnaryTests.getUnaryTests().stream().map(ut -> ut.apply(emptyContext, left)).collect(Collectors.toList());
        System.out.println(result);
        return result;
    }

    @Test
    public void test_Dash() {
        assertThat(parseCompileEvaluate("-", 1), is(Arrays.asList(true)));
        assertThat(parseCompileEvaluate("-, -", 1), is(Arrays.asList(true, true)));
    }
    
    @Test
    public void test_positiveUnaryTestIneq() {
        assertThat(parseCompileEvaluate("<47", 1), is(Arrays.asList(true)));
        assertThat(parseCompileEvaluate("<47, <100", 1), is(Arrays.asList(true, true)));
        assertThat(parseCompileEvaluate("<47, <100, <-47", 1), is(Arrays.asList(true, true, false)));
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 0), is(Arrays.asList(false, false, true, true)));
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 1), is(Arrays.asList(true, false, true, true)));
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 2), is(Arrays.asList(true, false, true, true)));
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 3), is(Arrays.asList(true, true, false, true)));
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 4), is(Arrays.asList(true, true, false, true)));
        assertThat(parseCompileEvaluate(">=1, >2, <3, <=4", 5), is(Arrays.asList(true, true, false, false)));
    }
    
    @Test
    public void test_positiveUnaryTestIneq_forEQ() {
        assertThat(parseCompileEvaluate("<47, =1", 1), is(Arrays.asList(true, true)));
        assertThat(parseCompileEvaluate("<47, =47", 1), is(Arrays.asList(true, false)));
        assertThat(parseCompileEvaluate("<47, 1", 1), is(Arrays.asList(true, true)));
        assertThat(parseCompileEvaluate("<47, 47", 1), is(Arrays.asList(true, false)));
    }

    @Test
    public void test_simpleUnaryTest_forRANGE() {
        assertThat(parseCompileEvaluate("[1..2]", 1), is(Arrays.asList(true)));
        assertThat(parseCompileEvaluate("[1..2], [2..3]", 1), is(Arrays.asList(true, false)));
        assertThat(parseCompileEvaluate("(1..2], [2..3]", 1), is(Arrays.asList(false, false)));
        assertThat(parseCompileEvaluate("(1..2], [2..3]", 2), is(Arrays.asList(true, true)));
    }

    private CompiledFEELUnaryTests parse(String input) {
        return parse( input, Collections.emptyMap() );
    }

    private CompiledFEELUnaryTests parse(String input, Map<String, Type> inputTypes) {
        FEEL_1_1Parser parser = FEELParser.parse( null, input, inputTypes, Collections.emptyMap() );

        ParseTree tree = parser.expressionList();

        DirectCompilerVisitor v = new DirectCompilerVisitor(inputTypes, true);
        DirectCompilerResult directResult = v.visit(tree);
        
        Expression expr = directResult.getExpression();
        CompiledFEELUnaryTests cu = new CompilerBytecodeLoader().makeFromJPUnaryTestsExpression(input, expr, directResult.getFieldDeclarations());

        return cu;
    }
    

}
