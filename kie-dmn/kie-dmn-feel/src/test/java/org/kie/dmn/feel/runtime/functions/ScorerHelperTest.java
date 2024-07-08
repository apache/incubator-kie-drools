/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.codegen.feel11.CodegenTestUtil;
import org.kie.dmn.feel.lang.EvaluationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.dmn.feel.runtime.functions.ScoreHelper.coercedToVarargsScore;
import static org.kie.dmn.feel.runtime.functions.ScoreHelper.lastParameterNotArrayScore;
import static org.kie.dmn.feel.runtime.functions.ScoreHelper.numberOfParametersScore;

class ScorerHelperTest {

    @Test
    void score() {
        Object[] originalInput = new Object[] { "String" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class };
        Object[] adaptedInput = new Object[] { "String" };
        ScoreHelper.Compares compares = new ScoreHelper.Compares(originalInput, adaptedInput, parameterTypes);
        int retrieved = ScoreHelper.score(compares);
        //     0 (nullCounts)
        //     0 (coercedToVarargs)
        //   100 (lastParameterNotArray)
        //  1000 (typeIdentityOfParameters)
        // 10000 (numberOfParameters)
        assertEquals(11100, retrieved);

        originalInput = new Object[] { "String", "34" };
        parameterTypes = new Class<?>[] { String.class, Integer.class };
        adaptedInput = new Object[] { "String", null };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, parameterTypes);
        retrieved = ScoreHelper.score(compares);
        //    -1 (nullCounts)
        //     0 (coercedToVarargs)
        //     0 (lastParameterNotArray) // 0 because last parameter of adaptedInput is null
        //   500 (typeIdentityOfParameters)
        // 10000 (numberOfParameters)
        assertEquals(10499, retrieved);

