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
package org.kie.dmn.feel.runtime.functions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class StartsWithFunctionTest {

    private StartsWithFunction startsWithFunction;

    @BeforeEach
    void setUp() {
        startsWithFunction = new StartsWithFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultError(startsWithFunction.invoke((String) null, null), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(startsWithFunction.invoke(null, "test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(startsWithFunction.invoke("test", null), InvalidParametersEvent.class);
    }

    @Test
    void invokeEmptyString() {
        FunctionTestUtil.assertResult(startsWithFunction.invoke("", ""), true);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("", "test"), false);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", ""), true);
    }

    @Test
    void invokeStartsWith() {
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "te"), true);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "t"), true);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "test"), true);
    }

    @Test
    void invokeNotStartsWith() {
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "tte"), false);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "tt"), false);
        FunctionTestUtil.assertResult(startsWithFunction.invoke("test", "ttest"), false);
    }
}