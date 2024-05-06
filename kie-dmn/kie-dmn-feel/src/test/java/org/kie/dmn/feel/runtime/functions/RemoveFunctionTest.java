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
package org.kie.dmn.feel.runtime.functions;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class RemoveFunctionTest {

    private RemoveFunction removeFunction;

    @BeforeEach
    void setUp() {
        removeFunction = new RemoveFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(removeFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(null, BigDecimal.ONE), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.emptyList(), null), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionZero() {
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.ZERO), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionOutOfListBounds() {
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(2)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(154)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(-2)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(-154)), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionPositive() {
        FunctionTestUtil.assertResultList(removeFunction.invoke(Collections.singletonList(1), BigDecimal.ONE), Collections.emptyList());
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.ONE),
                Arrays.asList("test", BigDecimal.valueOf(14)));
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(2)),
                Arrays.asList(1, BigDecimal.valueOf(14)));
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(3)),
                Arrays.asList(1, "test"));
    }

    @Test
    void invokePositionNegative() {
        FunctionTestUtil.assertResultList(removeFunction.invoke(Collections.singletonList(1), BigDecimal.valueOf(-1)), Collections.emptyList());
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(-1)),
                Arrays.asList(1, "test"));
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(-2)),
                Arrays.asList(1, BigDecimal.valueOf(14)));
        FunctionTestUtil.assertResultList(
                removeFunction.invoke(Arrays.asList(1, "test", BigDecimal.valueOf(14)), BigDecimal.valueOf(-3)),
                Arrays.asList("test", BigDecimal.valueOf(14)));
    }
}