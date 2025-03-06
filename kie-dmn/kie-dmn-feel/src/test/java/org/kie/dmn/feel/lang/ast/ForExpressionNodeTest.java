/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.lang.ast;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getAtLiteralRangeNode;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getIterationContextNode;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getListNode;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getNameRefNode;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getNestedListNode;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getLocalDateRangeNode;

class ForExpressionNodeTest {

    @Test
    void evaluateSimpleArray() {
        IterationContextNode x = getIterationContextNode("x", getListNode("[ 1, 2, 3, 4 ]", Arrays.asList("1", "2", "3", "4")), "x in [ 1, 2, 3, 4 ]");
        IterationContextNode y = getIterationContextNode("y", getNameRefNode(BuiltInType.UNKNOWN, "x"), "y in x");
        ForExpressionNode forExpressionNode = new ForExpressionNode(Arrays.asList(x, y), getNameRefNode(BuiltInType.UNKNOWN, "y"), "for x in [ 1, 2, 3, 4 ], y in x return y");
        Object retrieved = forExpressionNode.evaluate(EvaluationContextTestUtil.newEmptyEvaluationContext());
        assertThat(retrieved).isInstanceOf(List.class).asList().
                containsExactly(BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4));
    }

    @Test
    void evaluateDescendingRange() {
        RangeNode rangeNode = getAtLiteralRangeNode("[@\"1980-01-03T00:00:00\"..@\"1980-01-01T00:00:00\"]", "1980-01-03T00:00:00", "1980-01-01T00:00:00", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED);
        IterationContextNode i = getIterationContextNode("i", rangeNode, "i in [@\"1980-01-03T00:00:00\"..@\"1980-01-01T00:00:00\"]");
        ForExpressionNode forExpressionNode = new ForExpressionNode(Collections.singletonList(i), getNameRefNode(BuiltInType.UNKNOWN, "i"), "for i in [@\"1980-01-03T00:00:00\"..@\"1980-01-01T00:00:00\"] return i");
        Object retrieved = forExpressionNode.evaluate(EvaluationContextTestUtil.newEmptyEvaluationContext());
        assertThat(retrieved).isNull();
    }

    @Test
    void evaluateNestedArray() {
        Map<String, List<String>> firstIterationContext = new LinkedHashMap<>();
        firstIterationContext.put("1, 2", Arrays.asList("1", "2"));
        firstIterationContext.put("3, 4", Arrays.asList("3", "4"));
        IterationContextNode x = getIterationContextNode("x", getNestedListNode("[ [1, 2], [3, 4] ]", firstIterationContext), "x in [ [1, 2], [3, 4] ]");
        IterationContextNode y = getIterationContextNode("y", getNameRefNode(BuiltInType.UNKNOWN, "x"), "y in x");
        ForExpressionNode forExpressionNode = new ForExpressionNode(Arrays.asList(x, y), getNameRefNode(BuiltInType.UNKNOWN, "y"), "for x in [ [1, 2], [3, 4] ], y in x return y");
        Object retrieved = forExpressionNode.evaluate(EvaluationContextTestUtil.newEmptyEvaluationContext());
        assertThat(retrieved).isInstanceOf(List.class).asList().
                containsExactly(BigDecimal.ONE, BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4));

    }

    @Test
    void evaluateRange() {
        IterationContextNode x = getIterationContextNode("x", getLocalDateRangeNode("[1980-01-01 .. 1980-01-03]", LocalDate.of(1980, 1, 1), LocalDate.of(1980, 1, 3), RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED ), "x in [1980-01-01 .. 1980-01-03]");
        ForExpressionNode forExpressionNode = new ForExpressionNode(Collections.singletonList(x), getNameRefNode(BuiltInType.DATE, "x"), "for x in [1980-01-01 .. 1980-01-03] return x");
        Object retrieved = forExpressionNode.evaluate(EvaluationContextTestUtil.newEmptyEvaluationContext());
        assertThat(retrieved).isInstanceOf(List.class).asList().containsExactly(LocalDate.of(1980, 1, 1),
                LocalDate.of(1980, 1, 2), LocalDate.of(1980, 1, 3));
    }
}