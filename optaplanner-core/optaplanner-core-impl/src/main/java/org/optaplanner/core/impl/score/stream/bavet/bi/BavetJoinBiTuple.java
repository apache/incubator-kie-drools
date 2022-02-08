/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinTuple;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniTuple;

public final class BavetJoinBiTuple<A, B> extends BavetAbstractBiTuple<A, B>
        implements BavetJoinTuple {

    private final BavetJoinBiNode<A, B> node;
    private final BavetJoinBridgeUniTuple<A> aTuple;
    private final BavetJoinBridgeUniTuple<B> bTuple;
    private final List<BavetAbstractTuple> childTupleList = new ArrayList<>(1);

    public BavetJoinBiTuple(BavetJoinBiNode<A, B> node,
            BavetJoinBridgeUniTuple<A> aTuple, BavetJoinBridgeUniTuple<B> bTuple) {
        this.node = node;
        this.aTuple = aTuple;
        this.bTuple = bTuple;
    }

    @Override
    public String toString() {
        return "Join(" + getFactsString() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetJoinBiNode<A, B> getNode() {
        return node;
    }

    @Override
    public List<BavetAbstractTuple> getChildTupleList() {
        return childTupleList;
    }

    @Override
    public A getFactA() {
        return aTuple.getFactA();
    }

    @Override
    public B getFactB() {
        return bTuple.getFactA();
    }

    public BavetJoinBridgeUniTuple<A> getATuple() {
        return aTuple;
    }

    public BavetJoinBridgeUniTuple<B> getBTuple() {
        return bTuple;
    }

}
