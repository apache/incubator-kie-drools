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
package org.drools.ruleunits.dsl;

import java.util.List;
import java.util.Set;

import org.drools.core.base.accumulators.AverageAccumulateFunction;
import org.drools.core.base.accumulators.CollectListAccumulateFunction;
import org.drools.core.base.accumulators.CollectSetAccumulateFunction;
import org.drools.core.base.accumulators.CountAccumulateFunction;
import org.drools.core.base.accumulators.IntegerMaxAccumulateFunction;
import org.drools.core.base.accumulators.IntegerMinAccumulateFunction;
import org.drools.core.base.accumulators.IntegerSumAccumulateFunction;
import org.drools.core.base.accumulators.LongMaxAccumulateFunction;
import org.drools.core.base.accumulators.LongMinAccumulateFunction;
import org.drools.core.base.accumulators.LongSumAccumulateFunction;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.accumulate.Accumulator1;

import static org.drools.model.functions.Function1.identity;

/**
 * A set of convenient factory methods to create the accumulators used in the rule unit Java DSL.
 */
public class Accumulators {

    public static <A, B> Accumulator1<A, Long> count() {
        return new Accumulator1<>(identity(), CountAccumulateFunction::new, Long.class);
    }

    public static <A, B> Accumulator1<A, List> collect() {
        return collect(identity());
    }

    public static <A, B> Accumulator1<A, List> collect(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, CollectListAccumulateFunction::new, List.class);
    }

    public static <A, B> Accumulator1<A, Set> collectSet() {
        return collectSet(identity());
    }

    public static <A, B> Accumulator1<A, Set> collectSet(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, CollectSetAccumulateFunction::new, Set.class);
    }

    public static <A, B> Accumulator1<A, Integer> sum(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, IntegerSumAccumulateFunction::new, Integer.class);
    }

    public static <A, B> Accumulator1<A, Long> sumLong(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, LongSumAccumulateFunction::new, Long.class);
    }

    public static <A, B> Accumulator1<A, Double> avg(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, AverageAccumulateFunction::new, Double.class);
    }

    public static <A, B> Accumulator1<A, Integer> min(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, IntegerMinAccumulateFunction::new, Integer.class);
    }

    public static <A, B> Accumulator1<A, Long> minLong(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, LongMinAccumulateFunction::new, Long.class);
    }

    public static <A, B> Accumulator1<A, Integer> max(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, IntegerMaxAccumulateFunction::new, Integer.class);
    }

    public static <A, B> Accumulator1<A, Long> maxLong(Function1<A, B> bindingFunc) {
        return new Accumulator1<>(bindingFunc, LongMaxAccumulateFunction::new, Long.class);
    }
}
