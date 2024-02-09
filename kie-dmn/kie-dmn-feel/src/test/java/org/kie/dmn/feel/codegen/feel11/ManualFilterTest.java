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

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.gt;

public class ManualFilterTest {

    public static final Logger LOG = LoggerFactory.getLogger(ManualFilterTest.class);
    
    public static class ManualFilterExpression implements CompiledFEELExpression {

        public static final java.math.BigDecimal K_1 = new java.math.BigDecimal(1, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_2 = new java.math.BigDecimal(2, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_3 = new java.math.BigDecimal(3, java.math.MathContext.DECIMAL128);

        public static final java.math.BigDecimal K_4 = new java.math.BigDecimal(4, java.math.MathContext.DECIMAL128);

        /**   FEEL: [1, 2, 3, 4][item > 2]   */
        @Override
        public Object apply(EvaluationContext feelExprCtx) {
            return CompiledFEELSupport.filter(feelExprCtx, java.util.Arrays.asList(K_1, K_2, K_3, K_4)).with(new java.util.function.Function<EvaluationContext, Object>() {

                @Override
                public Object apply(EvaluationContext feelExprCtx) {
                    return gt(feelExprCtx.getValue("item"), K_2);
                }
            });
        }

    }

    @Test
    public void testManualContext() {
        CompiledFEELExpression compiledExpression = new ManualFilterExpression();
        LOG.debug("{}", compiledExpression);

        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        Object result = compiledExpression.apply(emptyContext);
        LOG.debug("{}", result);

        assertThat(result).asList().containsExactly(BigDecimal.valueOf(3), BigDecimal.valueOf(4));
    }

}
