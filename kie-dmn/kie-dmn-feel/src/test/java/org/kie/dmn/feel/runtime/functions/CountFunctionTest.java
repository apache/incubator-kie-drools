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
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class CountFunctionTest {

    private CountFunction countFunction;

    @Before
    public void setUp() {
        countFunction = new CountFunction();
    }

    @Test
    public void invokeParamListNull() {
        FunctionTestUtil.assertResultError(countFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamListEmpty() {
        FunctionTestUtil.assertResult(countFunction.invoke(new ArrayList<>()), BigDecimal.ZERO);
    }

    @Test
    public void invokeParamListNonEmpty() {
        FunctionTestUtil.assertResult(countFunction.invoke(Arrays.asList(1, 2, "test")), BigDecimal.valueOf(3));
    }

    @Test
    public void invokeParamArrayNull() {
        FunctionTestUtil.assertResultError(countFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamArrayEmpty() {
        FunctionTestUtil.assertResult(countFunction.invoke(new Object[]{}), BigDecimal.ZERO);
    }

    @Test
    public void invokeParamArrayNonEmpty() {
        FunctionTestUtil.assertResult(countFunction.invoke(new Object[]{1, 2, "test"}), BigDecimal.valueOf(3));
    }

}