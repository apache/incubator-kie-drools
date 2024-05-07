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

class ConcatenateFunctionTest {

    private ConcatenateFunction concatenateFunction;

    @BeforeEach
    void setUp() {
        concatenateFunction = new ConcatenateFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(concatenateFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    void invokeEmptyArray() {
        FunctionTestUtil.assertResultList(concatenateFunction.invoke(new Object[]{}), Collections.emptyList());
    }

    @Test
    void invokeArrayWithNull() {
        FunctionTestUtil.assertResultError(concatenateFunction.invoke(new Object[]{null}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(concatenateFunction.invoke(new Object[]{1, null}), InvalidParametersEvent.class);
    }

    @Test
    void invokeArrayWithList() {
        FunctionTestUtil.assertResultList(concatenateFunction.invoke(new Object[]{"test", 2, Arrays.asList(2, 3)}), Arrays.asList("test", 2, 2, 3));
    }

    @Test
    void invokeArrayWithoutList() {
        FunctionTestUtil.assertResultList(concatenateFunction.invoke(new Object[]{"test", 2, BigDecimal.valueOf(25.3)}), Arrays.asList("test", 2, BigDecimal.valueOf(25.3)));
    }

}