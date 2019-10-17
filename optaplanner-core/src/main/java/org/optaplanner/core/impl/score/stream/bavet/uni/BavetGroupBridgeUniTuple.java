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

import org.optaplanner.core.impl.score.stream.bavet.bi.BavetGroupBiTuple;

public final class BavetGroupBridgeUniTuple<A, NewA, ResultContainer_, NewB> extends BavetAbstractUniTuple<A> {

    private final BavetGroupBridgeUniNode<A, NewA, ResultContainer_, NewB> node;
    private final BavetAbstractUniTuple<A> parentTuple;

    private Runnable undoAccumulator;
    private BavetGroupBiTuple<NewA, ResultContainer_, NewB> childTuple;

    public BavetGroupBridgeUniTuple(BavetGroupBridgeUniNode<A, NewA, ResultContainer_, NewB> node,
            BavetAbstractUniTuple<A> parentTuple) {
        this.node = node;
        this.parentTuple = parentTuple;
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }


    @Override
    public String toString() {
        return "GroupBridge(" + getFactsString() + ") with " + (childTuple == null ? 0 : 1) + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetGroupBridgeUniNode<A, NewA, ResultContainer_, NewB> getNode() {
        return node;
    }

    @Override
    public A getFactA() {
        return parentTuple.getFactA();
    }

    public Runnable getUndoAccumulator() {
        return undoAccumulator;
    }

    public void setUndoAccumulator(Runnable undoAccumulator) {
        this.undoAccumulator = undoAccumulator;
    }

    public BavetGroupBiTuple<NewA, ResultContainer_, NewB> getChildTuple() {
        return childTuple;
    }

    public void setChildTuple(BavetGroupBiTuple<NewA, ResultContainer_, NewB> childTuple) {
        this.childTuple = childTuple;
    }

}
