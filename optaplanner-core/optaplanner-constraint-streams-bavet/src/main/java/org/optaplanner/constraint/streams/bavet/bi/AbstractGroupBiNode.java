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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.common.AbstractGroupNode;
import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;

import org.optaplanner.constraint.streams.bavet.common.Tuple;

abstract class AbstractGroupBiNode<OldA, OldB, OutTuple_ extends Tuple, GroupKey_, ResultContainer_, Result_>
        extends AbstractGroupNode<BiTuple<OldA, OldB>, OutTuple_, GroupKey_, ResultContainer_> {

    private final TriFunction<ResultContainer_, OldA, OldB, Runnable> accumulator;
    protected final Function<ResultContainer_, Result_> finisher;

    protected AbstractGroupBiNode(int groupStoreIndex,
            BiConstraintCollector<OldA, OldB, ResultContainer_, Result_> collector,
            Consumer<OutTuple_> nextNodesInsert, Consumer<OutTuple_> nextNodesRetract) {
        super(groupStoreIndex,
                collector == null ? null : collector.supplier(),
                nextNodesInsert, nextNodesRetract);
        accumulator = collector == null ? null : collector.accumulator();
        finisher = collector == null ? null : collector.finisher();
    }

    @Override
    protected final Runnable accumulate(ResultContainer_ resultContainer, BiTuple<OldA, OldB> tuple) {
        return accumulator.apply(resultContainer, tuple.factA, tuple.factB);
    }

}
