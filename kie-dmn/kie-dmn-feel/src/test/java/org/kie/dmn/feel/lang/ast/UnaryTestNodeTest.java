/*
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
package org.kie.dmn.feel.lang.ast;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.util.ClassLoaderUtil;

import static org.assertj.core.api.Assertions.assertThat;

class UnaryTestNodeTest {

    @ParameterizedTest
    @MethodSource("provideCollectionsForEqualityTest")
    void testAreCollectionsEqual(List<?> left, List<?> right, boolean expected) {
        Boolean result = UnaryTestNode.areCollectionsEqual(left, right);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideCollectionsForEqualityTest() {
        return Stream.of(
            Arguments.of(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3), true, "Equal collections"),
            Arguments.of(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3, 4), false, "Different sizes"),
            Arguments.of(Arrays.asList(1, 2, 3), Arrays.asList(1, 5, 3), false, "Different elements"),
            Arguments.of(Arrays.asList(1, 2, 3), Arrays.asList(3, 2, 1), false, "Different order"),
            Arguments.of(Collections.emptyList(), Collections.emptyList(), true, "Empty collections"),
            Arguments.of(Arrays.asList(1, null, 3), Arrays.asList(1, null, 3), true, "Null elements"),
            Arguments.of(Arrays.asList(1, null, 3), Arrays.asList(1, 2, 3), false, "Mismatched nulls"),
            Arguments.of(Arrays.asList("a", "b", "c"), Arrays.asList("a", "b", "c"), true, "Equal string collections")
        );
    }

    @ParameterizedTest
    @MethodSource("provideElementInCollectionTestCases")
    void testIsElementInCollection(List<?> collection, Object element, boolean expected) {
        Boolean result = UnaryTestNode.isElementInCollection(collection, element);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideElementInCollectionTestCases() {
        return Stream.of(Arguments.of(
                Arrays.asList(Arrays.asList(1, 2, 3, 4), Arrays.asList(1, 2, 3)),
                Arrays.asList(1, 2, 3), true, "List element found in collection"),
            Arguments.of(
                Arrays.asList(Arrays.asList(1, 2, 3, 4), Arrays.asList(1, 2)),
                Arrays.asList(1, 2, 3), false, "List element not found in collection"),
            Arguments.of(
                Arrays.asList(1, 2, 3, 4, 5), 3, true, "Element exists in collection"),
            Arguments.of(
                Arrays.asList(1, 2, 3, 4, 5), 10, false, "Element does not exist in collection"),
            Arguments.of(
                Arrays.asList(1, null, 3), null, true, "Null element in collection"),
            Arguments.of(
                Collections.emptyList(), 1, false, "Empty collection"));
    }

    @ParameterizedTest
    @MethodSource("provideElementsForEqualityTest")
    void testAreElementsEqual(Object left, Object right, boolean expected) {
        Boolean result = UnaryTestNode.areElementsEqual(left, right);
        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> provideElementsForEqualityTest() {
        return Stream.of(
            Arguments.of(42, 42, true, "Equal integers"),
            Arguments.of(42, 24, false, "Different integers"),
            Arguments.of("hello", "hello", true, "Equal strings"),
            Arguments.of("hello", "world", false, "Different strings"),
            Arguments.of(null, null, true, "Both null"),
            Arguments.of(42, null, false, "Left non-null, right null"),
            Arguments.of(null, 42, false, "Left null, right non-null")
        );
    }

    /**
     * Tests for evaluateRightValue method - without question mark
     */
    
    @Test
    void testEvaluateRightValue_SimpleValue_WithoutQuestionMark() {
        NumberNode valueNode = new NumberNode(BigDecimal.valueOf(42), "42");
        UnaryTestNode unaryTestNode = new UnaryTestNode(UnaryTestNode.UnaryOperator.EQ, valueNode);
        
        EvaluationContext context = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        Object left = BigDecimal.valueOf(100);
        Object result = unaryTestNode.evaluateRightValue(context, left);
        assertThat(result).isEqualTo(BigDecimal.valueOf(42));
    }

    @Test
    void testEvaluateRightValue_WithQuestionMark_NotInContext() {
        NameRefNode questionMarkNode = new NameRefNode(BuiltInType.UNKNOWN, "?");
        UnaryTestNode unaryTestNode = new UnaryTestNode(UnaryTestNode.UnaryOperator.EQ, questionMarkNode);
        
        EvaluationContext context = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        Object left = BigDecimal.valueOf(123);
        Object result = unaryTestNode.evaluateRightValue(context, left);
        assertThat(result).isEqualTo(BigDecimal.valueOf(123));
    }

    @Test
    void testEvaluateRightValue_WithQuestionMark_AlreadyInContext_SameValue() {
        NameRefNode questionMarkNode = new NameRefNode(BuiltInType.UNKNOWN, "?");
        UnaryTestNode unaryTestNode = new UnaryTestNode(UnaryTestNode.UnaryOperator.EQ, questionMarkNode);
        
        EvaluationContext context = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        Object left = BigDecimal.valueOf(123);
        context.setValue("?", left); // '?' already set to same value
        Object result = unaryTestNode.evaluateRightValue(context, left);
        assertThat(result).isEqualTo(BigDecimal.valueOf(123));
    }

    @Test
    void testEvaluateRightValue_WithQuestionMark_AlreadyInContext_DifferentValue() {
        NameRefNode questionMarkNode = new NameRefNode(BuiltInType.UNKNOWN, "?");
        UnaryTestNode unaryTestNode = new UnaryTestNode(UnaryTestNode.UnaryOperator.EQ, questionMarkNode);
        
        EvaluationContext context = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        Object left = BigDecimal.valueOf(123);
        context.setValue("?", BigDecimal.valueOf(999)); // '?' set to different value

        Object result = unaryTestNode.evaluateRightValue(context, left);
        assertThat(result).isEqualTo(BigDecimal.valueOf(123));
        assertThat(context.getValue("?")).isEqualTo(BigDecimal.valueOf(999));
    }

    @Test
    void testEvaluateRightValue_WithQuestionMarkInExpression() {
        NameRefNode questionMarkNode = new NameRefNode(BuiltInType.UNKNOWN, "?");
        NumberNode tenNode = new NumberNode(BigDecimal.valueOf(10), "10");
        InfixOpNode additionNode = new InfixOpNode(InfixOperator.ADD, questionMarkNode, tenNode, "? + 10");
        UnaryTestNode unaryTestNode = new UnaryTestNode(UnaryTestNode.UnaryOperator.EQ, additionNode);
        
        EvaluationContext context = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        Object left = BigDecimal.valueOf(5);
        Object result = unaryTestNode.evaluateRightValue(context, left);
        assertThat(result).isEqualTo(BigDecimal.valueOf(15));
    }

    @Test
    void testEvaluateRightValue_FrameCleanup() {
        NameRefNode questionMarkNode = new NameRefNode(BuiltInType.UNKNOWN, "?");
        UnaryTestNode unaryTestNode = new UnaryTestNode(UnaryTestNode.UnaryOperator.EQ, questionMarkNode);
        
        EvaluationContext context = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        context.setValue("originalVar", "originalValue");
        Object left = BigDecimal.valueOf(123);
        Object result = unaryTestNode.evaluateRightValue(context, left);
        assertThat(result).isEqualTo(BigDecimal.valueOf(123));
        assertThat(context.getValue("originalVar")).isEqualTo("originalValue");
    }

    @Test
    void testEvaluateRightValue_WithNull() {
        NullNode nullNode = new NullNode("null");
        UnaryTestNode unaryTestNode = new UnaryTestNode(UnaryTestNode.UnaryOperator.EQ, nullNode);
        
        EvaluationContext context = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        Object left = BigDecimal.valueOf(123);
        Object result = unaryTestNode.evaluateRightValue(context, left);
        assertThat(result).isNull();
    }

    @Test
    void testEvaluateRightValue_WithString() {
        StringNode stringNode = new StringNode("hello");
        UnaryTestNode unaryTestNode = new UnaryTestNode(UnaryTestNode.UnaryOperator.EQ, stringNode);
        
        EvaluationContext context = new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), new FEELEventListenersManager(), FEELDialect.FEEL);
        Object left = "world";
        Object result = unaryTestNode.evaluateRightValue(context, left);
        assertThat(result).isEqualTo("hello");
    }
}