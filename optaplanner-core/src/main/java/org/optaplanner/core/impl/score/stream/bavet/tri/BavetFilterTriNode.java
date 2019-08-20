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

import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;

public final class BavetFilterTriNode<A, B, C> extends BavetAbstractTriNode<A, B, C> {

    private final BavetAbstractTriNode<A, B, C> parentNode;
    private final TriPredicate<A, B, C> predicate;

    private final List<BavetAbstractTriNode<A, B, C>> childNodeList = new ArrayList<>();

    public BavetFilterTriNode(BavetConstraintSession session, int nodeOrder,
            BavetAbstractTriNode<A, B, C> parentNode, TriPredicate<A, B, C> predicate) {
        super(session, nodeOrder);
        this.parentNode = parentNode;
        this.predicate = predicate;
    }

    @Override
    public void addChildNode(BavetAbstractTriNode<A, B, C> childNode) {
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
        } else if (o instanceof BavetFilterTriNode) {
            BavetFilterTriNode<?, ?, ?> other = (BavetFilterTriNode<?, ?, ?>) o;
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
    public BavetFilterTriTuple<A, B, C> createTuple(BavetAbstractTriTuple<A, B, C> parentTuple) {
        // TODO Use childNodeList.size() to improve the tuple's childTupleList's capacity
        return new BavetFilterTriTuple<>(this, parentTuple);
    }

    public void refresh(BavetFilterTriTuple<A, B, C> tuple) {
        A a = tuple.getFactA();
        B b = tuple.getFactB();
        C c = tuple.getFactC();
        List<BavetAbstractTriTuple<A, B, C>> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractTriTuple<A, B, C> childTuple : childTupleList) {
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            if (predicate.test(a, b, c)) {
                for (BavetAbstractTriNode<A, B, C> childNode : childNodeList) {
                    BavetAbstractTriTuple<A, B, C> childTuple = childNode.createTuple(tuple);
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
