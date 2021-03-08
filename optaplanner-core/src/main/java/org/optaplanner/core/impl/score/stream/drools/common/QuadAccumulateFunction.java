/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.drools.common;

import java.util.Objects;

import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;

final class QuadAccumulateFunction<A, B, C, D, ResultContainer_, NewA>
        extends AbstractAccumulateFunction<ResultContainer_, QuadTuple<A, B, C, D>, NewA> {

    private final PentaFunction<ResultContainer_, A, B, C, D, Runnable> accumulator;

    public QuadAccumulateFunction(QuadConstraintCollector<A, B, C, D, ResultContainer_, NewA> collector) {
        super(collector.supplier(), collector.finisher());
        this.accumulator = Objects.requireNonNull(collector.accumulator());
    }

    @Override
    protected Runnable accumulate(ResultContainer_ container, QuadTuple<A, B, C, D> tuple) {
        return accumulator.apply(container, tuple.a, tuple.b, tuple.c, tuple.d);
    }

}
