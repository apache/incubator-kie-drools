/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.bavet.uni;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.optaplanner.core.api.score.stream.uni.UniCollector;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupedBiNode;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupedBiTuple;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;

public final class BavetGroupByBridgeUniNode<A, GroupKey_, ResultContainer_, Result_> extends BavetAbstractUniNode<A> {

    private final Function<A, GroupKey_> groupKeyMapping;
    private final UniCollector<A, ResultContainer_, Result_> collector;
    private final BavetGroupedBiNode<GroupKey_, ResultContainer_, Result_> biNode;

    private final Map<GroupKey_, BavetGroupedBiTuple<GroupKey_, ResultContainer_, Result_>> tupleMap;

    public BavetGroupByBridgeUniNode(BavetConstraintSession session, int nodeOrder,
            Function<A, GroupKey_> groupKeyMapping, UniCollector<A, ResultContainer_, Result_> collector,
            BavetGroupedBiNode<GroupKey_, ResultContainer_, Result_> biNode) {
        super(session, nodeOrder);
        this.groupKeyMapping = groupKeyMapping;
        this.collector = collector;
        this.biNode = biNode;
        tupleMap = new HashMap<>();
    }

    @Override
    public BavetGroupByBridgeUniTuple<A, GroupKey_, ResultContainer_, Result_> createTuple(BavetAbstractUniTuple<A> parentTuple) {
        return new BavetGroupByBridgeUniTuple<>(this, parentTuple);
    }

    public void refresh(BavetGroupByBridgeUniTuple<A, GroupKey_, ResultContainer_, Result_> tuple) {
        if (tuple.getChildTuple() != null) {
            BavetGroupedBiTuple<GroupKey_, ResultContainer_, Result_> childTuple = tuple.getChildTuple();
            GroupKey_ oldGroupKey = childTuple.getGroupKey();
            int parentCount = childTuple.decreaseParentCount();
            tuple.getUndoAccumulator().run();
            childTuple.clearResult();
            tuple.setChildTuple(null);
            tuple.setUndoAccumulator(null);
            if (parentCount == 0) {
                // Clean up tupleMap
                tupleMap.remove(oldGroupKey);
                session.transitionTuple(childTuple, BavetTupleState.DYING);
            } else {
                session.transitionTuple(childTuple, BavetTupleState.UPDATING);
            }
        }
        if (tuple.isActive()) {
            A a = tuple.getFactA();
            GroupKey_ groupKey = groupKeyMapping.apply(a);
            BavetGroupedBiTuple<GroupKey_, ResultContainer_, Result_> childTuple = tupleMap.computeIfAbsent(groupKey,
                    k -> biNode.createTuple(groupKey, collector.supplier().get()));
            int parentCount = childTuple.increaseParentCount();

            Runnable undoAccumulator = collector.accumulator().apply(childTuple.getResultContainer(), a);
            tuple.setUndoAccumulator(undoAccumulator);
            childTuple.clearResult();
            tuple.setChildTuple(childTuple);
            if (parentCount == 1) {
                session.transitionTuple(childTuple, BavetTupleState.CREATING);
            } else {
                // It might have just been created by an earlier tuple in the same nodeOrder
                if (childTuple.getState() != BavetTupleState.CREATING) {
                    session.transitionTuple(childTuple, BavetTupleState.UPDATING);
                }
            }
        }
        tuple.refreshed();
    }

    @Override
    public String toString() {
        return "GroupByBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    private class GroupKeyData {

        public int elementCount;
        public ResultContainer_ resultContainer;

        public GroupKeyData(ResultContainer_ resultContainer) {
            this.elementCount = 0;
            this.resultContainer = resultContainer;
        }
    }

}
