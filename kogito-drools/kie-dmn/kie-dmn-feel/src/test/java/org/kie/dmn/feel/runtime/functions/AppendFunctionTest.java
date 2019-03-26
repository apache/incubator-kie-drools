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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

public class AppendFunctionTest {

    private AppendFunction appendFunction;

    @Before
    public void setUp() {
        appendFunction = new AppendFunction();
    }

    @Test
    public void invokeInvalidParams() {
        FunctionTestUtil.assertResultError(appendFunction.invoke((List) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(appendFunction.invoke((List) null, new Object[]{}), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(appendFunction.invoke(Collections.emptyList(), null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeEmptyParams() {
        FunctionTestUtil.assertResultList(appendFunction.invoke(Collections.emptyList(), new Object[]{}), Collections.emptyList());
    }

    @Test
    public void invokeAppendNothing() {
        FunctionTestUtil.assertResultList(appendFunction.invoke(Arrays.asList("test"), new Object[]{}), Arrays.asList("test"));
        FunctionTestUtil.assertResultList(appendFunction.invoke(Arrays.asList("test", "test2"), new Object[]{}), Arrays.asList("test", "test2"));
    }

    @Test
    public void invokeAppendSomething() {
        FunctionTestUtil.assertResultList(appendFunction.invoke(Collections.emptyList(), new Object[]{"test"}), Arrays.asList("test"));
        FunctionTestUtil.assertResultList(appendFunction.invoke(Arrays.asList("test"), new Object[]{"test2"}), Arrays.asList("test", "test2"));
        FunctionTestUtil.assertResultList(appendFunction.invoke(Arrays.asList("test"), new Object[]{"test2", "test3"}), Arrays.asList("test", "test2", "test3"));
    }
}