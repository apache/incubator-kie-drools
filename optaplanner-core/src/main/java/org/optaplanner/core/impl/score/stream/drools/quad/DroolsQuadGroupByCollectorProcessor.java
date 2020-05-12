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
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractBiCollectingGroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;

final class DroolsQuadGroupByCollectorProcessor<A, B, C, D, NewA, NewB, NewC, NewD, ResultContainerC, ResultContainerD>
        extends
        DroolsAbstractBiCollectingGroupByCollectorProcessor<ResultContainerC, ResultContainerD, QuadTuple<A, B, C, D>, BiTuple<NewA, NewB>, QuadTuple<NewA, NewB, NewC, NewD>> {

    private final QuadFunction<A, B, C, D, NewA> groupKeyAMapping;
    private final QuadFunction<A, B, C, D, NewB> groupKeyBMapping;
    private final Supplier<ResultContainerC> supplierC;
    private final PentaFunction<ResultContainerC, A, B, C, D, Runnable> accumulatorC;
    private final Function<ResultContainerC, NewC> finisherC;
    private final Supplier<ResultContainerD> supplierD;
    private final PentaFunction<ResultContainerD, A, B, C, D, Runnable> accumulatorD;
    private final Function<ResultContainerD, NewD> finisherD;

    public DroolsQuadGroupByCollectorProcessor(QuadFunction<A, B, C, D, NewA> groupKeyAMapping,
            QuadFunction<A, B, C, D, NewB> groupKeyBMapping,
            QuadConstraintCollector<A, B, C, D, ResultContainerC, NewC> collectorC,
            QuadConstraintCollector<A, B, C, D, ResultContainerD, NewD> collectorD) {
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
    protected BiTuple<NewA, NewB> toKey(QuadTuple<A, B, C, D> tuple) {
        return new BiTuple<>(groupKeyAMapping.apply(tuple.a, tuple.b, tuple.c, tuple.d),
                groupKeyBMapping.apply(tuple.a, tuple.b, tuple.c, tuple.d));
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
    protected Runnable processFirst(QuadTuple<A, B, C, D> tuple, ResultContainerC container) {
        return accumulatorC.apply(container, tuple.a, tuple.b, tuple.c, tuple.d);
    }

    @Override
    protected Runnable processSecond(QuadTuple<A, B, C, D> tuple, ResultContainerD container) {
        return accumulatorD.apply(container, tuple.a, tuple.b, tuple.c, tuple.d);
    }

    @Override
    protected QuadTuple<NewA, NewB, NewC, NewD> toResult(BiTuple<NewA, NewB> key, ResultContainerC containerC,
            ResultContainerD containerD) {
        return new QuadTuple<>(key.a, key.b, finisherC.apply(containerC), finisherD.apply(containerD));
    }
}
