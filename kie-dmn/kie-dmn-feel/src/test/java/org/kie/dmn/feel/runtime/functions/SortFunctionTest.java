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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

class SortFunctionTest {

    private SortFunction sortFunction;

    @BeforeEach
    void setUp() {
        sortFunction = new SortFunction();
    }

    @Test
    void invokeListParamNull() {
        FunctionTestUtil.assertResultError(sortFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    void invokeListEmpty() {
        FunctionTestUtil.assertResultList(sortFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    void invokeListSingleItem() {
        FunctionTestUtil.assertResultList(sortFunction.invoke(Collections.singletonList(10)), Collections.singletonList(10));
    }

    @Test
    void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(
                sortFunction.invoke(Arrays.asList(10, "test", BigDecimal.TEN)),
                InvalidParametersEvent.class);
    }

    @Test
    void invokeList() {
        FunctionTestUtil.assertResultList(sortFunction.invoke(Arrays.asList(10, 4, 5, 12)), Arrays.asList(4, 5, 10, 12));
        FunctionTestUtil.assertResultList(sortFunction.invoke(Arrays.asList("a", "c", "b")), Arrays.asList("a", "b", "c"));
    }

    @Test
    void invokeWithSortFunctionNull() {
        FunctionTestUtil.assertResultList(
                sortFunction.invoke(null, Arrays.asList(10, 4, 5, 12), null), Arrays.asList(4, 5, 10, 12));
    }

    @Test
    void invokeWithSortFunction() {
        FunctionTestUtil.assertResultList(
                sortFunction.invoke(null, Arrays.asList(10, 4, 5, 12), getBooleanFunction(true)), Arrays.asList(12, 5, 4, 10));
        FunctionTestUtil.assertResultList(
                sortFunction.invoke(null, Arrays.asList(10, 4, 5, 12), getBooleanFunction(false)), Arrays.asList(10, 4, 5, 12));
    }

    @Test
    void invokeExceptionInSortFunction() {
        FunctionTestUtil.assertResultError(
                sortFunction.invoke(null, Arrays.asList(10, 4, 5, 12), getFunctionThrowingException()),
                InvalidParametersEvent.class);
    }

    private FEELFunction getBooleanFunction(final boolean functionResult) {
        return new FEELFunction() {
            @Override
            public String getName() {
                return "alwaysBoolean";
            }

            @Override
            public Symbol getSymbol() {
                return null;
            }

            @Override
            public List<List<Param>> getParameters() {
                return null;
            }

            @Override
            public Object invokeReflectively(final EvaluationContext ctx, final Object[] params) {
                return functionResult;
            }
        };
    }

    private FEELFunction getFunctionThrowingException() {
        return new FEELFunction() {
            @Override
            public String getName() {
                return "throwException";
            }

            @Override
            public Symbol getSymbol() {
                return null;
            }

            @Override
            public List<List<Param>> getParameters() {
                return null;
            }

            @Override
            public Object invokeReflectively(final EvaluationContext ctx, final Object[] params) {
                throw new IllegalStateException("test exception");
            }
        };
    }
}