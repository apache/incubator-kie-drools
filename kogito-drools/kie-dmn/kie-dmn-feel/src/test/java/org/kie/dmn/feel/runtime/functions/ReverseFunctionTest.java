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

public class ReverseFunctionTest {

    private ReverseFunction reverseFunction;

    @Before
    public void setUp() {
        reverseFunction = new ReverseFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(reverseFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyList() {
        FunctionTestUtil.assertResultList(reverseFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    public void invokeListTypeHomogenous() {
        FunctionTestUtil.assertResultList(reverseFunction.invoke(Arrays.asList(1, 2, 3, 4)), Arrays.asList(4, 3, 2, 1));
    }

    @Test
    public void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultList(
                reverseFunction.invoke(Arrays.asList(1, "test", BigDecimal.TEN, Collections.emptyList())),
                Arrays.asList(Collections.emptyList(), BigDecimal.TEN, "test", 1));

        FunctionTestUtil.assertResultList(
                reverseFunction.invoke(Arrays.asList(1, "test", BigDecimal.TEN, Arrays.asList(1, 2, 3))),
                Arrays.asList(Arrays.asList(1, 2, 3), BigDecimal.TEN, "test", 1));
    }
}