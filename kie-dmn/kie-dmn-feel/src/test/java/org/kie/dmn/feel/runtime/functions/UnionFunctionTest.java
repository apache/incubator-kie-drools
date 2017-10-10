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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class UnionFunctionTest {

    private UnionFunction unionFunction;

    @Before
    public void setUp() {
        unionFunction = new UnionFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(unionFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyArray() {
        FunctionTestUtil.assertResultList(unionFunction.invoke(new Object[]{}), Collections.emptyList());
    }

    @Test
    public void invokeSingleObject() {
        FunctionTestUtil.assertResult(unionFunction.invoke(new Object[]{10}), Collections.singletonList(10));
    }

    @Test
    public void invokeSingleObjectInAList() {
        FunctionTestUtil.assertResult(unionFunction.invoke(new Object[]{Collections.singletonList(10)}), Collections.singletonList(10));
    }

    @Test
    public void invokeSingleObjectInAnArray() {
        final int[] testArray = new int[]{10};
        FunctionTestUtil.assertResult(unionFunction.invoke(new Object[]{testArray}), Collections.singletonList(testArray));
    }

    @Test
    public void invokeListIsNull() {
        FunctionTestUtil.assertResultError(unionFunction.invoke(new Object[]{null}), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListContainsNull() {
        // TODO - Ask Edson about this - nulls in list
        FunctionTestUtil.assertResult(unionFunction.invoke(new Object[]{Arrays.asList(null, 10, null)}), Arrays.asList(null, 10));
    }

    @Test
    public void invokeListsNoDuplicates() {
        final Object[] params = new Object[]{Arrays.asList(10, 8, 3), Arrays.asList(1, 15, 2)};
        FunctionTestUtil.assertResultList(unionFunction.invoke(params), Arrays.asList(10, 8, 3, 1, 15, 2));
    }

    @Test
    public void invokeListsSomeDuplicates() {
        final Object[] params = new Object[]{Arrays.asList(10, 8, 3), Arrays.asList(1, 10, 2), Arrays.asList(10, 3, 2, 5)};
        FunctionTestUtil.assertResultList(unionFunction.invoke(params), Arrays.asList(10, 8, 3, 1, 2, 5));
    }

    @Test
    public void invokeListsAllDuplicates() {
        final Object[] params = new Object[]{Arrays.asList(10, 8, 3), Arrays.asList(10, 8, 3), Arrays.asList(3, 10, 8)};
        FunctionTestUtil.assertResultList(unionFunction.invoke(params), Arrays.asList(10, 8, 3));
    }

    @Test
    public void invokeListAndSingleObject() {
        FunctionTestUtil.assertResultList(unionFunction.invoke(new Object[]{Arrays.asList(10, 4, 5), 1}), Arrays.asList(10, 4, 5, 1));
    }

    @Test
    public void invokeListAndSingleObjectWithDuplicates() {
        FunctionTestUtil.assertResultList(unionFunction.invoke(new Object[]{5, Arrays.asList(10, 4, 5), 10}), Arrays.asList(5, 10, 4));
    }

    @Test
    public void invokeMixedTypes() {
        FunctionTestUtil.assertResultList(
                unionFunction.invoke(new Object[]{"test", Arrays.asList(10, "test", 5), BigDecimal.TEN}),
                Arrays.asList("test", 10, 5, BigDecimal.TEN));
    }
}