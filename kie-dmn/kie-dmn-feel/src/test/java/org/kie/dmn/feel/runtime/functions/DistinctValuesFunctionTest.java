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

class DistinctValuesFunctionTest {

    private DistinctValuesFunction distinctValuesFunction;

    @BeforeEach
    void setUp() {
        distinctValuesFunction = new DistinctValuesFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(distinctValuesFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamNotCollection() {
        FunctionTestUtil.assertResultList(
                distinctValuesFunction.invoke(BigDecimal.valueOf(10.1)),
                Collections.singletonList(BigDecimal.valueOf(10.1)));
    }

    @Test
    void invokeParamArray() {
        FunctionTestUtil.assertResultList(
                distinctValuesFunction.invoke(new Object[]{BigDecimal.valueOf(10.1)}),
                Collections.singletonList(new Object[]{BigDecimal.valueOf(10.1)}));
    }

    @Test
    void invokeEmptyList() {
        FunctionTestUtil.assertResultList(distinctValuesFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    void invokeList() {
        final List testValues = Arrays.asList(1, BigDecimal.valueOf(10.1), "test", 1, "test", BigDecimal.valueOf(10.1));
        FunctionTestUtil.assertResultList(
                distinctValuesFunction.invoke(testValues),
                Arrays.asList(1, BigDecimal.valueOf(10.1), "test"));
    }
}