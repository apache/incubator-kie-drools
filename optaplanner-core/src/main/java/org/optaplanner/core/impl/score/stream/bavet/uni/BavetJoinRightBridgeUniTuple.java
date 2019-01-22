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

import java.util.LinkedHashSet;
import java.util.Set;

import org.optaplanner.core.impl.score.stream.bavet.bi.BavetJoinBiTuple;

public final class BavetJoinRightBridgeUniTuple<A, B, Property_> extends BavetAbstractUniTuple<B> {

    private final BavetJoinRightBridgeUniNode<A, B, Property_> node;
    private final BavetAbstractUniTuple<B> parentTuple;

    private Set<BavetJoinBiTuple<A, B, Property_>> childTupleSet = new LinkedHashSet<>(); // TODO capacity

    public BavetJoinRightBridgeUniTuple(BavetJoinRightBridgeUniNode<A, B, Property_> node,
            BavetAbstractUniTuple<B> parentTuple) {
        this.node = node;
        this.parentTuple = parentTuple;
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    @Override
    public String toString() {
        return "JoinRightBridge(" + getFactA() + ") to " + childTupleSet.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetJoinRightBridgeUniNode<A, B, Property_> getNode() {
        return node;
    }

    @Override
    public B getFactA() {
        // Naming paradox:
        // If we rename getFactA() to getFact(), it is still there for joining a BiStream with a UniStream
        return parentTuple.getFactA();
    }

    public Set<BavetJoinBiTuple<A, B, Property_>> getChildTupleSet() {
        return childTupleSet;
    }

}
