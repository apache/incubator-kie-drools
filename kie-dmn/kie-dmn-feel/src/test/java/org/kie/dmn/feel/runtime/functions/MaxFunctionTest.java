/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class MaxFunctionTest {

    private MaxFunction maxFunction;

    @Before
    public void setUp() {
        maxFunction = new MaxFunction();
    }

    @Test
    public void invokeNullList() {
        FunctionTestUtil.assertResultError(maxFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyList() {
        FunctionTestUtil.assertResultError(maxFunction.invoke(Collections.emptyList()), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(maxFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(10.2))), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListOfIntegers() {
        FunctionTestUtil.assertResult(maxFunction.invoke(Collections.singletonList(1)), 1);
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList(1, 2, 3)), 3);
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList(1, 3, 2)), 3);
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList(3, 1, 2)), 3);
    }

    @Test
    public void invokeListOfStrings() {
        FunctionTestUtil.assertResult(maxFunction.invoke(Collections.singletonList("a")), "a");
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList("a", "b", "c")), "c");
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList("a", "c", "b")), "c");
        FunctionTestUtil.assertResult(maxFunction.invoke(Arrays.asList("c", "a", "b")), "c");
    }

    @Test
    public void invokeNullArray() {
        FunctionTestUtil.assertResultError(maxFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyArray() {
        FunctionTestUtil.assertResultError(maxFunction.invoke(new Object[]{}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayWithHeterogenousTypes() {
        FunctionTestUtil.assertResultError(maxFunction.invoke(new Object[]{1, "test", BigDecimal.valueOf(10.2)}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeArrayOfIntegers() {
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{1}), 1);
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{1, 2, 3}), 3);
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{1, 3, 2}), 3);
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{3, 1, 2}), 3);
    }

    @Test
    public void invokeArrayOfStrings() {
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{"a"}), "a");
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{"a", "b", "c"}), "c");
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{"a", "c", "b"}), "c");
        FunctionTestUtil.assertResult(maxFunction.invoke(new Object[]{"c", "a", "b"}), "c");
    }
}