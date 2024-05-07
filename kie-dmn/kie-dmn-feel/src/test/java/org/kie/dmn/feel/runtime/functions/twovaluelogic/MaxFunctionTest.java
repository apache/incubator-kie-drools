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

class MaxFunctionTest {

    private NNMaxFunction maxFunction;

    @BeforeEach
    void setUp() {
        maxFunction = new NNMaxFunction();
    }

    @Test
    void invokeNullList() {
        FunctionTestUtil.assertResult(maxFunction.invoke((List) null), null);
    }

    @Test
    void invokeEmptyList() {
        FunctionTestUtil.assertResult(maxFunction.invoke(Collections.emptyList()), null);
    }

    @Test
    void invokeListWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(maxFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(10.2))), InvalidParametersEvent.class);
    }

    @Test
    void invokeListOfIntegers() {
        FunctionTestUtil.assertResult(maxFunction.invoke(Collections.singletonList(1)), 1);
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList(1, 2, 3)), 3);
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList(1, 3, 2)), 3);
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList(3, 1, 2)), 3);
    }

    @Test
    void invokeListOfStrings() {
        FunctionTestUtil.assertResult(maxFunction.invoke(Collections.singletonList("a")), "a");
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList("a", "b", "c")), "c");
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList("a", "c", "b")), "c");
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList("c", "a", "b")), "c");
    }

    @Test
    void invokeNullArray() {
        FunctionTestUtil.assertResult(maxFunction.invoke((Object[]) null), null);
    }

    @Test
    void invokeEmptyArray() {
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{}), null);
    }

    @Test
    void invokeArrayWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(maxFunction.invoke(new Object[]{1, "test", BigDecimal.valueOf(10.2)}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayOfIntegers() {
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{1}), 1);
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{null, 1, 2, 3}), 3);
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{1, 3, null, 2}), 3);
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{3, 1, 2, null}), 3);
    }

    @Test
    void invokeArrayOfStrings() {
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{"a"}), "a");
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{null, "a", "b", "c"}), "c");
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{"a", null, "c", "b"}), "c");
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{"c", "a", "b", null}), "c");
    }
}