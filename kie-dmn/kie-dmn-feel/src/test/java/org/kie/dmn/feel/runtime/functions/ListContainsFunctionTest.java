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

class ListContainsFunctionTest {

    private ListContainsFunction listContainsFunction;

    @BeforeEach
    void setUp() {
        listContainsFunction = new ListContainsFunction();
    }

    @Test
    void invokeListNull() {
        FunctionTestUtil.assertResultError(listContainsFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(listContainsFunction.invoke(null, new Object()), InvalidParametersEvent.class);
    }

    @Test
    void invokeContainsNull() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.singletonList(null), null), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, null), null), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(null, 1), null), true);
    }

    @Test
    void invokeNotContainsNull() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.emptyList(), null), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.singletonList(1), null), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2), null), false);
    }

    @Test
    void invokeContains() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), "test"), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), 1), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), BigDecimal.ONE), true);
    }

    @Test
    void invokeNotContains() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), "testtt"), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), 3), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test"), BigDecimal.valueOf(3)), false);
    }
}