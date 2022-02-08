/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.uni;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

public final class DefaultUniConstraintCollector<A, ResultContainer_, Result_>
        implements UniConstraintCollector<A, ResultContainer_, Result_> {

    private final Supplier<ResultContainer_> supplier;
    private final BiFunction<ResultContainer_, A, Runnable> accumulator;
    private final Function<ResultContainer_, Result_> finisher;

    public DefaultUniConstraintCollector(Supplier<ResultContainer_> supplier,
            BiFunction<ResultContainer_, A, Runnable> accumulator,
            Function<ResultContainer_, Result_> finisher) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.finisher = finisher;
    }

    @Override
    public Supplier<ResultContainer_> supplier() {
        return supplier;
    }

    @Override
    public BiFunction<ResultContainer_, A, Runnable> accumulator() {
        return accumulator;
    }

    @Override
    public Function<ResultContainer_, Result_> finisher() {
        return finisher;
    }

}
