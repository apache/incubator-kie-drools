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

import java.util.List;
import java.util.Set;

import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiTuple;

public final class BavetJoinLeftBridgeUniTuple<A, B, Property_> extends BavetAbstractUniTuple<A> {

    private final BavetJoinLeftBridgeUniNode<A, B, Property_> node;
    private final BavetAbstractUniTuple<A> previousTuple;

    private Set<BavetJoinBiTuple<A, B, Property_>> downstreamTupleSet;

    public BavetJoinLeftBridgeUniTuple(BavetJoinLeftBridgeUniNode<A, B, Property_> node,
            BavetAbstractUniTuple<A> previousTuple) {
        this.node = node;
        this.previousTuple = previousTuple;
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    @Override
    public String toString() {
        return "JoinLeftBridge(" + getFactA() + ") to " + (downstreamTuple == null ? 0 : 1) + " downstream";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetJoinLeftBridgeUniNode<A, B, Property_> getNode() {
        return node;
    }

    @Override
    public A getFactA() {
        return previousTuple.getFactA();
    }

    public Set<BavetJoinBiTuple<A, B, Property_>> getDownstreamTupleSet() {
        return downstreamTupleSet;
    }

}
