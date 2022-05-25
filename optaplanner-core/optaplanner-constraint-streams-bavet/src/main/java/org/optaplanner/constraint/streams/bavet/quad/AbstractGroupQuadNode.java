/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.bavet.quad;

import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.core.api.function.PentaFunction;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintCollector;

import org.optaplanner.constraint.streams.bavet.common.AbstractGroupNode;
import org.optaplanner.constraint.streams.bavet.common.Tuple;

abstract class AbstractGroupQuadNode<OldA, OldB, OldC, OldD, OutTuple_ extends Tuple, GroupKey_, ResultContainer_, Result_>
        extends AbstractGroupNode<QuadTuple<OldA, OldB, OldC, OldD>, OutTuple_, GroupKey_, ResultContainer_> {

    private final PentaFunction<ResultContainer_, OldA, OldB, OldC, OldD, Runnable> accumulator;
    protected final Function<ResultContainer_, Result_> finisher;

    protected AbstractGroupQuadNode(int groupStoreIndex,
            QuadConstraintCollector<OldA, OldB, OldC, OldD, ResultContainer_, Result_> collector,
            Consumer<OutTuple_> nextNodesInsert, Consumer<OutTuple_> nextNodesRetract) {
        super(groupStoreIndex,
                collector == null ? null : collector.supplier(),
                nextNodesInsert, nextNodesRetract);
        accumulator = collector == null ? null : collector.accumulator();
        finisher = collector == null ? null : collector.finisher();
    }

    @Override
    protected final Runnable accumulate(ResultContainer_ resultContainer, QuadTuple<OldA, OldB, OldC, OldD> tuple) {
        return accumulator.apply(resultContainer, tuple.factA, tuple.factB, tuple.factC, tuple.factD);
    }

}
