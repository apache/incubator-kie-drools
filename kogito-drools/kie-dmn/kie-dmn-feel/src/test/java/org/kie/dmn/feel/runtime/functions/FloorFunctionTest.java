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

public class FloorFunctionTest {

    private FloorFunction floorFunction;

    @Before
    public void setUp() {
        floorFunction = new FloorFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultError(floorFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeZero() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.ZERO), BigDecimal.ZERO);
    }

    @Test
    public void invokePositive() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.valueOf(10.2)), BigDecimal.valueOf(10));
    }

    @Test
    public void invokeNegative() {
        FunctionTestUtil.assertResultBigDecimal(floorFunction.invoke(BigDecimal.valueOf(-10.2)), BigDecimal.valueOf(-11));
    }

}