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

import java.util.Arrays;

import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.impl.NamedParameter;

public class ManualNamedFunctionInvocationTest {
    
    static class NamedFunctionExample implements CompiledFEELExpression {
        static final java.math.BigDecimal K_1 = new java.math.BigDecimal(2, java.math.MathContext.DECIMAL128);
        static final String K_s = "FOOBAR";

        /**   FEEL: substring( start position: 2, string: "FOOBAR" )  */
        @Override
        public Object apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.invoke(
                    feelExprCtx,
                    feelExprCtx.getValue("substring"),
                    Arrays.asList(new NamedParameter("start position", K_1),
                                  new NamedParameter("string", K_s)));
        }

    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new NamedFunctionExample();
        System.out.println(compiledExpression);

        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        System.out.println(result);

    }

}
