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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.CompilerUtils.parseCompileEvaluate;

class ListReplaceFunctionTest {

    private ListReplaceFunction listReplaceFunction;

    @BeforeEach
    void setUp() {
        listReplaceFunction = ListReplaceFunction.INSTANCE;
    }

    @Test
    void invokeListNull() {
        FunctionTestUtil.assertResultError(listReplaceFunction.invoke(null, BigDecimal.ONE, ""), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionNull() {
        FunctionTestUtil.assertResultError(listReplaceFunction.invoke(new ArrayList(), (BigDecimal) null, ""), InvalidParametersEvent.class);
    }

    @Test
    void invokePositionInvalid() {
        FunctionTestUtil.assertResultError(listReplaceFunction.invoke(Collections.emptyList(), BigDecimal.ONE, ""), InvalidParametersEvent.class);
        List list = getList();
        FunctionTestUtil.assertResultError(listReplaceFunction.invoke(list, BigDecimal.ZERO, ""), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(listReplaceFunction.invoke(list, BigDecimal.valueOf(4), ""), InvalidParametersEvent.class);
    }

    @Test
    void invokeReplaceByPositionWithNull() {
        List list = getList();
        List expected = new ArrayList<>(list);
        expected.set(1, null);
        FunctionTestUtil.assertResult(listReplaceFunction.invoke(list, BigDecimal.valueOf(2), null), expected);
    }

    @Test
    void invokeReplaceByNegativePositionWithNotNull() {
        List list = getList();
        List expected = new ArrayList<>(list);
        expected.set(2, "test");
        FunctionTestUtil.assertResult(listReplaceFunction.invoke(list, BigDecimal.valueOf(-1), "test"), expected);
    }

    @Test
    void invokeReplaceByNegativePositionWithNull() {
        List list = getList();
        List expected = new ArrayList<>(list);
        expected.set(2, null);
        FunctionTestUtil.assertResult(listReplaceFunction.invoke(list, BigDecimal.valueOf(-1), null), expected);
    }

    @Test
    void invokeReplaceByPositionWithNotNull() {
        List list = getList();
        List expected = new ArrayList<>(list);
        expected.set(1, "test");
        FunctionTestUtil.assertResult(listReplaceFunction.invoke(list, BigDecimal.valueOf(2), "test"), expected);
    }


    @Test
    void invokeMatchNull() {
        FunctionTestUtil.assertResultError(listReplaceFunction.invoke(new ArrayList(), (AbstractCustomFEELFunction) null, ""), InvalidParametersEvent.class);
    }

    @Test
    void invokeMatchInvalid() {
        List list = Arrays.asList(2, 4, 7, 8);
        String validMatchFunction = "function(item, newItem) item + newItem";
        Object expressionObject = parseCompileEvaluate(validMatchFunction);
        assertThat(expressionObject).isInstanceOf(AbstractCustomFEELFunction.class);
        FunctionTestUtil.assertResultError(listReplaceFunction.invoke(list, (AbstractCustomFEELFunction)expressionObject, 3), InvalidParametersEvent.class);
    }

    @Test
    void invokeReplaceByMatchWithNull() {
        List list = getList();
        List expected = new ArrayList<>(list);
        expected.set(1, null);
        String validMatchFunction = "function(item, newItem) item = \"Element-1\"";
        Object expressionObject = parseCompileEvaluate(validMatchFunction);
        assertThat(expressionObject).isInstanceOf(AbstractCustomFEELFunction.class);
        FunctionTestUtil.assertResult(listReplaceFunction.invoke(list, (AbstractCustomFEELFunction)expressionObject, null), expected);
    }

    @Test
    void invokeReplaceByMatchWithNotNull() {
        String validMatchFunction = "function(item, newItem) item < newItem";
        Object expressionObject = parseCompileEvaluate(validMatchFunction);
        assertThat(expressionObject).isInstanceOf(AbstractCustomFEELFunction.class);
        List list = Arrays.asList(BigDecimal.valueOf(2), BigDecimal.valueOf(4), BigDecimal.valueOf(7), BigDecimal.valueOf(8));
        List expected = new ArrayList<>(list);
        expected.set(0, BigDecimal.valueOf(5));
        expected.set(1, BigDecimal.valueOf(5));
        FunctionTestUtil.assertResult(listReplaceFunction.invoke(list, (AbstractCustomFEELFunction)expressionObject, 5), expected);
    }

    private List getList() {
        return IntStream.range(0, 3).mapToObj(i -> "Element-"+i).toList();
    }

}