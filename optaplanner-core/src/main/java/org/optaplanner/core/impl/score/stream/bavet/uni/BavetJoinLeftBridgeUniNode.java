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
import java.util.Set;
import java.util.function.Function;

import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiNode;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiTuple;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndex;

public final class BavetJoinLeftBridgeUniNode<A, B> extends BavetAbstractUniNode<A> {

    private final Function<A, Object[]> mapping;
    private final BavetJoinBiNode<A, B> biNode;

    private final BavetIndex<A, BavetJoinLeftBridgeUniTuple<A, B>> index;

    public BavetJoinLeftBridgeUniNode(BavetConstraintSession session, int nodeOrder,
            Function<A, Object[]> mapping, BavetIndex<A, BavetJoinLeftBridgeUniTuple<A, B>> index,
            BavetJoinBiNode<A, B> biNode) {
        super(session, nodeOrder);
        this.mapping = mapping;
        this.index = index;
        this.biNode = biNode;
    }

    @Override
    public BavetJoinLeftBridgeUniTuple<A, B> createTuple(BavetAbstractUniTuple<A> parentTuple) {
        return new BavetJoinLeftBridgeUniTuple<>(this, parentTuple);
    }

    public void refresh(BavetJoinLeftBridgeUniTuple<A, B> tuple) {
        A a = tuple.getFactA();
        Set<BavetJoinBiTuple<A, B>> childTupleSet = tuple.getChildTupleSet();
        for (BavetJoinBiTuple<A, B> childTuple : childTupleSet) {
            boolean removed = childTuple.getBTuple().getChildTupleSet().remove(childTuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the fact (" + a
                        + ")'s childTuple cannot be removed from the other fact (" + childTuple.getFactB()
                        + ")'s join bridge.");
            }
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleSet.clear();
        if (tuple.getState() != BavetTupleState.CREATING) {
            // Clean up index
            index.remove(tuple);
        }
        if (tuple.isActive()) {
            Object[] indexProperties = mapping.apply(a);
            index.put(indexProperties, tuple);
            Set<BavetJoinRightBridgeUniTuple<A, B>> bTupleList = biNode.getRightIndex().get(indexProperties);
            for (BavetJoinRightBridgeUniTuple<A, B> bTuple : bTupleList) {
                if (!bTuple.isDirty()) {
                    BavetJoinBiTuple<A, B> childTuple = biNode.createTuple(tuple, bTuple);
                    childTupleSet.add(childTuple);
                    bTuple.getChildTupleSet().add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
        tuple.refreshed();
    }

    @Override
    public String toString() {
        return "JoinLeftBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public BavetIndex<A, BavetJoinLeftBridgeUniTuple<A, B>> getIndex() {
        return index;
    }

}