        originalInput = new Object[] { "String", "34" };
        parameterTypes = new Class<?>[] { String.class, Integer.class };
        adaptedInput = new Object[] { "String", 34 };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, parameterTypes);
        retrieved = ScoreHelper.score(compares);
        //     0 (nullCounts)
        //     0 (coercedToVarargs)
        //   100 (lastParameterNotArray)
        //   500 (typeIdentityOfParameters)
        // 10000 (numberOfParameters)
        assertEquals(10600, retrieved);

        originalInput = new Object[] { "StringA", "StringB" };
        parameterTypes = new Class<?>[] { String.class, String.class };
        adaptedInput = new Object[] { "StringA", "StringB" };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, parameterTypes);
        retrieved = ScoreHelper.score(compares);
        //     0 (nullCounts)
        //     0 (coercedToVarargs)
        //   100 (lastParameterNotArray)
        //  1000 (typeIdentityOfParameters)
        // 10000 (numberOfParameters)
        assertEquals(11100, retrieved);

        originalInput = new Object[] { "StringA", "StringB" };
        parameterTypes = new Class<?>[] { Object.class.arrayType() };
        adaptedInput = new Object[] { new Object[] {"StringA", "StringB"} };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, parameterTypes);
        retrieved = ScoreHelper.score(compares);
        //     0 (nullCounts)
        //    10 (coercedToVarargs)
        //     0 (lastParameterNotArray)
        //     0 (typeIdentityOfParameters)
        //     0 (numberOfParameters)
        assertEquals( coercedToVarargsScore, retrieved);

    }

    @Test
    void numberOfParameters() {
        Object[] originalInput = new Object[] { "String" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class };
        ScoreHelper.Compares compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        int retrieved = ScoreHelper.numberOfParameters.apply(compares);
        assertEquals(numberOfParametersScore, retrieved);

        originalInput = new Object[] { "String" };
        parameterTypes = new Class<?>[] { Object.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.numberOfParameters.apply(compares);
        assertEquals(numberOfParametersScore, retrieved);

        originalInput = new Object[] { new Object[]{ "String", 34 } };
        parameterTypes = new Class<?>[] { Object.class.arrayType() };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.numberOfParameters.apply(compares);
        assertEquals(numberOfParametersScore, retrieved);

        originalInput = new Object[] { "String", 34 };
        parameterTypes = new Class<?>[] { Object.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.numberOfParameters.apply(compares);
        assertEquals(0, retrieved);

        originalInput = new Object[] { "String", 34 };
        parameterTypes = new Class<?>[] { Object.class.arrayType() };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.numberOfParameters.apply(compares);
        assertEquals(0, retrieved);
    }

    @Test
    void typeIdentityOfParameters() {
        Object[] originalInput = new Object[] { "String" };
        Class<?>[] parameterTypes = new Class<?>[] { String.class };
        ScoreHelper.Compares compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        int retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(1000, retrieved);

        originalInput = new Object[] { "String" };
        parameterTypes = new Class<?>[] { Object.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(500, retrieved);

        originalInput = new Object[] { "String", 34 };
        parameterTypes = new Class<?>[] { String.class, Integer.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(1000, retrieved);

        originalInput = new Object[] { "String", 34 };
        parameterTypes = new Class<?>[] { String.class, Object.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(750, retrieved);

        originalInput = new Object[] { "String", "34" };
        parameterTypes = new Class<?>[] { String.class, Integer.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(500, retrieved);

        originalInput = new Object[] { "StringA", "StringB", 40 };
        parameterTypes = new Class<?>[] { String.class, Integer.class, Integer.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(667, retrieved);

        originalInput = new Object[] { "StringA", "StringB", 40 };
        parameterTypes = new Class<?>[] { String.class, Integer.class, String.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(333, retrieved);

        originalInput = new Object[] { CodegenTestUtil.newEmptyEvaluationContext(), "String" };
        parameterTypes = new Class<?>[] { EvaluationContext.class, String.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(1000, retrieved);

        originalInput = new Object[] { "String" };
        parameterTypes = new Class<?>[] {  Integer.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(0, retrieved);

        originalInput = new Object[] { "String" };
        parameterTypes = new Class<?>[] { EvaluationContext.class, String.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(1000, retrieved);

        originalInput = new Object[] { "String", 34 };
        parameterTypes = new Class<?>[] { EvaluationContext.class, String.class, Integer.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(1000, retrieved);

        originalInput = new Object[] { "String", "34" };
        parameterTypes = new Class<?>[] { EvaluationContext.class, String.class, Object.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(750, retrieved);

        originalInput = new Object[] { "String", "34" };
        parameterTypes = new Class<?>[] { EvaluationContext.class, String.class, Integer.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(500, retrieved);

        originalInput = new Object[] { "String" };
        parameterTypes = new Class<?>[] { EvaluationContext.class, Integer.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(0, retrieved);

        originalInput = new Object[] { CodegenTestUtil.newEmptyEvaluationContext(), "String" };
        parameterTypes = new Class<?>[] { EvaluationContext.class, Integer.class };
        compares = new ScoreHelper.Compares(originalInput, null, parameterTypes);
        retrieved = ScoreHelper.typeIdentityOfParameters.apply(compares);
        assertEquals(0, retrieved);
    }

    @Test
    void lastParameterNotArray(){
        Object[] adaptedInput = new Object[] { "String" };
        ScoreHelper.Compares compares = new ScoreHelper.Compares(null, adaptedInput, null);
        int retrieved = ScoreHelper.lastParameterNotArray.apply(compares);
        assertEquals(lastParameterNotArrayScore, retrieved);

        adaptedInput = new Object[] { "String", 34, new Object() };
        compares = new ScoreHelper.Compares(null, adaptedInput, null);
        retrieved = ScoreHelper.lastParameterNotArray.apply(compares);
        assertEquals(lastParameterNotArrayScore, retrieved);

        adaptedInput = new Object[] { new Object[]{} };
        compares = new ScoreHelper.Compares(null, adaptedInput, null);
        retrieved = ScoreHelper.lastParameterNotArray.apply(compares);
        assertEquals(0, retrieved);

        adaptedInput = new Object[] { "String", 34,  new Object[]{} };
        compares = new ScoreHelper.Compares(null, adaptedInput, null);
        retrieved = ScoreHelper.lastParameterNotArray.apply(compares);
        assertEquals(0, retrieved);
    }

    @Test
    void coercedToVarargs() {
        Object[] originalInput = new Object[] { "String" };
        Object[] adaptedInput = new Object[] { "String" };
        ScoreHelper.Compares compares = new ScoreHelper.Compares(originalInput, adaptedInput, null);
        int retrieved = ScoreHelper.coercedToVarargs.apply(compares);
        assertEquals(0, retrieved);

        originalInput = new Object[] { "String" };
        adaptedInput = new Object[] { new Object[] {"String"} };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, null);
        retrieved = ScoreHelper.coercedToVarargs.apply(compares);
        assertEquals(coercedToVarargsScore, retrieved);

        originalInput = new Object[] { "String", 34 };
        adaptedInput = new Object[] { new Object[] {"String", 34} };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, null);
        retrieved = ScoreHelper.coercedToVarargs.apply(compares);
        assertEquals(coercedToVarargsScore, retrieved);

        originalInput = new Object[] { CodegenTestUtil.newEmptyEvaluationContext(), "String", 34 };
        adaptedInput = new Object[] { new Object[] {"String", 34} };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, null);
        retrieved = ScoreHelper.coercedToVarargs.apply(compares);
        assertEquals(coercedToVarargsScore, retrieved);

        originalInput = new Object[] { "String", 34 };
        adaptedInput = new Object[] { CodegenTestUtil.newEmptyEvaluationContext(), new Object[] {"String", 34} };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, null);
        retrieved = ScoreHelper.coercedToVarargs.apply(compares);
        assertEquals(coercedToVarargsScore, retrieved);

        originalInput = new Object[] { CodegenTestUtil.newEmptyEvaluationContext(), "String", 34 };
        adaptedInput = new Object[] { CodegenTestUtil.newEmptyEvaluationContext(), new Object[] {"String", 34} };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, null);
        retrieved = ScoreHelper.coercedToVarargs.apply(compares);
        assertEquals(coercedToVarargsScore, retrieved);

        originalInput = new Object[] { new Object[] {"String", 34} };
        adaptedInput = new Object[] { new Object[] {"String", 34} };
        compares = new ScoreHelper.Compares(originalInput, adaptedInput, null);
        retrieved = ScoreHelper.coercedToVarargs.apply(compares);
        assertEquals(0, retrieved);
    }

    @Test
    void nullCounts() {
        Object[] adaptedInput = new Object[] { "String" };
        ScoreHelper.Compares compares = new ScoreHelper.Compares(null, adaptedInput, null);
        int retrieved = ScoreHelper.nullCounts.apply(compares);
        assertEquals(0, retrieved);

        adaptedInput = new Object[] {  };
        compares = new ScoreHelper.Compares(null, adaptedInput, null);
        retrieved = ScoreHelper.nullCounts.apply(compares);
        assertEquals(0, retrieved);

        adaptedInput = new Object[] { "String", null };
        compares = new ScoreHelper.Compares(null, adaptedInput, null);
        retrieved = ScoreHelper.nullCounts.apply(compares);
        assertEquals(-1, retrieved);

        adaptedInput = new Object[] { null };
        compares = new ScoreHelper.Compares(null, adaptedInput, null);
        retrieved = ScoreHelper.nullCounts.apply(compares);
        assertEquals(-1, retrieved);

        adaptedInput = new Object[] { null, new Object(), null };
        compares = new ScoreHelper.Compares(null, adaptedInput, null);
        retrieved = ScoreHelper.nullCounts.apply(compares);
        assertEquals(-2, retrieved);
    }

    @Test
    void nullCount() {
        Random random = new Random();
        int elements = random.nextInt(10);
        Object[] params = new Object[elements];
        int expectedCount = 0;
        for (int i = 0; i < elements; i++) {
            if (random.nextBoolean()) {
                params[i] = null;
                expectedCount++;
            } else {
                params[i] = new Object();
            }
        }
        assertEquals(expectedCount, ScoreHelper.nullCount(params));
    }
}