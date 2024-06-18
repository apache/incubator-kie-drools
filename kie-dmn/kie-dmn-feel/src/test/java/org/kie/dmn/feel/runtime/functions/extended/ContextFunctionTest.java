/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.runtime.functions.extended;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;

import java.util.List;

class ContextFunctionTest {

    private ContextFunction contextFunction;
    private record ContextEntry(String key, Object value) {}

    @BeforeEach
    void setUp() {
        contextFunction = new ContextFunction();
    }

    @Test
    void invokeListNull() {
        FunctionTestUtil.assertResultError(contextFunction.invoke(null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(contextFunction.invoke((Object) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeContainsNull() {
        FunctionTestUtil.assertResultError(contextFunction.invoke(List.of(new ContextEntry("name", null))), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(contextFunction.invoke(List.of(new ContextEntry(null, "John Doe"))), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(contextFunction.invoke(new ContextEntry("name", null)), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(contextFunction.invoke(new ContextEntry(null, "John Doe")), InvalidParametersEvent.class);
    }

    @Test
    void invokeDuplicateKey() {
        FunctionTestUtil.assertResultError(contextFunction.invoke(List.of(new ContextEntry("name", "John Doe"), new ContextEntry("name", "Doe John"))), InvalidParametersEvent.class);
    }
}