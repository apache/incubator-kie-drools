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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiNode;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiTuple;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetTupleState;

public final class BavetJoinRightBridgeUniNode<A, B, Property_> extends BavetAbstractUniNode<B> {

    private final Function<B, Property_> mapping;
    private final BavetJoinBiNode<A, B, Property_> biNode;

    private final Map<Property_, List<BavetJoinRightBridgeUniTuple<A, B, Property_>>> index;

    public BavetJoinRightBridgeUniNode(BavetConstraintSession session, int nodeOrder,
            Function<B, Property_> mapping, BavetJoinBiNode<A, B, Property_> biNode) {
        super(session, nodeOrder);
        this.mapping = mapping;
        this.biNode = biNode;
        index = new HashMap<>();
    }

    @Override
    public BavetJoinRightBridgeUniTuple<A, B, Property_> createTuple(BavetAbstractUniTuple<B> parentTuple) {
        return new BavetJoinRightBridgeUniTuple<>(this, parentTuple);
    }

    public void refresh(BavetJoinRightBridgeUniTuple<A, B, Property_> tuple) {
        B b = tuple.getFactA();
        Set<BavetJoinBiTuple<A, B, Property_>> childTupleSet = tuple.getChildTupleSet();
        for (BavetJoinBiTuple<A, B, Property_> childTuple : childTupleSet) {
            boolean removed = childTuple.getATuple().getChildTupleSet().remove(childTuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the fact (" + b
                        + ")'s childTuple cannot be removed from the other fact (" + childTuple.getFactA()
                        + ")'s join bridge.");
            }
            // TODO clean up index
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleSet.clear();
        if (tuple.isActive()) {
            Property_ property = mapping.apply(b);
            boolean added = index.computeIfAbsent(property, k -> new ArrayList<>()).add(tuple);
            if (!added) {
                throw new IllegalStateException("Impossible state: the fact (" + b + ") with property (" + property
                        + ") cannot be added to the index (" + index.keySet() + ").");
            }
            List<BavetJoinLeftBridgeUniTuple<A, B, Property_>> aTupleList = biNode.getATupleListByProperty(property);
            if (aTupleList != null) {
                for (BavetJoinLeftBridgeUniTuple<A, B, Property_> aTuple : aTupleList) {
                    BavetJoinBiTuple<A, B, Property_> childTuple = biNode.createTuple(aTuple, tuple);
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

    public List<BavetJoinRightBridgeUniTuple<A, B, Property_>> getTupleListByProperty(Property_ property) {
        return index.get(property);
    }

}
