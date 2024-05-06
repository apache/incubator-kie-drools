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

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class NotFunctionTest {

    private NotFunction notFunction;

    @BeforeEach
    void setUp() {
        notFunction = new NotFunction();
    }

    @Test
    void invokeNull() {
        FunctionTestUtil.assertResultNull(notFunction.invoke(null));
    }

    @Test
    void invokeWrongType() {
        FunctionTestUtil.assertResultError(notFunction.invoke(1), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(notFunction.invoke("test"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(notFunction.invoke(BigDecimal.ZERO), InvalidParametersEvent.class);
    }

    @Test
    void invokeTrue() {
        FunctionTestUtil.assertResult(notFunction.invoke(true), false);
    }

    @Test
    void invokeFalse() {
        FunctionTestUtil.assertResult(notFunction.invoke(false), true);
    }
}