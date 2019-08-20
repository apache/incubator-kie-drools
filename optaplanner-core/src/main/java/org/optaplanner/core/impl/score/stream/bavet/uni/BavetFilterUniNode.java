/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
import java.util.function.Predicate;

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;

public final class BavetFilterUniNode<A> extends BavetAbstractUniNode<A> {

    private final BavetAbstractUniNode<A> parentNode;
    private final Predicate<A> predicate;

    private List<BavetAbstractUniNode<A>> childNodeList = new ArrayList<>();

    public BavetFilterUniNode(BavetConstraintSession session, int nodeOrder,
            BavetAbstractUniNode<A> parentNode, Predicate<A> predicate) {
        super(session, nodeOrder);
        this.parentNode = parentNode;
        this.predicate = predicate;
    }

    @Override
    public void addChildNode(BavetAbstractUniNode<A> childNode) {
        childNodeList.add(childNode);
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    @Override
    public int hashCode() {
        // Similar to Object.hash() without autoboxing
        return 31 * System.identityHashCode(parentNode)
                + System.identityHashCode(predicate);
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

    public void refresh(BavetFilterUniTuple<A> tuple) {
        A a = tuple.getFactA();
        List<BavetAbstractUniTuple<A>> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractUniTuple<A> childTuple : childTupleList) {
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
        tuple.refreshed();
    }

    @Override
    public String toString() {
        return "Filter() with " + childNodeList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
