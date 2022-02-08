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

package org.optaplanner.core.impl.score.stream.bavet.uni;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeTuple;

public final class BavetJoinBridgeUniTuple<A> extends BavetAbstractUniTuple<A>
        implements BavetJoinBridgeTuple {

    protected final BavetAbstractUniTuple<A> parentTuple;
    private final BavetJoinBridgeUniNode<A> node;
    private final List<BavetAbstractTuple> childTupleList = new ArrayList<>();

    private Object[] indexProperties;

    public BavetJoinBridgeUniTuple(BavetJoinBridgeUniNode<A> node,
            BavetAbstractUniTuple<A> parentTuple) {
        this.parentTuple = parentTuple;
        this.node = node;
    }

    @Override
    public String toString() {
        return "JoinBridge(" + getFactsString() + ") with " + childTupleList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetJoinBridgeUniNode<A> getNode() {
        return node;
    }

    @Override
    public List<BavetAbstractTuple> getChildTupleList() {
        return childTupleList;
    }

    @Override
    public A getFactA() {
        return parentTuple.getFactA();
    }

    @Override
    public Object[] getIndexProperties() {
        return indexProperties;
    }

    @Override
    public void setIndexProperties(Object[] indexProperties) {
        this.indexProperties = indexProperties;
    }

}
