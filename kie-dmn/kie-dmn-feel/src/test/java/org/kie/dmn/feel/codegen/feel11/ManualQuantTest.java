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
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode.Quantifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.gt;

public class ManualQuantTest {

    public static final Logger LOG = LoggerFactory.getLogger(ManualQuantTest.class);

    public static class ManualFilterExpression implements CompiledFEELExpression {

        public static final java.math.BigDecimal K_80 = new java.math.BigDecimal(80, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_11 = new java.math.BigDecimal(11, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_100 = new java.math.BigDecimal(100, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_110 = new java.math.BigDecimal(110, java.math.MathContext.DECIMAL128);

        /**   FEEL: some price in [ 80, 11, 110 ] satisfies price > 100   */
        @Override
        public Object apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.quant(Quantifier.SOME, feelExprCtx)
                                      .with(c -> "price", c -> Arrays.asList(K_80, K_11, K_110))
                                      .satisfies(c -> gt(feelExprCtx.getValue("price"), K_100));
        }

    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new ManualFilterExpression();
        LOG.debug("{}", compiledExpression);

        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        LOG.debug("{}", result);

        assertThat(result, is(true));
    }

}
