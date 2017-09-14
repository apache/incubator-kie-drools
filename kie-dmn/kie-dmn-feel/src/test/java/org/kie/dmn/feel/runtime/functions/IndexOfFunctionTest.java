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

public class IndexOfFunctionTest {

    private IndexOfFunction indexOfFunction;

    @Before
    public void setUp() {
        indexOfFunction = new IndexOfFunction();
    }

    @Test
    public void invokeListNull() {
        FunctionTestUtil.assertResultError(indexOfFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(indexOfFunction.invoke(null, new Object()), InvalidParametersEvent.class);
    }

    @Test
    public void invokeMatchNull() {
        FunctionTestUtil.assertResultList(indexOfFunction.invoke(Collections.emptyList(), null), Collections.emptyList());
        FunctionTestUtil.assertResultList(indexOfFunction.invoke(Collections.singletonList("test"), null), Collections.emptyList());
        FunctionTestUtil.assertResultList(
                indexOfFunction.invoke(Arrays.asList("test", null), null),
                Collections.singletonList(BigDecimal.valueOf(2)));
        FunctionTestUtil.assertResultList(
                indexOfFunction.invoke(Arrays.asList(null, "test"), null),
                Collections.singletonList(BigDecimal.ONE));
        FunctionTestUtil.assertResultList(
                indexOfFunction.invoke(Arrays.asList("test", null, BigDecimal.ZERO), null),
                Collections.singletonList(BigDecimal.valueOf(2)));
        FunctionTestUtil.assertResultList(
                indexOfFunction.invoke(Arrays.asList("test", null, null, BigDecimal.ZERO), null),
                Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(3)));
    }

    @Test
    public void invokeBigDecimal() {
        FunctionTestUtil.assertResult(indexOfFunction.invoke(Arrays.asList("test", null, 12), BigDecimal.valueOf(12)), Collections.emptyList());
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(Arrays.asList("test", null, BigDecimal.valueOf(12)), BigDecimal.valueOf(12)),
                Collections.singletonList(BigDecimal.valueOf(3)));
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(
                        Arrays.asList("test", null, BigDecimal.valueOf(12)),
                        BigDecimal.valueOf(12).setScale(4, BigDecimal.ROUND_HALF_UP)),
                Collections.singletonList(BigDecimal.valueOf(3)));
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(
                        Arrays.asList(BigDecimal.valueOf(12.00), "test", null, BigDecimal.valueOf(12)),
                        BigDecimal.valueOf(12)),
                Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(4)));
    }

    @Test
    public void invokeMatchNotNull() {
        FunctionTestUtil.assertResult(indexOfFunction.invoke(Arrays.asList("test", null, 12), "testttt"), Collections.emptyList());
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(Arrays.asList("test", null, BigDecimal.valueOf(12)), "test"),
                Collections.singletonList(BigDecimal.valueOf(1)));
        FunctionTestUtil.assertResult(
                indexOfFunction.invoke(Arrays.asList("test", null, "test"),"test"),
                Arrays.asList(BigDecimal.valueOf(1), BigDecimal.valueOf(3)));
    }
}