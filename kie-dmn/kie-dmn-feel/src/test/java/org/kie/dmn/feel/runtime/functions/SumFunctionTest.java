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

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class SumFunctionTest {

    private SumFunction sumFunction;

    @Before
    public void setUp() {
        sumFunction = new SumFunction();
    }

    @Test
    public void invokeNullNumber() {
        FunctionTestUtil.assertResultError(sumFunction.invoke((Number) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeNullList() {
        FunctionTestUtil.assertResultError(sumFunction.invoke((List) null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeNullArray() {
        FunctionTestUtil.assertResultError(sumFunction.invoke((Object[]) null), InvalidParametersEvent.class);
    }
}