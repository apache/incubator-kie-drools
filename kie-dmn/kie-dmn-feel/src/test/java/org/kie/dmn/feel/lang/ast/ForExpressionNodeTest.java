/**
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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;

import static org.assertj.core.api.Assertions.assertThat;

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

    private IterationContextNode getIterationContextNode(String variableName, BaseNode expression, String text) {
        return new IterationContextNode(getNameDefNode(variableName), expression, null, text);
    }

    private NameDefNode getNameDefNode(String text) {
       return  new NameDefNode(Collections.singletonList(text), null, text);
    }

    private NameRefNode getNameRefNode(Type type, String text) {
        return  new NameRefNode(type, text);
    }

    private ListNode getNestedListNode(String text,  Map<String, List<String>> values) {
        List<BaseNode> elements = values.entrySet()
                .stream()
                .map(entry -> getListNode(entry.getKey(), entry.getValue()))
                .map(BaseNode.class::cast)
                .toList();
        return new ListNode(elements, text);
    }

    private ListNode getListNode(String text, List<String> values) {
        List<BaseNode> elements = values.stream()
                .map(value -> new NumberNode(new BigDecimal(value), value))
                .map(BaseNode.class::cast)
                .toList();
        return new ListNode(elements, text);
    }
}