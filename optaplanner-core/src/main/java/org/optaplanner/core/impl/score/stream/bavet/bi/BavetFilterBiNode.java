/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import java.util.Objects;
import java.util.function.BiPredicate;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;

public final class BavetFilterBiNode<A, B> extends BavetAbstractBiNode<A, B> {

    private final BavetAbstractBiNode<A, B> parentNode;
    private final BiPredicate<A, B> predicate;

    private final List<BavetAbstractBiNode<A, B>> childNodeList = new ArrayList<>();

    public BavetFilterBiNode(BavetConstraintSession session, int nodeIndex,
            BavetAbstractBiNode<A, B> parentNode, BiPredicate<A, B> predicate) {
        super(session, nodeIndex);
        this.parentNode = parentNode;
        this.predicate = predicate;
    }

    @Override
    public void addChildNode(BavetAbstractBiNode<A, B> childNode) {
        childNodeList.add(childNode);
    }

    @Override
    public List<BavetAbstractBiNode<A, B>> getChildNodeList() {
        return childNodeList;
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    @Override
    public int hashCode() {
        return Objects.hash(System.identityHashCode(parentNode), System.identityHashCode(predicate));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BavetFilterBiNode) {
            BavetFilterBiNode<?, ?> other = (BavetFilterBiNode<?, ?>) o;
            return parentNode == other.parentNode
                    && predicate == other.predicate;
        } else {
            return false;
        }
    }

    // ************************************************************************
    // Runtime
    // ************************************************************************

    @Override
    public BavetFilterBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        // TODO Use childNodeList.size() to improve the tuple's childTupleList's capacity
        return new BavetFilterBiTuple<>(this, parentTuple);
    }

    @Override
    public void refresh(BavetAbstractTuple uncastTuple) {
        BavetFilterBiTuple<A, B> tuple = (BavetFilterBiTuple<A, B>) uncastTuple;
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        List<BavetAbstractTuple> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractTuple childTuple : childTupleList) {
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            if (predicate.test(a, b)) {
                for (BavetAbstractBiNode<A, B> childNode : childNodeList) {
                    BavetAbstractBiTuple<A, B> childTuple = childNode.createTuple(tuple);
                    childTupleList.add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "Filter() with " + childNodeList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
