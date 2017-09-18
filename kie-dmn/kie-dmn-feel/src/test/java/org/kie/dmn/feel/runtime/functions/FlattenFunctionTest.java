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
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class FlattenFunctionTest {

    private FlattenFunction flattenFunction;

    @Before
    public void setUp() {
        flattenFunction = new FlattenFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(flattenFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamNotCollection() {
        FunctionTestUtil.assertResult(flattenFunction.invoke(BigDecimal.valueOf(10.2)), Collections.singletonList(BigDecimal.valueOf(10.2)));
        FunctionTestUtil.assertResult(flattenFunction.invoke("test"), Collections.singletonList("test"));
    }

    @Test
    public void invokeParamCollection() {
        FunctionTestUtil.assertResult(flattenFunction.invoke(Arrays.asList("test", 1, 2)), Arrays.asList("test", 1, 2));
        FunctionTestUtil.assertResult(flattenFunction.invoke(Arrays.asList("test", 1, 2, Arrays.asList(3, 4))), Arrays.asList("test", 1, 2, 3, 4));
        FunctionTestUtil.assertResult(flattenFunction.invoke(Arrays.asList("test", 1, 2, Arrays.asList(1, 2))), Arrays.asList("test", 1, 2, 1, 2));
        FunctionTestUtil.assertResult(
                flattenFunction.invoke(
                        Arrays.asList("test", 1, Arrays.asList(BigDecimal.ZERO, 3), 2, Arrays.asList(1, 2))),
                        Arrays.asList("test", 1, BigDecimal.ZERO, 3, 2, 1, 2));

        FunctionTestUtil.assertResult(
                flattenFunction.invoke(
                        Arrays.asList("test", 1, Arrays.asList(Arrays.asList(10, 15), BigDecimal.ZERO, 3), 2, Arrays.asList(1, 2))),
                Arrays.asList("test", 1, 10, 15, BigDecimal.ZERO, 3, 2, 1, 2));
    }
}