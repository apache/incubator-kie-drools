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
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.util.EvalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.lt;

public class ManualUnaryTestsTest {

    public static final Logger LOG = LoggerFactory.getLogger(ManualUnaryTestsTest.class);

    public static class ManualImpl1 implements CompiledFEELUnaryTests {

        private static final UnaryTest UT_a = (feelExprCtx, left) -> lt(left, new BigDecimal(47, MathContext.DECIMAL128));

        private static final UnaryTest UT_b = (feelExprCtx, left) -> lt(left, new BigDecimal(1, MathContext.DECIMAL128));

        @Override
        public List<UnaryTest> getUnaryTests() {
            return Arrays.asList(UT_a, UT_b);
        }

    }

    @Test
    public void testManualUnaryTests() {
        Object left = EvalHelper.coerceNumber(7);

        CompiledFEELUnaryTests compiledUnaryTests = new ManualImpl1();
        LOG.debug("{}", compiledUnaryTests);

        EvaluationContext emptyContext = CodegenTestUtil.newEmptyEvaluationContext();
        List<Boolean> result = compiledUnaryTests.getUnaryTests().stream().map(ut -> ut.apply(emptyContext, left)).collect(Collectors.toList());
        LOG.debug("{}", result);

        assertThat(result).asList().containsExactly(true, false);
    }

}
