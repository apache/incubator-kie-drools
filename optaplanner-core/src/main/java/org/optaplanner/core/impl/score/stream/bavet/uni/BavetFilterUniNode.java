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

import java.util.List;
import java.util.function.Predicate;

import org.optaplanner.core.impl.score.stream.bavet.session.BavetConstraintSession;
import org.optaplanner.core.impl.score.stream.bavet.session.BavetTupleState;

public final class BavetFilterUniNode<A> extends BavetAbstractUniNode<A> {

    private final Predicate<A> predicate;

    private final List<BavetAbstractUniNode<A>> childNodeList;

    public BavetFilterUniNode(BavetConstraintSession session, int nodeOrder,
            Predicate<A> predicate, List<BavetAbstractUniNode<A>> childNodeList) {
        super(session, nodeOrder);
        this.predicate = predicate;
        this.childNodeList = childNodeList;
    }

    @Override
    public BavetFilterUniTuple<A> createTuple(BavetAbstractUniTuple<A> parentTuple) {
        return new BavetFilterUniTuple<>(this, parentTuple, childNodeList.size());
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
        return "Filter() to " + childNodeList.size()  + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
