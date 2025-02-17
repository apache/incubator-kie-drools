/*
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
package org.kie.dmn.core.compiler;

import java.io.File;
import java.util.Collections;

import org.drools.util.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DMNConditionalEvaluator;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.impl.FEELImpl;
import org.kie.dmn.model.api.Conditional;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.v1_5.TLiteralExpression;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DMNEvaluatorCompilerTest {

    private static final FEELDialect DEFAULT_FEEL_DIALECT = FEELDialect.FEEL;
    private static Conditional expression;
    private static DMNCompilerContext DMN_COMPILER_CONTEXT;
    private static DMNEvaluatorCompiler dmnEvaluatorCompiler;
    private static DMNFEELHelper DMN_FEEL_HELPER;

    @BeforeAll
    static void setUp() {
        DMN_FEEL_HELPER = new DMNFEELHelper(Collections.emptyList(), DEFAULT_FEEL_DIALECT);
        DMN_COMPILER_CONTEXT = new DMNCompilerContext(DMN_FEEL_HELPER);

        DMNCompilerImpl compiler = new DMNCompilerImpl();
        dmnEvaluatorCompiler = new DMNEvaluatorCompiler(compiler);
    }

    @Test
    void getFEELDialectAdaptedFEELNoExpressionLanguage() {
        String expressionLanguage = null;
        LiteralExpression expression = getLiteralExpression(expressionLanguage);
        FEEL retrieved =  DMNEvaluatorCompiler.getFEELDialectAdaptedFEEL(DMN_COMPILER_CONTEXT, expression, expressionLanguage);
        assertThat(retrieved).isNotNull().isInstanceOf(FEELImpl.class);
        assertThat(((FEELImpl)retrieved).getFeelDialect()).isEqualTo(DEFAULT_FEEL_DIALECT);
    }

    @Test
    void getFEELDialectAdaptedFEELFEELURIExpressionLanguage() {
        LiteralExpression expression = getLiteralExpression(null);
        String expressionLanguage = expression.getURIFEEL();
        FEEL retrieved =  DMNEvaluatorCompiler.getFEELDialectAdaptedFEEL(DMN_COMPILER_CONTEXT, expression, expressionLanguage);
        assertThat(retrieved).isNotNull().isInstanceOf(FEELImpl.class);
        assertThat(((FEELImpl)retrieved).getFeelDialect()).isEqualTo(DEFAULT_FEEL_DIALECT);
    }

    @Test
    void getFEELDialectAdaptedFEELBFEELExpressionLanguage() {
        String expressionLanguage = FEELDialect.BFEEL.getNamespace();
        LiteralExpression expression = getLiteralExpression(expressionLanguage);
        FEEL retrieved =  DMNEvaluatorCompiler.getFEELDialectAdaptedFEEL(DMN_COMPILER_CONTEXT, expression, expressionLanguage);
        assertThat(retrieved).isNotNull().isInstanceOf(FEELImpl.class);
        assertThat(((FEELImpl)retrieved).getFeelDialect()).isEqualTo(FEELDialect.BFEEL);
    }

    @Test
    void getFEELDialectAdaptedFEELWrongExpressionLanguage() {
        String expressionLanguage = "something-else";
        LiteralExpression expression = getLiteralExpression(expressionLanguage);
        String expectedMessage = String.format("Unsupported FEEL language '%s'; allowed values are `null`, %s, %s", expressionLanguage, expression.getURIFEEL(), FEELDialect.BFEEL.getNamespace());
        assertThatThrownBy(() -> DMNEvaluatorCompiler.getFEELDialectAdaptedFEEL(DMN_COMPILER_CONTEXT, expression, expressionLanguage))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    private LiteralExpression getLiteralExpression(String expressionLanguage) {
        LiteralExpression toReturn = new TLiteralExpression();
        toReturn.setExpressionLanguage(expressionLanguage);
        return toReturn;
    }

    @Test
    void testCompileConditional() {
        String exprName = "testExpression";
        File modelFile = FileUtils.getFile("ConditionalEvent.dmn");
        Resource modelResource = ResourceFactory.newFileResource(modelFile);
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        assertThat(dmnRuntime).isNotNull();
        String nameSpace = "https://kie.org/dmn/_5B448C78-0DBF-4554-92A4-8C0247EB01FD";

        final DMNModel dmnModel = dmnRuntime.getModel(nameSpace, "DMN_00DF4B93-0243-4813-BA70-A1894AC723BE");
        assertThat(dmnModel).isNotNull();
        DMNBaseNode dmnBaseNode = getNodeByName(dmnModel, "B");
        DecisionNode decisionNode = (DecisionNode) dmnBaseNode;
        if (decisionNode.getDecision().getExpression() instanceof Conditional conditional) {
            expression.setIf(conditional.getIf());
            expression.setElse(conditional.getElse());
            expression.setThen(conditional.getThen());

            DMNExpressionEvaluator result = dmnEvaluatorCompiler.compileConditional(DMN_COMPILER_CONTEXT, (DMNModelImpl) dmnModel, dmnBaseNode, exprName, expression);

            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(DMNConditionalEvaluator.class);
        }

    }

    private DMNBaseNode getNodeByName(DMNModel dmnModel, String nodeName) {
        return (DMNBaseNode) dmnModel.getDecisions().stream()
                .filter(node -> node.getName().equals(nodeName))
                .findFirst().orElse(null);
    }
}