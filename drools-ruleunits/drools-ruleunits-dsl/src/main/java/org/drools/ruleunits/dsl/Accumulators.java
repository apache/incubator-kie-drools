/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.ruleunits.dsl;

import org.drools.core.base.accumulators.AverageAccumulateFunction;
import org.drools.core.base.accumulators.IntegerMaxAccumulateFunction;
import org.drools.core.base.accumulators.IntegerMinAccumulateFunction;
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

    public static <A, B> Accumulator1<A, Integer> min(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, IntegerMinAccumulateFunction::new, Integer.class);
    }

    public static <A, B> Accumulator1<A, Integer> max(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, IntegerMaxAccumulateFunction::new, Integer.class);
    }
}
