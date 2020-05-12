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

package org.optaplanner.core.impl.score.stream.drools.tri;

import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.tri.TriConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractUniCollectingGroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;

final class DroolsTriToBiGroupByCollectorProcessor<A, B, C, ResultContainer, NewA, NewB>
        extends
        DroolsAbstractUniCollectingGroupByCollectorProcessor<ResultContainer, TriTuple<A, B, C>, NewA, BiTuple<NewA, NewB>> {

    private final TriFunction<A, B, C, NewA> groupKeyMapping;
    private final Supplier<ResultContainer> supplier;
    private final QuadFunction<ResultContainer, A, B, C, Runnable> accumulator;
    private final Function<ResultContainer, NewB> finisher;

    public DroolsTriToBiGroupByCollectorProcessor(TriFunction<A, B, C, NewA> groupKeyMapping,
            TriConstraintCollector<A, B, C, ResultContainer, NewB> collector) {
        this.groupKeyMapping = groupKeyMapping;
        this.supplier = collector.supplier();
        this.accumulator = collector.accumulator();
        this.finisher = collector.finisher();
    }

    @Override
    protected NewA toKey(TriTuple<A, B, C> abcTriTuple) {
        return groupKeyMapping.apply(abcTriTuple.a, abcTriTuple.b, abcTriTuple.c);
    }

    @Override
    protected ResultContainer newContainer() {
        return supplier.get();
    }

    @Override
    protected Runnable process(TriTuple<A, B, C> abcTriTuple, ResultContainer container) {
        return accumulator.apply(container, abcTriTuple.a, abcTriTuple.b, abcTriTuple.c);
    }

    @Override
    protected BiTuple<NewA, NewB> toResult(NewA key, ResultContainer container) {
        return new BiTuple<>(key, finisher.apply(container));
    }

}
