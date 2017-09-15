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

public class SublistFunctionTest {

    private SublistFunction sublistFunction;

    @Before
    public void setUp() {
        sublistFunction = new SublistFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(sublistFunction.invoke(null, BigDecimal.ONE), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Collections.emptyList(), null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartZero() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.ZERO), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartOutOfListBounds() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.TEN), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.valueOf(-10)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLengthNegative() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.valueOf(1), BigDecimal.valueOf(-3)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeLengthOutOfListBounds() {
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.ONE, BigDecimal.valueOf(3)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(sublistFunction.invoke(Arrays.asList(1, 2), BigDecimal.valueOf(-1), BigDecimal.valueOf(3)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeStartPositive() {
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, 2, 3), BigDecimal.valueOf(2)), Arrays.asList(2, 3));
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, "test", 3), BigDecimal.valueOf(2)), Arrays.asList("test", 3));
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, "test", 3), BigDecimal.valueOf(2), BigDecimal.ONE), Collections.singletonList("test"));
    }

    @Test
    public void invokeStartNegative() {
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, 2, 3), BigDecimal.valueOf(-2)), Arrays.asList(2, 3));
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, "test", 3), BigDecimal.valueOf(-2)), Arrays.asList("test", 3));
        FunctionTestUtil.assertResult(sublistFunction.invoke(Arrays.asList(1, "test", 3), BigDecimal.valueOf(-2), BigDecimal.ONE), Collections.singletonList("test"));
    }
}