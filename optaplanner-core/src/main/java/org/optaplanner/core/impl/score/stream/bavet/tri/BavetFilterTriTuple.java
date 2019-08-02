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

package org.optaplanner.core.impl.score.stream.bavet.tri;

import java.util.ArrayList;
import java.util.List;

public final class BavetFilterTriTuple<A, B, C> extends BavetAbstractTriTuple<A, B, C> {

    private final BavetFilterTriNode<A, B, C> node;
    private final BavetAbstractTriTuple<A, B, C> parentTuple;

    protected List<BavetAbstractTriTuple<A, B, C>> childTupleList = null;

    public BavetFilterTriTuple(BavetFilterTriNode<A, B, C> node, BavetAbstractTriTuple<A, B, C> parentTuple) {
        this.node = node;
        this.parentTuple = parentTuple;
        childTupleList = new ArrayList<>();
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    @Override
    public String toString() {
        return "Filter(" + getFactsString() + ") with " + childTupleList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetFilterTriNode<A, B, C> getNode() {
        return node;
    }

    @Override
    public A getFactA() {
        return parentTuple.getFactA();
    }

    @Override
    public B getFactB() {
        return parentTuple.getFactB();
    }

    @Override
    public C getFactC() {
        return parentTuple.getFactC();
    }

    public List<BavetAbstractTriTuple<A, B, C>> getChildTupleList() {
        return childTupleList;
    }

}
