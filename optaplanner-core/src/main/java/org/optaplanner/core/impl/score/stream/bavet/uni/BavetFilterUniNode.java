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

package org.optaplanner.core.impl.score.stream.bavet.uni;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetAbstractTuple;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;

public final class BavetFilterUniNode<A> extends BavetAbstractUniNode<A> {

    private final BavetAbstractUniNode<A> parentNode;
    private final Predicate<A> predicate;

    private List<BavetAbstractUniNode<A>> childNodeList = new ArrayList<>();

    public BavetFilterUniNode(BavetConstraintSession session, int nodeIndex,
            BavetAbstractUniNode<A> parentNode, Predicate<A> predicate) {
        super(session, nodeIndex);
        this.parentNode = parentNode;
        this.predicate = predicate;
    }

    @Override
    public void addChildNode(BavetAbstractUniNode<A> childNode) {
        childNodeList.add(childNode);
    }

    @Override
    public List<BavetAbstractUniNode<A>> getChildNodeList() {
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
        } else if (o instanceof BavetFilterUniNode) {
            BavetFilterUniNode<?> other = (BavetFilterUniNode<?>) o;
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
    public BavetFilterUniTuple<A> createTuple(BavetAbstractUniTuple<A> parentTuple) {
        // TODO Use childNodeList.size() to improve the tuple's childTupleList's capacity
        return new BavetFilterUniTuple<>(this, parentTuple);
    }

    @Override
    public void refresh(BavetAbstractTuple uncastTuple) {
        BavetFilterUniTuple<A> tuple = (BavetFilterUniTuple<A>) uncastTuple;
        A a = tuple.getFactA();
        List<BavetAbstractTuple> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractTuple childTuple : childTupleList) {
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            if (predicate.test(a)) {
                for (BavetAbstractUniNode<A> childNode : childNodeList) {
                    BavetAbstractUniTuple<A> childTuple = childNode.createTuple(tuple);
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
