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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.function.Consumer;

import org.optaplanner.constraint.streams.bavet.bi.BiTuple;
import org.optaplanner.constraint.streams.bavet.common.Group;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;
import org.optaplanner.core.impl.util.Pair;

final class Group0Mapping2CollectorUniNode<OldA, A, B, ResultContainerA_, ResultContainerB_>
        extends AbstractGroupUniNode<OldA, BiTuple<A, B>, String, Object, Pair<A, B>> {

    private static final String NO_GROUP_KEY = "NO_GROUP";

    private final int outputStoreSize;

    public Group0Mapping2CollectorUniNode(int groupStoreIndex,
                                          UniConstraintCollector<OldA, ResultContainerA_, A> collectorA,
                                          UniConstraintCollector<OldA, ResultContainerB_, B> collectorB,
                                          Consumer<BiTuple<A, B>> nextNodesInsert, Consumer<BiTuple<A, B>> nextNodesRetract,
                                          int outputStoreSize) {
        super(groupStoreIndex, mergeCollectors(collectorA, collectorB), nextNodesInsert, nextNodesRetract);
        this.outputStoreSize = outputStoreSize;
    }

    static <OldA, A, B, ResultContainerA_, ResultContainerB_>
            UniConstraintCollector<OldA, Object, Pair<A, B>> mergeCollectors(
                    UniConstraintCollector<OldA, ResultContainerA_, A> collectorA,
                    UniConstraintCollector<OldA, ResultContainerB_, B> collectorB) {
        return (UniConstraintCollector<OldA, Object, Pair<A, B>>) ConstraintCollectors.compose(collectorA, collectorB,
                Pair::of);
    }

    @Override
    protected String createGroupKey(UniTuple<OldA> tuple) {
        return NO_GROUP_KEY;
    }

    @Override
    protected BiTuple<A, B> createOutTuple(Group<BiTuple<A, B>, String, Object> group) {
        Object resultContainer = group.resultContainer;
        Pair<A, B> result = finisher.apply(resultContainer);
        return new BiTuple<>(result.getKey(), result.getValue(), outputStoreSize);
    }

    @Override
    public String toString() {
        return "GroupUniNode 0+2";
    }

}
