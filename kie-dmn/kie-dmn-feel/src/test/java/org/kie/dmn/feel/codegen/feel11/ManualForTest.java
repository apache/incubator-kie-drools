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

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ManualForTest {
    
    public static final Logger LOG = LoggerFactory.getLogger(ManualForTest.class);

    public static class ManualFilterExpression implements CompiledFEELExpression {

        public static final java.math.BigDecimal K_1 = new java.math.BigDecimal(1, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_2 = new java.math.BigDecimal(2, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_3 = new java.math.BigDecimal(3, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_10 = new java.math.BigDecimal(10, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_20 = new java.math.BigDecimal(20, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_30 = new java.math.BigDecimal(30, java.math.MathContext.DECIMAL128);

        /**   FEEL: for x in [ 10, 20, 30 ], y in [ 1, 2, 3 ] return x * y   */
        @Override
        public Object apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.ffor(feelExprCtx)
                                      .with(c -> "x", c -> Arrays.asList(K_10, K_20, K_30))
                                      .with(c -> "y", c -> Arrays.asList(K_1, K_2, K_3))
                                      .rreturn(c -> ((BigDecimal) feelExprCtx.getValue("x")).multiply((BigDecimal) feelExprCtx.getValue("y")));
        }

    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new ManualFilterExpression();
        LOG.debug("{}", compiledExpression);

        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        LOG.debug("{}", result);

        assertThat(result, is(Arrays.asList(BigDecimal.valueOf(10), BigDecimal.valueOf(20), BigDecimal.valueOf(30),
                                            BigDecimal.valueOf(20), BigDecimal.valueOf(40), BigDecimal.valueOf(60),
                                            BigDecimal.valueOf(30), BigDecimal.valueOf(60), BigDecimal.valueOf(90))));
    }

}
