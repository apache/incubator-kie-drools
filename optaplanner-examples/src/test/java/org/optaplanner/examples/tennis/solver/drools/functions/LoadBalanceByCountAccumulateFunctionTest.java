/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.tennis.solver.drools.functions;

import org.junit.Test;

import static org.junit.Assert.*;

public class LoadBalanceByCountAccumulateFunctionTest {

    @Test
    public void accumulate() {
        LoadBalanceByCountAccumulateFunction function = new LoadBalanceByCountAccumulateFunction();
        LoadBalanceByCountAccumulateFunction.LoadBalanceByCountData context = function.createContext();
        function.init(context);
        Object a = new Object();
        Object b = new Object();
        Object c = new Object();
        function.accumulate(context, a);
        assertEquals(1000, function.getResult(context).getZeroDeviationSquaredSumRootMillis());
        function.accumulate(context, a);
        assertEquals(2000, function.getResult(context).getZeroDeviationSquaredSumRootMillis());
        function.accumulate(context, a);
        assertEquals(3000, function.getResult(context).getZeroDeviationSquaredSumRootMillis());
        function.reverse(context, a);
        assertEquals(2000, function.getResult(context).getZeroDeviationSquaredSumRootMillis());
        function.accumulate(context, b);
        assertEquals(2236, function.getResult(context).getZeroDeviationSquaredSumRootMillis());
        function.accumulate(context, c);
        assertEquals(2449, function.getResult(context).getZeroDeviationSquaredSumRootMillis());
        function.accumulate(context, c);
        assertEquals(3000, function.getResult(context).getZeroDeviationSquaredSumRootMillis());
        function.reverse(context, b);
        assertEquals(2828, function.getResult(context).getZeroDeviationSquaredSumRootMillis());
    }

}
