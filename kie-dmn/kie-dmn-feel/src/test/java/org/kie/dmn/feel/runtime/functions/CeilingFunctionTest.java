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
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class CeilingFunctionTest {

    private CeilingFunction ceilingFunction;

    @Before
    public void setUp() {
        ceilingFunction = new CeilingFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(ceilingFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeZero() {
        final BigDecimal value = BigDecimal.ZERO;
        FunctionTestUtil.assertResultBigDecimal(ceilingFunction.invoke(value), BigDecimal.ZERO);
    }

    @Test
    public void invokePositive() {
        final BigDecimal value = BigDecimal.valueOf(10.2);
        FunctionTestUtil.assertResultBigDecimal(ceilingFunction.invoke(value), BigDecimal.valueOf(11));
    }

    @Test
    public void invokeNegative() {
        final BigDecimal value = BigDecimal.valueOf(-10.2);
        FunctionTestUtil.assertResultBigDecimal(ceilingFunction.invoke(value), BigDecimal.valueOf(-10));
    }
}