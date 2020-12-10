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

package org.optaplanner.core.impl.score.stream.bavet.uni;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndex;

public final class BavetJoinBridgeUniNode<A> extends BavetAbstractUniNode<A>
        implements BavetJoinBridgeNode {

    private final BavetAbstractUniNode<A> parentNode;
    private final Function<A, Object[]> mapping;
    /** Calls {@link BavetJoinBiNode#refreshChildTuplesLeft(BavetJoinBridgeUniTuple)}, right or tri/quad/... variants. */
    private Consumer<BavetJoinBridgeUniTuple<A>> childTupleRefresher;

    private final BavetIndex<BavetJoinBridgeUniTuple<A>> index;

    public BavetJoinBridgeUniNode(BavetConstraintSession session, int nodeIndex, BavetAbstractUniNode<A> parentNode,
            Function<A, Object[]> mapping, BavetIndex<BavetJoinBridgeUniTuple<A>> index) {
        super(session, nodeIndex);
        this.parentNode = parentNode;
        this.mapping = mapping;
        this.index = index;
    }

    @Override
    public List<BavetAbstractUniNode<A>> getChildNodeList() {
        return Collections.emptyList();
    }

    @Override
    public BavetJoinBridgeUniTuple<A> createTuple(BavetAbstractUniTuple<A> parentTuple) {
        return new BavetJoinBridgeUniTuple<>(this, parentTuple);
    }

    @Override
    public void refresh(BavetAbstractTuple uncastTuple) {
        BavetJoinBridgeUniTuple<A> tuple = (BavetJoinBridgeUniTuple<A>) uncastTuple;
        A a = tuple.getFactA();
        if (tuple.getState() != BavetTupleState.CREATING) {
            // Clean up index
            index.remove(tuple);
        }
        if (tuple.isActive()) {
            Object[] indexProperties = mapping.apply(a);
            index.put(indexProperties, tuple);
        }
        childTupleRefresher.accept(tuple);
    }

    @Override
    public String toString() {
        return "JoinBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public BavetIndex<BavetJoinBridgeUniTuple<A>> getIndex() {
        return index;
    }

    public void setChildTupleRefresher(Consumer<BavetJoinBridgeUniTuple<A>> childTupleRefresher) {
        this.childTupleRefresher = childTupleRefresher;
    }

}
