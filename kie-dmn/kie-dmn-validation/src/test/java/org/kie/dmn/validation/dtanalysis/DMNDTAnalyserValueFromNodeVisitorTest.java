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
package org.kie.dmn.validation.dtanalysis;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.impl.FEELBuilder;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class DMNDTAnalyserValueFromNodeVisitorTest {

    private final FEEL tFEEL = FEELBuilder.builder().build();

    /**
     * None of these are valid FEEL expression ootb and cannot be used to determine discrete value in the domain
     */
    @Test
    void smokeTest() {
        DMNDTAnalyserValueFromNodeVisitor ut = new DMNDTAnalyserValueFromNodeVisitor(Collections.emptyList());
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("date()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("date and time()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("time()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("number()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("string()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("duration()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("years and months duration()")));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> ut.visit(compile("x()")));
    }

    private FunctionInvocationNode compile(String fnInvFEEL) {
        return (FunctionInvocationNode) tFEEL.processExpression(fnInvFEEL, tFEEL.newCompilerContext()).getInterpreted().getASTNode();
    }
}
