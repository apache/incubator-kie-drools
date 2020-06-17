/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.common.solver.drools.functions;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

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
        assertThat(function.getResult(context).getZeroDeviationSquaredSumRootMillis()).isEqualTo(1000);
        function.accumulate(context, a);
        assertThat(function.getResult(context).getZeroDeviationSquaredSumRootMillis()).isEqualTo(2000);
        function.accumulate(context, a);
        assertThat(function.getResult(context).getZeroDeviationSquaredSumRootMillis()).isEqualTo(3000);
        function.reverse(context, a);
        assertThat(function.getResult(context).getZeroDeviationSquaredSumRootMillis()).isEqualTo(2000);
        function.accumulate(context, b);
        assertThat(function.getResult(context).getZeroDeviationSquaredSumRootMillis()).isEqualTo(2236);
        function.accumulate(context, c);
        assertThat(function.getResult(context).getZeroDeviationSquaredSumRootMillis()).isEqualTo(2449);
        function.accumulate(context, c);
        assertThat(function.getResult(context).getZeroDeviationSquaredSumRootMillis()).isEqualTo(3000);
        function.reverse(context, b);
        assertThat(function.getResult(context).getZeroDeviationSquaredSumRootMillis()).isEqualTo(2828);
    }
}
