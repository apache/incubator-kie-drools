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

import org.optaplanner.core.impl.score.stream.bavet.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.common.BavetTupleState;

public final class BavetFromUniNode<A> extends BavetAbstractUniNode<A> {

    private final Class<A> fromClass;

    private List<BavetAbstractUniNode<A>> childNodeList = new ArrayList<>();

    public BavetFromUniNode(BavetConstraintSession session, int nodeOrder,
            Class<A> fromClass) {
        super(session, nodeOrder);
        this.fromClass = fromClass;
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
        return fromClass.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof BavetFromUniNode) {
            BavetFromUniNode<?> other = (BavetFromUniNode<?>) o;
            return fromClass.equals(other.fromClass);
        } else {
            return false;
        }
    }

    // ************************************************************************
    // Runtime
    // ************************************************************************

    public BavetFromUniTuple<A> createTuple(A a) {
        return new BavetFromUniTuple<>(this, a, childNodeList.size());
    }

    @Override
    public BavetAbstractUniTuple<A> createTuple(BavetAbstractUniTuple<A> parentTuple) {
        throw new IllegalStateException("The fromUniNode (" + getClass().getSimpleName()
                + ") can't have a parentTuple (" + parentTuple + ");");
    }

    public void refresh(BavetFromUniTuple<A> tuple) {
        List<BavetAbstractUniTuple<A>> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractUniTuple<A> childTuple : childTupleList) {
            // TODO the entire FromUniNode isn't really doing anything
            // so the destruction/construction is just an update op unless it's CREATING or DYING
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            for (BavetAbstractUniNode<A> childNode : childNodeList) {
                BavetAbstractUniTuple<A> childTuple = childNode.createTuple(tuple);
                childTupleList.add(childTuple);
                session.transitionTuple(childTuple, BavetTupleState.CREATING);
            }
        }
        tuple.refreshed();
    }

    @Override
    public String toString() {
        return "From(" + fromClass.getSimpleName() + ") with " + childNodeList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
