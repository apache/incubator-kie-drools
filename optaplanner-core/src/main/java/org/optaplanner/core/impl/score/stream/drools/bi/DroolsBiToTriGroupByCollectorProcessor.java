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

package org.optaplanner.core.impl.score.stream.drools.bi;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractUniCollectingGroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;

final class DroolsBiToTriGroupByCollectorProcessor<A, B, ResultContainer, NewA, NewB, NewC> extends
        DroolsAbstractUniCollectingGroupByCollectorProcessor<ResultContainer, BiTuple<A, B>, BiTuple<NewA, NewB>, TriTuple<NewA, NewB, NewC>> {

    private final BiFunction<A, B, NewA> groupKeyAMapping;
    private final BiFunction<A, B, NewB> groupKeyBMapping;
    private final Supplier<ResultContainer> supplier;
    private final TriFunction<ResultContainer, A, B, Runnable> accumulator;
    private final Function<ResultContainer, NewC> finisher;

    public DroolsBiToTriGroupByCollectorProcessor(BiFunction<A, B, NewA> groupKeyAMapping,
            BiFunction<A, B, NewB> groupKeyBMapping, BiConstraintCollector<A, B, ResultContainer, NewC> collector) {
        this.groupKeyAMapping = groupKeyAMapping;
        this.groupKeyBMapping = groupKeyBMapping;
        this.supplier = collector.supplier();
        this.accumulator = collector.accumulator();
        this.finisher = collector.finisher();
    }

    @Override
    protected BiTuple<NewA, NewB> toKey(BiTuple<A, B> abBiTuple) {
        return new BiTuple<>(groupKeyAMapping.apply(abBiTuple.a, abBiTuple.b),
                groupKeyBMapping.apply(abBiTuple.a, abBiTuple.b));
    }

    @Override
    protected ResultContainer newContainer() {
        return supplier.get();
    }

    @Override
    protected Runnable process(BiTuple<A, B> abBiTuple, ResultContainer container) {
        return accumulator.apply(container, abBiTuple.a, abBiTuple.b);
    }

    @Override
    protected TriTuple<NewA, NewB, NewC> toResult(BiTuple<NewA, NewB> key, ResultContainer container) {
        return new TriTuple<>(key.a, key.b, finisher.apply(container));
    }

}
