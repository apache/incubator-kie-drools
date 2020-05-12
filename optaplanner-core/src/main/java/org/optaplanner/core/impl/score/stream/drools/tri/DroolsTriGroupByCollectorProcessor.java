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

final class DroolsTriGroupByCollectorProcessor<A, B, C, ResultContainer, NewA, NewB, NewC>
        extends
        DroolsAbstractUniCollectingGroupByCollectorProcessor<ResultContainer, TriTuple<A, B, C>, BiTuple<NewA, NewB>, TriTuple<NewA, NewB, NewC>> {

    private final TriFunction<A, B, C, NewA> groupKeyAMapping;
    private final TriFunction<A, B, C, NewB> groupKeyBMapping;
    private final Supplier<ResultContainer> supplier;
    private final QuadFunction<ResultContainer, A, B, C, Runnable> accumulator;
    private final Function<ResultContainer, NewC> finisher;

    public DroolsTriGroupByCollectorProcessor(TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ResultContainer, NewC> collector) {
        this.groupKeyAMapping = groupKeyAMapping;
        this.groupKeyBMapping = groupKeyBMapping;
        this.supplier = collector.supplier();
        this.accumulator = collector.accumulator();
        this.finisher = collector.finisher();
    }

    @Override
    protected BiTuple<NewA, NewB> toKey(TriTuple<A, B, C> abcTriTuple) {
        return new BiTuple<>(groupKeyAMapping.apply(abcTriTuple.a, abcTriTuple.b, abcTriTuple.c),
                groupKeyBMapping.apply(abcTriTuple.a, abcTriTuple.b, abcTriTuple.c));
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
    protected TriTuple<NewA, NewB, NewC> toResult(BiTuple<NewA, NewB> key, ResultContainer container) {
        return new TriTuple<>(key.a, key.b, finisher.apply(container));
    }

}
