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

public final class TriangularNumbers {

    /**
     * This is the highest <em>n</em> for which the <em>n</em>th triangular number can be calculated using int arithmetic.
     */
    static final int HIGHEST_SAFE_N = 46340;

    /**
     * Calculate <em>n</em>th <a href="https://en.wikipedia.org/wiki/Triangular_number">triangular number</a>.
     * This is used to calculate the number of subLists for a given list variable of size <em>n</em>.
     * To be able to use {@code int} arithmetic to calculate the triangular number, <em>n</em> must be less than or equal to
     * {@link #HIGHEST_SAFE_N}. If the <em>n</em> is higher, the method throws an exception.
     *
     * @param n size of the triangle (the length of its side)
     * @return <em>n</em>th triangular number
     * @throws ArithmeticException if {@code n} is higher than {@link #HIGHEST_SAFE_N}
     */
    public static int nthTriangle(int n) throws ArithmeticException {
        return Math.multiplyExact(n, n + 1) / 2;
    }

    static double triangularRoot(int x) {
        return (Math.sqrt(8L * x + 1) - 1) / 2;
    }

    private TriangularNumbers() {
    }
}
