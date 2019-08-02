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

public final class BavetFilterBiTuple<A, B> extends BavetAbstractBiTuple<A, B> {

    private final BavetFilterBiNode<A, B> node;
    private final BavetAbstractBiTuple<A, B> parentTuple;

    protected List<BavetAbstractBiTuple<A, B>> childTupleList = null;

    public BavetFilterBiTuple(BavetFilterBiNode<A, B> node, BavetAbstractBiTuple<A, B> parentTuple) {
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
    public BavetFilterBiNode<A, B> getNode() {
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

    public List<BavetAbstractBiTuple<A, B>> getChildTupleList() {
        return childTupleList;
    }

}
