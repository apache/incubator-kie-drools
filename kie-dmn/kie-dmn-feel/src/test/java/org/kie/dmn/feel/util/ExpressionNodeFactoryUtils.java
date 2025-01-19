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
package org.kie.dmn.feel.util;

import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.ast.BaseNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.TemporalConstantNode;
import org.kie.dmn.feel.lang.types.BuiltInType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExpressionNodeFactoryUtils {

    public static IterationContextNode getIterationContextNode(String variableName, BaseNode expression, String text) {
        return new IterationContextNode(getNameDefNode(variableName), expression, null, text);
    }

    public static NameDefNode getNameDefNode(String text) {
        return new NameDefNode(Collections.singletonList(text), null, text);
    }

    public static NameRefNode getNameRefNode(Type type, String text) {
        return new NameRefNode(type, text);
    }

    public static ListNode getNestedListNode(String text, Map<String, List<String>> values) {
        List<BaseNode> elements = values.entrySet()
                .stream()
                .map(entry -> getListNode(entry.getKey(), entry.getValue()))
                .map(BaseNode.class::cast)
                .toList();
        return new ListNode(elements, text);
    }

    public static ListNode getListNode(String text, List<String> values) {
        List<BaseNode> elements = values.stream()
                .map(value -> {
                    if (value.matches("-?\\d+(\\.\\d+)?")) {
                        return new NumberNode(new BigDecimal(value), value);
                    } else if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        return new StringNode(value);
                    } else {
                        return new StringNode(value);
                    }
                })
                .map(BaseNode.class::cast)
                .toList();

        return new ListNode(elements, text);
    }

    public static TemporalConstantNode getTemporalConstantNode(Object value) {
        return new TemporalConstantNode(value, null, null, null);
    }

    public static RangeNode getRangeNode(String text, LocalDate start, LocalDate end, RangeNode.IntervalBoundary lowerBound, RangeNode.IntervalBoundary upperBound) {
        BaseNode nameRefNode = getNameRefNode(BuiltInType.DATE, "x");
        ListNode startParams = getListNode(start.toString(), List.of(start.toString()));
        ListNode endParams = getListNode(end.toString(), List.of(end.toString()));
        BaseNode startNode = new FunctionInvocationNode(nameRefNode, startParams, getTemporalConstantNode(start), start.toString());
        BaseNode endNode = new FunctionInvocationNode(nameRefNode, endParams, getTemporalConstantNode(end), end.toString());

        return new RangeNode(lowerBound, upperBound, startNode, endNode, text);
    }

}
