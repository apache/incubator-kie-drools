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

import java.util.LinkedHashSet;
import java.util.Set;

import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinTuple;

public final class BavetJoinBridgeBiTuple<A, B> extends BavetAbstractBiTuple<A, B>
        implements BavetJoinBridgeTuple {

    protected final BavetAbstractBiTuple<A, B> parentTuple;
    private final BavetJoinBridgeBiNode<A, B> node;

    protected Set<BavetJoinTuple> childTupleSet = new LinkedHashSet<>(); // TODO capacity
    private Object[] indexProperties;

    public BavetJoinBridgeBiTuple(BavetJoinBridgeBiNode<A, B> node,
            BavetAbstractBiTuple<A, B> parentTuple) {
        this.parentTuple = parentTuple;
        this.node = node;
    }

    @Override
    public void refresh() {
        node.refresh(this);
    }

    @Override
    public String toString() {
        return "JoinBridge(" + getFactsString() + ") with " + childTupleSet.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetJoinBridgeBiNode<A, B> getNode() {
        return node;
    }

    public Set<BavetJoinTuple> getChildTupleSet() {
        return childTupleSet;
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
    public Object[] getIndexProperties() {
        return indexProperties;
    }

    @Override
    public void setIndexProperties(Object[] indexProperties) {
        this.indexProperties = indexProperties;
    }

}
