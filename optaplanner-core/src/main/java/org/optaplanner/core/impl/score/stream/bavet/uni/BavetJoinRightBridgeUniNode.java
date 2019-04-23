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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiNode;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiTuple;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndex;

public final class BavetJoinRightBridgeUniNode<A, B> extends BavetAbstractUniNode<B> {

    private final Function<B, Object[]> mapping;
    private final BavetJoinBiNode<A, B> biNode;

    private final BavetIndex<B, BavetJoinRightBridgeUniTuple<A, B>> index;

    public BavetJoinRightBridgeUniNode(BavetConstraintSession session, int nodeOrder,
            Function<B, Object[]> mapping, BavetIndex<B, BavetJoinRightBridgeUniTuple<A, B>> index,
            BavetJoinBiNode<A, B> biNode) {
        super(session, nodeOrder);
        this.mapping = mapping;
        this.index = index;
        this.biNode = biNode;
    }

    @Override
    public BavetJoinRightBridgeUniTuple<A, B> createTuple(BavetAbstractUniTuple<B> parentTuple) {
        return new BavetJoinRightBridgeUniTuple<>(this, parentTuple);
    }

    public void refresh(BavetJoinRightBridgeUniTuple<A, B> tuple) {
        B b = tuple.getFactA();
        Set<BavetJoinBiTuple<A, B>> childTupleSet = tuple.getChildTupleSet();
        for (BavetJoinBiTuple<A, B> childTuple : childTupleSet) {
            boolean removed = childTuple.getATuple().getChildTupleSet().remove(childTuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the fact (" + b
                        + ")'s childTuple cannot be removed from the other fact (" + childTuple.getFactA()
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
            Object[] indexProperties = mapping.apply(b);
            index.put(indexProperties, tuple);
            Set<BavetJoinLeftBridgeUniTuple<A, B>> aTupleList = biNode.getLeftIndex().get(indexProperties);
            for (BavetJoinLeftBridgeUniTuple<A, B> aTuple : aTupleList) {
                if (!aTuple.isDirty()) {
                    BavetJoinBiTuple<A, B> childTuple = biNode.createTuple(aTuple, tuple);
                    aTuple.getChildTupleSet().add(childTuple);
                    childTupleSet.add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
        tuple.refreshed();
    }

    @Override
    public String toString() {
        return "JoinRightBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public BavetIndex<B, BavetJoinRightBridgeUniTuple<A, B>> getIndex() {
        return index;
    }

}
