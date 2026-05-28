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
package org.drools.mvel;

import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.Evaluator;
import org.drools.compiler.rule.builder.EvaluatorWrapper;
import org.drools.core.common.DefaultFactHandle;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;

class ASMConditionEvaluatorJitterTest {

    @Test
    void jitWithNonEmptyOperators() throws Exception {
        // incubator-kie-drools#6711
        // ASMConditionEvaluatorJitter.jitOperators() must generate bytecode calling
        // EvaluatorHelper.initOperators with BaseTuple.class (not Tuple.class) as
        // the second parameter, matching the actual method signature.
        java.lang.reflect.Constructor<ConditionAnalyzer.FixedValueCondition> ctor =
                ConditionAnalyzer.FixedValueCondition.class.getDeclaredConstructor(boolean.class);
        ctor.setAccessible(true);
        ConditionAnalyzer.Condition condition = ctor.newInstance(true);

        Declaration[] declarations = new Declaration[0];
        Evaluator mockEvaluator = mock(Evaluator.class);
        EvaluatorWrapper[] operators = new EvaluatorWrapper[]{ new EvaluatorWrapper(mockEvaluator, null, null) };

        ConditionEvaluator evaluator = ASMConditionEvaluatorJitter.jitEvaluator(
                "true", condition, declarations, operators, getClass().getClassLoader(), null);

        assertThatNoException().isThrownBy(() ->
                evaluator.evaluate(new DefaultFactHandle(1, new Object()), null, null));
    }
}
