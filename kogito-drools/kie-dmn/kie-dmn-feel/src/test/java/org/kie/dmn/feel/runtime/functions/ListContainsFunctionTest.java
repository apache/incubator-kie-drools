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
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class ListContainsFunctionTest {

    private ListContainsFunction listContainsFunction;

    @Before
    public void setUp() {
        listContainsFunction = new ListContainsFunction();
    }

    @Test
    public void invokeListNull() {
        FunctionTestUtil.assertResultError(listContainsFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(listContainsFunction.invoke(null, new Object()), InvalidParametersEvent.class);
    }

    @Test
    public void invokeContainsNull() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.singletonList(null), null), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, null), null), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(null, 1), null), true);
    }

    @Test
    public void invokeNotContainsNull() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.emptyList(), null), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Collections.singletonList(1), null), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2), null), false);
    }

    @Test
    public void invokeContains() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test", BigDecimal.ONE), "test"), true);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test", BigDecimal.ONE), BigDecimal.ONE), true);
    }

    @Test
    public void invokeNotContains() {
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test", BigDecimal.ONE), "testtt"), false);
        FunctionTestUtil.assertResult(listContainsFunction.invoke(Arrays.asList(1, 2, "test", BigDecimal.ONE), BigDecimal.valueOf(2)), false);
    }
}