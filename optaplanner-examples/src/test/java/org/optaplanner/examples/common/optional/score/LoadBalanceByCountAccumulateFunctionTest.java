package org.optaplanner.examples.common.optional.score;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class LoadBalanceByCountAccumulateFunctionTest {

    @Test
    void accumulate() {
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
