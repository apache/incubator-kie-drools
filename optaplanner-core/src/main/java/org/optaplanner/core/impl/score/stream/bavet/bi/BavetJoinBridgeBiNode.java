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

import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeNode;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;
import org.optaplanner.core.impl.score.stream.bavet.common.index.BavetIndex;
import org.optaplanner.core.impl.score.stream.bavet.tri.BavetJoinTriNode;

public final class BavetJoinBridgeBiNode<A, B> extends BavetAbstractBiNode<A, B>
        implements BavetJoinBridgeNode {

    private final BavetAbstractBiNode<A, B> parentNode;
    private final BiFunction<A, B, Object[]> mapping;
    /** Calls {@link BavetJoinTriNode#refreshChildTuplesLeft(BavetJoinBridgeBiTuple)}, right or tri/quad/... variants. */
    private Consumer<BavetJoinBridgeBiTuple<A, B>> childTupleRefresher;

    private final BavetIndex<BavetJoinBridgeBiTuple<A, B>> index;

    public BavetJoinBridgeBiNode(BavetConstraintSession session, int nodeOrder, BavetAbstractBiNode<A, B> parentNode,
            BiFunction<A, B, Object[]> mapping, BavetIndex<BavetJoinBridgeBiTuple<A, B>> index) {
        super(session, nodeOrder);
        this.parentNode = parentNode;
        this.mapping = mapping;
        this.index = index;
    }

    @Override
    public BavetJoinBridgeBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        return new BavetJoinBridgeBiTuple<>(this, parentTuple);
    }

    public void refresh(BavetJoinBridgeBiTuple<A, B> tuple) {
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        if (tuple.getState() != BavetTupleState.CREATING) {
            // Clean up index
            index.remove(tuple);
        }
        if (tuple.isActive()) {
            Object[] indexProperties = mapping.apply(a, b);
            index.put(indexProperties, tuple);
        }
        childTupleRefresher.accept(tuple);
        tuple.refreshed();
    }

    @Override
    public String toString() {
        return "JoinBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    public BavetIndex<BavetJoinBridgeBiTuple<A, B>> getIndex() {
        return index;
    }

    public void setChildTupleRefresher(Consumer<BavetJoinBridgeBiTuple<A, B>> childTupleRefresher) {
        this.childTupleRefresher = childTupleRefresher;
    }

}
