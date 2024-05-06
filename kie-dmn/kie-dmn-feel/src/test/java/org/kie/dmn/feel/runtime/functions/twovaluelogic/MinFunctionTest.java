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
package org.kie.dmn.feel.runtime.functions.twovaluelogic;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

class MinFunctionTest {

    private NNMinFunction minFunction;

    @BeforeEach
    void setUp() {
        minFunction = new NNMinFunction();
    }

    @Test
    void invokeNullList() {
        FunctionTestUtil.assertResult(minFunction.invoke((List) null), null);
    }

    @Test
    void invokeEmptyList() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    void invokeListWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(minFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(10.2))), InvalidParametersEvent.class);
    }

    @Test
    void invokeListOfIntegers() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.singletonList(1)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(null, 1, 2, 3)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(2, null, 1, 3)), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(2, 3, 1, null )), 1);
    }

    @Test
    void invokeListOfStrings() {
        FunctionTestUtil.assertResult(minFunction.invoke(Collections.singletonList("a")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList(null, "a", "b", "c")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("b", "a", null, "c")), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(Arrays.asList("b", "c", "a", null)), "a");
    }

    @Test
    void invokeNullArray() {
        FunctionTestUtil.assertResult(minFunction.invoke((Object[]) null), null);
    }

    @Test
    void invokeEmptyArray() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{}), null);
    }

    @Test
    void invokeArrayWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(minFunction.invoke(new Object[]{1, "test", BigDecimal.valueOf(10.2)}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayOfIntegers() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{1}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{1, 2, 3}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{2, 1, 3}), 1);
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{2, 3, 1}), 1);
    }

    @Test
    void invokeArrayOfStrings() {
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"a"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"a", "b", "c"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"b", "a", "c"}), "a");
        FunctionTestUtil.assertResult(minFunction.invoke(new Object[]{"b", "c", "a"}), "a");
    }
}