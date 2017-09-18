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

public class NotFunctionTest {

    private NotFunction notFunction;

    @Before
    public void setUp() {
        notFunction = new NotFunction();
    }

    @Test
    public void invokeNull() {
        FunctionTestUtil.assertResultNull(notFunction.invoke(null));
    }

    @Test
    public void invokeWrongType() {
        FunctionTestUtil.assertResultError(notFunction.invoke(1), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(notFunction.invoke("test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(notFunction.invoke(BigDecimal.ZERO), InvalidParametersEvent.class);
    }

    @Test
    public void invokeTrue() {
        FunctionTestUtil.assertResult(notFunction.invoke(true), false);
    }

    @Test
    public void invokeFalse() {
        FunctionTestUtil.assertResult(notFunction.invoke(false), true);
    }
}