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
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.feel.runtime.impl.RangeImpl;
import org.kie.dmn.feel.runtime.impl.UndefinedValueComparable;
import org.kie.dmn.feel.util.EvaluationContextTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.dmn.feel.lang.ast.RangeNode.isDescendingRange;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getAtLiteralRangeNode;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getLocalDateRangeNode;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getNumericRangeNode;
import static org.kie.dmn.feel.util.ExpressionNodeFactoryUtils.getStringRangeNode;

class RangeNodeTest {

    @ParameterizedTest
    @MethodSource("ascendingRanges")
    void evaluateAscendingRanges(RangeNode toTest) {
        assertThat(toTest).isNotNull();
        assertThat(toTest.evaluate(EvaluationContextTestUtil.newEmptyEvaluationContext()))
                .isNotNull()
                .isInstanceOf(RangeImpl.class);
    }

    @ParameterizedTest
    @MethodSource("descendingRanges")
    void evaluateDescendingRanges(RangeNode toTest) {
        assertThat(toTest).isNotNull();
        assertThat(toTest.evaluate(EvaluationContextTestUtil.newEmptyEvaluationContext())).isNull();
    }

    @Test
    void isDescendingRangeTest() {
        assertThat(isDescendingRange(BigDecimal.ONE, BigDecimal.TEN)).isFalse();
        assertThat(isDescendingRange(new UndefinedValueComparable(), BigDecimal.TEN)).isFalse();
        assertThat(isDescendingRange(BigDecimal.ONE, new UndefinedValueComparable())).isFalse();
        assertThat(isDescendingRange(new UndefinedValueComparable(), new UndefinedValueComparable())).isFalse();
        assertThat(isDescendingRange(BigDecimal.ONE, BigDecimal.ONE)).isFalse();
        assertThat(isDescendingRange(BigDecimal.TEN, BigDecimal.ONE)).isTrue();

        assertThat(isDescendingRange(null, BigDecimal.TEN)).isFalse();
        assertThat(isDescendingRange(BigDecimal.ONE, null)).isFalse();
        assertThat(isDescendingRange(null, null)).isFalse();
        assertThat(isDescendingRange(BigDecimal.ONE, BigDecimal.ONE)).isFalse();
        assertThat(isDescendingRange(BigDecimal.TEN, BigDecimal.ONE)).isTrue();
    }

    private static Collection<RangeNode> ascendingRanges() {
        return Arrays.asList(
                getNumericRangeNode("[1..3]", BigDecimal.ONE, BigDecimal.valueOf(3), RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getLocalDateRangeNode("[@\"1970-01-01\"..@\"1970-01-02\"]", LocalDate.of(1970, 1, 1), LocalDate.of(1970, 1, 2), RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getStringRangeNode("[\"a\"..\"z\"]", "a", "z", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getAtLiteralRangeNode("[@\"P1D\"..@\"P2D\"]", "P1D", "P2D", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getAtLiteralRangeNode("[@\"P1Y\"..@\"P2Y\"]", "P1Y", "P2Y", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getAtLiteralRangeNode("[@\"01:00:00\"..@\"02:00:00\"]", "01:00:00", "02:00:00", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED)
        );
    }

    private static Collection<RangeNode> descendingRanges() {
        return Arrays.asList(
                getNumericRangeNode("[3..1]", BigDecimal.valueOf(3), BigDecimal.ONE, RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getLocalDateRangeNode("[@\"1970-01-02\"..@\"1970-01-01\"]", LocalDate.of(1970, 1, 2), LocalDate.of(1970, 1, 1), RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getStringRangeNode("[\"z\"..\"a\"]", "z", "a", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getAtLiteralRangeNode("[@\"P2D\"..@\"P1D\"]", "P2D", "P1D", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getAtLiteralRangeNode("[@\"P2Y\"..@\"P1Y\"]", "P2Y", "P1Y", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED),
                getAtLiteralRangeNode("[@\"02:00:00\"..@\"01:00:00\"]", "02:00:00", "01:00:00", RangeNode.IntervalBoundary.CLOSED, RangeNode.IntervalBoundary.CLOSED)
        );
    }


}