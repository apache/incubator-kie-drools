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
package org.kie.kogito.serverless.workflow.utils;

import java.util.Collections;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.kogito.serverless.workflow.test.MockBuilder;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils.trimExpr;
import static org.mockito.Mockito.when;

public class ExpressionHandlerUtilsTest {

    @Test
    void testTrimExpression() {
        assertThat(trimExpr("${.pepe}")).isEqualTo(".pepe");
        assertThat(trimExpr("${ {name:.pepe} }")).isEqualTo("{name:.pepe}");
    }

    @ParameterizedTest(name = "{index} \"{0}\" is resolved to \"{1}\"")
    @MethodSource("provideExpressionsToTestWithWorkflow")
    public void testPrepareExpressionFromContextAndWorkflow(String expr, String result, Workflow workflow) {
        String resolvedExpr = ExpressionHandlerUtils.replaceExpr(workflow, expr);

        assertThat(resolvedExpr).isEqualTo(result);
    }

    private static Stream<Arguments> provideExpressionsToTestWithWorkflow() {
        return Stream.of(
                /* expression, expected, workflow */
                Arguments.of("$WORKFLOW.id", "$WORKFLOW.id", getWorkflow()),
                Arguments.of("$WORKFLOW.instanceId", "$WORKFLOW.instanceId", getWorkflow()),
                Arguments.of("$WORKFLOW.name", "$WORKFLOW.name", getWorkflow()),
                Arguments.of("$CONST.one", "$CONST.one", getWorkflow()),
                Arguments.of("$CONST.nonexistent and $CONST.one", "$CONST.nonexistent and $CONST.one", getWorkflow()),
                Arguments.of("$CONST.some.nested", "$CONST.some.nested", getWorkflow()),
                Arguments.of("$CONST.some-key", "$CONST.some-key", getWorkflow()),
                Arguments.of("$CONST.\"some-key\"", "$CONST.\"some-key\"", getWorkflow()),
                Arguments.of("$CONST.\"unique-key\"", "$CONST.\"unique-key\"", getWorkflow()),
                Arguments.of("$CONST.injectedsecret", "$CONST.injectedsecret", getWorkflow()),
                Arguments.of("$CONST.injectedworkflow", "$CONST.injectedworkflow", getWorkflow()),
                Arguments.of("$SECRET.lettersonly", "$SECRET.lettersonly", getWorkflow()),
                Arguments.of("$SECRET.underscore_secret", "$SECRET.underscore_secret", getWorkflow()),
                Arguments.of("$SECRET.dot.secret", "$SECRET.dot.secret", getWorkflow()),
                Arguments.of("$SECRET.\"dot.secret\"", "$SECRET.\"dot.secret\"", getWorkflow()),
                Arguments.of("$SECRET.dash-secret", "$SECRET.dash-secret", getWorkflow()),
                Arguments.of("$SECRET.\"dash-secret\"", "$SECRET.\"dash-secret\"", getWorkflow()),
                Arguments.of("fn:expression-workflow-id", "$WORKFLOW.id", getWorkflow()),
                Arguments.of("fn:expression.constant", "$CONST.some.nested", getWorkflow()),
                Arguments.of("fn:expression_secret", "$SECRET.lettersonly", getWorkflow()),
                Arguments.of("something ${ fn:expression.constant }", "something ${ fn:expression.constant }", getWorkflow()),
                Arguments.of("${ fn:expression.constant }", "$CONST.some.nested", getWorkflow()),
                Arguments.of("{{ fn:expression.constant }}", "$CONST.some.nested", getWorkflow()));
    }

    private static Workflow getWorkflow() {
        Workflow wfl = MockBuilder.workflow()
                .withConstants(Collections.singletonMap("one", "value"))
                .withConstants(Collections.singletonMap("some-key", "value"))
                .withConstants(Collections.singletonMap("unique-key", "value"))
                .withConstants(Collections.singletonMap("injectedsecret", "$SECRET.dash"))
                .withConstants(Collections.singletonMap("injectedworkflow", "$WORKFLOW.id"))
                .withConstants(Collections.singletonMap("some.nested", "value2"))
                .withConstants(Collections.singletonMap("some", Collections.singletonMap("nested", "value")))
                .withFunctionDefinitionMock(fd -> {
                    when(fd.getType()).thenReturn(FunctionDefinition.Type.EXPRESSION);
                    when(fd.getOperation()).thenReturn("$WORKFLOW.id");
                    when(fd.getName()).thenReturn("expression-workflow-id");
                })
                .withFunctionDefinitionMock(fd -> {
                    when(fd.getType()).thenReturn(FunctionDefinition.Type.EXPRESSION);
                    when(fd.getOperation()).thenReturn("$SECRET.lettersonly");
                    when(fd.getName()).thenReturn("expression_secret");
                })
                .withFunctionDefinitionMock(fd -> {
                    when(fd.getType()).thenReturn(FunctionDefinition.Type.EXPRESSION);
                    when(fd.getOperation()).thenReturn("$CONST.some.nested");
                    when(fd.getName()).thenReturn("expression.constant");
                })
                .build();
        return wfl;
    }

}
