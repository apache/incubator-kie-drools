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
import java.time.Duration;
import java.time.Period;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import static java.math.BigDecimal.valueOf;

class AbsFunctionTest {

    private AbsFunction absFunction;

    @BeforeEach
    void setUp() {
        absFunction = AbsFunction.INSTANCE;
    }

    @Test
    void absFunctionNumber() {
        FunctionTestUtil.assertResult(absFunction.invoke(valueOf(10)), valueOf(10));
        FunctionTestUtil.assertResult(absFunction.invoke(valueOf(-10)), valueOf(10));
        FunctionTestUtil.assertResultError(absFunction.invoke((BigDecimal) null), InvalidParametersEvent.class);
    }

    @Test
    void absFunctionDuration() {
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(100, 50 )),
                Duration.ofSeconds(100, 50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(-100, 50 )),
                Duration.ofSeconds(100, -50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(100, -50 )),
                Duration.ofSeconds(100, -50));
        FunctionTestUtil.assertResult(absFunction.invoke(Duration.ofSeconds(-100, -50 )),
                Duration.ofSeconds(100, 50));
        FunctionTestUtil.assertResultError(absFunction.invoke((Duration)null),
                InvalidParametersEvent.class);
    }

    @Test
    void absFunctionPeriod() {
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( 100, 50, 0 ) ),
                Period.of(100, 50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -100, 50, 0 ) ),
                Period.of(100, -50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( 100, -50, 0 ) ),
                Period.of(100, -50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -100, -50, 0 ) ),
                Period.of(100, 50, 0));
        FunctionTestUtil.assertResult(absFunction.invoke(Period.of( -1, 30, 0 ) ),
                Period.of(-1, 30, 0));
        FunctionTestUtil.assertResultError(absFunction.invoke((Period) null ),
                InvalidParametersEvent.class);
    }


}