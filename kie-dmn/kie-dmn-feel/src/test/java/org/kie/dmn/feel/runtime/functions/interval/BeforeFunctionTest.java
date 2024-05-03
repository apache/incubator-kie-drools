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

class BeforeFunctionTest {

    private BeforeFunction beforeFunction;

    @BeforeEach
    void setUp() {
        beforeFunction = BeforeFunction.INSTANCE;
    }

    @Test
    void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(beforeFunction.invoke((Comparable) null, "b"), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(beforeFunction.invoke("a", (Comparable) null), InvalidParametersEvent.class);
    }

    @Test
    void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError( beforeFunction.invoke("a", BigDecimal.valueOf(2) ), InvalidParametersEvent.class );
    }

    @Test
    void invokeParamSingles() {
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a", "b" ), Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a", "a" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "b", "a" ), Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( BigDecimal.valueOf(2), BigDecimal.valueOf(1) ), Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(2) ), Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( BigDecimal.valueOf(1), BigDecimal.valueOf(1) ), Boolean.FALSE );
    }

    @Test
    void invokeParamSingleAndRange() {
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "f",
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.OPEN, "a", "f", Range.RangeBoundary.CLOSED )),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke( "a",
                new RangeImpl( Range.RangeBoundary.CLOSED, "b", "f", Range.RangeBoundary.CLOSED )),
                Boolean.TRUE );
    }

    @Test
    void invokeParamRangeAndSingle() {
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "f" ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "a"),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN ),
                "f" ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                "g" ),
                Boolean.TRUE );
    }

    @Test
    void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "g", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.FALSE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.CLOSED ),
                new RangeImpl( Range.RangeBoundary.OPEN, "f", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
        FunctionTestUtil.assertResult( beforeFunction.invoke(
                new RangeImpl( Range.RangeBoundary.CLOSED, "a", "f", Range.RangeBoundary.OPEN ),
                new RangeImpl( Range.RangeBoundary.CLOSED, "f", "k", Range.RangeBoundary.CLOSED ) ),
                Boolean.TRUE );
    }

}