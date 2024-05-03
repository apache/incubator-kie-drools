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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class InsertBeforeFunctionTest {

    private InsertBeforeFunction insertBeforeFunction;

    @BeforeEach
    void setUp() {
        insertBeforeFunction = new InsertBeforeFunction();
    }

    @Test
    void invokeListNull() {
        FunctionTestUtil.assertResultError(insertBeforeFunction.invoke(null, BigDecimal.ZERO, new Object()), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionNull() {
        FunctionTestUtil.assertResultError(insertBeforeFunction.invoke(Collections.emptyList(), null, new Object()), InvalidParametersEvent.class);
    }

    @Test
    void invokeListPositionNull() {
        FunctionTestUtil.assertResultError(insertBeforeFunction.invoke(null, null, new Object()), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionZero() {
        FunctionTestUtil.assertResultError(insertBeforeFunction.invoke(Collections.emptyList(), BigDecimal.ZERO, new Object()), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionOutsideListBounds() {
        FunctionTestUtil.assertResultError(insertBeforeFunction.invoke(Collections.emptyList(), BigDecimal.ONE, new Object()), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(insertBeforeFunction.invoke(Collections.emptyList(), BigDecimal.valueOf(-1), new Object()), InvalidParametersEvent.class);
    }

    @Test
    void invokeInsertIntoEmptyList() {
        // According to spec, inserting into empty list shouldn't be possible. For inserting into empty list, user should use append() function.
        FunctionTestUtil.assertResultError(insertBeforeFunction.invoke(Collections.emptyList(), BigDecimal.ONE, null), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionPositive() {
        FunctionTestUtil.assertResult(insertBeforeFunction.invoke(Collections.singletonList("test"), BigDecimal.ONE, null), Arrays.asList(null, "test"));
        FunctionTestUtil.assertResult(
                insertBeforeFunction.invoke(Arrays.asList("test", null, BigDecimal.ZERO), BigDecimal.valueOf(2), "testtt"),
                Arrays.asList("test", "testtt", null, BigDecimal.ZERO));
        FunctionTestUtil.assertResult(
                insertBeforeFunction.invoke(Arrays.asList("test", null, BigDecimal.ZERO), BigDecimal.valueOf(3), "testtt"),
                Arrays.asList("test", null, "testtt", BigDecimal.ZERO));
    }

    @Test
    void invokePositionNegative() {
        FunctionTestUtil.assertResult(insertBeforeFunction.invoke(Collections.singletonList("test"), BigDecimal.valueOf(-1), null), Arrays.asList(null, "test"));
        FunctionTestUtil.assertResult(
                insertBeforeFunction.invoke(Arrays.asList("test", null, BigDecimal.ZERO), BigDecimal.valueOf(-2), "testtt"),
                Arrays.asList("test", "testtt", null, BigDecimal.ZERO));
        FunctionTestUtil.assertResult(
                insertBeforeFunction.invoke(Arrays.asList("test", null, BigDecimal.ZERO), BigDecimal.valueOf(-3), "testtt"),
                Arrays.asList("testtt", "test", null, BigDecimal.ZERO));
    }
}