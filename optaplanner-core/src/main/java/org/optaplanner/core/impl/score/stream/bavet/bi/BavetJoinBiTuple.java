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

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinLeftBridgeUniTuple;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinRightBridgeUniTuple;

public final class BavetJoinBiTuple<A, B> extends BavetAbstractBiTuple<A, B> {

    private final BavetJoinBiNode<A, B> node;
    private final BavetJoinLeftBridgeUniTuple<A, B> aTuple;
    private final BavetJoinRightBridgeUniTuple<A, B> bTuple;

    protected List<BavetAbstractBiTuple<A, B>> childTupleList = null;

    public BavetJoinBiTuple(BavetJoinBiNode<A, B> node,
            BavetJoinLeftBridgeUniTuple<A, B> aTuple, BavetJoinRightBridgeUniTuple<A, B> bTuple) {
        this.node = node;
        this.aTuple = aTuple;
        this.bTuple = bTuple;
        childTupleList = new ArrayList<>();
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    @Override
    public String toString() {
        return "Join(" + getFactA() + ", " + getFactB() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetJoinBiNode<A, B> getNode() {
        return node;
    }

    @Override
    public A getFactA() {
        return aTuple.getFactA();
    }

    @Override
    public B getFactB() {
        return bTuple.getFactA();
    }

    public BavetJoinLeftBridgeUniTuple<A, B> getATuple() {
        return aTuple;
    }

    public BavetJoinRightBridgeUniTuple<A, B> getBTuple() {
        return bTuple;
    }

    public List<BavetAbstractBiTuple<A, B>> getChildTupleList() {
        return childTupleList;
    }

}
