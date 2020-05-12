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

package org.optaplanner.core.impl.score.stream.drools.quad;

import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractUniCollectingGroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;

final class DroolsQuadToTriGroupByCollectorProcessor<A, B, C, D, ResultContainer, NewA, NewB, NewC> extends
        DroolsAbstractUniCollectingGroupByCollectorProcessor<ResultContainer, QuadTuple<A, B, C, D>, BiTuple<NewA, NewB>, TriTuple<NewA, NewB, NewC>> {

    private final QuadFunction<A, B, C, D, NewA> groupKeyAMapping;
    private final QuadFunction<A, B, C, D, NewB> groupKeyBMapping;
    private final Supplier<ResultContainer> supplier;
    private final PentaFunction<ResultContainer, A, B, C, D, Runnable> accumulator;
    private final Function<ResultContainer, NewC> finisher;

    public DroolsQuadToTriGroupByCollectorProcessor(QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainer, NewC> collector) {
        this.groupKeyAMapping = groupKeyAMapping;
        this.groupKeyBMapping = groupKeyBMapping;
        this.supplier = collector.supplier();
        this.accumulator = collector.accumulator();
        this.finisher = collector.finisher();
    }

    @Override
    protected BiTuple<NewA, NewB> toKey(QuadTuple<A, B, C, D> abcdQuadTuple) {
        return new BiTuple<>(groupKeyAMapping.apply(abcdQuadTuple.a, abcdQuadTuple.b, abcdQuadTuple.c, abcdQuadTuple.d),
                groupKeyBMapping.apply(abcdQuadTuple.a, abcdQuadTuple.b, abcdQuadTuple.c, abcdQuadTuple.d));
    }

    @Override
    protected ResultContainer newContainer() {
        return supplier.get();
    }

    @Override
    protected Runnable process(QuadTuple<A, B, C, D> abcdQuadTuple, ResultContainer container) {
        return accumulator.apply(container, abcdQuadTuple.a, abcdQuadTuple.b, abcdQuadTuple.c, abcdQuadTuple.d);
    }

    @Override
    protected TriTuple<NewA, NewB, NewC> toResult(BiTuple<NewA, NewB> key, ResultContainer container) {
        return new TriTuple<>(key.a, key.b, finisher.apply(container));
    }

}
