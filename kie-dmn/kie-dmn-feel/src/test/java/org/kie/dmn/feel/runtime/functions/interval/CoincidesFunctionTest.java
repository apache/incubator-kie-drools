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
package org.kie.dmn.feel.runtime.functions.interval;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;

class CoincidesFunctionTest {

    private CoincidesFunction coincidesFunction;

    @BeforeEach
    void setUp() {
        coincidesFunction = CoincidesFunction.INSTANCE;
    }

    @Test
    void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(coincidesFunction.invoke(null, "b"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(coincidesFunction.invoke("a", null), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( coincidesFunction.invoke("a", BigDecimal.valueOf(2) ), InvalidParametersEvent.class );
    }

    @Test
    void invokeParamSingles() {
        FunctionTestUtil.assertResult( coincidesFunction.invoke( "a", "b" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( "a", "a" ), Boolean.TRUE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( "b", "a" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( BigDecimal.valueOf(2), BigDecimal.valueOf(1) ), Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(2) ), Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(1) ), Boolean.TRUE );
    }

    @Test
    void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.OPEN ),
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.OPEN ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "g", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.OPEN, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( coincidesFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN ) ),
                Boolean.FALSE );
    }

}