package org.drools.ruleunits.dsl;

import org.drools.core.base.accumulators.AverageAccumulateFunction;
import org.drools.core.base.accumulators.IntegerSumAccumulateFunction;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;

public class Accumulators {

    public static <A, B> Accumulator1<A, Integer> sum(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, IntegerSumAccumulateFunction::new, Integer.class);
    }

    public static <A, B> Accumulator1<A, Double> avg(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, AverageAccumulateFunction::new, Double.class);
    }
}
