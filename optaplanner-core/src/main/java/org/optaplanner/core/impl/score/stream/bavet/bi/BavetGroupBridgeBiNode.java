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

package org.optaplanner.core.impl.score.stream.bavet.bi;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.optaplanner.core.api.score.stream.bi.BiConstraintCollector;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;

public class BavetGroupBridgeBiNode<A, B, NewA, ResultContainer_, NewB> extends BavetAbstractBiNode<A, B> {
    private final BavetAbstractBiNode<A, B> parentNode;
    private final BiFunction<A, B, NewA> groupKeyMapping;
    private final BiConstraintCollector<A, B, ResultContainer_, NewB> collector;
    private final BavetGroupBiNode<NewA, ResultContainer_, NewB> groupNode;

    private final Map<NewA, BavetGroupBiTuple<NewA, ResultContainer_, NewB>> tupleMap;

    public BavetGroupBridgeBiNode(BavetConstraintSession session, int nodeOrder, BavetAbstractBiNode<A, B> parentNode,
            BiFunction<A, B, NewA> groupKeyMapping, BiConstraintCollector<A, B, ResultContainer_, NewB> collector,
            BavetGroupBiNode<NewA, ResultContainer_, NewB> groupNode) {
        super(session, nodeOrder);
        this.parentNode = parentNode;
        this.groupKeyMapping = groupKeyMapping;
        this.collector = collector;
        this.groupNode = groupNode;
        tupleMap = new HashMap<>();
    }

    @Override
    public BavetGroupBridgeBiTuple<A, B, NewA, ResultContainer_, NewB> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        return new BavetGroupBridgeBiTuple<>(this, parentTuple);
    }

    public void refresh(BavetGroupBridgeBiTuple<A, B, NewA, ResultContainer_, NewB> tuple) {
        if (tuple.getChildTuple() != null) {
            BavetGroupBiTuple<NewA, ResultContainer_, NewB> childTuple = tuple.getChildTuple();
            NewA oldGroupKey = childTuple.getGroupKey();
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
            B b = tuple.getFactB();
            NewA groupKey = groupKeyMapping.apply(a, b);
            BavetGroupBiTuple<NewA, ResultContainer_, NewB> childTuple = tupleMap.computeIfAbsent(groupKey,
                    k -> groupNode.createTuple(groupKey, collector.supplier().get()));
            int parentCount = childTuple.increaseParentCount();

            Runnable undoAccumulator = collector.accumulator().apply(childTuple.getResultContainer(), a, b);
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
        return "GroupBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}