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
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractBiCollectingGroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;
import org.optaplanner.core.impl.score.stream.drools.common.TriTuple;

final class DroolsTriToQuadGroupByCollectorProcessor<A, B, C, NewA, NewB, NewC, NewD, ResultContainerC, ResultContainerD>
        extends
        DroolsAbstractBiCollectingGroupByCollectorProcessor<ResultContainerC, ResultContainerD, TriTuple<A, B, C>, BiTuple<NewA, NewB>, QuadTuple<NewA, NewB, NewC, NewD>> {

    private final TriFunction<A, B, C, NewA> groupKeyAMapping;
    private final TriFunction<A, B, C, NewB> groupKeyBMapping;
    private final Supplier<ResultContainerC> supplierC;
    private final QuadFunction<ResultContainerC, A, B, C, Runnable> accumulatorC;
    private final Function<ResultContainerC, NewC> finisherC;
    private final Supplier<ResultContainerD> supplierD;
    private final QuadFunction<ResultContainerD, A, B, C, Runnable> accumulatorD;
    private final Function<ResultContainerD, NewD> finisherD;

    public DroolsTriToQuadGroupByCollectorProcessor(TriFunction<A, B, C, NewA> groupKeyAMapping,
            TriFunction<A, B, C, NewB> groupKeyBMapping,
            TriConstraintCollector<A, B, C, ResultContainerC, NewC> collectorC,
            TriConstraintCollector<A, B, C, ResultContainerD, NewD> collectorD) {
        this.groupKeyAMapping = groupKeyAMapping;
        this.groupKeyBMapping = groupKeyBMapping;
        this.supplierC = collectorC.supplier();
        this.accumulatorC = collectorC.accumulator();
        this.finisherC = collectorC.finisher();
        this.supplierD = collectorD.supplier();
        this.accumulatorD = collectorD.accumulator();
        this.finisherD = collectorD.finisher();
    }

    @Override
    protected BiTuple<NewA, NewB> toKey(TriTuple<A, B, C> tuple) {
        return new BiTuple<>(groupKeyAMapping.apply(tuple.a, tuple.b, tuple.c),
                groupKeyBMapping.apply(tuple.a, tuple.b, tuple.c));
    }

    @Override
    protected ResultContainerC newFirstContainer() {
        return supplierC.get();
    }

    @Override
    protected ResultContainerD newSecondContainer() {
        return supplierD.get();
    }

    @Override
    protected Runnable processFirst(TriTuple<A, B, C> tuple, ResultContainerC container) {
        return accumulatorC.apply(container, tuple.a, tuple.b, tuple.c);
    }

    @Override
    protected Runnable processSecond(TriTuple<A, B, C> tuple, ResultContainerD container) {
        return accumulatorD.apply(container, tuple.a, tuple.b, tuple.c);
    }

    @Override
    protected QuadTuple<NewA, NewB, NewC, NewD> toResult(BiTuple<NewA, NewB> key, ResultContainerC containerC,
            ResultContainerD containerD) {
        return new QuadTuple<>(key.a, key.b, finisherC.apply(containerC), finisherD.apply(containerD));
    }
}
