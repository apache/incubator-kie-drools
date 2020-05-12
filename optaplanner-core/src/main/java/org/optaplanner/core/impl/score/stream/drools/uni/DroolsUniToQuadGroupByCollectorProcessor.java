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

package org.optaplanner.core.impl.score.stream.drools.uni;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.score.stream.drools.common.BiTuple;
import org.optaplanner.core.impl.score.stream.drools.common.DroolsAbstractBiCollectingGroupByCollectorProcessor;
import org.optaplanner.core.impl.score.stream.drools.common.QuadTuple;

final class DroolsUniToQuadGroupByCollectorProcessor<A, NewA, NewB, NewC, NewD, ResultContainerC, ResultContainerD>
        extends
        DroolsAbstractBiCollectingGroupByCollectorProcessor<ResultContainerC, ResultContainerD, A, BiTuple<NewA, NewB>, QuadTuple<NewA, NewB, NewC, NewD>> {

    private final Function<A, NewA> groupKeyAMapping;
    private final Function<A, NewB> groupKeyBMapping;
    private final Supplier<ResultContainerC> supplierC;
    private final BiFunction<ResultContainerC, A, Runnable> accumulatorC;
    private final Function<ResultContainerC, NewC> finisherC;
    private final Supplier<ResultContainerD> supplierD;
    private final BiFunction<ResultContainerD, A, Runnable> accumulatorD;
    private final Function<ResultContainerD, NewD> finisherD;

    public DroolsUniToQuadGroupByCollectorProcessor(Function<A, NewA> groupKeyAMapping, Function<A, NewB> groupKeyBMapping,
            UniConstraintCollector<A, ResultContainerC, NewC> collectorC,
            UniConstraintCollector<A, ResultContainerD, NewD> collectorD) {
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
    protected BiTuple<NewA, NewB> toKey(A a) {
        return new BiTuple<>(groupKeyAMapping.apply(a), groupKeyBMapping.apply(a));
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
    protected Runnable processFirst(A a, ResultContainerC container) {
        return accumulatorC.apply(container, a);
    }

    @Override
    protected Runnable processSecond(A a, ResultContainerD container) {
        return accumulatorD.apply(container, a);
    }

    @Override
    protected QuadTuple<NewA, NewB, NewC, NewD> toResult(BiTuple<NewA, NewB> key, ResultContainerC containerC,
            ResultContainerD containerD) {
        return new QuadTuple<>(key.a, key.b, finisherC.apply(containerC), finisherD.apply(containerD));
    }
}
