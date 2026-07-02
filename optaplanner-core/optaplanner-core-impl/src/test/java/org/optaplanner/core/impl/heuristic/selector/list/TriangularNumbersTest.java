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

package org.optaplanner.core.impl.heuristic.selector.list;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TriangularNumbersTest {

    @Test
    void overflow() {
        assertThatThrownBy(() -> TriangularNumbers.nthTriangle(TriangularNumbers.HIGHEST_SAFE_N + 1))
                .isInstanceOf(ArithmeticException.class)
                .hasMessage("integer overflow");
    }

    static Stream<Arguments> nthProvider() {
        return Stream.of(
                arguments(0, 0),
                arguments(1, 1),
                arguments(2, 3),
                arguments(3, 6),
                arguments(4, 10),
                arguments(5, 15),
                arguments(23169, 268_412_865),
                arguments(TriangularNumbers.HIGHEST_SAFE_N, 1_073_720_970));
    }

    @ParameterizedTest
    @MethodSource("nthProvider")
    void nthTriangle(int n, int nthTriangularNumber) {
        assertThat(TriangularNumbers.nthTriangle(n)).isEqualTo(nthTriangularNumber);
    }

    @ParameterizedTest
    @MethodSource("nthProvider")
    void triangularRoot(int n, int nthTriangularNumber) {
        assertThat(TriangularNumbers.triangularRoot(nthTriangularNumber)).isEqualTo(n);
    }

    static Stream<Arguments> nonTriangularRoots() {
        return Stream.of(
                arguments(2, 1),
                arguments(4, 2),
                arguments(5, 2),
                arguments(7, 3),
                arguments(8, 3),
                arguments(9, 3),
                arguments(268_435_456, 23169));
    }

    @ParameterizedTest
    @MethodSource
    void nonTriangularRoots(int x, int rootFloor) {
        assertThat(TriangularNumbers.triangularRoot(x)).isStrictlyBetween((double) rootFloor, rootFloor + 1.0);
    }
}
