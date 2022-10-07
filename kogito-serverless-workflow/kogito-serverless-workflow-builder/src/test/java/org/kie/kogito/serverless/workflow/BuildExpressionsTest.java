/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.kogito.serverless.workflow.utils.ExpressionHandlerUtils;

import io.serverlessworkflow.api.Workflow;
import io.serverlessworkflow.api.functions.FunctionDefinition;
import io.serverlessworkflow.api.functions.FunctionDefinition.Type;
import io.serverlessworkflow.api.workflow.Functions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BuildExpressionsTest {
    @Test
    void testReplaceExpr() {
        Workflow flow = mock(Workflow.class);
        FunctionDefinition functionDefinition = new FunctionDefinition();
        functionDefinition.setType(Type.EXPRESSION);
        functionDefinition.setOperation(".pepe");
        functionDefinition.setName("pepe");
        Functions functions = new Functions(Collections.singletonList(functionDefinition));
        when(flow.getFunctions()).thenReturn(functions);

        assertThat(ExpressionHandlerUtils.replaceExpr(flow, "\"fn:pepe\"")).isEqualTo("\"fn:pepe\"");
        assertThat(ExpressionHandlerUtils.replaceExpr(flow, "fn:pepe")).isEqualTo(".pepe");
        assertThat(ExpressionHandlerUtils.replaceExpr(flow, "${fn:pepe}")).isEqualTo(".pepe");
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> ExpressionHandlerUtils.replaceExpr(flow, "${fn:NoPepe}"));
    }
}